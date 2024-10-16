from parsers.repo_parser import RepoParser
import os
import json
import networkx as nx

def main():
    repo_path = '/home/sxj/Desktop/Workspace/Development/RepoRepresentation/RepoRep/examples'
    repo_parser = RepoParser()

    # 解析仓库
    dir_structure, combined_graph = repo_parser.parse_repo(repo_path)

    # 确保输出目录存在
    os.makedirs('./results', exist_ok=True)

    # 导出目录结构
    with open('./results/directory_structure.json', 'w') as f:
        json.dump(dir_structure, f, indent=4)

    # 导出合并后的图
    repo_parser.ast_generator.export_graph_to_json(combined_graph, './results/combined_ast_graph.json')

    print("解析完成。目录结构和AST图已导出到results文件夹。")

if __name__ == "__main__":
    main()
