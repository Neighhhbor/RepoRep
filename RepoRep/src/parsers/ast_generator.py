from tree_sitter import Language, Parser
import tree_sitter_go as tsg
import tree_sitter_java as tsjava
import tree_sitter_python as tspython
import networkx as nx
import os
import json
from .config import LANGUAGE_MAP
import logging

logger = logging.getLogger(__name__)
logger.setLevel(logging.INFO)
# 配置logger输出格式
formatter = logging.Formatter('%(name)s: %(message)s')
handler = logging.StreamHandler()
handler.setFormatter(formatter)
logger.handlers = [handler]


class ASTGenerator:
    def __init__(self):
        self.languages = {
            'go': Language(tsg.language()),
            'java': Language(tsjava.language()),
            'python': Language(tspython.language())
        }
        self.parsers = {lang: Parser() for lang in self.languages}
        for lang, parser in self.parsers.items():
            parser.language = self.languages[lang]

        self.node_id_counter = 0
        self.current_file_path = None

    def generate_node_id(self):
        abs_path = os.path.abspath(self.current_file_path)
        node_id = f"{abs_path}_{self.node_id_counter}"
        self.node_id_counter += 1
        return node_id

    def parse_file(self, file_path):
        ext = os.path.splitext(file_path)[1][1:]
        lang = LANGUAGE_MAP.get(ext)
        if lang is None:
            raise ValueError(f"Unsupported file extension: {ext}")

        with open(file_path, 'rb') as file:
            content = file.read()
        return self.parsers[lang].parse(content)

    def generate_ast(self, file_path):
        self.current_file_path = file_path
        tree = self.parse_file(file_path)
        return self.cursor_traverse(tree)

    def cursor_traverse(self, tree):
        G = nx.MultiDiGraph()
        cursor = tree.walk()
        root_id = None

        def traverse(cursor, parent_id=None):
            nonlocal root_id
            node = cursor.node
            node_id = self.node_id_counter
            self.node_id_counter += 1

            node_data = {
                'id': node_id,
                'file_path': self.current_file_path,
                'type': node.type,
                'start_byte': node.start_byte,
                'end_byte': node.end_byte,
                'start_point': node.start_point,
                'end_point': node.end_point,
                'is_named': node.is_named,
                'parent': parent_id,
                'children': []
            }

            if parent_id is None:
                root_id = node_id
            else:
                G.nodes[parent_id]['children'].append(node_id)
                G.add_edge(parent_id, node_id, relationship='CONTAINS')

            G.add_node(node_id, **node_data)

            if cursor.goto_first_child():
                while True:
                    child_id = traverse(cursor, node_id)
                    if not cursor.goto_next_sibling():
                        break
                cursor.goto_parent()

            return node_id

        traverse(cursor)
        G.graph['root'] = root_id
        return G

    def export_graph_to_json(self, G, output_file):
        data = {
            'nodes': [G.nodes[node] for node in G.nodes],
            'edges': [{'source': source, 'target': target, 'relationship': data['relationship']}
                      for source, target, data in G.edges(data=True)]
        }
        with open(output_file, 'w') as f:
            json.dump(data, f, indent=4)


if __name__ == "__main__":
    ast_generator = ASTGenerator()
    G = ast_generator.generate_ast(
        "/home/sxj/Desktop/Workspace/Development/RepoRepresentation/RepoRep/examples/example.go")
    ast_summary = {
        "node_count": G.number_of_nodes(),
        "edge_count": G.number_of_edges(),
        "node_type_statistics": {},
        "graph_structure": {}
    }

    # Count node types
    for node, data in G.nodes(data=True):
        node_type = data['type']
        if node_type in ast_summary["node_type_statistics"]:
            ast_summary["node_type_statistics"][node_type] += 1
        else:
            ast_summary["node_type_statistics"][node_type] = 1

    # Build graph structure
    for node in G.nodes():
        ast_summary["graph_structure"][node] = {
            "attributes": G.nodes[node]
        }

    # 将结果保存到本地日志文件
    log_file_path = 'ast_summary.log'
    with open(log_file_path, 'w', encoding='utf-8') as log_file:
        json.dump(ast_summary, log_file, ensure_ascii=False, indent=2)
    logging.info(f"AST摘要已保存到文件: {log_file_path}")

    # 导出解析得到的图
    output_file = 'ast_graph.json'
    ast_generator.export_graph_to_json(G, output_file)
    logging.info(f"AST图已导出到文件: {output_file}")
