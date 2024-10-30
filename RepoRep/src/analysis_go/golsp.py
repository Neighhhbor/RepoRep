import socket
import json
import os
import time
import networkx as nx
import logging
import re
from tqdm import tqdm  # Import tqdm for the progress bar
import sys
import argparse

# Configure logging
logging.basicConfig(
    level=logging.INFO,  # Set to DEBUG to capture all debug messages
    format='%(asctime)s - %(levelname)s - %(message)s',
)

LSP_HOST = 'localhost'
LSP_PORT = 8080

opened_files = set()
request_id = 1
pending_requests = {}  # Track requests by their IDs


def send_request(sock, message):
    """Send a JSON-RPC message."""
    global request_id
    message["id"] = request_id
    pending_requests[request_id] = message  # Track the request
    request_id += 1

    message_str = json.dumps(message)
    content_length = len(message_str)
    header = f"Content-Length: {content_length}\r\n\r\n"
    full_message = header + message_str

    logging.debug(f"Sending Request (ID: {message['id']}):\n{full_message}")
    try:
        sock.sendall(full_message.encode('utf-8'))
    except socket.error as e:
        logging.error(f"Socket error while sending request ID {message['id']}: {e}")
        del pending_requests[message["id"]]
        return False
    return True


def read_exact(sock, nbytes):
    """Read an exact number of bytes from the socket."""
    data = b''
    while len(data) < nbytes:
        more = sock.recv(nbytes - len(data))
        if not more:
            raise EOFError(f'Expected {nbytes} bytes but only received {len(data)} bytes before the connection closed.')
        data += more
    return data


def receive_response(sock):
    """Receive a response from the server and parse the full JSON-RPC message."""
    sock.settimeout(30.0)  # Set a longer timeout to handle initialization delays
    while True:
        try:
            # Read the header first to get the content length
            header = ""
            while "\r\n\r\n" not in header:
                header += sock.recv(1).decode('utf-8')

            # Extract Content-Length from the header
            match = re.search(r"Content-Length: (\d+)", header)
            if not match:
                logging.error(f"Malformed header: {header}")
                continue

            content_length = int(match.group(1))
            # Read the content based on Content-Length
            content = read_exact(sock, content_length).decode('utf-8')

            try:
                message = json.loads(content)
                logging.debug(f"Received Message:\n{json.dumps(message, indent=2)}")
                
                # Log any 'window/logMessage' notifications
                if message.get("method") == "window/logMessage":
                    logging.info(f"Log Message from Server: {message['params']['message']}")
                
                if "id" in message and message["id"] in pending_requests:
                    del pending_requests[message["id"]]  # Remove from pending requests
                    return message
            except json.JSONDecodeError as e:
                logging.error(f"JSON Decode Error: {content} - {e}")
        except socket.timeout as e:
            logging.warning(f"Socket timeout while receiving response: {e}")
            return None


def send_request_and_wait(sock, message):
    """Send a request and wait for its response."""
    if send_request(sock, message):
        return receive_response(sock)
    return None


def initialize(sock, root_uri):
    """Send the initialize request and wait for the response."""
    message = {
        "jsonrpc": "2.0",
        "method": "initialize",
        "params": {
            "processId": os.getpid(),
            "rootUri": root_uri,
            "capabilities": {},
            "workspaceFolders": [{"uri": root_uri, "name": "Workspace"}],
        }
    }
    response = send_request_and_wait(sock, message)
    if response and "result" in response:
        logging.info("Go Language Server initialized successfully.")
        send_initialized_notification(sock)
    else:
        logging.error("Failed to initialize Go Language Server.")


def send_initialized_notification(sock):
    """Send the initialized notification after successful initialization."""
    message = {
        "jsonrpc": "2.0",
        "method": "initialized",
        "params": {}
    }
    send_request(sock, message)


def did_open_file(sock, file_uri, content):
    """Send didOpen notification to inform the server that a file has been opened."""
    message = {
        "jsonrpc": "2.0",
        "method": "textDocument/didOpen",
        "params": {
            "textDocument": {
                "uri": file_uri,
                "languageId": "Go",
                "version": 1,
                "text": content
            }
        }
    }
    send_request(sock, message)


def request_definition(sock, file_uri, position):
    """Send a definition request and wait for the response."""
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
        logging.debug(f"Definition found: {response['result']}")
        return response["result"]
    return None


def process_ast_nodes(sock, graph):
    """Traverse AST nodes and request symbol definitions."""
    total_nodes = len(graph.nodes)  # Total number of nodes to process

    # Use tqdm to create a progress bar
    with tqdm(total=total_nodes, desc="Processing AST Nodes", unit="node") as pbar:
        for node_id, node_data in graph.nodes(data=True):
            if node_data["type"] in ["method_declaration", "class_declaration", 'identifier']:
                file_id = node_data["file_id"]
                file_path = graph.nodes[file_id]["path"]
                file_uri = f"file://{file_path}"

                position = {
                    "line": node_data["start_point"][0],
                    "character": node_data["start_point"][1]
                }
                # Open the file if it's not already opened
                if file_uri not in opened_files:
                    with open(file_path, 'r') as f:
                        content = f.read()
                    did_open_file(sock, file_uri, content)
                    opened_files.add(file_uri)

                # Request symbol definition (synchronously)
                result = request_definition(sock, file_uri, position)
                if result:
                    node_data["definition"] = result

            # Update the progress bar
            pbar.update(1)


def main():
    """Main function to connect to the server and process AST nodes."""
    # 检查命令行参数
    parser = argparse.ArgumentParser(description="Parse a source code repository and generate its representation graph.")
    parser.add_argument('repo_path', type=str, help="Path to the repository to be parsed.")
    parser.add_argument('--output_dir', type=str, default="./output", help="Directory where the output will be saved.")
    args = parser.parse_args()

    repo_path = args.repo_path
    results_dir = os.path.join(args.output_dir, os.path.basename(repo_path))
    os.makedirs(results_dir, exist_ok=True)
    root_uri = f"file://{repo_path}"
    graph_path = os.path.join(results_dir, 'repoparser.json')
    
    try:
        with open(graph_path, 'r') as f:
            graph = nx.node_link_graph(json.load(f), edges="links")
    except FileNotFoundError:
        logging.error(f"未找到图文件: {graph_path}")
        return

    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as sock:
        try:
            sock.connect((LSP_HOST, LSP_PORT))
        except ConnectionRefusedError:
            logging.error("无法连接到 LSP 服务器")
            return

        sock.settimeout(10.0)
        
        # 将 ROOT_URI 传递给 initialize 函数
        initialize(sock, root_uri)
        process_ast_nodes(sock, graph)

        # 保存结果
        output_path = os.path.join(results_dir, 'definitiongraph.json')
        with open(output_path, 'w') as f:
            json.dump(nx.node_link_data(graph, edges="links"), f, indent=4)
        logging.info(f"定义图已保存到 {output_path}")


if __name__ == "__main__":
    main()
