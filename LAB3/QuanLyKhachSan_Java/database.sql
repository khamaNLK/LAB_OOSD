-- 1. TẠO CƠ SỞ DỮ LIỆU
IF DB_ID('QuanLyKhachSan_Lab03') IS NULL
BEGIN
    CREATE DATABASE QuanLyKhachSan_Lab03;
END
GO

USE QuanLyKhachSan_Lab03;
GO

-- 2. TẠO CÁC BẢNG CỐT LÕI
IF OBJECT_ID('KhuVuc', 'U') IS NULL
CREATE TABLE KhuVuc ( 
    MaKhuVuc VARCHAR(20) NOT NULL PRIMARY KEY, 
    TenKhuVuc NVARCHAR(100) NOT NULL UNIQUE 
); 

IF OBJECT_ID('Phong', 'U') IS NULL
CREATE TABLE Phong ( 
    SoPhong VARCHAR(20) NOT NULL PRIMARY KEY, 
    MaKhuVuc VARCHAR(20) NOT NULL, 
    SoNguoiToiDa INT NOT NULL CHECK(SoNguoiToiDa > 0), 
    DonGiaNgay DECIMAL(18,2) NOT NULL CHECK(DonGiaNgay >= 0),  
    TrangThai NVARCHAR(30) NOT NULL DEFAULT N'Trống', 
    CONSTRAINT CK_Phong_TrangThai CHECK(TrangThai IN (N'Trống', N'Đã đặt', N'Đang ở', N'Bảo trì')), 
    CONSTRAINT FK_Phong_KhuVuc FOREIGN KEY(MaKhuVuc) REFERENCES KhuVuc(MaKhuVuc) 
); 

IF OBJECT_ID('TienNghi', 'U') IS NULL
CREATE TABLE TienNghi ( 
    MaTienNghi VARCHAR(30) NOT NULL PRIMARY KEY, 
    TenTienNghi NVARCHAR(100) NOT NULL,
    TinhTrangHienTai NVARCHAR(100) NULL
); 

-- Bảng này đáp ứng quy tắc: "1 thiết bị / 1 phòng / 1 ngày"
IF OBJECT_ID('PhieuLapDat', 'U') IS NULL
CREATE TABLE PhieuLapDat ( 
    SoPhieuLapDat VARCHAR(30) NOT NULL PRIMARY KEY, 
    MaTienNghi VARCHAR(30) NOT NULL, 
    SoPhong VARCHAR(20) NOT NULL, 
    NgayLap DATE NOT NULL, 
    CONSTRAINT UQ_PhieuLapDat_ThietBi_Ngay UNIQUE(MaTienNghi, NgayLap),  
    CONSTRAINT FK_LapDat_TienNghi FOREIGN KEY(MaTienNghi) REFERENCES TienNghi(MaTienNghi), 
    CONSTRAINT FK_LapDat_Phong FOREIGN KEY(SoPhong) REFERENCES Phong(SoPhong)
); 

IF OBJECT_ID('KhachHang', 'U') IS NULL
CREATE TABLE KhachHang ( 
    MaKhach VARCHAR(20) NOT NULL PRIMARY KEY, 
    HoTen NVARCHAR(120) NOT NULL, 
    SoCMND VARCHAR(30) NOT NULL UNIQUE 
); 

IF OBJECT_ID('NhanVien', 'U') IS NULL
CREATE TABLE NhanVien ( 
    MaNV VARCHAR(20) NOT NULL PRIMARY KEY, 
    HoTen NVARCHAR(120) NOT NULL
); 

IF OBJECT_ID('PhieuDatPhong', 'U') IS NULL
CREATE TABLE PhieuDatPhong ( 
    SoPhieuDat VARCHAR(30) NOT NULL PRIMARY KEY, 
    MaKhach VARCHAR(20) NOT NULL, 
    MaNV VARCHAR(20) NULL,
    NgayLap DATETIME NOT NULL DEFAULT GETDATE(),
    NgayNhan DATE NOT NULL, 
    NgayTraDuKien DATE NOT NULL, 
    NgayNhanThucTe DATETIME NULL,
    NgayTraThucTe DATETIME NULL,
    TienCoc DECIMAL(18,2) NOT NULL DEFAULT 0 CHECK(TienCoc >= 0),  
    TrangThai NVARCHAR(30) NOT NULL DEFAULT N'Đã đặt', 
    CONSTRAINT CK_PhieuDat_Ngay CHECK(NgayTraDuKien >= NgayNhan), 
    CONSTRAINT FK_PhieuDat_Khach FOREIGN KEY(MaKhach) REFERENCES KhachHang(MaKhach),
    CONSTRAINT FK_PhieuDat_NV FOREIGN KEY(MaNV) REFERENCES NhanVien(MaNV)
); 

IF OBJECT_ID('ChiTietDatPhong', 'U') IS NULL
CREATE TABLE ChiTietDatPhong ( 
    SoPhieuDat VARCHAR(30) NOT NULL, 
    SoPhong VARCHAR(20) NOT NULL, 
    SoNguoi INT NOT NULL CHECK(SoNguoi > 0), 
    PRIMARY KEY(SoPhieuDat, SoPhong), 
    CONSTRAINT FK_CTDat_Phieu FOREIGN KEY(SoPhieuDat) REFERENCES PhieuDatPhong(SoPhieuDat), 
    CONSTRAINT FK_CTDat_Phong FOREIGN KEY(SoPhong) REFERENCES Phong(SoPhong) 
); 

IF OBJECT_ID('DichVu', 'U') IS NULL
CREATE TABLE DichVu ( 
    MaDV VARCHAR(20) NOT NULL PRIMARY KEY, 
    TenDV NVARCHAR(120) NOT NULL, 
    DonGia DECIMAL(18,2) NOT NULL CHECK(DonGia >= 0) 
); 

-- Hỗ trợ cộng dồn dịch vụ cùng ngày & tương thích với Java Swing
IF OBJECT_ID('ChiTietSuDungDV', 'U') IS NULL
CREATE TABLE ChiTietSuDungDV ( 
    ID INT IDENTITY(1,1) PRIMARY KEY,
    SoPhieuDat VARCHAR(30) NOT NULL, 
    MaDV VARCHAR(20) NOT NULL, 
    SoLuong INT NOT NULL CHECK(SoLuong > 0), 
    DonGia DECIMAL(18,2) NOT NULL, 
    ThanhTien AS (CONVERT(DECIMAL(18,2), SoLuong * DonGia)) PERSISTED,
    NgaySuDung DATE NOT NULL DEFAULT GETDATE(),
    CONSTRAINT FK_CTSDDV_Phieu FOREIGN KEY(SoPhieuDat) REFERENCES PhieuDatPhong(SoPhieuDat),
    CONSTRAINT FK_CTSDDV_DV FOREIGN KEY(MaDV) REFERENCES DichVu(MaDV)
); 

-- Thêm bảng Phiếu Đền Bù theo đúng yêu cầu đề bài
IF OBJECT_ID('PhieuDenBu', 'U') IS NULL
CREATE TABLE PhieuDenBu (
    MaPhieuDB VARCHAR(30) NOT NULL PRIMARY KEY,
    SoPhieuDat VARCHAR(30) NOT NULL,
    MaTienNghi VARCHAR(30) NOT NULL,
    SoLuongLoi INT NOT NULL CHECK(SoLuongLoi > 0),
    TongTienPhat DECIMAL(18,2) NOT NULL,
    CONSTRAINT FK_DenBu_PhieuDat FOREIGN KEY(SoPhieuDat) REFERENCES PhieuDatPhong(SoPhieuDat),
    CONSTRAINT FK_DenBu_TienNghi FOREIGN KEY(MaTienNghi) REFERENCES TienNghi(MaTienNghi)
);

IF OBJECT_ID('HoaDon', 'U') IS NULL
CREATE TABLE HoaDon ( 
    SoHoaDon VARCHAR(30) NOT NULL PRIMARY KEY, 
    SoPhieuDat VARCHAR(30) NOT NULL UNIQUE, 
    NhanVienLap VARCHAR(20) NULL,
    NgayLap DATETIME NOT NULL DEFAULT GETDATE(),
    TienPhong DECIMAL(18,2) NOT NULL CHECK(TienPhong >= 0), 
    TienDichVu DECIMAL(18,2) NOT NULL CHECK(TienDichVu >= 0),
    TienDenBu DECIMAL(18,2) NOT NULL DEFAULT 0 CHECK(TienDenBu >= 0),
    TrangThai NVARCHAR(30) NOT NULL DEFAULT N'Chưa thanh toán',  
    CONSTRAINT FK_HoaDon_PhieuDat FOREIGN KEY(SoPhieuDat) REFERENCES PhieuDatPhong(SoPhieuDat),
    CONSTRAINT FK_HoaDon_NV FOREIGN KEY(NhanVienLap) REFERENCES NhanVien(MaNV)
); 

IF OBJECT_ID('ThanhToan', 'U') IS NULL
CREATE TABLE ThanhToan ( 
    MaThanhToan VARCHAR(30) NOT NULL PRIMARY KEY, 
    SoHoaDon VARCHAR(30) NOT NULL, 
    HinhThuc NVARCHAR(30) NOT NULL, 
    SoTien DECIMAL(18,2) NOT NULL CHECK(SoTien > 0), 
    CONSTRAINT CK_ThanhToan_HinhThuc CHECK(HinhThuc IN (N'Tiền mặt', N'Chuyển khoản', N'Thẻ', N'Ví điện tử')), 
    CONSTRAINT FK_ThanhToan_HoaDon FOREIGN KEY(SoHoaDon) REFERENCES HoaDon(SoHoaDon)
); 
GO

-- 3. NẠP DỮ LIỆU MẪU
IF NOT EXISTS (SELECT 1 FROM NhanVien WHERE MaNV = 'NV01')
    INSERT INTO NhanVien (MaNV, HoTen) VALUES ('NV01', N'Lễ Tân 1');

IF NOT EXISTS (SELECT 1 FROM KhuVuc WHERE MaKhuVuc = 'A')
    INSERT INTO KhuVuc (MaKhuVuc, TenKhuVuc) VALUES ('A', N'Khu A');
IF NOT EXISTS (SELECT 1 FROM KhuVuc WHERE MaKhuVuc = 'B')
    INSERT INTO KhuVuc (MaKhuVuc, TenKhuVuc) VALUES ('B', N'Khu B');

IF NOT EXISTS (SELECT 1 FROM Phong WHERE SoPhong = 'A101')
    INSERT INTO Phong (SoPhong, MaKhuVuc, SoNguoiToiDa, DonGiaNgay, TrangThai) VALUES ('A101', 'A', 2, 600000, N'Trống');
IF NOT EXISTS (SELECT 1 FROM Phong WHERE SoPhong = 'A102')
    INSERT INTO Phong (SoPhong, MaKhuVuc, SoNguoiToiDa, DonGiaNgay, TrangThai) VALUES ('A102', 'A', 3, 800000, N'Trống');
IF NOT EXISTS (SELECT 1 FROM Phong WHERE SoPhong = 'B201')
    INSERT INTO Phong (SoPhong, MaKhuVuc, SoNguoiToiDa, DonGiaNgay, TrangThai) VALUES ('B201', 'B', 4, 1200000, N'Trống');

IF NOT EXISTS (SELECT 1 FROM KhachHang WHERE MaKhach = 'KH01')
    INSERT INTO KhachHang (MaKhach, HoTen, SoCMND) VALUES ('KH01', N'Nguyễn Văn A', '0123456789');

IF NOT EXISTS (SELECT 1 FROM DichVu WHERE MaDV = 'DV01')
    INSERT INTO DichVu (MaDV, TenDV, DonGia) VALUES ('DV01', N'Cà phê', 25000);
GO

-- 4. TẠO STORED PROCEDURE XỬ LÝ NGHIỆP VỤ ĐẶT PHÒNG
CREATE OR ALTER PROCEDURE sp_TaoDatPhong
    @SoPhieuDat VARCHAR(30),
    @MaKhach VARCHAR(20),
    @MaNV VARCHAR(20),
    @NgayNhan DATE,
    @NgayTra DATE,
    @TienCoc DECIMAL(18,2),
    @SoPhong VARCHAR(20),
    @SoNguoi INT
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;
    BEGIN TRY
        BEGIN TRANSACTION;
        
        -- 1. Kiểm tra sức chứa
        DECLARE @Max INT;
        SELECT @Max = SoNguoiToiDa FROM Phong WHERE SoPhong = @SoPhong;
        IF @SoNguoi > @Max
        BEGIN
            THROW 50001, N'Số người vượt quá sức chứa của phòng.', 1;
        END

        -- 2. Kiểm tra trùng lịch (Validation)
        IF EXISTS (
            SELECT 1 FROM ChiTietDatPhong c 
            JOIN PhieuDatPhong d ON c.SoPhieuDat = d.SoPhieuDat 
            WHERE c.SoPhong = @SoPhong AND d.TrangThai IN (N'Đã đặt', N'Đang ở') 
            AND @NgayNhan <= d.NgayTraDuKien AND @NgayTra >= d.NgayNhan
        )
        BEGIN
            THROW 50002, N'Phòng đã bị trùng lịch đặt.', 1;
        END

        -- 3. Thêm phiếu đặt (Đã bổ sung MaNV)
        INSERT INTO PhieuDatPhong (SoPhieuDat, MaKhach, MaNV, NgayNhan, NgayTraDuKien, TienCoc, TrangThai)
        VALUES (@SoPhieuDat, @MaKhach, @MaNV, @NgayNhan, @NgayTra, @TienCoc, N'Đã đặt');

        -- 4. Thêm chi tiết đặt
        INSERT INTO ChiTietDatPhong (SoPhieuDat, SoPhong, SoNguoi)
        VALUES (@SoPhieuDat, @SoPhong, @SoNguoi);

        -- 5. Cập nhật trạng thái phòng
        UPDATE Phong SET TrangThai = N'Đã đặt' WHERE SoPhong = @SoPhong;

        COMMIT TRANSACTION;
        SELECT @SoPhieuDat AS SoPhieuDat, N'Đã lập phiếu đặt phòng thành công.' AS ThongBao;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
        SELECT ERROR_NUMBER() AS MaLoi, ERROR_MESSAGE() AS ThongBao;
    END CATCH
END;
GO
USE QuanLyKhachSan_Lab03;
GO

-- Thêm cột LoaiPhong vào bảng Phong
ALTER TABLE Phong 
ADD LoaiPhong NVARCHAR(50) NOT NULL DEFAULT N'Phòng Đơn';
GO