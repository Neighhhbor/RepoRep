import os
import networkx as nx
from .ast_generator import ASTGenerator
from .config import SUPPORTED_EXTENSIONS
import logging

logger = logging.getLogger(__name__)


def export_graph_to_json(graph, file_path):
    with open(file_path, 'w') as f:
        json.dump(nx.node_link_data(graph, edges="links"), f, indent=4)


class RepoParser:
    def __init__(self):
        self.ast_generator = ASTGenerator()
        self.node_id_counter = 0

    def parse_directory_structure(self, repo_path):
        tree = {"name": os.path.basename(
            repo_path), "type": "directory", "children": []}

        def build_tree(path, node):
            for item in os.listdir(path):
                item_path = os.path.join(path, item)
                if os.path.isdir(item_path):
                    child = {"name": item, "type": "directory", "children": []}
                    node["children"].append(child)
                    build_tree(item_path, child)
                else:
                    ext = os.path.splitext(item)[1][1:]
                    if ext in SUPPORTED_EXTENSIONS:
                        node["children"].append(
                            {"name": item, "type": "file", "lang": ext, "path": item_path})

        build_tree(repo_path, tree)
        return tree

    def parse_repo(self, repo_path):
        logger.info(f"开始解析仓库: {repo_path}")
        project_graph = nx.MultiDiGraph()

        def process_directory(path, parent_id=None):
            nonlocal project_graph
            dir_node_id = self.generate_node_id()
            dir_name = os.path.basename(path)
            project_graph.add_node(dir_node_id, name=dir_name, type='directory', path=path)
            
            if parent_id is not None:
                project_graph.add_edge(parent_id, dir_node_id, relationship='CONTAINS')

            for item in os.listdir(path):
                item_path = os.path.join(path, item)
                if os.path.isdir(item_path):
                    process_directory(item_path, dir_node_id)
                else:
                    ext = os.path.splitext(item)[1][1:]
                    if ext in SUPPORTED_EXTENSIONS:
                        file_node_id = self.generate_node_id()
                        project_graph.add_node(file_node_id, name=item, type='file', path=item_path, lang=ext)
                        project_graph.add_edge(dir_node_id, file_node_id, relationship='CONTAINS')
                        
                        try:
                            file_ast = self.ast_generator.generate_ast(item_path)
                            root_ast_node = file_ast.graph['root']
                            project_graph.add_edge(file_node_id, root_ast_node, relationship='AST_ROOT')
                            project_graph = nx.compose(project_graph, file_ast)
                            project_graph.nodes[file_node_id]['ast_root'] = root_ast_node
                        except Exception as e:
                            logger.error(f"解析文件 {item_path} 时出错: {e}")

        process_directory(repo_path)
        return project_graph

    def generate_node_id(self):
        self.node_id_counter += 1
        return f"node_{self.node_id_counter}"


if __name__ == "__main__":
    repo_parser = RepoParser()
    dir_structure, project_graph = repo_parser.parse_repo(
        "/home/sxj/Desktop/Workspace/Development/RepoRepresentation/RepoRep/examples")

    # 打印目录结构
    print("目录结构:")
    print(json.dumps(dir_structure, ensure_ascii=False, indent=2))

    # 打印项目图信息
    print("\n项目图信息:")
    graph_info = {
        "节点数量": project_graph.number_of_nodes(),
        "边数量": project_graph.number_of_edges(),
        "节点类型统计": {},
        "边关系统计": {}
    }

    for node, data in project_graph.nodes(data=True):
        node_type = data.get('type', 'unknown')
        if node_type in graph_info["节点类型统计"]:
            graph_info["节点类型统计"][node_type] += 1
        else:
            graph_info["节点类型统计"][node_type] = 1

    for _, _, data in project_graph.edges(data=True):
        relationship = data.get('relationship', 'unknown')
        if relationship in graph_info["边关系统计"]:
            graph_info["边关系统计"][relationship] += 1
        else:
            graph_info["边关系统计"][relationship] = 1

    print(json.dumps(graph_info, ensure_ascii=False, indent=2))

