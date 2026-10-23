# BÁO CÁO KẾT QUẢ THỰC HIỆN ĐỀ TÀI
## BÀI 7: PHÂN TÍCH, THIẾT KẾ VÀ HIỆN THỰC HỆ THỐNG QUẢN LÝ NHÂN SỰ & TÍNH LƯƠNG TẠI CÔNG TY SƠN ĐÔNG Á

## 1. THÔNG TIN TỔNG QUAN ĐỀ TÀI

- **Tên đề tài:** Phân tích, thiết kế hướng đối tượng và hiện thực hệ thống Quản lý Nhân sự & Tính lương tại Công ty Sơn Đông Á (Bài thực hành số 7).
- **Phương pháp luận & Công cụ mô hình hóa:** UML 2.5 theo trình tự **Pha Phân tích (Analysis Phase) → Pha Thiết kế (Design Phase)**, biểu diễn theo chuẩn **StarUML** kết hợp mã lệnh **Draw.io (Mermaid)** đi kèm từng hình vẽ.
- **Hệ quản trị Cơ sở dữ liệu:** Microsoft SQL Server (`QuanLyNhanSuDongA` gồm 19 bảng quan hệ chuẩn 3NF, đầy đủ khóa chính, khóa ngoại, ràng buộc toàn vẹn và dữ liệu mẫu).
- **Ngôn ngữ & Kiến trúc phần mềm:** C# Windows Forms (`.NET Framework 4.7.2`) trên Visual Studio 2022, kết nối SQL Server qua **ADO.NET** theo kiến trúc 3 tầng: **`UI (Forms) → Business Logic (Services) → Data Access (Data/Db.cs) → SQL Server`**.

## 2. DANH MỤC SẢN PHẨM BÀN GIAO (DELIVERABLES)

Bộ sản phẩm báo cáo giảng viên gồm **03 tệp đồng bộ chặt chẽ** từ mô hình phân tích đến cơ sở dữ liệu và ứng dụng thực thi:

| STT | Tên tệp bàn giao | Định dạng | Quy mô & Nội dung đã hoàn thành |
| :-: | :--- | :---: | :--- |
| **1** | `Bai_7_Quan_ly_nhan_su.docx` | `.docx` | Báo cáo hoàn chỉnh dài **112 trang A4**, trình bày theo đúng bố cục mẫu học phần: Phân tích 8 quy trình nghiệp vụ, trả lời 6 yêu cầu thực hành UML (Pha phân tích → Pha thiết kế), đặc tả 19 bảng CSDL, giải thích kiến trúc mã nguồn C# 3 tầng, kịch bản kiểm thử và ma trận truy vết. Tích hợp **35 hình vẽ** (26 sơ đồ UML + 9 màn hình WinForms) kèm **35 khối lệnh vẽ Draw.io** ngay bên dưới mỗi hình. |
| **2** | `QuanLyNhanSuDongA.sql` | `.sql` | Kịch bản khởi tạo CSDL `QuanLyNhanSuDongA` gồm **19 bảng**, đầy đủ ràng buộc `PRIMARY KEY`, `FOREIGN KEY`, `UNIQUE`, `CHECK` và dữ liệu mẫu (Seed Data) phục vụ kiểm thử ngay các kịch bản tính lương tháng 10/2026 tại Công ty Sơn Đông Á. |
| **3** | `QuanLyNhanSuDongA_SourceCode.zip` | `.zip` | Bộ Solution Visual Studio 2022 (`QuanLyNhanSuDongA.sln`) hoàn chỉnh gồm 9 Form giao diện, 8 lớp Service nghiệp vụ và lớp `Db.cs` tích hợp cơ chế `EnsureDatabaseReady()` tự động khởi tạo CSDL và nạp dữ liệu mẫu ngay khi nhấn `F5`. |

## 3. NỘI DUNG VÀ KẾT QUẢ ĐÃ THỰC HIỆN THEO 4 MỤC TIÊU ĐỀ BÀI

### 3.1. Mục tiêu 1: Phân tích 8 quy trình nghiệp vụ cốt lõi tại Công ty Sơn Đông Á

Đã khảo sát, chuẩn hóa luồng công việc và cài đặt các quy tắc nghiệp vụ (Business Rules) cho cả 8 phân hệ:

1. **Nghiệp vụ 1 – Tuyển dụng và Thử việc:**
   - Kiểm tra điều kiện phòng ban có nhu cầu tuyển dụng (`NhuCauTuyenDung = 1`) trước khi lập quyết định tuyển dụng thử việc.
   - Quản lý thời gian thử việc (1 đến 12 tháng), mức lương thử việc, điều khoản thỏa thuận hai bên và theo dõi kết quả đánh giá cuối kỳ (`Đang thử việc`, `Đạt`, `Không đạt`).
2. **Nghiệp vụ 2 – Hợp đồng Lao động và Hồ sơ Bảo hiểm (BHXH, BHYT):**
   - Kiểm tra điều kiện tiên quyết: Chỉ nhân viên có kết quả đánh giá thử việc **`Đạt`** mới được ký Hợp đồng lao động chính thức (`Dài hạn` hoặc `Ngắn hạn`).
   - Kiểm tra điều kiện bảo hiểm: Chỉ nhân viên ký hợp đồng **`Dài hạn`** mới được lập Hồ sơ BHXH và BHYT, với tỷ lệ trích trừ người lao động là **8.0% BHXH** và **1.5% BHYT** (tổng cộng **9.5%** trên mức lương cơ sở đóng bảo hiểm).
3. **Nghiệp vụ 3 – Quyết định Chức vụ và Quyết định Lương:**
   - Quản lý lịch sử bổ nhiệm chức vụ (`QuyetDinhChucVu`) kèm phụ cấp chức vụ và tự động cập nhật mã chức vụ hiện hành (`MaCVHienTai`) vào hồ sơ nhân viên.
   - Quản lý quyết định lương chính thức (`QuyetDinhLuong`) gồm 3 khoản: **Lương cơ bản**, **Phụ cấp lương** và **Phụ cấp độc hại** (đặc thù môi trường hóa chất, pha màu và dung môi sơn).
4. **Nghiệp vụ 4 – Kế hoạch làm việc tuần:**
   - Hỗ trợ Trưởng bộ phận/Tổ trưởng lập kế hoạch phân ca làm việc tuần (`Ca hành chính`, `Ca sáng SX`, `Ca chiều SX`) và đăng ký số giờ phụ trội dự kiến cho từng nhân viên trong phòng ban.
5. **Nghiệp vụ 5 – Chấm công hằng tháng:**
   - Ban chấm công ghi nhận số ngày công chuẩn (mặc định 26 ngày/tháng) và số ngày công thực tế của từng nhân viên theo tháng/năm, kiểm soát chống trùng lặp bằng ràng buộc `UNIQUE (MaNV, Thang, Nam)`.
6. **Nghiệp vụ 6 – Quản lý Phụ trội (Làm thêm giờ / Tăng ca):**
   - Lập phiếu phụ trội theo 3 hình thức (`Tăng ca`, `Ngày nghỉ`, `Ngày lễ`) với hệ số phụ trội quy định của công ty là **1.5** (150% đơn giá giờ thường).
   - Công thức tự động nội suy đơn giá giờ thường và thành tiền phụ trội:
     - `Đơn giá giờ thường = Lương cơ bản / (26 ngày × 8 giờ)`
     - `Thành tiền phụ trội = Số giờ tham gia × Đơn giá giờ thường × 1.5`
7. **Nghiệp vụ 7 – Khen thưởng và Kỷ luật (Cá nhân & Tập thể):**
   - Quản lý Quyết định khen thưởng kèm Phiếu chi khen thưởng và Quyết định kỷ luật kèm Phiếu thu nộp phạt cho cả **Cá nhân (`MaNV`)** lẫn **Tập thể phòng ban (`MaPB`)**.
   - Đối với khen thưởng/kỷ luật cấp **Tập thể (`MaPB`)**, hệ thống tự động chia đều số tiền thưởng/phạt cho tổng số nhân viên đang làm việc tại phòng ban đó khi tổng hợp bảng lương tháng.
8. **Nghiệp vụ 8 – Tính lương hằng tháng:**
   - Tự động truy vấn, tổng hợp dữ liệu từ tất cả các phân hệ liên quan và tính lương thực lãnh theo công thức:
     - `Lương ngày công = (Lương cơ bản / Số ngày công chuẩn) × Số ngày công thực tế`
     - `Thực lãnh = Lương ngày công + Phụ cấp chức vụ + (Phụ cấp lương + Phụ cấp độc hại) + Tiền phụ trội + Tiền khen thưởng - Tiền kỷ luật trừ lương - Trích trừ BHXH & BHYT`

### 3.2. Mục tiêu 2: Trả lời 6 Yêu cầu Thực hành bằng bộ mô hình UML (Pha Phân tích → Pha Thiết kế)

Đã xây dựng đầy đủ **26 sơ đồ UML** theo chuẩn StarUML kèm bảng đặc tả và mã lệnh vẽ Draw.io:

| Yêu cầu thực hành | Nội dung mô hình hóa (Pha Phân tích → Pha Thiết kế) | Kết quả đạt được |
| :--- | :--- | :--- |
| **Yêu cầu 1: Bối cảnh & Tác nhân** | Xác định ranh giới hệ thống, 6 tác nhân (Ứng viên/Nhân viên, Phòng HC-NS, Ban Giám đốc, Trưởng bộ phận/Tổ trưởng, Ban Chấm công, Phòng TC-KT) và luồng dữ liệu giao tiếp. | 01 Sơ đồ Bối cảnh (Context Diagram) + Bảng phân tích vai trò 6 Actor. |
| **Yêu cầu 2: Mô hình Use Case** | Xây dựng Use Case tổng quát và phân rã thành 4 gói chức năng với đầy đủ quan hệ `<<include>>`, `<<extend>>` cùng bảng đặc tả chi tiết 8 Use Case cốt lõi. | 05 Sơ đồ Use Case + 08 Bảng đặc tả Use Case chuẩn RUP. |
| **Yêu cầu 3: Mô hình Hoạt động** | Thiết kế Activity Diagram có phân làn (Swimlanes) thể hiện rõ luồng xử lý, điểm rẽ nhánh kiểm tra điều kiện nghiệp vụ cho cả 8 quy trình. | 08 Sơ đồ Activity Diagram (tương ứng 8 quy trình nghiệp vụ). |
| **Yêu cầu 4: Mô hình Tương tác** | Xây dựng Sequence Diagram theo mẫu **BCE (`Boundary – Control – Entity`)** và ánh xạ trực tiếp sang các lớp **`Frm*` → `*Service` → `Db` → `SQL Server`**. | 08 Sơ đồ Sequence Diagram cho 8 quy trình nghiệp vụ. |
| **Yêu cầu 5: Mô hình Lớp & Trạng thái** | Xây dựng Sơ đồ lớp miền nghiệp vụ (Analysis Class Diagram), Sơ đồ lớp thiết kế 3 tầng (Design Class Diagram) và Sơ đồ trạng thái vòng đời Nhân viên – Hợp đồng – Bảng lương. | 02 Sơ đồ Class Diagram + 01 Sơ đồ State Machine. |
| **Yêu cầu 6: Thành phần, Triển khai & CSDL** | Thiết kế Component Diagram (3 tầng `Forms`, `Services`, `Data`), Deployment Diagram (Client WinForms ↔ SQL Server) và Lược đồ quan hệ CSDL (ERD). | 01 Component + 01 Deployment + 01 ERD (19 bảng). |

### 3.3. Mục tiêu 3: Thiết kế CSDL SQL Server (19 bảng) & Hiện thực C# WinForms (.NET Framework 4.7.2)

#### A. Cấu trúc 19 bảng Cơ sở dữ liệu `QuanLyNhanSuDongA`

| Phân nhóm | Danh sách các bảng vật lý trong SQL Server | Ràng buộc toàn vẹn tiêu biểu |
| :--- | :--- | :--- |
| **1. Danh mục nền tảng (3 bảng)** | `PhongBan`, `ChucVu`, `CaLamViec` | `UNIQUE(TenPB)`, `UNIQUE(TenCV)`, `CHECK(SoGioChuan > 0)` |
| **2. Tuyển dụng, Hợp đồng & BH (4 bảng)** | `NhanVien`, `QuyetDinhTuyenDung`, `HopDongLaoDong`, `HoSoBaoHiem` | `UNIQUE(CCCD)`, `CHECK(LoaiHopDong IN (N'Dài hạn', N'Ngắn hạn'))`, `UNIQUE(SoSoBHXH)` |
| **3. Chức vụ & Lương (2 bảng)** | `QuyetDinhChucVu`, `QuyetDinhLuong` | `CHECK(LuongCoBan > 0)`, `CHECK(PhuCapDocHai >= 0)` |
| **4. Kế hoạch, Chấm công & OT (5 bảng)** | `KeHoachTuan`, `ChiTietKeHoachTuan`, `BangChamCong`, `PhieuPhuTroi`, `ChiTietPhuTroi` | `UNIQUE(MaNV, Thang, Nam)` trên `BangChamCong`, `CHECK(HeSoPhuTroi = 1.5)` |
| **5. Thưởng/Phạt & Lương tháng (5 bảng)** | `QuyetDinhKhenThuong`, `PhieuChiKhenThuong`, `QuyetDinhKyLuat`, `PhieuThuNopPhat`, `BangLuongThang` | `CK_QDKT_DoiTuong` & `CK_QDKL_DoiTuong` (Cá nhân XOR Tập thể), `UNIQUE(MaNV, Thang, Nam)` trên `BangLuongThang` |

#### B. Kiến trúc mã nguồn C# WinForms (`UI → Service → Data`)

| Tầng kiến trúc | Các lớp / Tệp mã nguồn đã hiện thực | Trách nhiệm chính |
| :--- | :--- | :--- |
| **Tầng Giao diện (`Forms/`)** | `FrmMain`, `FrmDanhMuc`, `FrmTuyenDungThuViec`, `FrmHopDongBaoHiem`, `FrmQuyetDinhChucVuLuong`, `FrmKeHoachTuan`, `FrmChamCongPhuTroi`, `FrmKhenThuongKyLuat`, `FrmTinhLuongThang` + `FormHelper.cs` | Hiển thị dữ liệu qua `DataGridView`, thu thập thao tác người dùng, kiểm tra nhập liệu sơ bộ và gọi tầng `Services`. |
| **Tầng Nghiệp vụ (`Services/`)** | `DanhMucService`, `TuyenDungService`, `HopDongBaoHiemService`, `ChucVuLuongService`, `KeHoachTuanService`, `ChamCongPhuTroiService`, `KhenThuongKyLuatService`, `TinhLuongService`, `Models.cs` | Kiểm tra toàn bộ quy tắc nghiệp vụ, thực thi công thức tính phụ trội, phân bổ thưởng/phạt tập thể, tính trích trừ BHXH/BHYT và tổng hợp lương tháng trong giao dịch (`SqlTransaction`). |
| **Tầng Dữ liệu (`Data/`)** | `Db.cs` (`Query`, `Execute`, `Scalar`, `ExecuteInTransaction`, `EnsureDatabaseReady`) | Quản lý kết nối ADO.NET tham số hóa (`SqlParameter` chống SQL Injection), quản lý giao dịch và tự động khởi tạo CSDL khi chạy lần đầu. |

### 3.4. Mục tiêu 4: Kiểm thử theo kịch bản cụ thể & Ma trận Truy vết (RTM)

#### A. Ma trận Truy vết Yêu cầu → UML → Form/Service → Bảng CSDL → Test Case

| Mã YC | Nghiệp vụ cốt lõi | Use Case / Sơ đồ UML | Form & Service hiện thực | Bảng CSDL SQL Server | Mã Test Case |
| :---: | :--- | :--- | :--- | :--- | :---: |
| **YC01** | Quản lý Danh mục & Hồ sơ Nhân viên | `UC01`, `ACT01`, `SEQ01` | `FrmDanhMuc` / `DanhMucService` | `PhongBan`, `ChucVu`, `CaLamViec`, `NhanVien` | `TC01`, `TC02` |
| **YC02** | Tuyển dụng & Đánh giá Thử việc | `UC02`, `ACT02`, `SEQ02` | `FrmTuyenDungThuViec` / `TuyenDungService` | `QuyetDinhTuyenDung`, `NhanVien`, `PhongBan` | `TC03`, `TC04` |
| **YC03** | Ký HĐLĐ & Lập Hồ sơ Bảo hiểm | `UC03`, `ACT03`, `SEQ03` | `FrmHopDongBaoHiem` / `HopDongBaoHiemService` | `HopDongLaoDong`, `HoSoBaoHiem` | `TC05`, `TC06` |
| **YC04** | Quyết định Chức vụ & Quyết định Lương | `UC04`, `ACT04`, `SEQ04` | `FrmQuyetDinhChucVuLuong` / `ChucVuLuongService` | `QuyetDinhChucVu`, `QuyetDinhLuong`, `NhanVien` | `TC07` |
| **YC05** | Lập Kế hoạch làm việc tuần | `UC05`, `ACT05`, `SEQ05` | `FrmKeHoachTuan` / `KeHoachTuanService` | `KeHoachTuan`, `ChiTietKeHoachTuan` | `TC08` |
| **YC06** | Chấm công & Lập Phiếu Phụ trội (OT 1.5) | `UC06`, `ACT06`, `SEQ06` | `FrmChamCongPhuTroi` / `ChamCongPhuTroiService` | `BangChamCong`, `PhieuPhuTroi`, `ChiTietPhuTroi` | `TC09`, `TC10` |
| **YC07** | Khen thưởng (Phiếu chi) & Kỷ luật (Phiếu thu) | `UC07`, `ACT07`, `SEQ07` | `FrmKhenThuongKyLuat` / `KhenThuongKyLuatService` | `QuyetDinhKhenThuong`, `PhieuChiKhenThuong`, `QuyetDinhKyLuat`, `PhieuThuNopPhat` | `TC11` |
| **YC08** | Tổng hợp & Chốt Bảng lương tháng | `UC08`, `ACT08`, `SEQ08` | `FrmTinhLuongThang` / `TinhLuongService` | `BangLuongThang` (tổng hợp từ 18 bảng) | `TC12` |

#### B. Kết quả kiểm thử đối chiếu số liệu tính lương Tháng 10/2026

Kết quả chạy chức năng **"Xem trước / Tính lương tháng"** và **"Chốt & Lưu Bảng Lương"** cho kỳ lương **Tháng 10/2026** trên bộ dữ liệu mẫu:

| Mã NV | Họ và tên | Lương ngày công (26/26) | Phụ cấp CV | Phụ cấp Lương + Độc hại | Tiền Phụ trội (OT 1.5) | Tiền Khen thưởng | Kỷ luật trừ lương | Trích trừ BHXH + BHYT | Thực lãnh (VNĐ) |
| :---: | :--- | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: |
| **NV001** | Nguyễn Văn Hùng | 14,000,000 | 3,000,000 | 2,300,000 | 605,769 | 0 | 0 | 1,470,000 | **18,435,769** |
| **NV002** | Trần Quốc Bảo | 11,000,000 | 1,500,000 | 2,200,000 | 634,616 | 7,000,000 | 0 | 1,155,000 | **21,179,616** |
| **NV003** | Lê Thị Mai | 10,500,000 | 800,000 | 1,000,000 | 0 | 0 | 0 | 1,102,500 | **11,197,500** |
| **NV004** | Lê Văn Sơn | 9,000,000 | 0 | 2,000,000 | 259,615 | 5,000,000 | 500,000 | 0 | **15,759,615** |

- **Giải trình số liệu kiểm chứng:**
  - `NV002 (Trần Quốc Bảo)`: Nhận **2,000,000 đ** thưởng cá nhân (`QDKT2026_01`) + **5,000,000 đ** thưởng tập thể Phòng Sản xuất (`QDKT2026_02` tổng 10,000,000 đ chia đều cho 2 nhân sự chính thức thuộc `PB_SX` là `NV002` và `NV004`) = **7,000,000 đ**; phụ trội 2 phiếu (`PPT2026_10_01` và `PPT2026_10_03`, mỗi phiếu 4 giờ × 52,884.62 đ × 1.5 = 317,308 đ) = **634,616 đ**.
  - `NV004 (Lê Văn Sơn)`: Nhận **5,000,000 đ** phân bổ thưởng tập thể `PB_SX`, bị khấu trừ **500,000 đ** theo Phiếu thu nộp phạt `PTNP001` (`TruVaoBangLuong = 1`), và không trích trừ BHXH/BHYT (**0 đ**) do đang làm việc theo Hợp đồng **Ngắn hạn** (`HD2026_003`).

## 4. HƯỚNG DẪN CHẠY DEMO BÁO CÁO GIẢNG VIÊN

1. **Mở dự án:** Giải nén `QuanLyNhanSuDongA_SourceCode.zip` và mở tệp `QuanLyNhanSuDongA.sln` bằng **Visual Studio 2022** (hoặc Visual Studio 2019).
2. **Chạy chương trình (Zero-Configuration):** Nhấn **F5**. Lớp `Db.EnsureDatabaseReady()` sẽ tự động kết nối SQL Server (`(localdb)\MSSQLLocalDB`, `.\SQLEXPRESS` hoặc `localhost`), tự động khởi tạo CSDL `QuanLyNhanSuDongA` và nạp toàn bộ dữ liệu mẫu.
3. **Trình diễn các điểm nhấn nghiệp vụ:**
   - Mở **`2. Tuyển dụng - Thử việc`** → Thử lập quyết định tuyển dụng vào Phòng Hành chính Nhân sự (`PB_NS`, `NhuCauTuyenDung = 0`) để minh họa hệ thống tự động chặn và báo lỗi.
   - Mở **`3. Hợp đồng & Bảo hiểm`** → Thử lập hồ sơ bảo hiểm cho `NV004` (Hợp đồng ngắn hạn) để minh họa hệ thống kiểm tra ràng buộc chỉ cho phép Hợp đồng dài hạn.
   - Mở **`6. Chấm công & Phụ trội`** → Chọn nhân viên và nhập số giờ tăng ca, hệ thống tự động nội suy đơn giá giờ thường từ Quyết định lương và nhân hệ số `1.5`.
   - Mở **`8. Tính lương tháng`** → Chọn **Tháng 10 / Năm 2026**, nhấn **"Xem trước / Tính lương tháng"** và **"Chốt & Lưu Bảng Lương"** để trình diễn bảng lương tổng hợp chính xác như bảng đối chiếu ở Mục 3.4.
