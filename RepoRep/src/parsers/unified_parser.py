from tree_sitter import Language, Parser
import tree_sitter_go as tsg
import tree_sitter_java as tsjava
import tree_sitter_python as tspython
import networkx as nx
import json
import os

class UnifiedParser:
    def __init__(self):
        # 加载语言
        self.languages = {
            'go': Language(tsg.language()),
            'java': Language(tsjava.language()),
            'python': Language(tspython.language())
        }
        self.parsers = {lang: Parser() for lang in self.languages}
        for lang, parser in self.parsers.items():
            # 修改此行以适应新的 tree_sitter API
            parser.language = self.languages[lang]  # 使用属性而不是方法
        
        self.node_id_counter = 0  # 初始化计数器
        self.node_id_map = {}  # 用于存储节点 ID 的映射

    def generate_node_id(self):
        node_id = self.node_id_counter
        self.node_id_counter += 1
        return node_id

    def parse_file(self, file_path):
        ext = os.path.splitext(file_path)[1][1:]  # 获取文件后缀名
        ext_to_lang = {'go': 'go', 'java': 'java', 'py': 'python'}  # 添加扩展名到语言的映射
        lang = ext_to_lang.get(ext)  # 获取对应的语言
        if lang is None:
            raise ValueError(f"Unsupported file extension: {ext}")
        
        with open(file_path, 'rb') as file:
            content = file.read()
        return self.parsers[lang].parse(content)  # 使用语言名称而不是扩展名

    def cursor_traverse(self, tree):
        G = nx.MultiDiGraph()  # 创建有向多重图
        cursor = tree.walk()

        def traverse(cursor):
            node = cursor.node
            node_id = self.generate_node_id()  # 生成唯一 ID
            self.node_id_map[node] = node_id  # 记录节点 ID

            # 添加节点及其属性
            G.add_node(node_id, **{
                'type': node.type,
                'text': node.text.decode('utf-8') if node.text else '',
                'start_byte': node.start_byte,
                'end_byte': node.end_byte,
                'start_point': node.start_point,
                'end_point': node.end_point,
                'is_named': node.is_named,
                'child_count': len(node.children)
            })

            if cursor.goto_first_child():
                while True:
                    traverse(cursor)
                    if not cursor.goto_next_sibling():
                        break
                cursor.goto_parent()  # 确保返回到父节点

        traverse(cursor)
        return G

    def export_graph_to_json(self, G, output_file):
        data = {
            'nodes': [{'id': node, **G.nodes[node]} for node in G.nodes],
            'edges': [{'source': source, 'target': target} for source, target in G.edges]
        }
        with open(output_file, 'w') as f:
            json.dump(data, f, indent=4)

if __name__ == '__main__':
    file_path = '../../examples/example_go.go'  # 示例文件路径
    parser = UnifiedParser()
    tree = parser.parse_file(file_path)

    # 使用游标遍历并创建图
    G = parser.cursor_traverse(tree)

    # 导出图为 JSON
    output_file = './results/unified_tree_graph.json'
    parser.export_graph_to_json(G, output_file)
    print(f"语法树图已导出为 {output_file}")
