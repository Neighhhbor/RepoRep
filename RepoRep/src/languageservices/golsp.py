import socket
import json
import time
import logging

# Configure logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

# Connect to gopls using socket
def connect_to_gopls():
    client = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    client.settimeout(30000)
    client.connect(('localhost', 8080))
    return client

# Send JSON-RPC request over socket
def send_message_gopls(client, message):
    content = json.dumps(message)
    header = f"Content-Length: {len(content)}\r\n\r\n"
    try:
        client.sendall(header.encode('utf-8') + content.encode('utf-8'))
        logger.info(f"Sent to gopls: {content}")
    except socket.timeout:
        logger.error("Timeout occurred while sending a message to gopls.")

# Read response from gopls
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
                    logger.info(f"Received response from gopls: {response_json}")

                    # Handle server notifications or window messages
                    if "method" in response_json and response_json["method"] == "window/showMessage":
                        logger.info(f"Received message from gopls: {response_json['params']['message']}")
                    else:
                        return response_json
    except socket.timeout:
        logger.error("Timeout occurred while reading response from gopls.")
    return None

# Wait for gopls to finish loading packages
def wait_for_gopls_ready(client):
    max_attempts = 30
    for attempt in range(max_attempts):
        logger.info(f"Attempt {attempt + 1}/{max_attempts}: Checking if gopls is ready...")
        time.sleep(3)  # Wait for 3 seconds between checks

        # Check the logs for 'Loading packages...' messages
        response = read_response_gopls(client)
        if response and "method" in response and response["method"] == "window/showMessage":
            message = response["params"]["message"]
            if "Loading packages..." not in message:
                logger.info("gopls seems to be ready based on message logs.")
                return True
        elif response and 'result' in response:
            # If a normal response with 'result' is returned, it's likely ready
            logger.info("gopls returned a valid response, assuming it is ready.")
            return True

    logger.warning("gopls may not be fully initialized, but continuing with caution.")
    return False

# Main logic to interact with gopls
def main():
    client = connect_to_gopls()

    # Initialize gopls
    root_uri = "file:///home/sxj/Desktop/Workspace/Development/RepoRepresentation/RepoRep/examples/hello"
    initialize_request = {
        "jsonrpc": "2.0",
        "id": 1,
        "method": "initialize",
        "params": {
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
    }
    send_message_gopls(client, initialize_request)
    response = read_response_gopls(client)

    if response and "result" in response:
        logger.info(f"Initialization successful with response: {response}")

    # Send 'initialized' notification
    initialized_request = {
        "jsonrpc": "2.0",
        "method": "initialized",
        "params": {}
    }
    send_message_gopls(client, initialized_request)
    logger.info("Sent 'initialized' notification to gopls.")

    # Wait for gopls to finish loading packages
    if not wait_for_gopls_ready(client):
        logger.error("gopls is taking too long to be ready. Exiting.")
        return

    # After confirming gopls is ready, send a find definition request
    find_definition_request = {
        "jsonrpc": "2.0",
        "id": 2,
        "method": "textDocument/definition",
        "params": {
            "textDocument": {
                "uri": "file:///home/sxj/Desktop/Workspace/Development/RepoRepresentation/RepoRep/examples/hello/hello.go"
            },
            "position": {
                "line": 6,
                "character": 5
            }
        }
    }
    send_message_gopls(client, find_definition_request)
    response = read_response_gopls(client)

    if response:
        logger.info(f"Find definition response: {response}")

if __name__ == "__main__":
    main()
