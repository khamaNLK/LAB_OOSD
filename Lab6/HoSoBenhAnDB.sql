/* =====================================================================
   BAI 5 - QUAN LY HO SO BENH AN BENH NHAN
   SQL Server 2019+ / T-SQL
   Script thiet lap CSDL, bang, rang buoc, view va stored procedure.
   Chay tren CSDL HoSoBenhAnDB. Cac bang duoc tao neu chua ton tai.
   ===================================================================== */
IF DB_ID(N'HoSoBenhAnDB') IS NULL
    CREATE DATABASE HoSoBenhAnDB;
GO
USE HoSoBenhAnDB;
GO

/* 1. DANH MUC VA NHAN SU */
IF OBJECT_ID(N'dbo.Khoa', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Khoa (
        MaKhoa INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_Khoa PRIMARY KEY,
        TenKhoa NVARCHAR(100) NOT NULL CONSTRAINT UQ_Khoa_Ten UNIQUE,
        MoTa NVARCHAR(300) NULL,
        TrangThai BIT NOT NULL CONSTRAINT DF_Khoa_TrangThai DEFAULT (1)
    );
END;
GO
IF OBJECT_ID(N'dbo.NhanVien', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.NhanVien (
        MaNhanVien INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_NhanVien PRIMARY KEY,
        HoTen NVARCHAR(150) NOT NULL,
        VaiTro VARCHAR(20) NOT NULL,
        DienThoai VARCHAR(20) NULL,
        Email NVARCHAR(150) NULL,
        MaKhoa INT NULL,
        TrangThai BIT NOT NULL CONSTRAINT DF_NhanVien_TrangThai DEFAULT (1),
        CONSTRAINT CK_NhanVien_VaiTro CHECK (VaiTro IN ('TIEP_NHAN','BAC_SI','KY_THUAT_VIEN','THU_NGAN','CSKH','HO_SO','QUAN_TRI')),
        CONSTRAINT FK_NhanVien_Khoa FOREIGN KEY (MaKhoa) REFERENCES dbo.Khoa(MaKhoa)
    );
END;
GO
IF OBJECT_ID(N'dbo.BacSi', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.BacSi (
        MaBacSi INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_BacSi PRIMARY KEY,
        MaNhanVien INT NOT NULL CONSTRAINT UQ_BacSi_NhanVien UNIQUE,
        MaKhoa INT NOT NULL,
        SoChungChi NVARCHAR(50) NULL,
        ChuyenKhoa NVARCHAR(100) NULL,
        CONSTRAINT FK_BacSi_NhanVien FOREIGN KEY (MaNhanVien) REFERENCES dbo.NhanVien(MaNhanVien),
        CONSTRAINT FK_BacSi_Khoa FOREIGN KEY (MaKhoa) REFERENCES dbo.Khoa(MaKhoa)
    );
END;
GO

/* 2. BENH NHAN, HO SO, LE PHI NAM */
IF OBJECT_ID(N'dbo.BenhNhan', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.BenhNhan (
        MaBenhNhan BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT PK_BenhNhan PRIMARY KEY,
        HoTen NVARCHAR(150) NOT NULL,
        DiaChi NVARCHAR(300) NOT NULL,
        NgaySinh DATE NOT NULL,
        DienThoai VARCHAR(20) NOT NULL,
        Email NVARCHAR(150) NULL,
        DuongDanAnh NVARCHAR(260) NULL,
        NgayDangKy DATETIME2(0) NOT NULL CONSTRAINT DF_BenhNhan_NgayDangKy DEFAULT (SYSDATETIME()),
        CONSTRAINT CK_BenhNhan_NgaySinh CHECK (NgaySinh <= CONVERT(date, GETDATE()))
    );
    CREATE INDEX IX_BenhNhan_DienThoai ON dbo.BenhNhan(DienThoai);
    CREATE INDEX IX_BenhNhan_HoTen ON dbo.BenhNhan(HoTen);
END;
GO
IF OBJECT_ID(N'dbo.HoSoBenhAn', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.HoSoBenhAn (
        MaHoSo BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT PK_HoSoBenhAn PRIMARY KEY,
        MaBenhNhan BIGINT NOT NULL CONSTRAINT UQ_HoSoBenhAn_BenhNhan UNIQUE,
        NgayCap DATETIME2(0) NOT NULL CONSTRAINT DF_HoSoBenhAn_NgayCap DEFAULT (SYSDATETIME()),
        TrangThai VARCHAR(20) NOT NULL CONSTRAINT DF_HoSoBenhAn_TrangThai DEFAULT ('DANG_HOAT_DONG'),
        LanCapNhatCuoi DATETIME2(0) NULL,
        GhiChu NVARCHAR(500) NULL,
        CONSTRAINT CK_HoSoBenhAn_TrangThai CHECK (TrangThai IN ('DANG_HOAT_DONG','TAM_KHOA','NGUNG_SU_DUNG')),
        CONSTRAINT FK_HoSoBenhAn_BenhNhan FOREIGN KEY (MaBenhNhan) REFERENCES dbo.BenhNhan(MaBenhNhan)
    );
END;
GO
IF OBJECT_ID(N'dbo.LePhiNam', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.LePhiNam (
        MaLePhi BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT PK_LePhiNam PRIMARY KEY,
        MaBenhNhan BIGINT NOT NULL,
        NamApDung INT NOT NULL,
        SoTien DECIMAL(12,2) NOT NULL,
        NgayDong DATETIME2(0) NOT NULL CONSTRAINT DF_LePhiNam_NgayDong DEFAULT (SYSDATETIME()),
        MaThuNgan INT NOT NULL,
        TrangThaiThanhToan VARCHAR(15) NOT NULL CONSTRAINT DF_LePhiNam_TrangThai DEFAULT ('DA_THU'),
        CONSTRAINT UQ_LePhiNam_BenhNhan_Nam UNIQUE (MaBenhNhan, NamApDung),
        CONSTRAINT CK_LePhiNam_Nam CHECK (NamApDung BETWEEN 2000 AND 2100),
        CONSTRAINT CK_LePhiNam_SoTien CHECK (SoTien > 0),
        CONSTRAINT CK_LePhiNam_TrangThai CHECK (TrangThaiThanhToan IN ('DA_THU','HUY')),
        CONSTRAINT FK_LePhiNam_BenhNhan FOREIGN KEY (MaBenhNhan) REFERENCES dbo.BenhNhan(MaBenhNhan),
        CONSTRAINT FK_LePhiNam_ThuNgan FOREIGN KEY (MaThuNgan) REFERENCES dbo.NhanVien(MaNhanVien)
    );
END;
GO

/* 3. LICH HEN VA PHIEU KHAM */
IF OBJECT_ID(N'dbo.LichHen', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.LichHen (
        MaLichHen BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT PK_LichHen PRIMARY KEY,
        MaBenhNhan BIGINT NOT NULL,
        MaHoSo BIGINT NOT NULL,
        MaNhanVienTiepNhan INT NULL,
        NgayGioHen DATETIME2(0) NOT NULL,
        LoaiKham VARCHAR(15) NOT NULL CONSTRAINT DF_LichHen_Loai DEFAULT ('DINH_KY'),
        TrangThai VARCHAR(20) NOT NULL CONSTRAINT DF_LichHen_TrangThai DEFAULT ('DA_XAC_NHAN'),
        GhiChu NVARCHAR(500) NULL,
        NgayTao DATETIME2(0) NOT NULL CONSTRAINT DF_LichHen_NgayTao DEFAULT (SYSDATETIME()),
        CONSTRAINT CK_LichHen_Loai CHECK (LoaiKham IN ('DINH_KY','TAI_KHAM')),
        CONSTRAINT CK_LichHen_TrangThai CHECK (TrangThai IN ('DA_XAC_NHAN','DANG_KHAM','DA_KHAM','HUY')),
        CONSTRAINT FK_LichHen_BenhNhan FOREIGN KEY (MaBenhNhan) REFERENCES dbo.BenhNhan(MaBenhNhan),
        CONSTRAINT FK_LichHen_HoSo FOREIGN KEY (MaHoSo) REFERENCES dbo.HoSoBenhAn(MaHoSo),
        CONSTRAINT FK_LichHen_NhanVien FOREIGN KEY (MaNhanVienTiepNhan) REFERENCES dbo.NhanVien(MaNhanVien)
    );
    CREATE INDEX IX_LichHen_BenhNhan_Ngay ON dbo.LichHen(MaBenhNhan, NgayGioHen);
END;
GO
IF OBJECT_ID(N'dbo.PhieuKham', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.PhieuKham (
        MaPhieuKham BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT PK_PhieuKham PRIMARY KEY,
        MaHoSo BIGINT NOT NULL,
        MaBacSi INT NOT NULL,
        MaKhoa INT NOT NULL,
        MaLichHen BIGINT NULL CONSTRAINT UQ_PhieuKham_LichHen UNIQUE,
        NgayGioKham DATETIME2(0) NOT NULL CONSTRAINT DF_PhieuKham_Ngay DEFAULT (SYSDATETIME()),
        LoaiKham VARCHAR(15) NOT NULL CONSTRAINT DF_PhieuKham_Loai DEFAULT ('DIEU_TRI'),
        TrieuChung NVARCHAR(1000) NULL,
        ChanDoan NVARCHAR(1000) NULL,
        HuongDieuTri NVARCHAR(1000) NULL,
        PhiKham DECIMAL(12,2) NOT NULL CONSTRAINT DF_PhieuKham_Phi DEFAULT (0),
        TrangThai VARCHAR(20) NOT NULL CONSTRAINT DF_PhieuKham_TrangThai DEFAULT ('DANG_KHAM'),
        NgayHoanTat DATETIME2(0) NULL,
        CONSTRAINT CK_PhieuKham_Loai CHECK (LoaiKham IN ('DIEU_TRI','DINH_KY')),
        CONSTRAINT CK_PhieuKham_TrangThai CHECK (TrangThai IN ('DANG_KHAM','CHO_XET_NGHIEM','CHO_THANH_TOAN','HOAN_TAT','HUY')),
        CONSTRAINT CK_PhieuKham_Phi CHECK (PhiKham >= 0),
        CONSTRAINT FK_PhieuKham_HoSo FOREIGN KEY (MaHoSo) REFERENCES dbo.HoSoBenhAn(MaHoSo),
        CONSTRAINT FK_PhieuKham_BacSi FOREIGN KEY (MaBacSi) REFERENCES dbo.BacSi(MaBacSi),
        CONSTRAINT FK_PhieuKham_Khoa FOREIGN KEY (MaKhoa) REFERENCES dbo.Khoa(MaKhoa),
        CONSTRAINT FK_PhieuKham_LichHen FOREIGN KEY (MaLichHen) REFERENCES dbo.LichHen(MaLichHen)
    );
    CREATE INDEX IX_PhieuKham_HoSo_Ngay ON dbo.PhieuKham(MaHoSo, NgayGioKham DESC);
END;
GO

/* 4. XET NGHIEM */
IF OBJECT_ID(N'dbo.DanhMucXetNghiem', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.DanhMucXetNghiem (
        MaLoaiXN INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_DanhMucXetNghiem PRIMARY KEY,
        TenLoaiXN NVARCHAR(150) NOT NULL CONSTRAINT UQ_DanhMucXetNghiem_Ten UNIQUE,
        DonGia DECIMAL(12,2) NOT NULL,
        DonViTinh NVARCHAR(30) NULL,
        ThoiGianDuKienGio INT NULL,
        TrangThai BIT NOT NULL CONSTRAINT DF_DanhMucXetNghiem_TrangThai DEFAULT (1),
        CONSTRAINT CK_DanhMucXetNghiem_Gia CHECK (DonGia >= 0),
        CONSTRAINT CK_DanhMucXetNghiem_TG CHECK (ThoiGianDuKienGio IS NULL OR ThoiGianDuKienGio >= 0)
    );
END;
GO
IF OBJECT_ID(N'dbo.PhieuXetNghiem', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.PhieuXetNghiem (
        MaPhieuXN BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT PK_PhieuXetNghiem PRIMARY KEY,
        MaPhieuKham BIGINT NOT NULL,
        MaLoaiXN INT NOT NULL,
        MaBacSiChiDinh INT NOT NULL,
        MaKyThuatVien INT NULL,
        NgayChiDinh DATETIME2(0) NOT NULL CONSTRAINT DF_PhieuXN_NgayChiDinh DEFAULT (SYSDATETIME()),
        NgayTraKetQua DATETIME2(0) NULL,
        KetQua NVARCHAR(2000) NULL,
        DonViKetQua NVARCHAR(50) NULL,
        KetLuan NVARCHAR(1000) NULL,
        DonGia DECIMAL(12,2) NOT NULL,
        TrangThai VARCHAR(20) NOT NULL CONSTRAINT DF_PhieuXN_TrangThai DEFAULT ('CHO_LAM'),
        CONSTRAINT CK_PhieuXN_Gia CHECK (DonGia >= 0),
        CONSTRAINT CK_PhieuXN_TrangThai CHECK (TrangThai IN ('CHO_LAM','DANG_LAM','CO_KET_QUA','HUY')),
        CONSTRAINT FK_PhieuXN_PhieuKham FOREIGN KEY (MaPhieuKham) REFERENCES dbo.PhieuKham(MaPhieuKham),
        CONSTRAINT FK_PhieuXN_Loai FOREIGN KEY (MaLoaiXN) REFERENCES dbo.DanhMucXetNghiem(MaLoaiXN),
        CONSTRAINT FK_PhieuXN_BacSi FOREIGN KEY (MaBacSiChiDinh) REFERENCES dbo.BacSi(MaBacSi),
        CONSTRAINT FK_PhieuXN_KTV FOREIGN KEY (MaKyThuatVien) REFERENCES dbo.NhanVien(MaNhanVien)
    );
    CREATE INDEX IX_PhieuXN_PhieuKham ON dbo.PhieuXetNghiem(MaPhieuKham, TrangThai);
END;
GO

/* 5. THUOC VA TOA THUOC */
IF OBJECT_ID(N'dbo.Thuoc', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.Thuoc (
        MaThuoc INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_Thuoc PRIMARY KEY,
        TenThuoc NVARCHAR(200) NOT NULL CONSTRAINT UQ_Thuoc_Ten UNIQUE,
        DonViTinh NVARCHAR(50) NOT NULL,
        DonGia DECIMAL(12,2) NOT NULL,
        SoLuongTon DECIMAL(12,2) NOT NULL CONSTRAINT DF_Thuoc_Ton DEFAULT (0),
        TrangThai BIT NOT NULL CONSTRAINT DF_Thuoc_TrangThai DEFAULT (1),
        CONSTRAINT CK_Thuoc_DonGia CHECK (DonGia >= 0),
        CONSTRAINT CK_Thuoc_Ton CHECK (SoLuongTon >= 0)
    );
END;
GO
IF OBJECT_ID(N'dbo.ToaThuoc', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.ToaThuoc (
        MaToa BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT PK_ToaThuoc PRIMARY KEY,
        MaPhieuKham BIGINT NOT NULL CONSTRAINT UQ_ToaThuoc_PhieuKham UNIQUE,
        MaBacSi INT NOT NULL,
        NgayKe DATETIME2(0) NOT NULL CONSTRAINT DF_ToaThuoc_Ngay DEFAULT (SYSDATETIME()),
        LoiDan NVARCHAR(1000) NULL,
        CONSTRAINT FK_ToaThuoc_PhieuKham FOREIGN KEY (MaPhieuKham) REFERENCES dbo.PhieuKham(MaPhieuKham),
        CONSTRAINT FK_ToaThuoc_BacSi FOREIGN KEY (MaBacSi) REFERENCES dbo.BacSi(MaBacSi)
    );
END;
GO
IF OBJECT_ID(N'dbo.ChiTietToaThuoc', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.ChiTietToaThuoc (
        MaChiTiet BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT PK_ChiTietToaThuoc PRIMARY KEY,
        MaToa BIGINT NOT NULL,
        MaThuoc INT NOT NULL,
        LieuDung NVARCHAR(200) NOT NULL,
        SoLanNgay INT NOT NULL,
        SoNgay INT NOT NULL,
        SoLuong DECIMAL(12,2) NOT NULL,
        DonGia DECIMAL(12,2) NOT NULL,
        HuongDan NVARCHAR(500) NULL,
        CONSTRAINT UQ_ChiTietToaThuoc_Toa_Thuoc UNIQUE (MaToa, MaThuoc),
        CONSTRAINT CK_CTT_SoLan CHECK (SoLanNgay > 0),
        CONSTRAINT CK_CTT_SoNgay CHECK (SoNgay > 0),
        CONSTRAINT CK_CTT_SoLuong CHECK (SoLuong > 0),
        CONSTRAINT CK_CTT_DonGia CHECK (DonGia >= 0),
        CONSTRAINT FK_CTT_Toa FOREIGN KEY (MaToa) REFERENCES dbo.ToaThuoc(MaToa),
        CONSTRAINT FK_CTT_Thuoc FOREIGN KEY (MaThuoc) REFERENCES dbo.Thuoc(MaThuoc)
    );
END;
GO

/* 6. HOA DON VA THANH TOAN */
IF OBJECT_ID(N'dbo.HoaDon', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.HoaDon (
        MaHoaDon BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT PK_HoaDon PRIMARY KEY,
        MaPhieuKham BIGINT NOT NULL CONSTRAINT UQ_HoaDon_PhieuKham UNIQUE,
        NgayLap DATETIME2(0) NOT NULL CONSTRAINT DF_HoaDon_Ngay DEFAULT (SYSDATETIME()),
        TongTien DECIMAL(12,2) NOT NULL,
        TrangThai VARCHAR(25) NOT NULL CONSTRAINT DF_HoaDon_TrangThai DEFAULT ('CHUA_THANH_TOAN'),
        GhiChu NVARCHAR(500) NULL,
        CONSTRAINT CK_HoaDon_TongTien CHECK (TongTien >= 0),
        CONSTRAINT CK_HoaDon_TrangThai CHECK (TrangThai IN ('CHUA_THANH_TOAN','THANH_TOAN_MOT_PHAN','DA_THANH_TOAN')),
        CONSTRAINT FK_HoaDon_PhieuKham FOREIGN KEY (MaPhieuKham) REFERENCES dbo.PhieuKham(MaPhieuKham)
    );
END;
GO
IF OBJECT_ID(N'dbo.ThanhToan', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.ThanhToan (
        MaThanhToan BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT PK_ThanhToan PRIMARY KEY,
        MaHoaDon BIGINT NOT NULL,
        NgayThanhToan DATETIME2(0) NOT NULL CONSTRAINT DF_ThanhToan_Ngay DEFAULT (SYSDATETIME()),
        SoTien DECIMAL(12,2) NOT NULL,
        PhuongThuc VARCHAR(20) NOT NULL,
        MaThuNgan INT NOT NULL,
        MaGiaoDich NVARCHAR(100) NULL,
        CONSTRAINT CK_ThanhToan_SoTien CHECK (SoTien > 0),
        CONSTRAINT CK_ThanhToan_PhuongThuc CHECK (PhuongThuc IN ('TIEN_MAT','THE','CHUYEN_KHOAN')),
        CONSTRAINT UQ_ThanhToan_MaGiaoDich UNIQUE (MaGiaoDich),
        CONSTRAINT FK_ThanhToan_HoaDon FOREIGN KEY (MaHoaDon) REFERENCES dbo.HoaDon(MaHoaDon),
        CONSTRAINT FK_ThanhToan_ThuNgan FOREIGN KEY (MaThuNgan) REFERENCES dbo.NhanVien(MaNhanVien)
    );
    CREATE INDEX IX_ThanhToan_HoaDon ON dbo.ThanhToan(MaHoaDon);
END;
GO

/* 7. TYPE DUNG GUI DANH SACH THUOC TU WINFORMS */
IF TYPE_ID(N'dbo.ToaThuocItemType') IS NULL
    EXEC(N'CREATE TYPE dbo.ToaThuocItemType AS TABLE (
        MaThuoc INT NOT NULL PRIMARY KEY,
        LieuDung NVARCHAR(200) NOT NULL,
        SoLanNgay INT NOT NULL,
        SoNgay INT NOT NULL,
        SoLuong DECIMAL(12,2) NOT NULL,
        HuongDan NVARCHAR(500) NULL
    )');
GO

/* 8. VIEW TRA CUU */
CREATE OR ALTER VIEW dbo.v_LichSuKham
AS
SELECT bn.MaBenhNhan, bn.HoTen, bn.NgaySinh, bn.DienThoai,
       hs.MaHoSo, pk.MaPhieuKham, pk.NgayGioKham, pk.LoaiKham,
       bs.MaBacSi, nv.HoTen AS TenBacSi, k.TenKhoa,
       pk.TrieuChung, pk.ChanDoan, pk.HuongDieuTri, pk.TrangThai,
       pk.PhiKham, pk.NgayHoanTat
FROM dbo.BenhNhan bn
JOIN dbo.HoSoBenhAn hs ON hs.MaBenhNhan = bn.MaBenhNhan
LEFT JOIN dbo.PhieuKham pk ON pk.MaHoSo = hs.MaHoSo
LEFT JOIN dbo.BacSi bs ON bs.MaBacSi = pk.MaBacSi
LEFT JOIN dbo.NhanVien nv ON nv.MaNhanVien = bs.MaNhanVien
LEFT JOIN dbo.Khoa k ON k.MaKhoa = pk.MaKhoa;
GO
CREATE OR ALTER VIEW dbo.v_XetNghiemTheoPhieuKham
AS
SELECT xn.MaPhieuXN, xn.MaPhieuKham, xn.NgayChiDinh, dm.TenLoaiXN,
       xn.KetQua, xn.DonViKetQua, xn.KetLuan, xn.TrangThai, xn.DonGia
FROM dbo.PhieuXetNghiem xn
JOIN dbo.DanhMucXetNghiem dm ON dm.MaLoaiXN = xn.MaLoaiXN;
GO
CREATE OR ALTER VIEW dbo.v_ToaThuocChiTiet
AS
SELECT tt.MaToa, tt.MaPhieuKham, tt.NgayKe, t.MaThuoc, t.TenThuoc,
       ct.LieuDung, ct.SoLanNgay, ct.SoNgay, ct.SoLuong, ct.DonGia,
       ct.SoLuong * ct.DonGia AS ThanhTien, ct.HuongDan
FROM dbo.ToaThuoc tt
JOIN dbo.ChiTietToaThuoc ct ON ct.MaToa = tt.MaToa
JOIN dbo.Thuoc t ON t.MaThuoc = ct.MaThuoc;
GO

/* 9. NGHIEP VU: DANG KY LAN DAU + THU LE PHI NAM + CAP HO SO (ATOMIC) */
CREATE OR ALTER PROCEDURE dbo.sp_DangKyLanDau
    @HoTen NVARCHAR(150),
    @DiaChi NVARCHAR(300),
    @NgaySinh DATE,
    @DienThoai VARCHAR(20),
    @Email NVARCHAR(150) = NULL,
    @DuongDanAnh NVARCHAR(260) = NULL,
    @NamApDung INT,
    @SoTien DECIMAL(12,2),
    @MaThuNgan INT,
    @MaBenhNhan BIGINT OUTPUT,
    @MaHoSo BIGINT OUTPUT
AS
BEGIN
    SET NOCOUNT ON; SET XACT_ABORT ON;
    IF @SoTien <= 0 THROW 51001, N'Lệ phí năm phải lớn hơn 0.', 1;
    IF @NgaySinh > CONVERT(date, GETDATE()) THROW 51002, N'Ngày sinh không hợp lệ.', 1;
    IF NOT EXISTS (SELECT 1 FROM dbo.NhanVien WHERE MaNhanVien=@MaThuNgan AND VaiTro IN ('THU_NGAN','TIEP_NHAN','QUAN_TRI') AND TrangThai=1)
        THROW 51003, N'Nhân viên thu lệ phí không hợp lệ.', 1;
    BEGIN TRY
        BEGIN TRANSACTION;
        INSERT dbo.BenhNhan(HoTen, DiaChi, NgaySinh, DienThoai, Email, DuongDanAnh)
        VALUES (@HoTen, @DiaChi, @NgaySinh, @DienThoai, @Email, @DuongDanAnh);
        SET @MaBenhNhan = CONVERT(bigint, SCOPE_IDENTITY());
        INSERT dbo.LePhiNam(MaBenhNhan, NamApDung, SoTien, MaThuNgan, TrangThaiThanhToan)
        VALUES (@MaBenhNhan, @NamApDung, @SoTien, @MaThuNgan, 'DA_THU');
        INSERT dbo.HoSoBenhAn(MaBenhNhan) VALUES (@MaBenhNhan);
        SET @MaHoSo = CONVERT(bigint, SCOPE_IDENTITY());
        COMMIT TRANSACTION;
        SELECT @MaBenhNhan AS MaBenhNhan, @MaHoSo AS MaHoSo,
               N'Đăng ký, thu lệ phí năm và cấp hồ sơ thành công.' AS ThongBao;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
        THROW;
    END CATCH
END;
GO

/* 10. NGHIEP VU: DONG LE PHI NAM CHO BENH NHAN CU */
CREATE OR ALTER PROCEDURE dbo.sp_ThuLePhiNam
    @MaBenhNhan BIGINT,
    @NamApDung INT,
    @SoTien DECIMAL(12,2),
    @MaThuNgan INT
AS
BEGIN
    SET NOCOUNT ON; SET XACT_ABORT ON;
    IF @SoTien <= 0 THROW 51010, N'Lệ phí năm phải lớn hơn 0.', 1;
    BEGIN TRY
        BEGIN TRANSACTION;
        IF NOT EXISTS (SELECT 1 FROM dbo.BenhNhan WITH (UPDLOCK, HOLDLOCK) WHERE MaBenhNhan=@MaBenhNhan)
            THROW 51011, N'Không tìm thấy bệnh nhân.', 1;
        IF NOT EXISTS (SELECT 1 FROM dbo.NhanVien WHERE MaNhanVien=@MaThuNgan AND VaiTro IN ('THU_NGAN','TIEP_NHAN','QUAN_TRI') AND TrangThai=1)
            THROW 51012, N'Nhân viên thu lệ phí không hợp lệ.', 1;
        INSERT dbo.LePhiNam(MaBenhNhan, NamApDung, SoTien, MaThuNgan, TrangThaiThanhToan)
        VALUES (@MaBenhNhan, @NamApDung, @SoTien, @MaThuNgan, 'DA_THU');
        COMMIT TRANSACTION;
        SELECT N'Thu lệ phí năm thành công.' AS ThongBao;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
        THROW;
    END CATCH
END;
GO

/* 11. NGHIEP VU: DAT LICH KHAM DINH KY - TOI DA 1 LAN/QUY */
CREATE OR ALTER PROCEDURE dbo.sp_DatLichKhamDinhKy
    @MaBenhNhan BIGINT,
    @NgayGioHen DATETIME2(0),
    @MaNhanVienTiepNhan INT = NULL,
    @GhiChu NVARCHAR(500) = NULL,
    @MaLichHen BIGINT OUTPUT
AS
BEGIN
    SET NOCOUNT ON; SET XACT_ABORT ON;
    IF @NgayGioHen <= SYSDATETIME() THROW 51020, N'Ngày giờ hẹn phải ở tương lai.', 1;
    BEGIN TRY
        BEGIN TRANSACTION;
        IF NOT EXISTS (SELECT 1 FROM dbo.BenhNhan WITH (UPDLOCK, HOLDLOCK) WHERE MaBenhNhan=@MaBenhNhan)
            THROW 51021, N'Không tìm thấy bệnh nhân.', 1;
        IF NOT EXISTS (SELECT 1 FROM dbo.HoSoBenhAn hs JOIN dbo.BenhNhan bn ON bn.MaBenhNhan=hs.MaBenhNhan WHERE bn.MaBenhNhan=@MaBenhNhan AND hs.TrangThai='DANG_HOAT_DONG')
            THROW 51022, N'Bệnh nhân chưa có hồ sơ bệnh án đang hoạt động.', 1;
        IF NOT EXISTS (SELECT 1 FROM dbo.LePhiNam WHERE MaBenhNhan=@MaBenhNhan AND NamApDung=YEAR(@NgayGioHen) AND TrangThaiThanhToan='DA_THU')
            THROW 51023, N'Chưa đóng lệ phí năm áp dụng cho lịch hẹn.', 1;
        DECLARE @DauQuy DATE = CONVERT(date, DATEADD(QUARTER, DATEDIFF(QUARTER, 0, @NgayGioHen), 0));
        DECLARE @DauQuyKe DATE = DATEADD(QUARTER, 1, @DauQuy);
        IF EXISTS (
            SELECT 1 FROM dbo.LichHen WITH (UPDLOCK, HOLDLOCK)
            WHERE MaBenhNhan=@MaBenhNhan AND LoaiKham='DINH_KY'
              AND TrangThai IN ('DA_XAC_NHAN','DANG_KHAM','DA_KHAM')
              AND NgayGioHen >= @DauQuy AND NgayGioHen < @DauQuyKe
        ) OR EXISTS (
            SELECT 1 FROM dbo.PhieuKham pk JOIN dbo.HoSoBenhAn hs ON hs.MaHoSo=pk.MaHoSo
            WHERE hs.MaBenhNhan=@MaBenhNhan AND pk.LoaiKham='DINH_KY'
              AND pk.TrangThai NOT IN ('HUY')
              AND pk.NgayGioKham >= @DauQuy AND pk.NgayGioKham < @DauQuyKe
        )
            THROW 51024, N'Bệnh nhân đã có lịch hoặc lần khám định kỳ trong quý này.', 1;
        INSERT dbo.LichHen(MaBenhNhan, MaHoSo, MaNhanVienTiepNhan, NgayGioHen, LoaiKham, TrangThai, GhiChu)
        SELECT @MaBenhNhan, hs.MaHoSo, @MaNhanVienTiepNhan, @NgayGioHen, 'DINH_KY', 'DA_XAC_NHAN', @GhiChu
        FROM dbo.HoSoBenhAn hs WHERE hs.MaBenhNhan=@MaBenhNhan;
        SET @MaLichHen = CONVERT(bigint, SCOPE_IDENTITY());
        COMMIT TRANSACTION;
        SELECT @MaLichHen AS MaLichHen, N'Đặt lịch khám định kỳ thành công; quyền lợi khám định kỳ không thu phí khám.' AS ThongBao;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
        THROW;
    END CATCH
END;
GO

/* 12. NGHIEP VU: TAO PHIEU KHAM */
CREATE OR ALTER PROCEDURE dbo.sp_TaoPhieuKham
    @MaHoSo BIGINT,
    @MaBacSi INT,
    @MaKhoa INT,
    @LoaiKham VARCHAR(15),
    @TrieuChung NVARCHAR(1000) = NULL,
    @PhiKham DECIMAL(12,2) = 0,
    @MaLichHen BIGINT = NULL,
    @MaPhieuKham BIGINT OUTPUT
AS
BEGIN
    SET NOCOUNT ON; SET XACT_ABORT ON;
    IF @LoaiKham NOT IN ('DIEU_TRI','DINH_KY') THROW 51030, N'Loại khám không hợp lệ.', 1;
    IF @PhiKham < 0 THROW 51031, N'Phí khám không hợp lệ.', 1;
    BEGIN TRY
        BEGIN TRANSACTION;
        IF NOT EXISTS (SELECT 1 FROM dbo.HoSoBenhAn WITH (UPDLOCK, HOLDLOCK) WHERE MaHoSo=@MaHoSo AND TrangThai='DANG_HOAT_DONG')
            THROW 51032, N'Hồ sơ không tồn tại hoặc đã ngừng hoạt động.', 1;
        IF NOT EXISTS (SELECT 1 FROM dbo.BacSi WHERE MaBacSi=@MaBacSi) THROW 51033, N'Bác sĩ không hợp lệ.', 1;
        IF NOT EXISTS (SELECT 1 FROM dbo.Khoa WHERE MaKhoa=@MaKhoa AND TrangThai=1) THROW 51034, N'Khoa điều trị không hợp lệ.', 1;
        IF @LoaiKham='DINH_KY'
        BEGIN
            IF @MaLichHen IS NULL THROW 51035, N'Khám định kỳ phải có lịch hẹn.', 1;
            IF NOT EXISTS (SELECT 1 FROM dbo.LichHen WHERE MaLichHen=@MaLichHen AND MaHoSo=@MaHoSo AND LoaiKham='DINH_KY' AND TrangThai='DA_XAC_NHAN')
                THROW 51036, N'Lịch hẹn không hợp lệ hoặc đã được tiếp nhận.', 1;
            SET @PhiKham = 0;
            UPDATE dbo.LichHen SET TrangThai='DANG_KHAM' WHERE MaLichHen=@MaLichHen;
        END
        ELSE IF @MaLichHen IS NOT NULL
            THROW 51037, N'Phiếu khám điều trị thông thường không được gắn lịch khám định kỳ.', 1;
        INSERT dbo.PhieuKham(MaHoSo, MaBacSi, MaKhoa, MaLichHen, LoaiKham, TrieuChung, PhiKham, TrangThai)
        VALUES (@MaHoSo, @MaBacSi, @MaKhoa, @MaLichHen, @LoaiKham, @TrieuChung, @PhiKham, 'DANG_KHAM');
        SET @MaPhieuKham=CONVERT(bigint, SCOPE_IDENTITY());
        COMMIT TRANSACTION;
        SELECT @MaPhieuKham AS MaPhieuKham, N'Tạo phiếu khám thành công.' AS ThongBao;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
        THROW;
    END CATCH
END;
GO

/* 13. NGHIEP VU: CHI DINH XET NGHIEM - MA PHIEU KHAM LA FK BAT BUOC */
CREATE OR ALTER PROCEDURE dbo.sp_ChiDinhXetNghiem
    @MaPhieuKham BIGINT,
    @MaLoaiXN INT,
    @MaBacSiChiDinh INT,
    @MaPhieuXN BIGINT OUTPUT
AS
BEGIN
    SET NOCOUNT ON; SET XACT_ABORT ON;
    BEGIN TRY
        BEGIN TRANSACTION;
        IF NOT EXISTS (SELECT 1 FROM dbo.PhieuKham WITH (UPDLOCK, HOLDLOCK) WHERE MaPhieuKham=@MaPhieuKham AND TrangThai IN ('DANG_KHAM','CHO_XET_NGHIEM'))
            THROW 51040, N'Phiếu khám không tồn tại hoặc đã đóng.', 1;
        IF NOT EXISTS (SELECT 1 FROM dbo.DanhMucXetNghiem WHERE MaLoaiXN=@MaLoaiXN AND TrangThai=1)
            THROW 51041, N'Loại xét nghiệm không hợp lệ.', 1;
        INSERT dbo.PhieuXetNghiem(MaPhieuKham, MaLoaiXN, MaBacSiChiDinh, DonGia, TrangThai)
        SELECT @MaPhieuKham, @MaLoaiXN, @MaBacSiChiDinh, DonGia, 'CHO_LAM'
        FROM dbo.DanhMucXetNghiem WHERE MaLoaiXN=@MaLoaiXN;
        SET @MaPhieuXN=CONVERT(bigint, SCOPE_IDENTITY());
        UPDATE dbo.PhieuKham SET TrangThai='CHO_XET_NGHIEM' WHERE MaPhieuKham=@MaPhieuKham;
        COMMIT TRANSACTION;
        SELECT @MaPhieuXN AS MaPhieuXN, N'Đã tạo chỉ định xét nghiệm gắn với phiếu khám.' AS ThongBao;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
        THROW;
    END CATCH
END;
GO
CREATE OR ALTER PROCEDURE dbo.sp_CapNhatKetQuaXetNghiem
    @MaPhieuXN BIGINT,
    @MaKyThuatVien INT,
    @KetQua NVARCHAR(2000),
    @DonViKetQua NVARCHAR(50) = NULL,
    @KetLuan NVARCHAR(1000) = NULL
AS
BEGIN
    SET NOCOUNT ON; SET XACT_ABORT ON;
    IF NULLIF(LTRIM(RTRIM(@KetQua)), N'') IS NULL THROW 51042, N'Kết quả xét nghiệm không được để trống.', 1;
    UPDATE dbo.PhieuXetNghiem
    SET MaKyThuatVien=@MaKyThuatVien, NgayTraKetQua=SYSDATETIME(), KetQua=@KetQua,
        DonViKetQua=@DonViKetQua, KetLuan=@KetLuan, TrangThai='CO_KET_QUA'
    WHERE MaPhieuXN=@MaPhieuXN AND TrangThai IN ('CHO_LAM','DANG_LAM');
    IF @@ROWCOUNT=0 THROW 51043, N'Không tìm thấy chỉ định đang chờ kết quả.', 1;
    SELECT N'Cập nhật kết quả xét nghiệm thành công.' AS ThongBao;
END;
GO

/* 14. NGHIEP VU: LAP TOA THUOC + TRU KHO TRONG CUNG TRANSACTION */
CREATE OR ALTER PROCEDURE dbo.sp_LapToaThuoc
    @MaPhieuKham BIGINT,
    @MaBacSi INT,
    @LoiDan NVARCHAR(1000) = NULL,
    @Items dbo.ToaThuocItemType READONLY,
    @MaToa BIGINT OUTPUT
AS
BEGIN
    SET NOCOUNT ON; SET XACT_ABORT ON;
    IF NOT EXISTS (SELECT 1 FROM @Items) THROW 51050, N'Toa thuốc phải có ít nhất một loại thuốc.', 1;
    IF EXISTS (SELECT 1 FROM @Items WHERE SoLanNgay<=0 OR SoNgay<=0 OR SoLuong<=0)
        THROW 51051, N'Liều dùng, số lần, số ngày và số lượng phải hợp lệ.', 1;
    BEGIN TRY
        BEGIN TRANSACTION;
        IF NOT EXISTS (SELECT 1 FROM dbo.PhieuKham WITH (UPDLOCK, HOLDLOCK) WHERE MaPhieuKham=@MaPhieuKham AND TrangThai IN ('DANG_KHAM','CHO_XET_NGHIEM'))
            THROW 51052, N'Phiếu khám không tồn tại hoặc đã hoàn tất.', 1;
        IF NOT EXISTS (SELECT 1 FROM dbo.BacSi WHERE MaBacSi=@MaBacSi) THROW 51053, N'Bác sĩ kê toa không hợp lệ.', 1;
        IF EXISTS (
            SELECT 1 FROM @Items i
            LEFT JOIN dbo.Thuoc t WITH (UPDLOCK, HOLDLOCK) ON t.MaThuoc=i.MaThuoc
            WHERE t.MaThuoc IS NULL OR t.TrangThai=0 OR t.SoLuongTon < i.SoLuong
        ) THROW 51054, N'Thuốc không tồn tại, đã ngừng dùng hoặc không đủ tồn kho.', 1;
        INSERT dbo.ToaThuoc(MaPhieuKham, MaBacSi, LoiDan) VALUES (@MaPhieuKham, @MaBacSi, @LoiDan);
        SET @MaToa=CONVERT(bigint, SCOPE_IDENTITY());
        INSERT dbo.ChiTietToaThuoc(MaToa, MaThuoc, LieuDung, SoLanNgay, SoNgay, SoLuong, DonGia, HuongDan)
        SELECT @MaToa, i.MaThuoc, i.LieuDung, i.SoLanNgay, i.SoNgay, i.SoLuong, t.DonGia, i.HuongDan
        FROM @Items i JOIN dbo.Thuoc t ON t.MaThuoc=i.MaThuoc;
        UPDATE t SET t.SoLuongTon=t.SoLuongTon-i.SoLuong
        FROM dbo.Thuoc t JOIN @Items i ON i.MaThuoc=t.MaThuoc;
        IF NOT EXISTS (SELECT 1 FROM dbo.PhieuXetNghiem WHERE MaPhieuKham=@MaPhieuKham AND TrangThai NOT IN ('CO_KET_QUA','HUY'))
            UPDATE dbo.PhieuKham SET TrangThai='CHO_THANH_TOAN' WHERE MaPhieuKham=@MaPhieuKham;
        COMMIT TRANSACTION;
        SELECT @MaToa AS MaToa, N'Lập toa thuốc thành công; chi tiết toa và tồn kho đã được cập nhật đồng thời.' AS ThongBao;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
        THROW;
    END CATCH
END;
GO

/* 15. NGHIEP VU: LAP HOA DON SAU KHI CO DU KET QUA */
CREATE OR ALTER PROCEDURE dbo.sp_LapHoaDon
    @MaPhieuKham BIGINT,
    @MaHoaDon BIGINT OUTPUT
AS
BEGIN
    SET NOCOUNT ON; SET XACT_ABORT ON;
    BEGIN TRY
        BEGIN TRANSACTION;
        IF NOT EXISTS (SELECT 1 FROM dbo.PhieuKham WITH (UPDLOCK, HOLDLOCK) WHERE MaPhieuKham=@MaPhieuKham AND TrangThai IN ('DANG_KHAM','CHO_XET_NGHIEM','CHO_THANH_TOAN'))
            THROW 51060, N'Phiếu khám không hợp lệ để lập hóa đơn.', 1;
        IF EXISTS (SELECT 1 FROM dbo.PhieuXetNghiem WHERE MaPhieuKham=@MaPhieuKham AND TrangThai NOT IN ('CO_KET_QUA','HUY'))
            THROW 51061, N'Không thể lập hóa đơn khi còn xét nghiệm chưa có kết quả.', 1;
        IF EXISTS (SELECT 1 FROM dbo.HoaDon WHERE MaPhieuKham=@MaPhieuKham)
            THROW 51062, N'Phiếu khám này đã có hóa đơn.', 1;
        DECLARE @TongTien DECIMAL(12,2);
        SELECT @TongTien = pk.PhiKham
            + ISNULL((SELECT SUM(xn.DonGia) FROM dbo.PhieuXetNghiem xn WHERE xn.MaPhieuKham=pk.MaPhieuKham AND xn.TrangThai<>'HUY'),0)
            + ISNULL((SELECT SUM(ct.SoLuong*ct.DonGia) FROM dbo.ToaThuoc tt JOIN dbo.ChiTietToaThuoc ct ON ct.MaToa=tt.MaToa WHERE tt.MaPhieuKham=pk.MaPhieuKham),0)
        FROM dbo.PhieuKham pk WHERE pk.MaPhieuKham=@MaPhieuKham;
        SET @TongTien=ISNULL(@TongTien,0);
        INSERT dbo.HoaDon(MaPhieuKham, TongTien, TrangThai)
        VALUES (@MaPhieuKham, @TongTien, CASE WHEN @TongTien=0 THEN 'DA_THANH_TOAN' ELSE 'CHUA_THANH_TOAN' END);
        SET @MaHoaDon=CONVERT(bigint, SCOPE_IDENTITY());
        UPDATE dbo.PhieuKham SET TrangThai='CHO_THANH_TOAN' WHERE MaPhieuKham=@MaPhieuKham;
        COMMIT TRANSACTION;
        SELECT @MaHoaDon AS MaHoaDon, @TongTien AS TongTien, N'Lập hóa đơn thành công.' AS ThongBao;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
        THROW;
    END CATCH
END;
GO

/* 16. NGHIEP VU: THANH TOAN (CHO PHEP TRA NHIEU LAN, KHONG THU QUA TONG) */
CREATE OR ALTER PROCEDURE dbo.sp_GhiNhanThanhToan
    @MaHoaDon BIGINT,
    @SoTien DECIMAL(12,2),
    @PhuongThuc VARCHAR(20),
    @MaThuNgan INT,
    @MaGiaoDich NVARCHAR(100) = NULL,
    @MaThanhToan BIGINT OUTPUT
AS
BEGIN
    SET NOCOUNT ON; SET XACT_ABORT ON;
    IF @SoTien<=0 THROW 51070, N'Số tiền thanh toán phải lớn hơn 0.', 1;
    IF @PhuongThuc NOT IN ('TIEN_MAT','THE','CHUYEN_KHOAN') THROW 51071, N'Phương thức thanh toán không hợp lệ.', 1;
    BEGIN TRY
        BEGIN TRANSACTION;
        DECLARE @TongTien DECIMAL(12,2), @DaTra DECIMAL(12,2);
        SELECT @TongTien=TongTien FROM dbo.HoaDon WITH (UPDLOCK, HOLDLOCK) WHERE MaHoaDon=@MaHoaDon;
        IF @TongTien IS NULL THROW 51072, N'Không tìm thấy hóa đơn.', 1;
        SELECT @DaTra=ISNULL(SUM(SoTien),0) FROM dbo.ThanhToan WHERE MaHoaDon=@MaHoaDon;
        IF @SoTien > @TongTien-@DaTra THROW 51073, N'Số tiền vượt quá số dư hóa đơn còn phải thanh toán.', 1;
        INSERT dbo.ThanhToan(MaHoaDon, SoTien, PhuongThuc, MaThuNgan, MaGiaoDich)
        VALUES (@MaHoaDon, @SoTien, @PhuongThuc, @MaThuNgan, @MaGiaoDich);
        SET @MaThanhToan=CONVERT(bigint, SCOPE_IDENTITY());
        SET @DaTra=@DaTra+@SoTien;
        UPDATE dbo.HoaDon SET TrangThai=CASE WHEN @DaTra>=@TongTien THEN 'DA_THANH_TOAN' ELSE 'THANH_TOAN_MOT_PHAN' END
        WHERE MaHoaDon=@MaHoaDon;
        COMMIT TRANSACTION;
        SELECT @MaThanhToan AS MaThanhToan, @DaTra AS DaThanhToan, @TongTien-@DaTra AS ConNo,
               N'Ghi nhận thanh toán thành công.' AS ThongBao;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT>0 ROLLBACK TRANSACTION;
        THROW;
    END CATCH
END;
GO

/* 17. NGHIEP VU: CHI HOAN TAT DOT DIEU TRI SAU KHI THANH TOAN */
CREATE OR ALTER PROCEDURE dbo.sp_HoanTatDotDieuTri
    @MaPhieuKham BIGINT
AS
BEGIN
    SET NOCOUNT ON; SET XACT_ABORT ON;
    BEGIN TRY
        BEGIN TRANSACTION;
        DECLARE @MaHoaDon BIGINT, @TongTien DECIMAL(12,2), @DaTra DECIMAL(12,2);
        SELECT @MaHoaDon=hd.MaHoaDon, @TongTien=hd.TongTien
        FROM dbo.HoaDon hd WITH (UPDLOCK, HOLDLOCK)
        WHERE hd.MaPhieuKham=@MaPhieuKham;
        IF @MaHoaDon IS NULL THROW 51080, N'Chưa lập hóa đơn cho phiếu khám.', 1;
        SELECT @DaTra=ISNULL(SUM(SoTien),0) FROM dbo.ThanhToan WHERE MaHoaDon=@MaHoaDon;
        IF @TongTien>0 AND @DaTra<@TongTien THROW 51081, N'Chưa thể hoàn tất: viện phí chưa được thanh toán đủ.', 1;
        IF EXISTS (SELECT 1 FROM dbo.PhieuXetNghiem WHERE MaPhieuKham=@MaPhieuKham AND TrangThai NOT IN ('CO_KET_QUA','HUY'))
            THROW 51082, N'Chưa thể hoàn tất: còn xét nghiệm chưa có kết quả.', 1;
        UPDATE dbo.PhieuKham SET TrangThai='HOAN_TAT', NgayHoanTat=SYSDATETIME()
        WHERE MaPhieuKham=@MaPhieuKham AND TrangThai<>'HUY';
        IF @@ROWCOUNT=0 THROW 51083, N'Phiếu khám không tồn tại hoặc đã hủy.', 1;
        UPDATE hs SET LanCapNhatCuoi=SYSDATETIME()
        FROM dbo.HoSoBenhAn hs JOIN dbo.PhieuKham pk ON pk.MaHoSo=hs.MaHoSo
        WHERE pk.MaPhieuKham=@MaPhieuKham;
        UPDATE lh SET TrangThai='DA_KHAM'
        FROM dbo.LichHen lh JOIN dbo.PhieuKham pk ON pk.MaLichHen=lh.MaLichHen
        WHERE pk.MaPhieuKham=@MaPhieuKham AND lh.TrangThai='DANG_KHAM';
        COMMIT TRANSACTION;
        SELECT N'Đợt điều trị đã hoàn tất; lịch sử được cập nhật vào hồ sơ bệnh án.' AS ThongBao;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT>0 ROLLBACK TRANSACTION;
        THROW;
    END CATCH
END;
GO

/* 18. NGHIEP VU: TRA CUU HO SO VA LICH SU (NHIEU RESULT SET) */
CREATE OR ALTER PROCEDURE dbo.sp_TraCuuLichSuBenhAn
    @MaHoSo BIGINT = NULL,
    @HoTen NVARCHAR(150) = NULL,
    @DienThoai VARCHAR(20) = NULL
AS
BEGIN
    SET NOCOUNT ON;
    IF @MaHoSo IS NULL AND NULLIF(LTRIM(RTRIM(@HoTen)),N'') IS NULL AND NULLIF(LTRIM(RTRIM(@DienThoai)),'') IS NULL
        THROW 51090, N'Nhập mã hồ sơ, họ tên hoặc số điện thoại để tra cứu.', 1;
    SELECT bn.MaBenhNhan, bn.HoTen, bn.NgaySinh, bn.DiaChi, bn.DienThoai, hs.MaHoSo, hs.NgayCap, hs.TrangThai AS TrangThaiHoSo
    FROM dbo.BenhNhan bn JOIN dbo.HoSoBenhAn hs ON hs.MaBenhNhan=bn.MaBenhNhan
    WHERE (@MaHoSo IS NULL OR hs.MaHoSo=@MaHoSo)
      AND (@HoTen IS NULL OR bn.HoTen LIKE N'%'+@HoTen+N'%')
      AND (@DienThoai IS NULL OR bn.DienThoai=@DienThoai);
    SELECT * FROM dbo.v_LichSuKham
    WHERE (@MaHoSo IS NULL OR MaHoSo=@MaHoSo)
      AND (@HoTen IS NULL OR HoTen LIKE N'%'+@HoTen+N'%')
      AND (@DienThoai IS NULL OR DienThoai=@DienThoai)
    ORDER BY NgayGioKham DESC;
END;
GO

/* 19. DU LIEU DANH MUC MAU - CO THE DOI THEO QUY DINH THUC TE */
IF NOT EXISTS (SELECT 1 FROM dbo.Khoa WHERE TenKhoa=N'Nội tổng quát')
    INSERT dbo.Khoa(TenKhoa, MoTa) VALUES (N'Nội tổng quát', N'Khám và điều trị nội khoa');
IF NOT EXISTS (SELECT 1 FROM dbo.Khoa WHERE TenKhoa=N'Xét nghiệm')
    INSERT dbo.Khoa(TenKhoa, MoTa) VALUES (N'Xét nghiệm', N'Thực hiện xét nghiệm cận lâm sàng');
IF NOT EXISTS (SELECT 1 FROM dbo.NhanVien WHERE Email=N'thunghan.demo@benhvien.local')
    INSERT dbo.NhanVien(HoTen,VaiTro,DienThoai,Email,MaKhoa)
    SELECT N'Nhân viên thu ngân mẫu','THU_NGAN','0900000001',N'thunghan.demo@benhvien.local',MaKhoa FROM dbo.Khoa WHERE TenKhoa=N'Nội tổng quát';
IF NOT EXISTS (SELECT 1 FROM dbo.NhanVien WHERE Email=N'bacsi.demo@benhvien.local')
    INSERT dbo.NhanVien(HoTen,VaiTro,DienThoai,Email,MaKhoa)
    SELECT N'Bác sĩ mẫu','BAC_SI','0900000002',N'bacsi.demo@benhvien.local',MaKhoa FROM dbo.Khoa WHERE TenKhoa=N'Nội tổng quát';
IF NOT EXISTS (SELECT 1 FROM dbo.BacSi bs JOIN dbo.NhanVien nv ON nv.MaNhanVien=bs.MaNhanVien WHERE nv.Email=N'bacsi.demo@benhvien.local')
    INSERT dbo.BacSi(MaNhanVien,MaKhoa,SoChungChi,ChuyenKhoa)
    SELECT nv.MaNhanVien,k.MaKhoa,N'MAU-001',N'Nội tổng quát' FROM dbo.NhanVien nv CROSS JOIN dbo.Khoa k
    WHERE nv.Email=N'bacsi.demo@benhvien.local' AND k.TenKhoa=N'Nội tổng quát';
IF NOT EXISTS (SELECT 1 FROM dbo.DanhMucXetNghiem WHERE TenLoaiXN=N'Công thức máu')
    INSERT dbo.DanhMucXetNghiem(TenLoaiXN,DonGia,DonViTinh,ThoiGianDuKienGio) VALUES (N'Công thức máu',120000,N'lần',4);
IF NOT EXISTS (SELECT 1 FROM dbo.DanhMucXetNghiem WHERE TenLoaiXN=N'Đường huyết')
    INSERT dbo.DanhMucXetNghiem(TenLoaiXN,DonGia,DonViTinh,ThoiGianDuKienGio) VALUES (N'Đường huyết',80000,N'lần',2);
IF NOT EXISTS (SELECT 1 FROM dbo.Thuoc WHERE TenThuoc=N'Paracetamol 500 mg')
    INSERT dbo.Thuoc(TenThuoc,DonViTinh,DonGia,SoLuongTon) VALUES (N'Paracetamol 500 mg',N'viên',1500,500);
IF NOT EXISTS (SELECT 1 FROM dbo.Thuoc WHERE TenThuoc=N'Vitamin C 500 mg')
    INSERT dbo.Thuoc(TenThuoc,DonViTinh,DonGia,SoLuongTon) VALUES (N'Vitamin C 500 mg',N'viên',1000,300);
GO

/* 20. CAC LENH KIEM THU MAU (MO COMMENT KHI MUON CHAY)
DECLARE @MaBN BIGINT, @MaHS BIGINT, @MaThuNganTest BIGINT;
SELECT TOP (1) @MaThuNganTest=MaNhanVien FROM dbo.NhanVien WHERE VaiTro='THU_NGAN' ORDER BY MaNhanVien;
EXEC dbo.sp_DangKyLanDau
  @HoTen=N'Nguyễn Văn An', @DiaChi=N'Địa chỉ mẫu', @NgaySinh='1998-05-12',
  @DienThoai='0901234567', @Email=NULL, @DuongDanAnh=NULL,
  @NamApDung=YEAR(GETDATE()), @SoTien=500000,
  @MaThuNgan=@MaThuNganTest,
  @MaBenhNhan=@MaBN OUTPUT, @MaHoSo=@MaHS OUTPUT;
SELECT @MaBN AS MaBenhNhan, @MaHS AS MaHoSo;

-- Sau khi có mã bác sĩ và mã khoa mẫu, tạo phiếu khám, chỉ định xét nghiệm,
-- ghi kết quả, tạo TVP thuốc, lập toa, lập hóa đơn, thanh toán và hoàn tất.
-- Cần thay các biến đầu vào theo ID sinh ra trong chính CSDL đang chạy.
*/
GO
