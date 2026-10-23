/* =========================================================================
   BÀI 7: HỆ THỐNG QUẢN LÝ NHÂN SỰ - CÔNG TY SƠN ĐÔNG Á
   SQL Server Script (DDL + Constraints + Seed Data)
   ========================================================================= */
IF DB_ID(N'QuanLyNhanSuDongA') IS NULL
    CREATE DATABASE QuanLyNhanSuDongA;
GO
USE QuanLyNhanSuDongA;
GO

IF OBJECT_ID('dbo.BangLuongThang', 'U') IS NOT NULL DROP TABLE dbo.BangLuongThang;
IF OBJECT_ID('dbo.PhieuThuNopPhat', 'U') IS NOT NULL DROP TABLE dbo.PhieuThuNopPhat;
IF OBJECT_ID('dbo.QuyetDinhKyLuat', 'U') IS NOT NULL DROP TABLE dbo.QuyetDinhKyLuat;
IF OBJECT_ID('dbo.PhieuChiKhenThuong', 'U') IS NOT NULL DROP TABLE dbo.PhieuChiKhenThuong;
IF OBJECT_ID('dbo.QuyetDinhKhenThuong', 'U') IS NOT NULL DROP TABLE dbo.QuyetDinhKhenThuong;
IF OBJECT_ID('dbo.ChiTietPhuTroi', 'U') IS NOT NULL DROP TABLE dbo.ChiTietPhuTroi;
IF OBJECT_ID('dbo.PhieuPhuTroi', 'U') IS NOT NULL DROP TABLE dbo.PhieuPhuTroi;
IF OBJECT_ID('dbo.BangChamCong', 'U') IS NOT NULL DROP TABLE dbo.BangChamCong;
IF OBJECT_ID('dbo.ChiTietKeHoachTuan', 'U') IS NOT NULL DROP TABLE dbo.ChiTietKeHoachTuan;
IF OBJECT_ID('dbo.KeHoachTuan', 'U') IS NOT NULL DROP TABLE dbo.KeHoachTuan;
IF OBJECT_ID('dbo.QuyetDinhLuong', 'U') IS NOT NULL DROP TABLE dbo.QuyetDinhLuong;
IF OBJECT_ID('dbo.QuyetDinhChucVu', 'U') IS NOT NULL DROP TABLE dbo.QuyetDinhChucVu;
IF OBJECT_ID('dbo.HoSoBaoHiem', 'U') IS NOT NULL DROP TABLE dbo.HoSoBaoHiem;
IF OBJECT_ID('dbo.HopDongLaoDong', 'U') IS NOT NULL DROP TABLE dbo.HopDongLaoDong;
IF OBJECT_ID('dbo.QuyetDinhTuyenDung', 'U') IS NOT NULL DROP TABLE dbo.QuyetDinhTuyenDung;
IF OBJECT_ID('dbo.NhanVien', 'U') IS NOT NULL DROP TABLE dbo.NhanVien;
IF OBJECT_ID('dbo.CaLamViec', 'U') IS NOT NULL DROP TABLE dbo.CaLamViec;
IF OBJECT_ID('dbo.ChucVu', 'U') IS NOT NULL DROP TABLE dbo.ChucVu;
IF OBJECT_ID('dbo.PhongBan', 'U') IS NOT NULL DROP TABLE dbo.PhongBan;
GO

CREATE TABLE dbo.PhongBan (
    MaPB VARCHAR(20) PRIMARY KEY,
    TenPB NVARCHAR(150) NOT NULL UNIQUE,
    DienThoai VARCHAR(20) NULL,
    NhuCauTuyenDung BIT NOT NULL DEFAULT 1
);

CREATE TABLE dbo.ChucVu (
    MaCV VARCHAR(20) PRIMARY KEY,
    TenCV NVARCHAR(120) NOT NULL UNIQUE,
    PhuCapMacDinh DECIMAL(18,2) NOT NULL DEFAULT 0 CHECK (PhuCapMacDinh >= 0)
);

CREATE TABLE dbo.CaLamViec (
    MaCa VARCHAR(20) PRIMARY KEY,
    TenCa NVARCHAR(100) NOT NULL,
    GioBatDau VARCHAR(10) NOT NULL,
    GioKetThuc VARCHAR(10) NOT NULL,
    SoGioChuan DECIMAL(5,2) NOT NULL DEFAULT 8 CHECK (SoGioChuan > 0)
);

CREATE TABLE dbo.NhanVien (
    MaNV VARCHAR(20) PRIMARY KEY,
    MaPB VARCHAR(20) NOT NULL REFERENCES dbo.PhongBan(MaPB),
    MaCVHienTai VARCHAR(20) NULL REFERENCES dbo.ChucVu(MaCV),
    HoTen NVARCHAR(150) NOT NULL,
    NgaySinh DATE NOT NULL,
    GioiTinh NVARCHAR(10) NOT NULL,
    CCCD VARCHAR(20) NOT NULL UNIQUE,
    DienThoai VARCHAR(20) NOT NULL,
    DiaChi NVARCHAR(250) NULL,
    NgayVaoLam DATE NOT NULL,
    TrangThaiLamViec NVARCHAR(40) NOT NULL DEFAULT N'Thử việc',
    CONSTRAINT CK_NV_TrangThai CHECK (TrangThaiLamViec IN (N'Thử việc', N'Chính thức', N'Nghỉ việc'))
);

CREATE TABLE dbo.QuyetDinhTuyenDung (
    SoQDTD VARCHAR(20) PRIMARY KEY,
    MaNV VARCHAR(20) NOT NULL REFERENCES dbo.NhanVien(MaNV),
    MaPB VARCHAR(20) NOT NULL REFERENCES dbo.PhongBan(MaPB),
    NgayKy DATE NOT NULL,
    ThoiGianThuViecThang INT NOT NULL CHECK (ThoiGianThuViecThang BETWEEN 1 AND 12),
    LuongThuViec DECIMAL(18,2) NOT NULL CHECK (LuongThuViec > 0),
    DieuKhoanHaiBen NVARCHAR(1000) NOT NULL,
    KetQuaThuViec NVARCHAR(40) NOT NULL DEFAULT N'Đang thử việc',
    CONSTRAINT CK_QDTD_KetQua CHECK (KetQuaThuViec IN (N'Đang thử việc', N'Đạt', N'Không đạt'))
);

CREATE TABLE dbo.HopDongLaoDong (
    SoHD VARCHAR(20) PRIMARY KEY,
    MaNV VARCHAR(20) NOT NULL REFERENCES dbo.NhanVien(MaNV),
    LoaiHopDong NVARCHAR(30) NOT NULL,
    NgayKy DATE NOT NULL,
    NgayBatDau DATE NOT NULL,
    NgayKetThuc DATE NULL,
    NoiDungHopDong NVARCHAR(1000) NOT NULL,
    TrangThaiHD NVARCHAR(40) NOT NULL DEFAULT N'Đang hiệu lực',
    CONSTRAINT CK_HD_Loai CHECK (LoaiHopDong IN (N'Dài hạn', N'Ngắn hạn')),
    CONSTRAINT CK_HD_Ngay CHECK (NgayKetThuc IS NULL OR NgayKetThuc >= NgayBatDau)
);

CREATE TABLE dbo.HoSoBaoHiem (
    MaHoSoBH VARCHAR(20) PRIMARY KEY,
    SoHD VARCHAR(20) NOT NULL UNIQUE REFERENCES dbo.HopDongLaoDong(SoHD),
    MaNV VARCHAR(20) NOT NULL UNIQUE REFERENCES dbo.NhanVien(MaNV),
    SoSoBHXH VARCHAR(30) NOT NULL UNIQUE,
    SoTheBHYT VARCHAR(30) NOT NULL UNIQUE,
    TyLeBHXH DECIMAL(5,2) NOT NULL DEFAULT 8.0,
    TyLeBHYT DECIMAL(5,2) NOT NULL DEFAULT 1.5,
    MucDongHangThang DECIMAL(18,2) NOT NULL CHECK (MucDongHangThang > 0)
);

CREATE TABLE dbo.QuyetDinhChucVu (
    SoQDCV VARCHAR(20) PRIMARY KEY,
    MaNV VARCHAR(20) NOT NULL REFERENCES dbo.NhanVien(MaNV),
    MaCV VARCHAR(20) NOT NULL REFERENCES dbo.ChucVu(MaCV),
    NgayHieuLuc DATE NOT NULL,
    PhuCapChucVu DECIMAL(18,2) NOT NULL DEFAULT 0 CHECK (PhuCapChucVu >= 0),
    NoiDungQuyetDinh NVARCHAR(500) NULL
);

CREATE TABLE dbo.QuyetDinhLuong (
    SoQDL VARCHAR(20) PRIMARY KEY,
    MaNV VARCHAR(20) NOT NULL REFERENCES dbo.NhanVien(MaNV),
    NgayHieuLuc DATE NOT NULL,
    LuongCoBan DECIMAL(18,2) NOT NULL CHECK (LuongCoBan > 0),
    PhuCapLuong DECIMAL(18,2) NOT NULL DEFAULT 0 CHECK (PhuCapLuong >= 0),
    PhuCapDocHai DECIMAL(18,2) NOT NULL DEFAULT 0 CHECK (PhuCapDocHai >= 0),
    GhiChu NVARCHAR(500) NULL
);

CREATE TABLE dbo.KeHoachTuan (
    MaKeHoach VARCHAR(20) PRIMARY KEY,
    MaPB VARCHAR(20) NOT NULL REFERENCES dbo.PhongBan(MaPB),
    Tuan INT NOT NULL CHECK (Tuan BETWEEN 1 AND 53),
    TuNgay DATE NOT NULL,
    DenNgay DATE NOT NULL,
    NguoiLap NVARCHAR(120) NOT NULL,
    CONSTRAINT CK_KHT_Ngay CHECK (DenNgay >= TuNgay)
);

CREATE TABLE dbo.ChiTietKeHoachTuan (
    MaKeHoach VARCHAR(20) NOT NULL REFERENCES dbo.KeHoachTuan(MaKeHoach),
    MaNV VARCHAR(20) NOT NULL REFERENCES dbo.NhanVien(MaNV),
    MaCa VARCHAR(20) NOT NULL REFERENCES dbo.CaLamViec(MaCa),
    NgayLamViec DATE NOT NULL,
    GioPhuTroiDuKien DECIMAL(5,2) NOT NULL DEFAULT 0 CHECK (GioPhuTroiDuKien >= 0),
    GhiChu NVARCHAR(300) NULL,
    PRIMARY KEY (MaKeHoach, MaNV, MaCa, NgayLamViec)
);

CREATE TABLE dbo.BangChamCong (
    MaChamCong VARCHAR(20) PRIMARY KEY,
    MaNV VARCHAR(20) NOT NULL REFERENCES dbo.NhanVien(MaNV),
    Thang INT NOT NULL CHECK (Thang BETWEEN 1 AND 12),
    Nam INT NOT NULL CHECK (Nam BETWEEN 2020 AND 2100),
    SoNgayCongChuan DECIMAL(5,2) NOT NULL DEFAULT 26 CHECK (SoNgayCongChuan > 0),
    SoNgayCongThucTe DECIMAL(5,2) NOT NULL CHECK (SoNgayCongThucTe >= 0),
    NguoiChamCong NVARCHAR(120) NOT NULL DEFAULT N'Ban chấm công',
    CONSTRAINT UX_ChamCong_NV_ThangNam UNIQUE (MaNV, Thang, Nam)
);

CREATE TABLE dbo.PhieuPhuTroi (
    SoPhieuPT VARCHAR(20) PRIMARY KEY,
    NgayPhuTroi DATE NOT NULL,
    SoGioPhuTroi DECIMAL(5,2) NOT NULL CHECK (SoGioPhuTroi > 0),
    HinhThucPhuTroi NVARCHAR(50) NOT NULL,
    HeSoPhuTroi DECIMAL(4,2) NOT NULL DEFAULT 1.5,
    NguoiLap NVARCHAR(120) NOT NULL DEFAULT N'Ban chấm công',
    CONSTRAINT CK_PPT_HinhThuc CHECK (HinhThucPhuTroi IN (N'Tăng ca', N'Ngày lễ', N'Ngày nghỉ')),
    CONSTRAINT CK_PPT_HeSo CHECK (HeSoPhuTroi = 1.5)
);

CREATE TABLE dbo.ChiTietPhuTroi (
    SoPhieuPT VARCHAR(20) NOT NULL REFERENCES dbo.PhieuPhuTroi(SoPhieuPT),
    MaNV VARCHAR(20) NOT NULL REFERENCES dbo.NhanVien(MaNV),
    SoGioThamGia DECIMAL(5,2) NOT NULL CHECK (SoGioThamGia > 0),
    DonGiaGioThuong DECIMAL(18,2) NOT NULL CHECK (DonGiaGioThuong > 0),
    ThanhTienPhuTroi DECIMAL(18,2) NOT NULL CHECK (ThanhTienPhuTroi > 0),
    PRIMARY KEY (SoPhieuPT, MaNV)
);

CREATE TABLE dbo.QuyetDinhKhenThuong (
    SoQDKT VARCHAR(20) PRIMARY KEY,
    LoaiDoiTuong NVARCHAR(20) NOT NULL,
    MaNV VARCHAR(20) NULL REFERENCES dbo.NhanVien(MaNV),
    MaPB VARCHAR(20) NULL REFERENCES dbo.PhongBan(MaPB),
    NgayKy DATE NOT NULL,
    LoaiKhenThuong NVARCHAR(150) NOT NULL,
    SoTienThuong DECIMAL(18,2) NOT NULL CHECK (SoTienThuong > 0),
    NguoiKy NVARCHAR(120) NOT NULL DEFAULT N'Giám đốc',
    CONSTRAINT CK_QDKT_DoiTuong CHECK (
        (LoaiDoiTuong = N'Cá nhân' AND MaNV IS NOT NULL AND MaPB IS NULL) OR
        (LoaiDoiTuong = N'Tập thể' AND MaPB IS NOT NULL AND MaNV IS NULL)
    )
);

CREATE TABLE dbo.PhieuChiKhenThuong (
    SoPhieuChi VARCHAR(20) PRIMARY KEY,
    SoQDKT VARCHAR(20) NOT NULL UNIQUE REFERENCES dbo.QuyetDinhKhenThuong(SoQDKT),
    NgayChi DATE NOT NULL,
    SoTienChi DECIMAL(18,2) NOT NULL CHECK (SoTienChi > 0),
    ThangTinhLuong INT NOT NULL CHECK (ThangTinhLuong BETWEEN 1 AND 12),
    NamTinhLuong INT NOT NULL CHECK (NamTinhLuong BETWEEN 2020 AND 2100)
);

CREATE TABLE dbo.QuyetDinhKyLuat (
    SoQDKL VARCHAR(20) PRIMARY KEY,
    LoaiDoiTuong NVARCHAR(20) NOT NULL,
    MaNV VARCHAR(20) NULL REFERENCES dbo.NhanVien(MaNV),
    MaPB VARCHAR(20) NULL REFERENCES dbo.PhongBan(MaPB),
    NgayKy DATE NOT NULL,
    HinhThucKyLuat NVARCHAR(200) NOT NULL,
    SoTienPhat DECIMAL(18,2) NOT NULL CHECK (SoTienPhat > 0),
    CONSTRAINT CK_QDKL_DoiTuong CHECK (
        (LoaiDoiTuong = N'Cá nhân' AND MaNV IS NOT NULL AND MaPB IS NULL) OR
        (LoaiDoiTuong = N'Tập thể' AND MaPB IS NOT NULL AND MaNV IS NULL)
    )
);

CREATE TABLE dbo.PhieuThuNopPhat (
    SoPhieuThu VARCHAR(20) PRIMARY KEY,
    SoQDKL VARCHAR(20) NOT NULL UNIQUE REFERENCES dbo.QuyetDinhKyLuat(SoQDKL),
    NgayThu DATE NOT NULL,
    SoTienThuPhat DECIMAL(18,2) NOT NULL CHECK (SoTienThuPhat > 0),
    TruVaoBangLuong BIT NOT NULL DEFAULT 1,
    ThangTruLuong INT NOT NULL CHECK (ThangTruLuong BETWEEN 1 AND 12),
    NamTruLuong INT NOT NULL CHECK (NamTruLuong BETWEEN 2020 AND 2100)
);

CREATE TABLE dbo.BangLuongThang (
    MaBangLuong VARCHAR(20) PRIMARY KEY,
    MaNV VARCHAR(20) NOT NULL REFERENCES dbo.NhanVien(MaNV),
    Thang INT NOT NULL CHECK (Thang BETWEEN 1 AND 12),
    Nam INT NOT NULL CHECK (Nam BETWEEN 2020 AND 2100),
    LuongCoBan DECIMAL(18,2) NOT NULL,
    PhuCapChucVu DECIMAL(18,2) NOT NULL DEFAULT 0,
    PhuCapLuongDocHai DECIMAL(18,2) NOT NULL DEFAULT 0,
    SoNgayCong DECIMAL(5,2) NOT NULL DEFAULT 0,
    TienLuongNgayCong DECIMAL(18,2) NOT NULL DEFAULT 0,
    TienPhuTroi DECIMAL(18,2) NOT NULL DEFAULT 0,
    TienKhenThuong DECIMAL(18,2) NOT NULL DEFAULT 0,
    TienKyLuatTru DECIMAL(18,2) NOT NULL DEFAULT 0,
    TrichTruBHXH_BHYT DECIMAL(18,2) NOT NULL DEFAULT 0,
    ThucLanh DECIMAL(18,2) NOT NULL DEFAULT 0,
    TrangThai NVARCHAR(40) NOT NULL DEFAULT N'Đã tính',
    CONSTRAINT UX_BangLuong_NV_ThangNam UNIQUE (MaNV, Thang, Nam)
);
GO

INSERT INTO dbo.PhongBan (MaPB, TenPB, DienThoai, NhuCauTuyenDung) VALUES
('PB_TCKT', N'Phòng Tài chính Kế toán', '02838991101', 1),
('PB_KT',   N'Phòng Kỹ thuật Pha màu',  '02838991102', 1),
('PB_SX',   N'Phòng Sản xuất Sơn',      '02838991103', 1),
('PB_NS',   N'Phòng Hành chính Nhân sự','02838991100', 0);

INSERT INTO dbo.ChucVu (MaCV, TenCV, PhuCapMacDinh) VALUES
('CV_TP', N'Trưởng phòng',       3000000),
('CV_TT', N'Tổ trưởng sản xuất', 1500000),
('CV_KT', N'Kế toán viên',        800000),
('CV_NV', N'Nhân viên / Công nhân',    0);

INSERT INTO dbo.CaLamViec (MaCa, TenCa, GioBatDau, GioKetThuc, SoGioChuan) VALUES
('CA_HC', N'Ca hành chính', '08:00', '17:00', 8.0),
('CA1',   N'Ca sáng SX',    '06:00', '14:00', 8.0),
('CA2',   N'Ca chiều SX',   '14:00', '22:00', 8.0);

INSERT INTO dbo.NhanVien (MaNV, MaPB, MaCVHienTai, HoTen, NgaySinh, GioiTinh, CCCD, DienThoai, DiaChi, NgayVaoLam, TrangThaiLamViec) VALUES
('NV001', 'PB_KT',   'CV_TP', N'Nguyễn Văn Hùng', '1988-05-12', N'Nam', '079088001122', '0903111222', N'Quận Bình Thạnh, TP.HCM', '2024-03-01', N'Chính thức'),
('NV002', 'PB_SX',   'CV_TT', N'Trần Quốc Bảo',   '1992-08-20', N'Nam', '079092003344', '0903222333', N'TP. Thủ Đức, TP.HCM',     '2024-06-01', N'Chính thức'),
('NV003', 'PB_TCKT', 'CV_KT', N'Lê Thị Mai',      '1995-11-10', N'Nữ',  '079195005566', '0903333444', N'Quận Gò Vấp, TP.HCM',     '2025-01-15', N'Chính thức'),
('NV004', 'PB_SX',   'CV_NV', N'Lê Văn Sơn',      '1998-02-18', N'Nam', '079098007788', '0903444555', N'Quận 12, TP.HCM',         '2026-04-01', N'Chính thức'),
('NV005', 'PB_TCKT', NULL,    N'Hoàng Thị Lan',   '2001-09-05', N'Nữ',  '079201009900', '0903555666', N'Quận Tân Bình, TP.HCM',   '2026-09-15', N'Thử việc'),
('NV006', 'PB_KT',   NULL,    N'Phạm Minh Tuấn',  '1999-07-22', N'Nam', '079099001234', '0903666777', N'Quận Phú Nhuận, TP.HCM',  '2026-08-15', N'Thử việc');

INSERT INTO dbo.QuyetDinhTuyenDung (SoQDTD, MaNV, MaPB, NgayKy, ThoiGianThuViecThang, LuongThuViec, DieuKhoanHaiBen, KetQuaThuViec) VALUES
('QDTD001', 'NV001', 'PB_KT',   '2024-03-01', 2, 9000000, N'Thử việc kỹ sư pha màu 2 tháng; tuân thủ quy trình kỹ thuật Sơn Đông Á.', N'Đạt'),
('QDTD002', 'NV002', 'PB_SX',   '2024-06-01', 2, 7500000, N'Thử việc tại phân xưởng sơn công nghiệp 2 tháng; đảm bảo ATLĐ.',         N'Đạt'),
('QDTD003', 'NV005', 'PB_TCKT', '2026-09-15', 2, 8000000, N'Thử việc kế toán tổng hợp 2 tháng; bảo mật chứng từ tài chính.',          N'Đang thử việc'),
('QDTD004', 'NV006', 'PB_KT',   '2026-08-15', 2, 8500000, N'Thử việc chuyên viên kiểm định chất lượng sơn 2 tháng.',                  N'Đạt');

INSERT INTO dbo.HopDongLaoDong (SoHD, MaNV, LoaiHopDong, NgayKy, NgayBatDau, NgayKetThuc, NoiDungHopDong, TrangThaiHD) VALUES
('HD2024_001', 'NV001', N'Dài hạn',  '2024-05-01', '2024-05-01', '2027-05-01', N'Hợp đồng lao động dài hạn 36 tháng; đóng đầy đủ BHXH, BHYT.', N'Đang hiệu lực'),
('HD2024_002', 'NV002', N'Dài hạn',  '2024-08-01', '2024-08-01', '2027-08-01', N'Hợp đồng lao động dài hạn 36 tháng tại Phòng Sản xuất.',      N'Đang hiệu lực'),
('HD2025_003', 'NV003', N'Dài hạn',  '2025-03-15', '2025-03-15', '2028-03-15', N'Hợp đồng lao động dài hạn 36 tháng tại Phòng Kế toán.',       N'Đang hiệu lực'),
('HD2026_003', 'NV004', N'Ngắn hạn', '2026-06-01', '2026-06-01', '2026-12-31', N'Hợp đồng lao động ngắn hạn 7 tháng theo mùa vụ sản xuất.',     N'Đang hiệu lực');

INSERT INTO dbo.HoSoBaoHiem (MaHoSoBH, SoHD, MaNV, SoSoBHXH, SoTheBHYT, TyLeBHXH, TyLeBHYT, MucDongHangThang) VALUES
('BH001', 'HD2024_001', 'NV001', '7924008821', 'DN4797924008821', 8.0, 1.5, 1470000),
('BH002', 'HD2024_002', 'NV002', '7925003319', 'DN4797925003319', 8.0, 1.5, 1155000),
('BH003', 'HD2025_003', 'NV003', '7925009912', 'DN4797925009912', 8.0, 1.5, 1102500);

INSERT INTO dbo.QuyetDinhChucVu (SoQDCV, MaNV, MaCV, NgayHieuLuc, PhuCapChucVu, NoiDungQuyetDinh) VALUES
('QDCV001', 'NV001', 'CV_TP', '2024-05-01', 3000000, N'Bổ nhiệm Trưởng phòng Kỹ thuật Pha màu'),
('QDCV002', 'NV002', 'CV_TT', '2024-08-01', 1500000, N'Bổ nhiệm Tổ trưởng phân xưởng sơn dung môi'),
('QDCV003', 'NV003', 'CV_KT', '2025-03-15',  800000, N'Bổ nhiệm Kế toán viên phụ trách tiền lương'),
('QDCV004', 'NV004', 'CV_NV', '2026-06-01',       0, N'Công nhân vận hành máy khuấy sơn');

INSERT INTO dbo.QuyetDinhLuong (SoQDL, MaNV, NgayHieuLuc, LuongCoBan, PhuCapLuong, PhuCapDocHai, GhiChu) VALUES
('QDL001', 'NV001', '2024-05-01', 14000000, 1500000,  800000, N'Lương chính thức Trưởng phòng Kỹ thuật'),
('QDL002', 'NV002', '2024-08-01', 11000000, 1000000, 1200000, N'Lương Tổ trưởng SX + phụ cấp độc hại dung môi sơn'),
('QDL003', 'NV003', '2025-03-15', 10500000, 1000000,       0, N'Lương chính thức Kế toán viên'),
('QDL004', 'NV004', '2026-06-01',  9000000,  500000, 1500000, N'Lương công nhân pha sơn + phụ cấp độc hại cao');

INSERT INTO dbo.KeHoachTuan (MaKeHoach, MaPB, Tuan, TuNgay, DenNgay, NguoiLap) VALUES
('KHT2026_W43', 'PB_SX', 43, '2026-10-19', '2026-10-25', N'Trần Quốc Bảo');

INSERT INTO dbo.ChiTietKeHoachTuan (MaKeHoach, MaNV, MaCa, NgayLamViec, GioPhuTroiDuKien, GhiChu) VALUES
('KHT2026_W43', 'NV002', 'CA1', '2026-10-20', 4.0, N'Tăng ca mẻ sơn chống thấm'),
('KHT2026_W43', 'NV004', 'CA1', '2026-10-20', 4.0, N'Phụ vận hành máy khuấy');

INSERT INTO dbo.BangChamCong (MaChamCong, MaNV, Thang, Nam, SoNgayCongChuan, SoNgayCongThucTe, NguoiChamCong) VALUES
('CC202610_01', 'NV001', 10, 2026, 26, 26, N'Ban chấm công'),
('CC202610_02', 'NV002', 10, 2026, 26, 26, N'Ban chấm công'),
('CC202610_03', 'NV003', 10, 2026, 26, 26, N'Ban chấm công'),
('CC202610_04', 'NV004', 10, 2026, 26, 26, N'Ban chấm công');

INSERT INTO dbo.PhieuPhuTroi (SoPhieuPT, NgayPhuTroi, SoGioPhuTroi, HinhThucPhuTroi, HeSoPhuTroi, NguoiLap) VALUES
('PPT2026_10_01', '2026-10-20', 4.0, N'Tăng ca',   1.5, N'Ban chấm công'),
('PPT2026_10_02', '2026-10-22', 6.0, N'Ngày nghỉ', 1.5, N'Ban chấm công'),
('PPT2026_10_03', '2026-10-24', 4.0, N'Tăng ca',   1.5, N'Ban chấm công');

INSERT INTO dbo.ChiTietPhuTroi (SoPhieuPT, MaNV, SoGioThamGia, DonGiaGioThuong, ThanhTienPhuTroi) VALUES
('PPT2026_10_01', 'NV002', 4.0, 52884.62, 317308),
('PPT2026_10_01', 'NV004', 4.0, 43269.23, 259615),
('PPT2026_10_02', 'NV001', 6.0, 67307.69, 605769),
('PPT2026_10_03', 'NV002', 4.0, 52884.62, 317308);

INSERT INTO dbo.QuyetDinhKhenThuong (SoQDKT, LoaiDoiTuong, MaNV, MaPB, NgayKy, LoaiKhenThuong, SoTienThuong, NguoiKy) VALUES
('QDKT2026_01', N'Cá nhân', 'NV002', NULL,    '2026-10-15', N'Sáng kiến cải tiến quy trình chống lắng sơn', 2000000,  N'Giám đốc'),
('QDKT2026_02', N'Tập thể', NULL,    'PB_SX', '2026-10-20', N'Thưởng ngày lễ & hoàn thành vượt sản lượng Q3', 10000000, N'Giám đốc');

INSERT INTO dbo.PhieuChiKhenThuong (SoPhieuChi, SoQDKT, NgayChi, SoTienChi, ThangTinhLuong, NamTinhLuong) VALUES
('PCKT001', 'QDKT2026_01', '2026-10-16', 2000000,  10, 2026),
('PCKT002', 'QDKT2026_02', '2026-10-20', 10000000, 10, 2026);

INSERT INTO dbo.QuyetDinhKyLuat (SoQDKL, LoaiDoiTuong, MaNV, MaPB, NgayKy, HinhThucKyLuat, SoTienPhat) VALUES
('QDKL2026_01', N'Cá nhân', 'NV004', NULL, '2026-10-18', N'Vi phạm quy định mang mặt nạ phòng độc khi pha dung môi', 500000);

INSERT INTO dbo.PhieuThuNopPhat (SoPhieuThu, SoQDKL, NgayThu, SoTienThuPhat, TruVaoBangLuong, ThangTruLuong, NamTruLuong) VALUES
('PTNP001', 'QDKL2026_01', '2026-10-18', 500000, 1, 10, 2026);
GO
