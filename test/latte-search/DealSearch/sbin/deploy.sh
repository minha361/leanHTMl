echo "Path to solr $1"
if [ ! -d "$1" ]; then
	echo -e "\nsolr directory is not exist"
	exit 1
fi

SOLR_DIR=$1
SOLR_HOME=$SOLR_DIR/server/solr

echo "SOLR_DIR - $SOLR_DIR"
echo "SOLR_HOME - $SOLR_HOME"

if [ ! -d "$SOLR_HOME" ]; then
	echo -e "\nSOLR_DIR/server/solr is not exits"
	exit 3
fi

if [ ! -d "deal" ]; then
	echo -e "\ndeal core config is not exits, check source again from git"
	exit 4
fi

WORKING_DIR=${PWD}
cd $SOLR_HOME
ln -sfn $WORKING_DIR/deal deal

if [ -d "$2" ]; then
	$SOLR_DIR/server/scripts/cloud-scripts/zkcli.sh -zkhost  $2 -cmd clear /configs/deal
	$SOLR_DIR/server/scripts/cloud-scripts/zkcli.sh -cmd upconfig -zkhost  $2 --confname deal --solrhome  $SOLR_HOME --confdir $SOLR_HOME/deal/conf
fi
