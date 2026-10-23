using System;
using System.Data;
using QuanLyNhanSuDongA.Data;

namespace QuanLyNhanSuDongA.Services
{
    public class DanhMucService
    {
        public DataTable LayPhongBan() =>
            Db.Query(@"SELECT pb.MaPB, pb.TenPB, pb.DienThoai, pb.NhuCauTuyenDung,
                              COUNT(nv.MaNV) AS SoNhanVien
                       FROM dbo.PhongBan pb
                       LEFT JOIN dbo.NhanVien nv ON nv.MaPB = pb.MaPB
                       GROUP BY pb.MaPB, pb.TenPB, pb.DienThoai, pb.NhuCauTuyenDung
                       ORDER BY pb.MaPB");

        public DataTable LayPhongBanCoNhuCau() =>
            Db.Query("SELECT MaPB, MaPB + N' - ' + TenPB AS HienThi FROM dbo.PhongBan WHERE NhuCauTuyenDung = 1 ORDER BY MaPB");

        public DataTable LayChucVu() =>
            Db.Query("SELECT MaCV, TenCV, PhuCapMacDinh, MaCV + N' - ' + TenCV AS HienThi FROM dbo.ChucVu ORDER BY MaCV");

        public DataTable LayCaLamViec() =>
            Db.Query("SELECT MaCa, TenCa, GioBatDau, GioKetThuc, SoGioChuan, MaCa + N' - ' + TenCa AS HienThi FROM dbo.CaLamViec ORDER BY MaCa");

        public DataTable LayTatCaNhanVien() =>
            Db.Query(@"SELECT nv.MaNV, nv.HoTen, pb.TenPB, ISNULL(cv.TenCV, N'(Chưa bổ nhiệm)') AS TenCV,
                              nv.GioiTinh, nv.NgaySinh, nv.CCCD, nv.DienThoai, nv.NgayVaoLam, nv.TrangThaiLamViec
                       FROM dbo.NhanVien nv
                       JOIN dbo.PhongBan pb ON pb.MaPB = nv.MaPB
                       LEFT JOIN dbo.ChucVu cv ON cv.MaCV = nv.MaCVHienTai
                       ORDER BY nv.MaNV");

        public DataTable LayNhanVienDangLamViec() =>
            Db.Query("SELECT MaNV, HoTen, MaPB, TrangThaiLamViec, MaNV + N' - ' + HoTen AS HienThi FROM dbo.NhanVien WHERE TrangThaiLamViec <> N'Nghỉ việc' ORDER BY MaNV");

        public KetQuaXuLy ThemHoacCapNhatPhongBan(string maPB, string tenPB, string dienThoai, bool nhuCau)
        {
            if (string.IsNullOrWhiteSpace(maPB) || string.IsNullOrWhiteSpace(tenPB))
                return KetQuaXuLy.Fail("Mã phòng ban và tên phòng ban không được để trống.");
            try
            {
                int tonTai = Convert.ToInt32(Db.Scalar("SELECT COUNT(*) FROM dbo.PhongBan WHERE MaPB = @ma", Db.P("@ma", maPB.Trim())));
                if (tonTai == 0)
                {
                    Db.Execute("INSERT INTO dbo.PhongBan(MaPB, TenPB, DienThoai, NhuCauTuyenDung) VALUES(@ma, @ten, @dt, @nc)",
                        Db.P("@ma", maPB.Trim()), Db.P("@ten", tenPB.Trim()), Db.P("@dt", dienThoai), Db.P("@nc", nhuCau));
                    return KetQuaXuLy.Ok("Đã thêm phòng ban mới.");
                }
                Db.Execute("UPDATE dbo.PhongBan SET TenPB=@ten, DienThoai=@dt, NhuCauTuyenDung=@nc WHERE MaPB=@ma",
                    Db.P("@ma", maPB.Trim()), Db.P("@ten", tenPB.Trim()), Db.P("@dt", dienThoai), Db.P("@nc", nhuCau));
                return KetQuaXuLy.Ok("Đã cập nhật thông tin phòng ban.");
            }
            catch (Exception ex) { return KetQuaXuLy.Fail("Lỗi CSDL: " + ex.Message); }
        }

        public KetQuaXuLy ThemHoacCapNhatChucVu(string maCV, string tenCV, decimal phuCapMacDinh)
        {
            if (string.IsNullOrWhiteSpace(maCV) || string.IsNullOrWhiteSpace(tenCV))
                return KetQuaXuLy.Fail("Mã chức vụ và tên chức vụ không được để trống.");
            if (phuCapMacDinh < 0)
                return KetQuaXuLy.Fail("Phụ cấp chức vụ mặc định không được âm.");
            try
            {
                int tonTai = Convert.ToInt32(Db.Scalar("SELECT COUNT(*) FROM dbo.ChucVu WHERE MaCV = @ma", Db.P("@ma", maCV.Trim())));
                if (tonTai == 0)
                {
                    Db.Execute("INSERT INTO dbo.ChucVu(MaCV, TenCV, PhuCapMacDinh) VALUES(@ma, @ten, @pc)",
                        Db.P("@ma", maCV.Trim()), Db.P("@ten", tenCV.Trim()), Db.P("@pc", phuCapMacDinh));
                    return KetQuaXuLy.Ok("Đã thêm chức vụ mới.");
                }
                Db.Execute("UPDATE dbo.ChucVu SET TenCV=@ten, PhuCapMacDinh=@pc WHERE MaCV=@ma",
                    Db.P("@ma", maCV.Trim()), Db.P("@ten", tenCV.Trim()), Db.P("@pc", phuCapMacDinh));
                return KetQuaXuLy.Ok("Đã cập nhật chức vụ.");
            }
            catch (Exception ex) { return KetQuaXuLy.Fail("Lỗi CSDL: " + ex.Message); }
        }

        public KetQuaXuLy ThemHoacCapNhatCa(string maCa, string tenCa, string gioBD, string gioKT, decimal soGio)
        {
            if (string.IsNullOrWhiteSpace(maCa) || string.IsNullOrWhiteSpace(tenCa))
                return KetQuaXuLy.Fail("Mã ca và tên ca không được để trống.");
            if (soGio <= 0) return KetQuaXuLy.Fail("Số giờ chuẩn của ca phải > 0.");
            try
            {
                int tonTai = Convert.ToInt32(Db.Scalar("SELECT COUNT(*) FROM dbo.CaLamViec WHERE MaCa = @ma", Db.P("@ma", maCa.Trim())));
                if (tonTai == 0)
                {
                    Db.Execute("INSERT INTO dbo.CaLamViec(MaCa, TenCa, GioBatDau, GioKetThuc, SoGioChuan) VALUES(@ma, @ten, @bd, @kt, @gio)",
                        Db.P("@ma", maCa.Trim()), Db.P("@ten", tenCa.Trim()), Db.P("@bd", gioBD), Db.P("@kt", gioKT), Db.P("@gio", soGio));
                    return KetQuaXuLy.Ok("Đã thêm ca làm việc mới.");
                }
                Db.Execute("UPDATE dbo.CaLamViec SET TenCa=@ten, GioBatDau=@bd, GioKetThuc=@kt, SoGioChuan=@gio WHERE MaCa=@ma",
                    Db.P("@ma", maCa.Trim()), Db.P("@ten", tenCa.Trim()), Db.P("@bd", gioBD), Db.P("@kt", gioKT), Db.P("@gio", soGio));
                return KetQuaXuLy.Ok("Đã cập nhật ca làm việc.");
            }
            catch (Exception ex) { return KetQuaXuLy.Fail("Lỗi CSDL: " + ex.Message); }
        }
    }
}
