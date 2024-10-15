from tree_sitter import Language, Parser
import tree_sitter_java as tsjava
import networkx as nx
import json

# 加载 Java 语言
JAVA_LANGUAGE = Language(tsjava.language())

class JavaParser():
    def __init__(self):
        self.parser = Parser(JAVA_LANGUAGE)
        self.node_id_counter = 0  # 初始化计数器
        self.node_id_map = {}  # 用于存储节点 ID 的映射

    def generate_node_id(self):
        node_id = self.node_id_counter
        self.node_id_counter += 1
        return node_id

    def parse_file(self, file_path):
        with open(file_path, 'rb') as file:
            content = file.read()
        return self.parser.parse(content)

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
                'parent': self.node_id_map.get(node.parent) if node.parent else None,
                'child_count': len(node.children)
            })

            if cursor.goto_first_child():
                while True:
                    traverse(cursor)
                    if not cursor.goto_next_sibling():
                        break
                cursor.goto_parent()

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
    parser = JavaParser()
    file_path = '../../examples/example_java.java'  # 示例文件路径
    tree = parser.parse_file(file_path)

    # 使用游标遍历并创建图
    G = parser.cursor_traverse(tree)

    # 导出图为 JSON
    output_file = './results/java_tree_graph.json'
    parser.export_graph_to_json(G, output_file)
    print(f"语法树图已导出为 {output_file}")
