import asyncio
import websockets
import json

# Connect to Python LSP Server using WebSockets
async def connect_to_pylsp():
    uri = "ws://localhost:3000"
    async with websockets.connect(uri) as websocket:
        # Step 1: Send an initialize request to the Python LSP Server
        initialize_params = {
            "processId": None,
            "rootUri": "file:///path/to/your/project",  # Change this to your project root
            "capabilities": {}
        }
        initialize_request = {
            "jsonrpc": "2.0",
            "id": 1,
            "method": "initialize",
            "params": initialize_params
        }
        
        await send_message(websocket, initialize_request)
        
        # Step 2: Wait for the response to the initialize request
        response = await receive_message(websocket)
        print("Initialization Response:", response)
        
        # Step 3: Send a textDocument/didOpen notification to let the server know about the file
        open_params = {
            "textDocument": {
                "uri": "file:///path/to/your/project/example.py",  # Change to the Python file
                "languageId": "python",
                "version": 1,
                "text": "print('Hello World')"
            }
        }
        open_request = {
            "jsonrpc": "2.0",
            "method": "textDocument/didOpen",
            "params": open_params
        }
        
        await send_message(websocket, open_request)
        
        # Step 4: Request a definition for an identifier
        definition_params = {
            "textDocument": {
                "uri": "file:///path/to/your/project/example.py"
            },
            "position": {
                "line": 0,  # Change to the line number where the identifier is located (zero-based)
                "character": 6  # Change to the character index (zero-based)
            }
        }
        definition_request = {
            "jsonrpc": "2.0",
            "id": 2,
            "method": "textDocument/definition",
            "params": definition_params
        }

        await send_message(websocket, definition_request)
        
        # Step 5: Wait for the definition response
        definition_response = await receive_message(websocket)
        print("Definition Response:", definition_response)

# Helper function to send JSON-RPC messages over WebSocket
async def send_message(websocket, message):
    content = json.dumps(message)
    await websocket.send(content)
    print(f"Sent: {content}")

# Helper function to receive JSON-RPC messages over WebSocket
async def receive_message(websocket):
    response = await websocket.recv()
    response_json = json.loads(response)
    return response_json

# Main entry point
if __name__ == "__main__":
    asyncio.run(connect_to_pylsp())
