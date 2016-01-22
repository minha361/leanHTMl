# Introduction
DataImport là công cụ đánh chỉ mục toàn bộ dữ liệu từ sql server vào solr

# Instruction
- Tiền điều kiện
	- Một solr server được cài đặt với 2 core là *product* và *suggestion*
	- Core *product* có 2 file cấu hình đúng là: *schema.xml* và *solrconfig.xml*
	- Core *suggestion* có 2 file cấu hình đúng là *schema.xml* và *solrconfig.xml*
	- 2 file jar: *ProductTransformer.jar* và *sqljdbc41.jar*. Lấy trong thư mục *extensions* của thư mục *mario*
	- File *db-data-config.xml* cho core *product*
	- File *db-data-config.xml* cho core *suggestion*
	- Tất cả các stored procedures trong thư mục *sqls* của thư mục *mario* có trên cơ sở dữ liệu sử dụng
- Bước 1: copy 2 file jar đến *path_to_solr/contrib/dataimporthandler/lib*
- Bước 2: 
	- copy *db-data-config.xml* của core *product* vào *path_to_solr/server/solr/product/conf*
	- copy *db-data-config.xml của *suggestion* vào *path_to_solr/server/solr/suggestion/conf*
- Bước 3:
	- Sửa thông tin database trong *db-data-config.xml* của core *product*
	- Sửa thông tin database trong *db-data-config.xml* của core *suggestion*
- Bước 4: Dừng *mario-consumer* và khởi động lại solr
- Bước 5:
	- Chạy full-index của core *product*
	- Chạy full-index của core *suggestion*
- Bước 6: Nhớ bật lại *mario-consumer* nhé!