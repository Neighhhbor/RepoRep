import streamlit as st
import igraph as ig
import leidenalg as la
import matplotlib.pyplot as plt
from tqdm import tqdm
import json
import os
from pyvis.network import Network
import streamlit.components.v1 as components
import metis  # 导入 METIS 库
from functools import lru_cache  # 缓存文件读取

# 设置页面为宽屏布局
st.set_page_config(layout="wide")

RESULTDIR = "/home/sxj/Desktop/Workspace/Development/RepoRepresentation/RepoRep/src/output/aixcoderhub"

@lru_cache(maxsize=32)
def read_file_slice(filepath, start_byte, end_byte):
    """读取文件的指定切片并缓存"""
    try:
        with open(filepath, 'r', encoding='utf-8') as f:
            f.seek(start_byte)
            return f.read(end_byte - start_byte)
    except FileNotFoundError:
        st.error(f"File not found: {filepath}")
        return ""

def load_graph_from_json(filename):
    """从 JSON 文件加载图"""
    with open(filename, 'r') as infile:
        graph_data = json.load(infile)

    vertices = [v['id'] if 'id' in v else f"node_{i}" for i, v in enumerate(graph_data['nodes'])]
    edges = [(link['source'], link['target']) for link in graph_data['links']]

    ig_G = ig.Graph(directed=True)
    ig_G.add_vertices(vertices)
    ig_G.add_edges(edges)
    
    for i, v in enumerate(graph_data['nodes']):
        for key, value in v.items():
            if key != 'id':
                ig_G.vs[i][key] = value

    return ig_G

def apply_community_detection(algorithm_name, ig_G):
    """应用选择的社区检测算法"""
    if algorithm_name == "Leiden":
        partition = la.find_partition(ig_G, la.ModularityVertexPartition)
    elif algorithm_name == "Louvain":
        partition = la.find_partition(ig_G, la.RBConfigurationVertexPartition)
    elif algorithm_name == "Label Propagation":
        partition = ig_G.community_label_propagation()
    elif algorithm_name == "Walktrap":
        partition = ig_G.community_walktrap().as_clustering()
    elif algorithm_name == "Infomap":
        partition = ig_G.community_infomap()
    elif algorithm_name == "Multilevel (METIS)":
        partition = apply_multilevel_partitioning(ig_G)
    else:
        st.error(f"Unknown algorithm: {algorithm_name}")
        return None

    return partition

def apply_multilevel_partitioning(ig_G):
    """使用 METIS 进行多级划分"""
    import networkx as nx
    G_nx = nx.Graph()

    for vertex in ig_G.vs:
        G_nx.add_node(vertex.index, **vertex.attributes())

    for edge in ig_G.es:
        source, target = edge.source, edge.target
        G_nx.add_edge(source, target, weight=edge["weight"] if "weight" in edge.attributes() else 1)

    edgecuts, parts = metis.part_graph(G_nx, nparts=4)
    membership = parts

    class PseudoPartition:
        def __init__(self, membership):
            self.membership = membership
            self.num_communities = len(set(membership))
        
        @property
        def modularity(self):
            return ig_G.modularity(membership)

    return PseudoPartition(membership)

def plot_community(community_id, partition, ig_G):
    """绘制单个社区并显示"""
    community_nodes = [i for i, membership in enumerate(partition.membership) if membership == community_id]
    subgraph = ig_G.subgraph(community_nodes)
    layout = subgraph.layout("fr")
    fig, ax = plt.subplots(figsize=(12, 8))

    vertex_labels = subgraph.vs['id'] if 'id' in subgraph.vs.attributes() else [str(i) for i in range(subgraph.vcount())]
    ig.plot(subgraph, layout=layout, vertex_label=vertex_labels, vertex_size=20, target=ax)

    plt.title(f"Community {community_id} Structure")
    st.pyplot(fig)

def display_community_info(community_id, partition, ig_G):
    """显示社区中的所有节点及其属性"""
    community_nodes = [i for i, membership in enumerate(partition.membership) if membership == community_id]
    st.write(f"### Community {community_id} Nodes and Attributes")

    for node_id in community_nodes:
        node = ig_G.vs[node_id]
        node_name = node['name'] if 'name' in node.attributes() else f"node_{node_id}"
        st.write(f"- **Node Name**: {node_name}")

        # 读取并显示代码片段（如果存在）
        if node["type"] in {"program", "identifier"}:
            file_id = node["file_id"]
            file_node = ig_G.vs.find(id=file_id)  # 查找文件节点
            filepath = file_node["path"]
            content = read_file_slice(filepath, node["start_byte"], node["end_byte"])

            with st.expander(f"Code for {node_name}", expanded=False):
                st.code(content, language="python")

def plot_interactive_communities(ig_G, partition):
    """使用 Pyvis 绘制交互式社区检测图"""
    net = Network(notebook=False, height="1000px", width="100%", bgcolor="#f0f0f0", font_color="black")
    community_colors = partition.membership
    num_communities = max(community_colors) + 1
    palette = plt.get_cmap("tab10", num_communities)
    
    for vertex_id in range(len(ig_G.vs)):
        node_name = ig_G.vs[vertex_id]['name'] if 'name' in ig_G.vs.attributes() else f"node_{vertex_id}"
        community_id = partition.membership[vertex_id]
        color = palette(community_id)
        color_hex = f'#{int(color[0]*255):02x}{int(color[1]*255):02x}{int(color[2]*255):02x}'
        net.add_node(vertex_id, label=node_name, title=node_name, color=color_hex, size=30)
    
    for edge in ig_G.es:
        source, target = edge.source, edge.target
        relationship = edge['relationship'] if 'relationship' in edge.attributes() else "None"
        net.add_edge(source, target, title=relationship, width=2)

    net.show("interactive_graph.html")
    components.html(open("interactive_graph.html").read(), height=1000)

def analyze_communities(graph_filename, algorithm_name):
    """执行社区分析并显示结果"""
    ig_G = load_graph_from_json(graph_filename)
    partition = apply_community_detection(algorithm_name, ig_G)

    if partition:
        st.session_state['ig_G'] = ig_G
        st.session_state['partition'] = partition

        community_count = len(set(partition.membership))
        st.write(f"**Community Count:** {community_count}")
        st.write(f"**Modularity:** {partition.modularity}")

        selected_community = st.selectbox("Select a community", range(community_count))
        if selected_community is not None:
            display_community_info(selected_community, partition, ig_G)
            plot_community(selected_community, partition, ig_G)

def get_json_files(directory):
    """获取目录中的所有 JSON 文件"""
    return [f for f in os.listdir(directory) if f.endswith('.json')]

if __name__ == "__main__":
    st.title("Community Detection Analysis")

    # 获取 JSON 文件列表
    json_files = get_json_files(RESULTDIR)
    selected_file = st.selectbox("Select a graph file", json_files)

    # 用户选择社区检测算法
    algorithm_name = st.selectbox(
        "Select Community Detection Algorithm",
        ["Leiden", "Louvain", "Label Propagation", "Walktrap", "Infomap", "Multilevel (METIS)"]
    )

    # 点击按钮进行分析
    if st.button("Analyze"):
        analyze_communities(os.path.join(RESULTDIR, selected_file), algorithm_name)
