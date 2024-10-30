import networkx as nx
import json
import logging
from tqdm import tqdm  # 引入 tqdm 进度条库

# 配置日志
logging.basicConfig(
    level=logging.INFO,  # 设置日志级别为 DEBUG
    format='%(asctime)s - %(levelname)s - %(message)s',
)
logger = logging.getLogger(__name__)  # 创建日志记录器

def find_definition_node(graph, definition):
    """
    根据 definition 信息找到对应的定义节点。
    """
    uri = definition["uri"].replace("file://", "")  # 转换 URI 为文件路径
    start_line = definition["range"]["start"]["line"]
    start_char = definition["range"]["start"]["character"]
    end_line = definition["range"]["end"]["line"]
    end_char = definition["range"]["end"]["character"]

    file_id = find_file_id_by_uri(graph, uri)
    if file_id is None:
        logger.debug(f"未找到与 URI 匹配的文件: {uri}")
        return None

    return find_node_in_file(graph, file_id, start_line, start_char, end_line, end_char)

def find_file_id_by_uri(graph, uri):
    """
    根据 URI 在图中找到对应的文件节点 ID (file_id)。
    """
    for node_id, node_data in graph.nodes(data=True):
        if node_data.get("type") == "file" and node_data.get("path") == uri:
            return node_id  # 返回文件节点的 ID（file_id）
    return None  # 未找到文件节点

def find_node_in_file(graph, file_id, start_line, start_char, end_line, end_char):
    """
    在给定的文件节点中查找与位置匹配的 AST 节点。
    """
    for node_id, node_data in graph.nodes(data=True):
        if node_data.get("file_id") == file_id:
            start_point = node_data.get("start_point", [None, None])
            end_point = node_data.get("end_point", [None, None])
            if (start_point[0] == start_line and start_point[1] == start_char
                and end_point[0] == end_line and end_point[1] == end_char):
                return node_id
    return None  # 未找到匹配的节点

def add_definition_id_to_nodes(graph):
    """
    在每个有 definition 信息的节点上添加 defid 字段。
    """
    nodes = list(graph.nodes(data=True))  # 将节点转为列表
    for node_id, node_data in tqdm(nodes, desc="Processing Nodes"):  # 使用 tqdm 进度条
        if "definition" in node_data:
            definition = node_data["definition"][0]  # 获取第一个定义信息
            def_node_id = find_definition_node(graph, definition)
            if def_node_id:
                node_data["defid"] = def_node_id
                logger.debug(f"节点 {node_id} 的定义节点 ID 为 {def_node_id}")
            else:
                logger.debug(f"未找到定义节点: {definition['uri']}")

def load_graph(input_path):
    """
    从 JSON 文件加载图数据。
    """
    try:
        with open(input_path, 'r') as f:
            data = json.load(f)
        graph = nx.node_link_graph(data, edges="links")
        logger.debug(f"成功加载图: {input_path}")
        return graph
    except FileNotFoundError:
        logger.debug(f"未找到图文件: {input_path}")
        return None
    except Exception as e:
        logger.debug(f"加载图时出错: {e}")
        return None

def save_graph(graph, output_path):
    """
    将处理后的图保存为 JSON 文件。
    """
    try:
        data = nx.node_link_data(graph, edges="links")
        with open(output_path, 'w') as f:
            json.dump(data, f, indent=4)
        logger.debug(f"图已成功保存到: {output_path}")
    except Exception as e:
        logger.debug(f"保存图时出错: {e}")

def main():
    """
    主程序入口，加载图数据，处理 definition 信息，并保存结果。
    """
    reponame = 'aixcoderhub'
    input_path = f'/home/sxj/Desktop/Workspace/Development/RepoRepresentation/RepoRep/src/output/{reponame}/definitiongraph.json'
    output_path = f'/home/sxj/Desktop/Workspace/Development/RepoRepresentation/RepoRep/src/output/{reponame}/processed_graph.json'

    graph = load_graph(input_path)
    if graph is None:
        return

    add_definition_id_to_nodes(graph)
    save_graph(graph, output_path)

if __name__ == "__main__":
    main()
