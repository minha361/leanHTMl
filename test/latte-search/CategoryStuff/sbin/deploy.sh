SOLR_DIR=$1
echo "SOLR_DIR - $SOLR_DIR"
WORKING_DIR=${PWD}
ln -sfn $WORKING_DIR $SOLR_DIR/server/solr/category-info