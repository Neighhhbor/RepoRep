from tree_sitter import Language, Parser
import tree_sitter_go as tsg
import tree_sitter_java as tsjava
import tree_sitter_python as tspython
import networkx as nx
import os
import json
from config import LANGUAGE_MAP

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
        self.node_id_counter = 0
        tree = self.parse_file(file_path)
        return self.cursor_traverse(tree)

    def cursor_traverse(self, tree):
        G = nx.MultiDiGraph()
        cursor = tree.walk()

        def traverse(cursor, parent_id=None):
            node = cursor.node
            node_id = self.generate_node_id()

            G.add_node(node_id, **{
                'type': node.type,
                'text': node.text.decode('utf-8') if node.text else '',
                'start_byte': node.start_byte,
                'end_byte': node.end_byte,
                'start_point': node.start_point,
                'end_point': node.end_point,
                'is_named': node.is_named,
                'child_count': len(node.children),
                'file_path': os.path.abspath(self.current_file_path)
            })

            if parent_id is not None:
                G.add_edge(parent_id, node_id, relationship='CONTAINS')

            if cursor.goto_first_child():
                while True:
                    traverse(cursor, node_id)
                    if not cursor.goto_next_sibling():
                        break
                cursor.goto_parent()

        traverse(cursor)
        return G

    def export_graph_to_json(self, G, output_file):
        data = {
            'nodes': [{'id': node, **G.nodes[node]} for node in G.nodes],
            'edges': [{'source': source, 'target': target, 'relationship': data['relationship']} 
                      for source, target, data in G.edges(data=True)]
        }
        with open(output_file, 'w') as f:
            json.dump(data, f, indent=4)
