import os
import networkx as nx
from .ast_generator import ASTGenerator
from config import SUPPORTED_EXTENSIONS
import logging

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

class RepoParser:
    def __init__(self):
        self.ast_generator = ASTGenerator()

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
        all_graphs = {}

        def process_files(node, current_path):
            if node['type'] == 'file':
                file_path = os.path.join(current_path, node['name'])
                logger.info(f"Processing file: {file_path}")
                try:
                    graph = self.ast_generator.generate_ast(file_path)
                    all_graphs[file_path] = graph
                except Exception as e:
                    print(f"Error parsing {file_path}: {e}")
            elif node['type'] == 'directory':
                for child in node.get('children', []):
                    process_files(child, os.path.join(current_path, node['name']))

        process_files(dir_structure, os.path.dirname(repo_path))
        combined_graph = nx.compose_all(all_graphs.values()) if all_graphs else nx.MultiDiGraph()
        
        return dir_structure, combined_graph
