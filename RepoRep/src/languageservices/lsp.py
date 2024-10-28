import os
import sys
import json
import socket
import logging
import time
from tqdm import tqdm
from websockets.sync.client import connect

# Configure logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s',filename='lsp.log', filemode='w')
logger = logging.getLogger(__name__)

# Load the graph from JSON
def load_graph_data(graph_file_path):
    with open(graph_file_path, 'r') as f:
        return json.load(f)

# Connect to gopls using socket
def connect_to_gopls():
    client = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    client.settimeout(1800)  # Set a timeout of 10 seconds for all socket operations
    client.connect(('localhost', 8080))  # Updated to match the provided gopls command
    return client

# Connect to pylsp using WebSocket synchronously
def connect_to_pylsp():
    uri = "ws://localhost:3000"
    try:
        return connect(uri, timeout=10)
    except Exception as e:
        logger.error(f"Failed to connect to pylsp: {e}")
        raise

# Send JSON-RPC request over WebSocket (for pylsp)
def send_message_pylsp(websocket, message):
    content = json.dumps(message)
    try:
        websocket.send(content)
        logger.info(f"Sent to pylsp: {content}")
    except Exception as e:
        logger.error(f"Error occurred while trying to send a message to pylsp: {e}")

# Send JSON-RPC request over socket (for gopls)
def send_message_gopls(client, message):
    content = json.dumps(message)
    header = f"Content-Length: {len(content)}\r\n\r\n"
    try:
        client.sendall(header.encode('utf-8') + content.encode('utf-8'))
        logger.info(f"Sent to gopls: {content}")
    except socket.timeout:
        logger.error("Timeout occurred while sending a message to gopls.")

# Parse the header and content from LSP response (for gopls)
def read_response_gopls(client):
    response = b""
    try:
        while True:
            data = client.recv(4096)
            if not data:
                break
            response += data

            if b"\r\n\r\n" in response:
                header, rest = response.split(b"\r\n\r\n", 1)
                content_length = None
                for line in header.split(b"\r\n"):
                    if line.startswith(b"Content-Length:"):
                        content_length = int(line.split(b": ")[1])
                        break

                if content_length and len(rest) >= content_length:
                    body = rest[:content_length]
                    response_json = json.loads(body)
                    # Handle server notifications or window messages
                    if "method" in response_json and response_json["method"] == "window/showMessage":
                        logger.info(f"Received message from gopls: {response_json['params']['message']}")
                    else:
                        return response_json
    except socket.timeout:
        logger.error("Timeout occurred while reading response from gopls.")
    return None

# Function to send didOpen notification to pylsp
def send_did_open_notification_pylsp(websocket, file_path):
    with open(file_path, 'r') as f:
        file_content = f.read()

    params = {
        "textDocument": {
            "uri": f"file://{file_path}",
            "languageId": "python",
            "version": 1,
            "text": file_content
        }
    }
    did_open_request = {
        "jsonrpc": "2.0",
        "method": "textDocument/didOpen",
        "params": params
    }
    send_message_pylsp(websocket, did_open_request)
    try:
        response = websocket.recv()
        response_json = json.loads(response)
        logger.info(f"Received didOpen response from pylsp: {response_json}")
    except Exception as e:
        logger.error(f"Error occurred while receiving didOpen response from pylsp: {e}")

# Send initialize request to pylsp
def initialize_pylsp(websocket, root_uri):
    params = {
        "processId": None,
        "rootUri": root_uri,
        "capabilities": {},
        "workspaceFolders": [
            {
                "uri": root_uri,
                "name": "example_workspace"
            }
        ]
    }
    initialize_request = {
        "jsonrpc": "2.0",
        "id": 1,
        "method": "initialize",
        "params": params
    }
    send_message_pylsp(websocket, initialize_request)
    try:
        response = websocket.recv()
        response_json = json.loads(response)
        logger.info(f"Received initialization response from pylsp: {response_json}")
    except Exception as e:
        logger.error(f"Error occurred while receiving initialization response from pylsp: {e}")

# Send initialize request to gopls
def initialize_gopls(client, root_uri):
    params = {
        "processId": None,
        "rootUri": root_uri,
        "capabilities": {},
        "workspaceFolders": [
            {
                "uri": root_uri,
                "name": "example_workspace"
            }
        ]
    }
    initialize_request = {
        "jsonrpc": "2.0",
        "id": 1,
        "method": "initialize",
        "params": params
    }
    send_message_gopls(client, initialize_request)
    response = read_response_gopls(client)
    logger.info(f"Received initialization response from gopls: {response}")

    # Send 'initialized' notification after receiving initialization response
    initialized_request = {
        "jsonrpc": "2.0",
        "method": "initialized",
        "params": {}
    }
    send_message_gopls(client, initialized_request)
    logger.info("Sent 'initialized' notification to gopls.")

    # Poll for server readiness
    for _ in range(60):  # Poll for up to 60 seconds
        time.sleep(3)  # Wait for three seconds to give gopls more time to initialize
        ready_check = {
            "jsonrpc": "2.0",
            "method": "initialized",
            "params": {}
        }
        send_message_gopls(client, ready_check)
        response = read_response_gopls(client)
        if response and 'result' in response:
            logger.info("gopls is fully initialized and ready.")
            return
    logger.warning("gopls may not be fully initialized, continuing with caution.")

# Extract identifier text from file given byte range
def extract_text_from_file(file_path, start_byte, end_byte):
    try:
        with open(file_path, 'r') as f:
            f.seek(start_byte)
            return f.read(end_byte - start_byte)
    except Exception as e:
        logger.error(f"An error occurred while extracting text from file {file_path}: {e}")
        return None

# Request definition for Python identifiers (WebSocket)
def request_definition_pylsp(websocket, file_uri, line, character, node_text, results):
    params = {
        "textDocument": {
            "uri": file_uri
        },
        "position": {
            "line": line,
            "character": character
        }
    }
    definition_request = {
        "jsonrpc": "2.0",
        "id": 1,
        "method": "textDocument/definition",
        "params": params
    }

    try:
        logger.info(f"Requesting definition for '{node_text}' at {file_uri}:{line}:{character}")
        send_message_pylsp(websocket, definition_request)
        response = websocket.recv()
        response_json = json.loads(response)
        logger.info(f"Received response for '{node_text}': {response_json}")
        if "result" in response_json and response_json["result"]:
            logger.info(f"Definition for '{node_text}' found: {response_json['result']}")
            results.append({
                "node_text": node_text,
                "definition": response_json['result']
            })
        else:
            logger.info(f"No definition found for '{node_text}'.")
            results.append({
                "node_text": node_text,
                "definition": None
            })
    except Exception as e:
        logger.error(f"An error occurred while requesting definition for '{node_text}': {e}")
        results.append({
            "node_text": node_text,
            "definition": None,
            "error": str(e)
        })

# Request definition for Go identifiers (synchronous, socket)
def request_definition_gopls(client, file_uri, line, character, node_text, results):
    params = {
        "textDocument": {
            "uri": file_uri
        },
        "position": {
            "line": line,
            "character": character
        }
    }
    definition_request = {
        "jsonrpc": "2.0",
        "id": 1,
        "method": "textDocument/definition",
        "params": params
    }

    logger.info(f"Requesting definition for '{node_text}' at {file_uri}:{line}:{character}")
    send_message_gopls(client, definition_request)
    response = read_response_gopls(client)
    logger.info(f"Received response for '{node_text}': {response}")
    if response and "result" in response and response["result"]:
        logger.info(f"Definition for '{node_text}' found: {response['result']}")
        results.append({
            "node_text": node_text,
            "definition": response['result']
        })
    else:
        logger.info(f"No definition found for '{node_text}'")
        results.append({
            "node_text": node_text,
            "definition": None
        })

def main(graph_file_path, output_file_path):
    # Load the graph data
    graph_data = load_graph_data(graph_file_path)
    
    # Initialize pylsp and gopls connections
    pylsp_websocket = None
    gopls_client = None

    # List to collect results
    results = []

    # Global set to track opened files
    opened_files = set()

    try:
        # Attempt to connect to pylsp
        pylsp_websocket = connect_to_pylsp()  # Establish the WebSocket connection
        initialize_pylsp(pylsp_websocket, "file:///home/sxj/Desktop/Workspace/Development/RepoRepresentation/RepoRep/examples/wire")
        
        # Attempt to connect to gopls
        gopls_client = connect_to_gopls()  # Connect to gopls
        initialize_gopls(gopls_client, "file:///home/sxj/Desktop/Workspace/Development/RepoRepresentation/RepoRep/examples/wire")

        # Progress bar setup with tqdm
        with tqdm(total=len(graph_data['nodes']), desc="Processing Nodes") as pbar:
            # Iterate over nodes to find definitions
            for node in graph_data['nodes']:
                try:
                    # Extract the file path from the node
                    file_path = node.get('file_path')
                    if not file_path:
                        logger.warning(f"Warning: 'file_path' is missing for node {node.get('id')}. Skipping node.")
                        pbar.update(1)
                        continue

                    if node['type'] == 'identifier':
                        # Use the file_path directly from the node
                        file_path = node.get('file_path')
                        if not file_path:
                            logger.warning(f"Warning: 'file_path' is missing for node {node.get('id')}. Skipping node.")
                            pbar.update(1)
                            continue

                        # Extract the text from the file based on byte range
                        start_byte = node.get('start_byte')
                        end_byte = node.get('end_byte')
                        if start_byte is None or end_byte is None:
                            logger.warning(f"Warning: Byte range is missing for node {node.get('id')}. Skipping node.")
                            pbar.update(1)
                            continue

                        node_text = extract_text_from_file(file_path, start_byte, end_byte)
                        if not node_text:
                            logger.warning(f"Warning: Failed to extract text for node {node.get('id')}. Skipping node.")
                            pbar.update(1)
                            continue

                        file_uri = f"file://{file_path}"
                        line, character = node['start_point']
                        if not isinstance(line, int) or not isinstance(character, int):
                            logger.warning(f"Warning: 'start_point' for node {node.get('id')} is not valid. Skipping node.")
                            pbar.update(1)
                            continue

                        # Open the file in the LSP server if not already opened
                        if file_path not in opened_files:
                            if file_path.endswith('.py'):
                                send_did_open_notification_pylsp(pylsp_websocket, file_path)
                            else:
                                send_message_gopls(gopls_client, {
                                    "jsonrpc": "2.0",
                                    "method": "textDocument/didOpen",
                                    "params": {
                                        "textDocument": {
                                            "uri": file_uri,
                                            "languageId": "go",
                                            "version": 1,
                                            "text": open(file_path, 'r').read()
                                        }
                                    }
                                })
                            opened_files.add(file_path)

                        # Request definitions using the correct LSP server based on the file type
                        if file_path.endswith('.py'):
                            # Use pylsp for Python files
                            request_definition_pylsp(pylsp_websocket, file_uri, line, character, node_text, results)
                        elif file_path.endswith('.go'):
                            # Use gopls for Go files
                            request_definition_gopls(gopls_client, file_uri, line, character, node_text, results)
                except Exception as e:
                    logger.error(f"An error occurred while processing node {node}: {e}")
                    results.append({
                        "node_text": node_text if 'node_text' in locals() else 'unknown',
                        "definition": None,
                        "error": str(e)
                    })
                
                # Update the progress bar
                pbar.update(1)

    except Exception as e:
        logger.error(f"An error occurred: {e}")
    finally:
        # Close both clients if they were successfully initialized
        if pylsp_websocket is not None:
            pylsp_websocket.close()  # Ensure that the WebSocket is closed properly
        if gopls_client is not None:
            gopls_client.close()  # Close the gopls socket connection

        # Save the results to a file
        with open(output_file_path, 'w') as f:
            if results:
                json.dump(results, f, indent=4)
                logger.info(f"Definitions saved to {output_file_path}")
            else:
                json.dump({"message": "No definitions were found."}, f, indent=4)
                logger.info(f"No definitions were found.")

# Run the main function
if __name__ == "__main__":
    graph_file_path = "/home/sxj/Desktop/Workspace/Development/RepoRepresentation/RepoRep/src/output/initial_parse_output.json"
    output_file_path = "/home/sxj/Desktop/Workspace/Development/RepoRepresentation/RepoRep/src/output/definition_results.json"
    main(graph_file_path, output_file_path)
