import os
import json
import sys

def reformat_json_file(file_path):
    try:
        with open(file_path, 'r', encoding='utf-8') as file:
            data = json.load(file)
        
        with open(file_path, 'w', encoding='utf-8') as file:
            json.dump(data, file, indent=4, ensure_ascii=False)
        
        print(f"成功重写文件: {file_path}")
    except json.JSONDecodeError:
        print(f"错误: {file_path} 不是有效的JSON文件")
    except Exception as e:
        print(f"处理文件 {file_path} 时发生错误: {str(e)}")

def process_directory(directory):
    for root, dirs, files in os.walk(directory):
        for file in files:
            if file.endswith('.json'):
                file_path = os.path.join(root, file)
                reformat_json_file(file_path)

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("使用方法: python reformat_json_files.py <目录路径>")
        sys.exit(1)
    
    directory = sys.argv[1]
    if not os.path.isdir(directory):
        print(f"错误: {directory} 不是一个有效的目录")
        sys.exit(1)
    
    print(f"开始处理目录: {directory}")
    process_directory(directory)
    print("处理完成")
