REPO_PATH="/home/sxj/Desktop/Workspace/Development/RepoRepresentation/RepoRep/examples/Go/wire"
OUTPUT_DIR="./output"

python repo_parser.py $REPO_PATH --output_dir $OUTPUT_DIR
python golsp.py $REPO_PATH --output_dir $OUTPUT_DIR
python defid_parser.py $REPO_PATH --output_dir $OUTPUT_DIR
#     语言无关
# ------------------------------------------------
#     语言有关
python relation_parser.py $REPO_PATH --output_dir $OUTPUT_DIR