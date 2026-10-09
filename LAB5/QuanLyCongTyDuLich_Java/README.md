# HỆ THỐNG QUẢN LÝ CÔNG TY DU LỊCH VĂN HÓA VIỆT (JAVA SWING / JDBC)

## 1. Giới thiệu
Dự án được xây dựng hoàn toàn bằng ngôn ngữ Java (Java 17+), áp dụng kiến trúc phân tầng:
- **UI (Presentation)**: Java Swing (MainFrame, ChuyenLeFrame, DangKyDoanFrame, ...)
- **Service (Business Logic)**: TourService, DangKyDoanService, PhanCongService, ...
- **Data Access Layer**: Db.java (JDBC PreparedStatement, Connection Pool, Transaction Management)
- **Database**: SQL Server (QuanLyCongTyDuLich)

## 2. Cách chạy trên MacOS với Visual Studio Code
1. Mở thư mục `QuanLyCongTyDuLich_Java` bằng Visual Studio Code trên macOS.
2. Khởi chạy CSDL SQL Server qua Docker Desktop (nếu chạy local trên Mac):
   ```bash
   docker run -e "ACCEPT_EULA=Y" -e "SA_PASSWORD=YourPassword123" -p 1433:1433 -d mcr.microsoft.com/mssql/server:2022-latest
   ```
3. Chạy script SQL trong thư mục `database/QuanLyCongTyDuLich.sql`.
4. Mở file `src/main/java/quanlycongtydulich/ui/MainFrame.java` và bấm **F5** (hoặc nhấn nút Run Java phía trên bên phải của VS Code) để chạy ứng dụng giao diện trực tiếp trên Mac!
