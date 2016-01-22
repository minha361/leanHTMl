# Introductions
	- *Product* là một project có nhiệm vụ chứa các cấu hình cho core product tại adayroi, chứa các class chung nhất cho tất cả các nghiệp vụ liên quan đến product, và có nhiệm vụ tổ hợp tất cả các project khác liên quan đến product thành một sản phẩm đóng gói hoàn chỉnh
	- Hiện tại, *latte-search* chưa chính thức được đưa vào sử dụng, nên đang có một cách đóng gói compound, tức là đưa tất cả các nghiệp vụ vào trong file jar *latte-compound*, file jar này có trên ivy và được sử dụng trong light-search.
	
# Setup guide
	- Quy cách đóng gói: sử dụng target *build_1_jar* của build.xml. Nếu cần update lên ivy hì sử dụng target *update-ivy*
	- Trong light-search thử sử dụng ivy với quy tắc <dependency org="lib.product" name="latte-compound" rev="1.0.0" conf="lib->default"/>
	- Các cấu hình hiện tại của handler cho mobile sẽ có trong file solrconfig.xml trên git của project này
	- R&D có một api mới đầu vào là một product_id, đầu ra là thông tin sản phẩm bao gồm cả thuộc tính
	- Lưu ý cấu hình của VinSimilarity chỉ sử dụng được cho tên, mục đích thiết kế chỉ có vậy