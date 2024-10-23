import tree_sitter_python as tspython
from tree_sitter import Language, Parser
import os

# Adding support for Java and Go
import tree_sitter_java as tsjava
import tree_sitter_go as tsgo

class Node:
    def __init__(self, name, node_type, code=None, signature=None, parent_fullname=None):
        self.name = name
        self.node_type = node_type  # 'directory', 'module', 'class', 'function'
        self.children = []
        self.code = code
        self.signature = signature

        # Generate fullname: from root node to current node's path
        if parent_fullname:
            self.fullname = f"{parent_fullname}.{name}"
        else:
            self.fullname = name

    def add_child(self, child_node):
        self.children.append(child_node)

class ContainsParser:
    def __init__(self, project_path, repo_name):
        self.project_path = project_path
        self.repo_name = repo_name
        self.parsers = self._init_parsers()
        self.root = Node(repo_name, 'directory')  # Root node of the project
        self.nodes = {repo_name: self.root}  # Store all created nodes
        self.defined_symbols = {}  # Store class and function definitions, key is name, value is a list of definition paths

    def _init_parsers(self):
        parsers = {}
        parsers['python'] = Parser()
        parsers['java'] = Parser()
        parsers['go'] = Parser()

        parsers['python'].set_language(Language(tspython.language()))
        parsers['java'].set_language(Language(tsjava.language()))
        parsers['go'].set_language(Language(tsgo.language()))

        return parsers

    def parse(self):
        self._build_tree(self.project_path, self.root)

    def _build_tree(self, current_path, parent_node):
        for item in os.listdir(current_path):
            item_path = os.path.join(current_path, item)
            if os.path.isdir(item_path):
                # Create directory node
                dir_node = self._create_node(item, 'directory', parent_node)
                # Recursively traverse subdirectories
                self._build_tree(item_path, dir_node)
            elif any(item.endswith(ext) for ext in self.parsers.keys()):
                # Create module node and parse
                self._parse_file(item_path, parent_node)

    def _get_file_extension(self, filename):
        # Return file extension based on the filename
        if filename.endswith('.py'):
            return 'python'
        elif filename.endswith('.java'):
            return 'java'
        elif filename.endswith('.go'):
            return 'go'
        else:
            return None

    def _create_node(self, name, node_type, parent_node, code=None):
        # Remove file extension for module nodes
        language = self._get_file_extension(name)
        if node_type == 'module' and language:
            name = name[:-(len(f".{language}"))]  # Remove extension

        # Generate fullname
        if parent_node.fullname:
            full_name = f"{parent_node.fullname}.{name}"
        else:
            full_name = name

        # Create node
        node = Node(name, node_type, code=code, parent_fullname=parent_node.fullname)
        parent_node.add_child(node)
        self.nodes[full_name] = node

        return node

    def _parse_file(self, file_path, parent_node):
        language = self._get_file_extension(file_path)
        if not language:
            return

        with open(file_path, "r") as file:
            file_content = file.read()

        # Create module node with the file content as code
        module_node = self._create_node(os.path.basename(file_path), 'module', parent_node, code=file_content)

        parser = self.parsers[language]
        tree = parser.parse(bytes(file_content, "utf8"))

        # Recursively build the tree structure inside the file
        self._extract_items(tree.root_node, file_path, module_node)

    def _extract_items(self, node, file_path, parent_node):
        for child in node.children:
            if child.type in ['class_declaration', 'class_definition']:  # Handle classes in different languages
                class_name = self._get_node_text(child.child_by_field_name('name'), file_path)
                class_signature = class_name
                class_node = Node(class_name, 'class', self._get_code_segment(child, file_path), class_signature, parent_node.fullname)
                parent_node.add_child(class_node)
                self.nodes[class_node.fullname] = class_node

                # Register class to defined_symbols
                self._register_symbol(class_name, class_node.fullname)

                # Recursively process child nodes
                self._extract_items(child, file_path, class_node)

            elif child.type in ['method_declaration', 'function_definition']:  # Handle functions or methods
                func_name = self._get_node_text(child.child_by_field_name('name'), file_path)
                func_signature = self._get_signature(child, file_path)
                func_node = Node(func_name, 'function', self._get_code_segment(child, file_path), func_signature, parent_node.fullname)
                parent_node.add_child(func_node)

                # Register function to defined_symbols
                self.nodes[func_node.fullname] = func_node
                self._register_symbol(func_name, func_node.fullname)

                # Recursively process child nodes
                self._extract_items(child, file_path, func_node)

            else:
                # Recursively process other child nodes
                self._extract_items(child, file_path, parent_node)

    def _register_symbol(self, name, fullname):
        """
        Register the definition of a function or class in the defined_symbols dictionary.
        """
        if name in self.defined_symbols:
            self.defined_symbols[name].append(fullname)
        else:
            self.defined_symbols[name] = [fullname]

    def _get_node_text(self, node, file_path):
        if node is None:
            return ""
        start_byte = node.start_byte
        end_byte = node.end_byte

        with open(file_path, "r", encoding="utf-8") as file:
            file_content = file.read()

        return file_content[start_byte:end_byte]

    def _get_signature(self, node, file_path):
        """
        Extract the signature of the function or method.
        """
        signature = ""

        for child in node.children:
            if child.type == 'block':
                break
            signature += self._get_node_text(child, file_path) + " "

        return signature.strip()

    def _get_code_segment(self, node, file_path):
        return self._get_node_text(node, file_path)

# Example usage:
# parser = ContainsParser("/path/to/project", "my_repo")
# parser.parse()
