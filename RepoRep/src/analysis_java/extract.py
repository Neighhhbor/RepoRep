import networkx as nx
import json
import logging

# 配置日志
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
)
logger = logging.getLogger(__name__)

# 保留的节点类型和关键字段
RETAINED_TYPES = {"file", "program", "identifier", "directory"}
AST_NODE_FIELDS = {"file_id", "start_byte", "end_byte", "id"}

def extract_subgraph(graph):
    """
    从原始图中提取符合要求的子图，并进行剪枝。
    """
    # 初始化子图
    subgraph = nx.DiGraph()

    # 遍历原始图中的所有节点
    for node_id, node_data in graph.nodes(data=True):
        node_type = node_data.get("type")

        # 保留特定类型的节点或简化 AST 节点
        if node_type in RETAINED_TYPES:
            if node_id.startswith("a_"):
                add_ast_node_to_subgraph(subgraph, node_id, node_data)
            else:
                subgraph.add_node(node_id, **node_data)
        elif node_type in {"class_declaration", "method_declaration"}:
            replace_with_identifier(graph, subgraph, node_id)
        # else:
        #     # 如果是 AST 节点，提取关键字段
        #     add_ast_node_to_subgraph(subgraph, node_id, node_data)

    # 更新边关系，确保只有子图中的节点之间的关系
    add_valid_edges(graph, subgraph)

    return subgraph

def replace_with_identifier(graph, subgraph, declaration_id):
    """
    用 identifier 替代 class 或 method declaration 节点。
    """
    identifier_id = None
    # 查找 declaration 的 identifier 子节点
    for child_id in graph.nodes[declaration_id].get("children", []):
        child_node = graph.nodes.get(child_id)
        if child_node and child_node["field_name"] == "name" and child_node["type"] == "identifier":
            identifier_id = child_id
            break  # 找到第一个 name 节点后停止

    if identifier_id:
        # 添加 identifier 节点到子图中
        identifier_data = graph.nodes[identifier_id]
        add_ast_node_to_subgraph(subgraph, identifier_id, identifier_data)
        logger.debug(f"保留 identifier 节点: {identifier_id} 替代 {declaration_id}")

        # 传递祖先节点的 CONTAINS 关系
        parent_id = find_retained_ancestor(graph, declaration_id)
        if parent_id:
            subgraph.add_edge(parent_id, identifier_id, relationship="CONTAINS", edge_type="ast_to_ast")
            logger.debug(f"传递 CONTAINS 关系: {parent_id} -> {identifier_id}")

def add_ast_node_to_subgraph(subgraph, node_id, node_data):
    """
    将简化后的 AST 节点添加到子图中，只保留必要的字段。
    """
    simplified_data = {key: node_data[key] for key in AST_NODE_FIELDS if key in node_data}
    subgraph.add_node(node_id, **simplified_data)
    logger.debug(f"添加简化后的 AST 节点: {node_id}")

def find_retained_ancestor(graph, node_id):
    """
    递归向上查找，直到找到需要保留的祖先节点。
    """
    parent_id = graph.nodes[node_id].get("parent")

    while parent_id:
        parent_node = graph.nodes.get(parent_id)
        if parent_node and parent_node["type"] in RETAINED_TYPES:
            return parent_id  # 找到符合条件的祖先节点
        parent_id = parent_node.get("parent") if parent_node else None

    return None  # 如果没有找到符合条件的祖先节点

def add_valid_edges(graph, subgraph):
    """
    遍历原始图中的边，确保只有子图中的节点之间的边被添加。
    """
    for u, v, data in graph.edges(data=True):
        if subgraph.has_node(u) and subgraph.has_node(v):
            subgraph.add_edge(u, v, **data)
            logger.debug(f"添加边: {u} -> {v}")

def load_graph(input_path):
    """
    从 JSON 文件加载图数据。
    """
    try:
        with open(input_path, 'r') as f:
            data = json.load(f)
        graph = nx.node_link_graph(data, edges="links")
        logger.info(f"成功加载图: {input_path}")
        return graph
    except FileNotFoundError:
        logger.error(f"未找到图文件: {input_path}")
        return None

def save_graph(graph, output_path):
    """
    将处理后的图保存为 JSON 文件。
    """
    try:
        data = nx.node_link_data(graph, edges="links")
        with open(output_path, 'w') as f:
            json.dump(data, f, indent=4)
        logger.info(f"图已成功保存到: {output_path}")
    except Exception as e:
        logger.error(f"保存图时出错: {e}")

def main():
    """
    主程序入口，加载图数据，提取子图，并保存结果。
    """
    reponame = 'aixcoderhub'
    input_path = f'/home/sxj/Desktop/Workspace/Development/RepoRepresentation/RepoRep/src/output/{reponame}/relationgraph.json'
    output_path = f'/home/sxj/Desktop/Workspace/Development/RepoRepresentation/RepoRep/src/output/{reponame}/{reponame}.json'

    # 加载图数据
    graph = load_graph(input_path)
    if graph is None:
        return

    # 提取子图
    subgraph = extract_subgraph(graph)

    # 保存子图
    save_graph(subgraph, output_path)

if __name__ == "__main__":
    main()
