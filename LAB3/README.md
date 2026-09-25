# LAB 3: THIẾT KẾ VÀ XÂY DỰNG HỆ THỐNG QUẢN LÝ KHÁCH SẠN

- **Họ và tên sinh viên:** Nguyễn Lữ Khâm
- **MSSV:** 1250080079
- **Tên bài Lab:** Lab 3 - Thiết kế và Hiện thực Hệ thống Hướng đối tượng Quản lý Khách sạn
- **Môi trường & Version:**
  - Ngôn ngữ lập trình: Java (JDK 17+)
  - Giao diện: Java Swing
  - Hệ quản trị CSDL: SQL Server 2022 (Docker container) và công cụ là Azure Data Studio.
  - Thư viện kết nối CSDL: `mssql-jdbc`
  - IDE: Visual Studio Code

## 1. Nội dung đã thực hiện
- Khảo sát và phân tích hiện trạng hệ thống quản lý khách sạn.
- Xác định và đặc tả các tác nhân (Actor), Use Case trọng tâm (Đặt phòng, Trả phòng & Thanh toán, Quản lý danh mục...).
- Thiết kế đầy đủ các biểu đồ UML: Use Case, Lớp phân tích/chi tiết, Tuần tự, Trạng thái, Hoạt động.
- Thiết kế Cơ sở dữ liệu (Database Schema) với các ràng buộc chuẩn xác.
- Hiện thực hóa thiết kế thông qua mã nguồn Java Swing theo kiến trúc hướng module (tách biệt UI và Service logic).
- Lập trình đầy đủ các chức năng Thêm/Xóa/Sửa danh mục, Nghiệp vụ Đặt/Nhận/Trả phòng và Lập Hóa đơn.
- Kiểm thử các quy tắc nghiệp vụ khắt khe: Sức chứa, Trùng lịch

## 2. Kết quả đạt được
- Hệ thống chạy mượt mà, giao diện trực quan.
- Dữ liệu thêm/sửa/xóa được lưu thành công vào cơ sở dữ liệu SQL Server.
- Xử lý tốt các logic phức tạp như chặn đặt phòng trùng lịch, bắt lỗi nhập liệu sai

## 3. Hướng dẫn Giảng viên kiểm tra / chạy lại ứng dụng
Để chạy ứng dụng trên máy giảng viên, xin vui lòng làm theo các bước sau:
1. **Thiết lập CSDL:** Mở SQL Server Management Studio (SSMS), mở file `database.sql` đính kèm trong thư mục này và chạy toàn bộ Script để tạo CSDL `QuanLyKhachSan_Lab03` cùng các bảng, dữ liệu mẫu.
2. **Cập nhật chuỗi kết nối:** Mở file `src/DbConnection.java` và cấu hình lại chuỗi kết nối JDBC (Tên user, mật khẩu `sa`) sao cho khớp với môi trường của máy chủ chạy SQL Server.
3. **Biên dịch & Chạy ứng dụng:**
   - Compile code: Chạy lệnh `javac -d bin src/*.java` tại thư mục gốc của project.
   - Chạy ứng dụng: Chạy lệnh `java -cp bin:lib/mssql-jdbc.jar App` (hoặc chạy trực tiếp file `FrmMain.java` qua IDE).
4. **Thao tác kiểm thử:**
   - Vào mục **Đặt/Nhận Phòng** -> Thử lập phiếu với số người > sức chứa hoặc ngày bị trùng để test validation (trigger lỗi từ SQL ném lên UI).
   - Vào mục **Trả Phòng** -> Chọn Phiếu và ấn Tạo Hóa Đơn để kiểm tra khả năng tự động tính tổng tiền.
