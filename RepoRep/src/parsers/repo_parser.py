import os
import networkx as nx
from .ast_generator import ASTGenerator
from .config import SUPPORTED_EXTENSIONS
import logging

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

class RepoParser:
    def __init__(self):
        self.ast_generator = ASTGenerator()
        self.node_id_counter = 0
        
    def parse_directory(self, repo_path):
        tree = {"name": os.path.basename(repo_path), "type": "directory", "children": []}
        
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
                        node["children"].append({"name": item, "type": "file", "lang": ext})
        
        build_tree(repo_path, tree)
        return tree

    def parse_repo(self, repo_path):
        logger.info(f"Parsing repository: {repo_path}")
        dir_structure = self.parse_directory(repo_path)
        project_graph = nx.MultiDiGraph()

        def process_files(node, current_path):
            nonlocal project_graph
            node_id = self.generate_node_id()
            project_graph.add_node(node_id, **node)

            if node['type'] == 'file':
                file_path = os.path.join(current_path, node['name'])
                logger.info(f"Processing file: {file_path}")
                if os.path.exists(file_path):
                    try:
                        file_ast = self.ast_generator.generate_ast(file_path)
                        # 将文件AST的根节点与文件节点连接
                        root_ast_node = next(iter(file_ast.nodes()))
                        project_graph.add_edge(node_id, root_ast_node, relationship='AST_ROOT')
                        # 将文件AST合并到项目图中
                        project_graph = nx.compose(project_graph, file_ast)
                    except Exception as e:
                        logger.error(f"Error parsing {file_path}: {e}")
                else:
                    logger.warning(f"File not found: {file_path}")
            elif node['type'] == 'directory':
                new_path = os.path.join(current_path, node['name'])
                for child in node.get('children', []):
                    process_files(child, new_path)

        process_files(dir_structure, os.path.dirname(repo_path))
        return dir_structure, project_graph

    def generate_node_id(self):
        self.node_id_counter += 1
        return f"node_{self.node_id_counter}"

if __name__ == "__main__":
    repo_parser = RepoParser()
    dir_structure, project_graph = repo_parser.parse_repo("/home/sxj/Desktop/Workspace/Development/RepoRepresentation/RepoRep/examples")
    
    # 保存结果到results目录
    import json
    import os

    results_dir = "results"
    os.makedirs(results_dir, exist_ok=True)

    with open(os.path.join(results_dir, "dir_structure.json"), "w") as f:
        json.dump(dir_structure, f, indent=2)
    
    with open(os.path.join(results_dir, "project_graph.json"), "w") as f:
        json.dump(project_graph, f, indent=2)
    
    print("解析结果已保存到results目录中。")
    