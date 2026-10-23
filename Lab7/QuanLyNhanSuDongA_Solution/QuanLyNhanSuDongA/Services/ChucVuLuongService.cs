using System;
using System.Data;
using QuanLyNhanSuDongA.Data;

namespace QuanLyNhanSuDongA.Services
{
    public class ChucVuLuongService
    {
        public DataTable LayDanhSachQuyetDinh() =>
            Db.Query(@"SELECT nv.MaNV, nv.HoTen, ISNULL(cv.TenCV, N'(Chưa bổ nhiệm)') AS TenCV,
                              ISNULL(qdcv.SoQDCV, '-') AS SoQDCV, ISNULL(qdcv.PhuCapChucVu, 0) AS PhuCapChucVu,
                              ISNULL(qdl.SoQDL, '-') AS SoQDL, ISNULL(qdl.LuongCoBan, 0) AS LuongCoBan,
                              ISNULL(qdl.PhuCapLuong, 0) AS PhuCapLuong, ISNULL(qdl.PhuCapDocHai, 0) AS PhuCapDocHai
                       FROM dbo.NhanVien nv
                       LEFT JOIN dbo.ChucVu cv ON cv.MaCV = nv.MaCVHienTai
                       OUTER APPLY (SELECT TOP 1 * FROM dbo.QuyetDinhChucVu WHERE MaNV = nv.MaNV ORDER BY NgayHieuLuc DESC, SoQDCV DESC) qdcv
                       OUTER APPLY (SELECT TOP 1 * FROM dbo.QuyetDinhLuong WHERE MaNV = nv.MaNV ORDER BY NgayHieuLuc DESC, SoQDL DESC) qdl
                       ORDER BY nv.MaNV");

        public KetQuaXuLy BanHanhQDChucVu(string soQDCV, string maNV, string maCV, DateTime ngayHL, decimal phuCapCV, string noiDung)
        {
            if (string.IsNullOrWhiteSpace(soQDCV) || string.IsNullOrWhiteSpace(maNV) || string.IsNullOrWhiteSpace(maCV))
                return KetQuaXuLy.Fail("Vui lòng nhập Số QĐ chức vụ, Nhân viên và Chức vụ.");
            if (phuCapCV < 0) return KetQuaXuLy.Fail("Phụ cấp chức vụ không được âm.");

            using (var cn = Db.OpenConnection())
            using (var tr = cn.BeginTransaction())
            {
                try
                {
                    Db.ExecuteInTran(cn, tr,
                        @"INSERT INTO dbo.QuyetDinhChucVu(SoQDCV, MaNV, MaCV, NgayHieuLuc, PhuCapChucVu, NoiDungQuyetDinh)
                          VALUES(@so, @nv, @cv, @ngay, @pc, @nd)",
                        Db.P("@so", soQDCV.Trim()), Db.P("@nv", maNV), Db.P("@cv", maCV),
                        Db.P("@ngay", ngayHL.Date), Db.P("@pc", phuCapCV), Db.P("@nd", noiDung));

                    Db.ExecuteInTran(cn, tr,
                        "UPDATE dbo.NhanVien SET MaCVHienTai = @cv WHERE MaNV = @nv",
                        Db.P("@cv", maCV), Db.P("@nv", maNV));

                    tr.Commit();
                    return KetQuaXuLy.Ok("Đã ban hành Quyết định chức vụ " + soQDCV + ".");
                }
                catch (Exception ex)
                {
                    tr.Rollback();
                    return KetQuaXuLy.Fail("Lỗi ban hành QĐ chức vụ: " + ex.Message);
                }
            }
        }

        public KetQuaXuLy BanHanhQDLuong(string soQDL, string maNV, DateTime ngayHL,
            decimal luongCB, decimal phuCapLuong, decimal phuCapDocHai, string ghiChu)
        {
            if (string.IsNullOrWhiteSpace(soQDL) || string.IsNullOrWhiteSpace(maNV))
                return KetQuaXuLy.Fail("Số quyết định lương và nhân viên không được để trống.");
            if (luongCB <= 0 || phuCapLuong < 0 || phuCapDocHai < 0)
                return KetQuaXuLy.Fail("Lương cơ bản phải > 0; phụ cấp lương và phụ cấp độc hại phải >= 0 (BR05).");

            try
            {
                Db.Execute(
                    @"INSERT INTO dbo.QuyetDinhLuong(SoQDL, MaNV, NgayHieuLuc, LuongCoBan, PhuCapLuong, PhuCapDocHai, GhiChu)
                      VALUES(@so, @nv, @ngay, @lcb, @pcl, @pcdh, @gc)",
                    Db.P("@so", soQDL.Trim()), Db.P("@nv", maNV), Db.P("@ngay", ngayHL.Date),
                    Db.P("@lcb", luongCB), Db.P("@pcl", phuCapLuong), Db.P("@pcdh", phuCapDocHai), Db.P("@gc", ghiChu));
                return KetQuaXuLy.Ok($"Đã ban hành Quyết định lương {soQDL} (Lương CB: {luongCB:N0} đ).");
            }
            catch (Exception ex) { return KetQuaXuLy.Fail("Lỗi ban hành QĐ lương: " + ex.Message); }
        }
    }
}
