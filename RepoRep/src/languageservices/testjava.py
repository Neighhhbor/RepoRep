import socket
import json
import os
import time

# Java Language Server 的地址和端口
LSP_HOST = 'localhost'
LSP_PORT = 5006

# 要请求的文件路径
FILE_PATH = "/home/sxj/Desktop/Workspace/Development/RepoRepresentation/RepoRep/examples/Java/HelloWorld/HelloWorld.java"
# 设置项目根目录
ROOT_URI = "/home/sxj/Desktop/Workspace/Development/RepoRepresentation/RepoRep"

# 初始化请求的 ID（用于 JSON-RPC 协议）
request_id = 1

def send_request(sock, message):
    """发送 JSON-RPC 消息给 Java Language Server，并打印日志。"""
    message_str = json.dumps(message, indent=2)
    content_length = len(message_str)
    header = f"Content-Length: {content_length}\r\n\r\n"
    full_message = header + message_str

    print("\nSending Request:")
    print(message_str)  # 打印发送的请求消息

    sock.sendall(full_message.encode('utf-8'))

def receive_response(sock):
    """接收并解析服务器的响应，打印日志。"""
    response = ""
    messages = []

    while True:
        chunk = sock.recv(4096).decode('utf-8')
        response += chunk

        while "\r\n\r\n" in response:
            header, rest = response.split("\r\n\r\n", 1)
            content_length = int(header.split(": ")[1])

            if len(rest) >= content_length:
                content = rest[:content_length]
                response = rest[content_length:]
                try:
                    message = json.loads(content)
                    print("\nReceived Response:")
                    print(json.dumps(message, indent=2))  # 打印收到的响应消息
                    messages.append(message)
                except json.JSONDecodeError:
                    print("JSON Decode Error:", content)
            else:
                break

        if messages:
            break

    return messages

def main():
    global request_id

    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as sock:
        sock.connect((LSP_HOST, LSP_PORT))

        # 初始化服务器
        initialize_message = {
            "jsonrpc": "2.0",
            "id": request_id,
            "method": "initialize",
            "params": {
                "processId": os.getpid(),
                "rootUri": f"file://{ROOT_URI}",
                "capabilities": {}
            }
        }
        send_request(sock, initialize_message)
        request_id += 1

        # 等待服务器准备就绪
        if not wait_for_server_ready(sock):
            print("服务器初始化超时。")
            return

        # 配置更新
        update_configuration(sock)

        # 打开文件通知
        with open(FILE_PATH, 'r') as f:
            file_content = f.read()

        did_open_message = {
            "jsonrpc": "2.0",
            "method": "textDocument/didOpen",
            "params": {
                "textDocument": {
                    "uri": f"file://{FILE_PATH}",
                    "languageId": "java",
                    "version": 1,
                    "text": file_content
                }
            }
        }
        send_request(sock, did_open_message)

        time.sleep(2)

        # 发送符号定义请求
        definition_message = {
            "jsonrpc": "2.0",
            "id": request_id,
            "method": "textDocument/definition",
            "params": {
                "textDocument": {"uri": f"file://{FILE_PATH}"},
                "position": {"line": 2, "character": 10}
            }
        }
        send_request(sock, definition_message)
        request_id += 1

        process_responses(sock)

def wait_for_server_ready(sock):
    """等待服务器准备就绪。"""
    start_time = time.time()
    while time.time() - start_time < 120:
        responses = receive_response(sock)
        for response in responses:
            if response.get("method") == "language/status" and response["params"].get("type") == "Started":
                print("服务器已准备就绪。")
                return True
        time.sleep(1)
    return False

def update_configuration(sock):
    """发送配置更新请求，并打印日志。"""
    config_message = {
        "jsonrpc": "2.0",
        "method": "workspace/didChangeConfiguration",
        "params": {
            "settings": {
                "java": {
                    "home": "/path/to/java/home",  # 可选: 设置 Java 环境路径
                    "project": {
                        "referencedLibraries": ["lib/**/*.jar"]
                   
