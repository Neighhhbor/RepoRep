import os
import json

class DirectoryParser:
    def __init__(self):
        self.supported_extensions = {'go', 'java', 'py'}

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
                    if ext in self.supported_extensions:
                        node["children"].append({"name": item, "type": "file", "lang": ext})
        
        build_tree(repo_path, tree)
        return tree

    def export_tree_to_json(self, tree, output_file):
        # 确保输出目录存在
        os.makedirs(os.path.dirname(output_file), exist_ok=True)
        
        with open(output_file, 'w') as f:
            json.dump(tree, f, indent=4)
