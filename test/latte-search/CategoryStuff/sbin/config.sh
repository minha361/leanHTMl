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



sed -i -- "s/@ip/$db_host/g" conf/data-sources.xml
sed -i -- "s/@port/$db_port/g" conf/data-sources.xml
sed -i -- "s/@username/$db_user/g" conf/data-sources.xml
sed -i -- "s/@pass/$db_pass/g" conf/data-sources.xml

echo "Successfully!"