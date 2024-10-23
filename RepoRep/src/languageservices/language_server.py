import json
import socket

# Connect to gopls server
def connect_to_gopls():
    client = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    client.connect(('localhost', 8080))
    return client

# Send JSON-RPC message to gopls server
def send_message(client, message):
    content = json.dumps(message)
    header = f"Content-Length: {len(content)}\r\n\r\n"
    client.sendall(header.encode('utf-8') + content.encode('utf-8'))

# Create an LSP request
def create_request(method, params, request_id=1):
    return {
        "jsonrpc": "2.0",
        "id": request_id,
        "method": method,
        "params": params
    }

# Parse the header and content from LSP response
def read_response(client):
    response = b""
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
                return json.loads(body)

# Initialize gopls server
def initialize_gopls(client, root_uri):
    params = {
        "processId": None,
        "rootUri": root_uri,
        "capabilities": {}
    }
    request = create_request("initialize", params)
    send_message(client, request)
    response = read_response(client)
    return response

# Request definition location from gopls
def request_definition(client, file_uri, line, character):
    params = {
        "textDocument": {
            "uri": file_uri
        },
        "position": {
            "line": line,
            "character": character
        }
    }
    request = create_request("textDocument/definition", params, request_id=2)
    send_message(client, request)
    response = read_response(client)
    return response

# Load graph data from a JSON file
def load_graph_data(file_path):
    with open(file_path, 'r') as file:
        return json.load(file)

# Find definitions for each identifier in the graph data
def find_definitions_for_identifiers(graph_data):
    # Connect to gopls server
    client = connect_to_gopls()

    # Step 1: Initialize gopls
    root_uri = "file:///home/sxj/Desktop/Workspace/Development/RepoRepresentation/RepoRep/examples"
    init_response = initialize_gopls(client, root_uri)
    if "error" in init_response:
        print(f"Initialization error: {init_response['error']}")
        client.close()
        return

    # Step 2: Iterate through each identifier and find its definition
    for node in graph_data.get('nodes', []):
        if node.get('type') == 'identifier':
            file_path = node['file_path']
            file_uri = f"file://{file_path}"
            start_point = node['start_point']
            line = start_point[0]  # Zero-based line number
            character = start_point[1]  # Zero-based character index

            # Request definition of the identifier
            definition_response = request_definition(client, file_uri, line, character)
            if "result" in definition_response and definition_response["result"]:
                definition = definition_response["result"][0]  # Typically, a list is returned
                start = definition["range"]["start"]
                end = definition["range"]["end"]

                print(f"Definition for identifier '{node['text']}' found at:")
                print(f"File: {definition['uri']}")
                print(f"Start - Line: {start['line']}, Character: {start['character']}")
                print(f"End - Line: {end['line']}, Character: {end['character']}")
            else:
                print(f"No definition found for identifier '{node['text']}'")

    # Close connection to gopls
    client.close()

# Main function to load the graph and find definitions
if __name__ == "__main__":
    # Load graph data from the JSON file
    graph_file_path = "/home/sxj/Desktop/Workspace/Development/RepoRepresentation/RepoRep/src/output/processed_project_graph.json"
    graph_data = load_graph_data(graph_file_path)

    # Find definitions for each identifier in the graph data
    find_definitions_for_identifiers(graph_data)
