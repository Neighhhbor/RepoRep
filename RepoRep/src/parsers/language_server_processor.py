from .language_server_client import LanguageServerClient
import logging

logger = logging.getLogger(__name__)

class LanguageServerProcessor:
    def __init__(self):
        self.language_server = LanguageServerClient()
    
    def process_file(self, file_path, file_node_id, project_graph):
        try:
            identifiers = self.language_server.get_identifiers(file_path)
            for identifier in identifiers:
                self.process_identifier(file_path, file_node_id, identifier, project_graph)
        except Exception as e:
            logger.error(f"处理文件 {file_path} 时出错: {e}")
    
    def process_identifier(self, file_path, file_node_id, identifier, project_graph):
        identifier_node_id = f"identifier_{file_node_id}_{identifier.name}"
        project_graph.add_node(identifier_node_id, type='identifier', name=identifier.name, location=identifier.location)
        project_graph.add_edge(file_node_id, identifier_node_id, relationship='CONTAINS')
        
        try:
            definition = self.language_server.get_definition(file_path, identifier.location)
            if definition:
                definition_node_id = f"definition_{file_node_id}_{definition.name}"
                project_graph.add_node(definition_node_id, type='definition', name=definition.name, location=definition.location)
                project_graph.add_edge(identifier_node_id, definition_node_id, relationship='DEFINED_BY')
        except Exception as e:
            logger.error(f"处理标识符 {identifier.name} 的定义时出错: {e}")
