import asyncio
import websockets
import json
import time
import os
import logging

# Configure logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

# 连接到 Python Language Server (pylsp) 的 WebSocket
HOST = 'localhost'  # Python Language Server 运行的主机地址
PORT = 3000         # Python Language Server 监听的端口

async def log(message):
    logger.info(message)

async def send_request(uri, request):
    """发送请求并读取响应"""
    async with websockets.connect(uri) as websocket:
        request_str = json.dumps(request)
        await log(f"Sending: {request_str}")
        await websocket.send(request_str)
        
        while True:
            try:
                response = await websocket.recv()
                await log(f"Received: {response}")
                response_data = json.loads(response)
                if "id" in response_data and response_data["id"] == request.get("id"):
                    break
            except websockets.ConnectionClosed as e:
                await log(f"Connection closed: {e}")
                break
            except json.JSONDecodeError as e:
                await log(f"Failed to parse JSON response: {e}")
                break

async def send_message_pylsp(websocket, message):
    """发送 JSON-RPC 请求到 pylsp"""
    content = json.dumps(message)
    await log(f"Sending: {content}")
    await websocket.send(content)

    try:
        response = await websocket.recv()
        response_json = json.loads(response)
        await log(f"Received response from pylsp: {response_json}")
    except websockets.ConnectionClosed as e:
        await log(f"Connection closed while waiting for response: {e}")
    except json.JSONDecodeError as e:
        await log(f"Failed to parse JSON response: {e}")

async def send_did_open_notification_pylsp(websocket, file_path):
    """发送 didOpen 通知，用于通知服务器某个文件已经打开"""
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
    await send_message_pylsp(websocket, did_open_request)

async def main():
    uri = f"ws://{HOST}:{PORT}"
    file_path = "/home/sxj/Desktop/Workspace/Development/RepoRepresentation/RepoRep/examples/helloworld.py"
    
    async with websockets.connect(uri) as websocket:
        # 1. 发送初始化请求
        initialize_request = {
            "jsonrpc": "2.0",
            "id": 1,
            "method": "initialize",
            "params": {
                "processId": None,
                "rootUri": "file:///home/sxj/Desktop/Workspace/Development/RepoRepresentation/RepoRep/examples",
                "capabilities": {},
                "workspaceFolders": [
                    {
                        "uri": "file:///home/sxj/Desktop/Workspace/Development/RepoRepresentation/RepoRep/examples",
                        "name": "examples"
                    }
                ]
            }
        }
        await send_message_pylsp(websocket, initialize_request)
        
        # 等待一段时间，模拟客户端处理
        await asyncio.sleep(1)

        # 2. 发送 textDocument/didOpen 请求，用于通知服务器某个文件已经打开
        await send_did_open_notification_pylsp(websocket, file_path)

        # 等待一段时间，模拟客户端处理
        await asyncio.sleep(1)

        # 3. 发送 textDocument/definition 请求，用于查找某个符号的定义
        find_definition_request = {
            "jsonrpc": "2.0",
            "id": 3,
            "method": "textDocument/definition",
            "params": {
                "textDocument": {
                    "uri": f"file://{file_path}"
                },
                "position": {
                    "line": 0,
                    "character": 4  # 查找 "hello_world" 的定义位置
                }
            }
        }
        await send_message_pylsp(websocket, find_definition_request)

        # 等待一段时间，模拟客户端处理
        await asyncio.sleep(1)

# 运行主函数
asyncio.run(main())