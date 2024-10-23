import os
import sys
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from parsers.repo_parser import RepoParser
import os
import json
import networkx as nx
# from processors.identifier_definition_processor import IdentifierDefinitionProcessor



def export_graph_to_json(graph, file_path):
    with open(file_path, 'w') as f:
        json.dump(nx.node_link_data(graph, edges="links"), f, indent=4)

def main():
    # 设置仓库路径
    repo_path = "/home/sxj/Desktop/Workspace/Development/RepoRepresentation/RepoRep/examples"
    repo_parser = RepoParser()

    # 解析仓库结构和生成项目图
    project_graph = repo_parser.parse_repo(repo_path)

    # 设置结果保存目录
    results_dir = os.path.join(os.path.dirname(__file__), 'output')
    os.makedirs(results_dir, exist_ok=True)

    # 保存初始解析结果
    save_initial_results(results_dir, project_graph)


    print("解析完成。所有结果已导出到output文件夹。")

def save_initial_results(results_dir, project_graph):
    with open(os.path.join(results_dir, 'initial_parse_output.json'), 'w') as f:
        json.dump(nx.node_link_data(project_graph, edges="links"), f, indent=4)


if __name__ == "__main__":
    main()
