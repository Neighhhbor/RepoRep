import socket
import json
import os
import time
import networkx as nx
import logging
from tqdm import tqdm  # Import tqdm for the progress bar

# Configure logging
logging.basicConfig(
    level=logging.INFO,  # Set to DEBUG to capture all debug messages
    format='%(asctime)s - %(levelname)s - %(message)s',
)

LSP_HOST = 'localhost'
LSP_PORT = 5006

ROOT_URI = "file:///home/sxj/Desktop/Workspace/Development/RepoRepresentation/RepoRep/examples/Java/aixcoderhub"
opened_files = set()
request_id = 1


def send_request(sock, message):
    """发送 JSON-RPC 消息。"""
    global request_id
    message["id"] = request_id
    message_str = json.dumps(message)
    content_length = len(message_str)
    header = f"Content-Length: {content_length}\r\n\r\n"
    full_message = header + message_str

    logging.debug(f"Sending Request (ID: {request_id}):\n{full_message}")
    try:
        sock.sendall(full_message.encode('utf-8'))
        request_id += 1
    except socket.error as e:
        logging.error(f"Socket error while sending request ID {request_id}: {e}")
        return False
    return True


def receive_response(sock):
    """接收服务器的响应，并解析完整的 JSON-RPC 消息。"""
    response = ""
    while True:
        try:
            chunk = sock.recv(4096).decode('utf-8')
            if not chunk:
                logging.warning("No data received, server may have closed the connection.")
                return None

            response += chunk
            logging.debug(f"Raw response received: {response}")

            # Process the response if we have a complete header and body
            while "\r\n\r\n" in response:
                # Split the header and the remaining data (body)
                header, rest = response.split("\r\n\r\n", 1)

                if "Content-Length: " not in header:
                    logging.error(f"Malformed header (missing Content-Length): {header}")
                    response = rest  # Reset response to remaining data and continue
                    continue

                try:
                    # Extract the Content-Length value
                    content_length_str = header.split("Content-Length: ")[1].strip()
                    content_length = int(content_length_str)
                except (IndexError, ValueError) as e:
                    logging.error(f"Error parsing Content-Length: {header} - {e}")
                    response = rest  # Reset response to remaining data and continue
                    continue

                # Ensure the remaining response contains the full content
                if len(rest) >= content_length:
                    # Extract the JSON content and update the response buffer
                    content = rest[:content_length]
                    response = rest[content_length:]

                    try:
                        # Parse the JSON content into a message
                        message = json.loads(content)
                        logging.debug(f"Received Message:\n{json.dumps(message, indent=2)}")
                        return message
                    except json.JSONDecodeError as e:
                        logging.error(f"JSON Decode Error: {content} - {e}")
                else:
                    # Wait for more data if the content is incomplete
                    break

        except socket.timeout as e:
            logging.warning(f"Socket timeout: {e}")
            return None
        except socket.error as e:
            logging.error(f"Socket error while receiving response: {e}")
            return None


def send_request_and_wait(sock, message):
    """发送请求并等待响应。"""
    if send_request(sock, message):
        return receive_response(sock)
    return None


def initialize(sock):
    """发送初始化请求并等待响应。"""
    message = {
        "jsonrpc": "2.0",
        "method": "initialize",
        "params": {
            "processId": os.getpid(),
            "rootUri": ROOT_URI,
            "capabilities": {},
            "workspaceFolders": [{"uri": ROOT_URI, "name": "aixcorderhub"}],
            "settings": {
                "java": {
                    "home": "/usr/lib/jvm/java-17-openjdk-amd64",  # 设置 JDK 路径
                    "project": {
                        "referencedLibraries": ["lib/**/*.jar"]
                    }
                }
            }
        }
    }
    response = send_request_and_wait(sock, message)
    if response:
        logging.info("Java Language Server initialized successfully.")
    else:
        logging.error("Failed to initialize Java Language Server.")


def did_open_file(sock, file_uri, content):
    """发送 didOpen 通知，告知服务器打开文件。"""
    message = {
        "jsonrpc": "2.0",
        "method": "textDocument/didOpen",
        "params": {
            "textDocument": {
                "uri": file_uri,
                "languageId": "java",
                "version": 1,
                "text": content
            }
        }
    }
    send_request_and_wait(sock, message)


def request_definition(sock, file_uri, position):
    """发送符号定义请求，并同步等待响应。"""
    message = {
        "jsonrpc": "2.0",
        "method": "textDocument/definition",
        "params": {
            "textDocument": {"uri": file_uri},
            "position": position
        }
    }
    response = send_request_and_wait(sock, message)
    if response and "result" in response:
        logging.info(f"Definition found: {response['result']}")
        return response["result"]
    return None


def process_ast_nodes(sock, graph):
    """遍历 AST 节点，并请求符号定义。"""
    total_nodes = len(graph.nodes)  # Total number of nodes to process

    # Use tqdm to create a progress bar
    with tqdm(total=total_nodes, desc="Processing AST Nodes", unit="node") as pbar:
        for node_id, node_data in graph.nodes(data=True):
            if node_data["type"] in ["method_declaration", "class_declaration", 'identifier']:
                file_path = node_data["file_path"]
                file_uri = f"file://{file_path}"
                position = {
                    "line": node_data["start_point"][0],
                    "character": node_data["start_point"][1]
                }
                # node_startbyte = node_data["start_byte"]
                # node_endbyte = node_data["end_byte"]

                # # Read node content from the file
                # with open(file_path, 'r') as f:
                #     content = f.read()
                # node_content = content[node_startbyte:node_endbyte]

                # logging.debug(f"Node content:\n{node_content}")

                # Open the file if it's not already opened
                if file_uri not in opened_files:
                    with open(file_path, 'r') as f:
                        content = f.read()
                    did_open_file(sock, file_uri, content)
                    opened_files.add(file_uri)

                # Request symbol definition (synchronously)
                request_definition(sock, file_uri, position)

            # Update the progress bar
            pbar.update(1)


def main():
    """程序主入口，负责连接服务器并处理 AST 节点。"""
    reponame = 'aixcoderhub'
    graph_path = f'/home/sxj/Desktop/Workspace/Development/RepoRepresentation/RepoRep/src/output/{reponame}.json'
    try:
        with open(graph_path, 'r') as f:
            graph = nx.node_link_graph(json.load(f), edges="links")
    except FileNotFoundError:
        logging.debug(f"Graph file not found: {graph_path}")
        return

    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as sock:
        try:
            sock.connect((LSP_HOST, LSP_PORT))
        except ConnectionRefusedError:
            logging.debug("Cannot connect to LSP server.")
            return

        sock.settimeout(10.0)  # Set a reasonable timeout for socket operations
        initialize(sock)
        process_ast_nodes(sock, graph)

        output_path = f'/home/sxj/Desktop/Workspace/Development/RepoRepresentation/RepoRep/src/output/{reponame}_definitiongraph.json'
        with open(output_path, 'w') as f:
            json.dump(nx.node_link_data(graph, edges="links"), f, indent=4)
        logging.debug(f"Definition graph saved to {output_path}")

if __name__ == "__main__":
    main()
