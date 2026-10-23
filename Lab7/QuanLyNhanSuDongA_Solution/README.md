# HỆ THỐNG QUẢN LÝ NHÂN SỰ & TÍNH LƯƠNG - CÔNG TY SƠN ĐÔNG Á (BÀI 7)
**Kiến trúc:** C# WinForms (.NET Framework 4.7.2) theo mô hình 3 lớp `UI (Forms) -> Business Logic (Services) -> Data Access (Data/Db.cs) -> SQL Server`.

## 1. Cấu trúc thư mục Solution
- `QuanLyNhanSuDongA.sln`: File Solution mở trực tiếp bằng **Visual Studio 2022** (hoặc VS 2019).
- `QuanLyNhanSuDongA/Database/QuanLyNhanSuDongA.sql`: Script SQL Server tạo đầy đủ 19 bảng, khóa chính, khóa ngoại, ràng buộc `CHECK`/`UNIQUE` và dữ liệu mẫu Công ty Sơn Đông Á.
- `QuanLyNhanSuDongA/Data/Db.cs`: Lớp kết nối ADO.NET (`SqlConnection`, `SqlTransaction`). **Đặc biệt:** Hàm `Db.EnsureDatabaseReady()` tự động dò `(localdb)\MSSQLLocalDB`, `.\SQLEXPRESS`, `localhost` và **tự động tạo CSDL + nạp dữ liệu mẫu** nếu bạn chưa chạy script SQL trước đó.
- `QuanLyNhanSuDongA/Services/`: Gồm `Models.cs` và 8 lớp Service nghiệp vụ (`DanhMucService`, `TuyenDungService`, `HopDongBaoHiemService`, `ChucVuLuongService`, `KeHoachTuanService`, `ChamCongPhuTroiService`, `KhenThuongKyLuatService`, `TinhLuongService`).
- `QuanLyNhanSuDongA/Forms/`: Gồm `FormHelper.cs` và 9 Form hoàn chỉnh (cả `.cs` và `.Designer.cs`), có sẵn đầy đủ Control mà không cần kéo thả lại.
- `QuanLyNhanSuDongA/UML/Images/`: Bộ 35 hình ảnh mô hình UML (Use Case, Class, State, Sequence, VOPC, Activity, ERD) và ảnh giao diện WinForms.

## 2. Hướng dẫn chạy nhanh (2 cách)
### Cách 1: Chạy trực tiếp trong Visual Studio 2022 (Tự động khởi tạo CSDL)
1. Giải nén file `QuanLyNhanSuDongA_SourceCode.zip`.
2. Mở file `QuanLyNhanSuDongA.sln` bằng **Visual Studio 2022**.
3. Nhấn **F5 (Start)** để chạy ngay. Khi `FrmMain` mở lên, `Db.EnsureDatabaseReady()` sẽ tự động kiểm tra và tạo CSDL `QuanLyNhanSuDongA` trên `(localdb)\MSSQLLocalDB` hoặc `.\SQLEXPRESS` từ file `Database/QuanLyNhanSuDongA.sql`.

### Cách 2: Chạy chủ động trên SSMS trước khi mở Visual Studio
1. Mở **SQL Server Management Studio (SSMS)**, kết nối vào `(localdb)\MSSQLLocalDB` (hoặc `.\SQLEXPRESS`).
2. Mở file `QuanLyNhanSuDongA/Database/QuanLyNhanSuDongA.sql` và nhấn **F5 (Execute)**.
3. Nếu dùng `.\SQLEXPRESS`, chỉnh `Data Source=.\SQLEXPRESS` trong `App.config` (hoặc để `Db.cs` tự động dò), sau đó nhấn **F5** trong Visual Studio.
