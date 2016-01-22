# Introduction
	- Thực hiện tìm kiếm các sản phẩm tương tự
	-  Trình tự có các bước đơn giản như sau
		- B1: Chọc một phát vào solr để lấy ra full thông tin của các sản phẩm đưa vào bằng product_id
 		- B2: Đi qua từng sản phẩm, lấy ra các rule thỏa mãn cho từng sản phẩm rồi sinh ra SolrParams truy vấn cho từng sản phẩm đó để lấy gợi ý. Ở bước này không áp dụng các điều kiện onsite và lọc theo thành phố đâu nhá. Cải tiến là với các rule mà không có điều kiện sắp xếp (ưu tiên) thì nên sắp xếp ngẫu nhiên với seed thay đổi định kì
		- B3: Thực hiện song song chọc vào solr để lấy ra tập các product_id thỏa mãn các SolrParams ở bước trên. Ở bước này thì phải áp dụng lọc theo thành phố và onsite rồi
		- B4: Với các product_id đó chọc solr phát nữa (sắp xếp ngẫu nhiên theo seed thay đổi định kì) để lấy ra các thông tin cần thiết để trả về

# Installation guide
	- Copy vào chỗ nào đó đẹp đẹp trong solr
	- Trỏ tới file jar trong solrconfig.xml
	- Thêm một cấu hình handler trong solrconfig.xml
  
# Troubleshoot