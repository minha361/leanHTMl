# Introduction
  - *latte-search* là một tập các project định nghĩa các *solr custom handler* để trả ra dữ liệu cho tất cả các bên (FE1, FE2, CM, CS, Combo)
  - Project "CommonSearch" định nghĩa các Interface, Base class và các công cụ chung nhất cho cho tất cả các Project khác
  - Các Project khác sẽ thực hiện một nghiệp vụ mà nó đảm nhiệm, xem *README* của từng project để biết thêm chi tiết

# Installation guide
  - Tiền điều kiện
    - Một HazelCast server đã được cài đặt và fetch đầy đủ dữ liệu
      - Để làm được điều này, cần bật chương trình *MarioConsumer* lên để dữ liệu được fill đẩy đủ vào cache (1 instance được đi kèm với MarioConsumer)
      - Sau đó bật một instance thứ 2 của Hazelcast để instance này cũng có đẩy đủ dữ liệu
      - Tắt *MarioConsumer* nếu muốn thực hiện *full-import* để tránh conflict dữ liệu. Khi tắt *MarioConsumer* thì *latte-search* sẽ lấy dữ liệu từ instance thứ 2 của Hazelcast
  - Cài đặt (thông qua công cụ CI *Jenkins*)
    - 

# Troubleshoot