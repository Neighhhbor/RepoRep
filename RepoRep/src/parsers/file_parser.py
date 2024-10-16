from tree_sitter import Language, Parser
import tree_sitter_go as tsg
import tree_sitter_java as tsjava
import tree_sitter_python as tspython
import networkx as nx
import json
import os

class FileParser:
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
        self.node_id_map = {}
        self.current_file_path = None

    def generate_node_id(self):
        abs_path = os.path.abspath(self.current_file_path)
        node_id = f"{abs_path}_{self.node_id_counter}"
        self.node_id_counter += 1
        return node_id

    def parse_file(self, file_path):
        ext = os.path.splitext(file_path)[1][1:]
        ext_to_lang = {'go': 'go', 'java': 'java', 'py': 'python'}
        lang = ext_to_lang.get(ext)
        if lang is None:
            raise ValueError(f"Unsupported file extension: {ext}")
        
        with open(file_path, 'rb') as file:
            content = file.read()
        return self.parsers[lang].parse(content)

    def cursor_traverse(self, tree):
        G = nx.MultiDiGraph()
        cursor = tree.walk()

        def traverse(cursor, parent_id=None):
            node = cursor.node
            node_id = self.generate_node_id()
            self.node_id_map[node] = node_id

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

    def parse_and_traverse(self, file_path):
        self.current_file_path = os.path.abspath(file_path)
        self.node_id_counter = 0
        tree = self.parse_file(self.current_file_path)
        return self.cursor_traverse(tree)

    def export_graph_to_json(self, G, output_file):
        data = {
            'nodes': [{'id': node, **G.nodes[node]} for node in G.nodes],
            'edges': [{'source': source, 'target': target, 'relationship': data['relationship']} 
                      for source, target, data in G.edges(data=True)]
        }
        with open(output_file, 'w') as f:
            json.dump(data, f, indent=4)

if __name__ == '__main__':
    parser = FileParser()
    examples_dir = '../../examples'
    
    for filename in os.listdir(examples_dir):
        file_path = os.path.join(examples_dir, filename)
        if os.path.isfile(file_path):
            try:
                print(f"Parsing {filename}...")
                graph = parser.parse_and_traverse(file_path)
                output_file = f'./results/{os.path.splitext(filename)[0]}_ast_graph.json'
                parser.export_graph_to_json(graph, output_file)
                print(f"AST graph for {filename} exported to {output_file}")
            except Exception as e:
                print(f"Error parsing {filename}: {str(e)}")
    
    print("Parsing complete.")
