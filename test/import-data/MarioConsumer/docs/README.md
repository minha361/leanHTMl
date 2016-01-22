# Introduction
![Mario](mario.png)

*Mario-consumer* là chương trình dùng để đồng bộ dữ liệu với CM. 
CM sẽ đẩy dữ liệu và RabitMq mỗi khi dữ liệu và sản phẩm có sự thay đổi.
*Mario-consumer* sẽ lấy dữ liệu từ Queue để cập nhật vào solr
Mỗi dữ liệu CM đẩy vào Queue sẽ có một trường *type*, và *Mario-consumer* sẽ biết phải cập nhật vào solr như thế nào dựa vào trường *type* đó

# Installation
## Tiền điều kiện
- Một RabbitMq server được cài đặt. Các thông tin cần biết sau cài đặt là ip address, port, user, password and queue name.
- Một mancenter web application được cài đặt. Các thông tin cần biết sau cài đặt là ip address, port, user and password.
- Một solr server được cài đặt, các thông tin cần biết sau cài đặt: ip address, port và 2 core names: product + suggestion

## Cài đặt
- Bước 1: giải nén file "mario-consumer-{version}.zip" vào thư mục "/opt"
- Bước 2: cấu hình hazelcast trong file cấu hình "conf/hazelcast.xml"
	- name and password của hazelcast group, tại *group* element
	- ip address, port của mancenter, tại *management-center* element
	- port của hazelcast instance, tại *network -> port* element
	- ip address của hazelcast instance, tại *network -> join -> tcp-ip* element
- Bước 3: sửa "import data" extension config tại "extensions/import data/extension.xml":
	- Thông tin kết nối với db của CM tại *datasource -> sql -> variables*
		- ip address, port and database name của Category Management database
		- *connection timeout* tại *idleTimeout variable*, để mặc định là 30000 (30s) để đủ lớn để tránh đứt kết nối do các vấn đề hạ tầng
	- Thông tin kết nối đến RabbitMq tại *gateways -> rabbitmq -> variables*
	- Thông tin cần cho khởi động "mario-consumer" at *handles -> lifecycle -> entry* với "com.adr.bigdata.indexing.OnStartupHandler" handler:
		- "dataSourceName" variable: Đặt là datasource name tại "datasources" element
		- "deltaTimeUpdateCache" varibale: số miliseconds mà *mario-consumer* quét từ database thông tin của category, merchant, brand và cập nhật vào cache.
	- Thông tin dùng để lập lịch gọi đến một dataimport handler nào đó		
		- "solrPath" variable: full path to product core, ví dụ http://localhost:8983/solr/product.
		- "deltaTimeScanDB" varibale: số miliseconds mà *mario-consumer* quét toàn bộ thông tin thay đổi dựa vào @LastUpdatedTime và cập nhật lại vào solr.
	- Thông tin cấu hình cho sự cập nhật tại *handle -> request -> entry* với "com.adr.bigdata.indexing.OnDataUpdateHandler" handler:
		- *worker* element: số worker làm việc
		- *ringbuffersize* element:
		- "solrConfigName" tên của *entry* tại thẻ *solrs*. Đến đây nhứ cấu hình lại thẻ này cho đúng :3
		- "numDocPerRequest" variable: số tài liệu được batch upload vào solr.
		- "dataSourceName" variable: Đặt là datasource name tại "datasources" element.
		- "commit": Nếu commit=true, mỗi khi đẩy dữ liệu lên solr sẽ gọi một lệnh *commit* vào solr. Và ngược lại.
		- "waitFlush": Chỉ có ý nghĩa khi commit=true. Khi commit sẽ đợi solr flush cache xong.
		- "waithSearcher": Chỉ có ý nghĩa khi commit=true. Khi commit xong sẽ đợi solr tạo một searcher mới.
		- "softCommit": Chỉ có ý nghĩa khi commit=true. Chỉ thực hiện soft commit.
		- "timeZoneGap": Múi giờ của server Database. Dùng để chuyển đối tượng date và timestamp (lí do là DB không lưu timezone).
- Bước 4: Run start.sh file to start mario

# Troubleshoot
## Làm sao để biết hệ thống import đã chạy đúng hay chưa?
- Kiểm tra xem công cụ *RabbitMQ Management* đã chạy hay chưa. Khi CM chưa đẩy dữ liệu vào thì trang list queue sẽ trống.
- Sử dụng công cụ *Mancenter* xem đã có đẩy đủ các cacheMap sau hay chưa: brand, category, categoryParent, merchant, attributeCategoryFilter. Ngoài ra kiểm tra xem các node của Hazelcast đã đúng hay chưa và có ổn định hay chưa, tránh tình trạng nhiều cụm Hazelcast cùng đòi một *Mancenter*. 
- Sử dụng chương trình *MarioProducer* (file .jar) để đọc file .dat đi kèm (cùng thư mục với file .jar) để đẩy vào queue. Sau đó check trên solr xem những dữ liệu có trong file .dat đã có trên solr chưa. *Main class* của file jar này là: *com.mario.producer.Test*. Để có được file jar thì dùng *ant* build từ project *MarioProducer*

## Làm gì khi có lỗi ta
- Check file log tại ./logs/console.log xem có exception mới gì không.
- Nếu không có log mới thì dùng lệnh *jps* xem Mario có chạy không, không chạy thì nguy.
- Nếu thấy không chạy thì chạy file start-safemode.sh để check log ở console.
- Nếu vẫn không tìm ra nguyên nhân thì liên hệ với team để được giải quyết.