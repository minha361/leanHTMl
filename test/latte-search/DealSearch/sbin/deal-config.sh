if [ -z "$db_host" ]; then 
	echo "db_host bat buoc phai ton tai"
	exit 1
fi
if [ -z "$db_port" ]; then 
	echo "db_port bat buoc phai ton tai"
	exit 1
fi
if [ -z "$deal_user" ]; then 
	echo "deal_user bat buoc phai ton tai"
	exit 1
fi
if [ -z "$deal_pass" ]; then 
	echo "deal_pass bat buoc phai ton tai"
	exit 1
fi

sed -i -- "s/@ip/$db_host/g" deal/conf/db-data-config.xml
sed -i -- "s/@port/$db_port/g" deal/conf/db-data-config.xml
sed -i -- "s/@username_deal/$deal_user/g" deal/conf/db-data-config.xml
sed -i -- "s/@pass_deal/$deal_pass/g" deal/conf/db-data-config.xml

echo "echo thanh cong phat cho hung khoi"