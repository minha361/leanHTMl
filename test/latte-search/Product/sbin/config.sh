if [ -z "$hazelcast_group_name" ]; then 
	echo "hazelcast_group_name bat buoc phai ton tai"
	exit 1
fi
if [ -z "$hazelcast_group_pass" ]; then 
	echo "hazelcast_group_pass bat buoc phai ton tai"
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

#hazelcast-client.xml 
sed -i -- "s/@name_hazelcast/$hazelcast_group_name/g" product/conf/hazelcast-client.xml
sed -i -- "s/@pass_hazelcast/$hazelcast_group_pass/g" product/conf/hazelcast-client.xml
# do something rat kinh khung
arr=$(echo $hazelcast_join_host | tr "," "\n")
hosts=""
for x in $arr
do
    hosts="$hosts<address>$x<\/address>"
done
echo "hosts: $hosts"
sed -i -- "s/@iphazelcast/$hosts/g" product/conf/hazelcast-client.xml

sed -i -- "s/@ip/$db_host/g" product/conf/db-data-config.xml
sed -i -- "s/@port/$db_port/g" product/conf/db-data-config.xml
sed -i -- "s/@cm_db_name/$db_name/g" product/conf/db-data-config.xml
sed -i -- "s/@username/$db_user/g" product/conf/db-data-config.xml
sed -i -- "s/@pass/$db_pass/g" product/conf/db-data-config.xml

echo "Successfully!"