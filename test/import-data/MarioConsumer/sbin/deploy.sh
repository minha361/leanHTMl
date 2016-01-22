if [ -z "$hazelcast_group_name" ]; then 
	echo "hazelcast_group_name bat buoc phai ton tai"
	exit 1
fi
if [ -z "$hazelcast_group_pass" ]; then 
	echo "hazelcast_group_pass bat buoc phai ton tai"
	exit 1
fi
if [ -z "$mancenter_host" ]; then 
	echo "mancenter_host bat buoc phai ton tai"
	exit 1
fi
if [ -z "$mancenter_port" ]; then 
	echo "mancenter_port bat buoc phai ton tai"
	exit 1
fi
if [ -z "$hazelcast_join_host" ]; then 
	echo "hazelcast_join_host bat buoc phai ton tai"
	exit 1
fi
if [ -z "$db_host" ]; then 
	echo "db_host bat buoc phai ton tai"
	exit 1
fi
if [ -z "$db_port" ]; then 
	echo "db_port bat buoc phai ton tai"
	exit 1
fi
if [ -z "$db_user" ]; then 
	echo "db_user bat buoc phai ton tai"
	exit 1
fi
if [ -z "$db_pass" ]; then 
	echo "db_pass bat buoc phai ton tai"
	exit 1
fi
if [ -z "$solr_host" ]; then 
	echo "solr_host bat buoc phai ton tai"
	exit 1
fi
if [ -z "$solr_port" ]; then 
	echo "solr_port bat buoc phai ton tai"
	exit 1
fi
if [ -z "$rabbitmq_host" ]; then 
	echo "rabbitmq_host bat buoc phai ton tai"
	exit 1
fi
if [ -z "$rabbitmq_port" ]; then 
	echo "rabbitmq_port bat buoc phai ton tai"
	exit 1
fi
if [ -z "$rabbitmq_user" ]; then 
	echo "rabbitmq_user bat buoc phai ton tai"
	exit 1
fi
if [ -z "$rabbitmq_pass" ]; then 
	echo "rabbitmq_pass bat buoc phai ton tai"
	exit 1
fi
if [ -z "$rabbitmq_queue_name" ]; then 
	echo "rabbitmq_queue_name bat buoc phai ton tai"
	exit 1
fi
if [ -z "$db_name" ]; then 
	echo "db_name bat buoc phai ton tai"
	exit 1
fi
if [ -z "$db_host_trending" ]; then 
	echo "db_host_trending bat buoc phai ton tai"
	exit 1
fi
if [ -z "$db_port_trending" ]; then 
	echo "db_port_trending bat buoc phai ton tai"
	exit 1
fi
if [ -z "$db_name_trending" ]; then 
	echo "db_name_trending bat buoc phai ton tai"
	exit 1
fi
if [ -z "$db_user_trending" ]; then 
	echo "db_user_trending bat buoc phai ton tai"
	exit 1
fi
if [ -z "$db_pass_trending" ]; then 
	echo "db_pass_trending bat buoc phai ton tai"
	exit 1
fi
if [ -z "$rabbitmq_exchange_name" ]; then 
	echo "you don't use exchange"
	rabbitmq_exchange_name=""
fi

# extension.xml
sed -i -- "s/@db_host_trending/$db_host_trending/g" extensions/import\ data/extension.xml
sed -i -- "s/@db_port_trending/$db_port_trending/g" extensions/import\ data/extension.xml
sed -i -- "s/@db_name_trending/$db_name_trending/g" extensions/import\ data/extension.xml
sed -i -- "s/@db_user_trending/$db_user_trending/g" extensions/import\ data/extension.xml
sed -i -- "s/@db_pass_trending/$db_pass_trending/g" extensions/import\ data/extension.xml

sed -i -- "s/@db_host/$db_host/g" extensions/import\ data/extension.xml
sed -i -- "s/@db_port/$db_port/g" extensions/import\ data/extension.xml
sed -i -- "s/@db_user/$db_user/g" extensions/import\ data/extension.xml
sed -i -- "s/@db_pass/$db_pass/g" extensions/import\ data/extension.xml
sed -i -- "s/@db_name/$db_name/g" extensions/import\ data/extension.xml
sed -i -- "s/@solr_host/$solr_host/g" extensions/import\ data/extension.xml
sed -i -- "s/@solr_port/$solr_port/g" extensions/import\ data/extension.xml
sed -i -- "s/@rabbitmq_host/$rabbitmq_host/g" extensions/import\ data/extension.xml
sed -i -- "s/@rabbitmq_port/$rabbitmq_port/g" extensions/import\ data/extension.xml
sed -i -- "s/@rabbitmq_user/$rabbitmq_user/g" extensions/import\ data/extension.xml
sed -i -- "s/@rabbitmq_pass/$rabbitmq_pass/g" extensions/import\ data/extension.xml
sed -i -- "s/@rabbitmq_queue_name/$rabbitmq_queue_name/g" extensions/import\ data/extension.xml
sed -i -- "s/@rabbitmq_exchange_name/$rabbitmq_exchange_name/g" extensions/import\ data/extension.xml

# hazelcast.xml
sed -i -- "s/@hazelcast_group_name/$hazelcast_group_name/g" conf/hazelcast.xml
sed -i -- "s/@hazelcast_group_pass/$hazelcast_group_pass/g" conf/hazelcast.xml
sed -i -- "s/@mancenter_host/$mancenter_host/g" conf/hazelcast.xml
sed -i -- "s/@mancenter_port/$mancenter_port/g" conf/hazelcast.xml
# do something rat kinh khung
arr=$(echo $hazelcast_join_host | tr "," "\n")
hosts=""
for x in $arr
do
    hosts="$hosts<interface>$x<\/interface>"
done
echo "hosts: $hosts"
sed -i -- "s/@hazelcast_join_host/$hosts/g" conf/hazelcast.xml
echo "echo thanh cong phat cho hung khoi"
 