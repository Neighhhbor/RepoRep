REPO_PATH="/home/sxj/Desktop/Workspace/Development/RepoRepresentation/RepoRep/examples/Java/aixcoderhub"
RESULTDIR="./output"

python repo_parser.py $REPO_PATH --output_dir $RESULTDIR
python jdtlsp.py $REPO_PATH --output_dir $RESULTDIR  
python defid_parser.py $REPO_PATH --output_dir $RESULTDIR
python relation_parser.py $REPO_PATH --output_dir $RESULTDIR