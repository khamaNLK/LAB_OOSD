using System;
using System.Data;
using QuanLyNhanSuDongA.Data;

namespace QuanLyNhanSuDongA.Services
{
    public class TuyenDungService
    {
        public DataTable LayDanhSachThuViec() =>
            Db.Query(@"SELECT qd.SoQDTD, qd.MaNV, nv.HoTen, pb.TenPB, qd.NgayKy,
                              qd.ThoiGianThuViecThang, qd.LuongThuViec, qd.DieuKhoanHaiBen,
                              qd.KetQuaThuViec, nv.TrangThaiLamViec
                       FROM dbo.QuyetDinhTuyenDung qd
                       JOIN dbo.NhanVien nv ON nv.MaNV = qd.MaNV
                       JOIN dbo.PhongBan pb ON pb.MaPB = qd.MaPB
                       ORDER BY qd.NgayKy DESC");

        public KetQuaXuLy TiepNhanThuViec(HoSoNhanVienMoi nv, string soQDTD, string maPB,
            DateTime ngayKy, int soThangThuViec, decimal luongThuViec, string dieuKhoanHaiBen)
        {
            if (nv == null || string.IsNullOrWhiteSpace(nv.MaNV) || string.IsNullOrWhiteSpace(nv.HoTen))
                return KetQuaXuLy.Fail("Vui lòng nhập đầy đủ mã và họ tên nhân viên tuyển dụng.");
            if (string.IsNullOrWhiteSpace(soQDTD) || string.IsNullOrWhiteSpace(maPB))
                return KetQuaXuLy.Fail("Số quyết định tuyển dụng và phòng ban không được để trống.");
            if (soThangThuViec < 1 || soThangThuViec > 12)
                return KetQuaXuLy.Fail("Thời gian thử việc phải từ 1 đến 12 tháng.");
            if (luongThuViec <= 0)
                return KetQuaXuLy.Fail("Mức lương thử việc phải lớn hơn 0.");
            if (string.IsNullOrWhiteSpace(dieuKhoanHaiBen))
                return KetQuaXuLy.Fail("Quyết định tuyển dụng phải ghi rõ điều khoản của hai bên (BR01).");

            var coNhuCau = Db.Scalar("SELECT NhuCauTuyenDung FROM dbo.PhongBan WHERE MaPB = @pb", Db.P("@pb", maPB));
            if (coNhuCau == null || !Convert.ToBoolean(coNhuCau))
                return KetQuaXuLy.Fail("Phòng ban được chọn hiện không có nhu cầu tuyển dụng (BR01).");

            using (var cn = Db.OpenConnection())
            using (var tr = cn.BeginTransaction())
            {
                try
                {
                    Db.ExecuteInTran(cn, tr,
                        @"INSERT INTO dbo.NhanVien(MaNV, MaPB, HoTen, NgaySinh, GioiTinh, CCCD, DienThoai, DiaChi, NgayVaoLam, TrangThaiLamViec)
                          VALUES(@maNV, @maPB, @hoTen, @ngaySinh, @gioiTinh, @cccd, @dienThoai, @diaChi, @ngayVao, N'Thử việc')",
                        Db.P("@maNV", nv.MaNV.Trim()), Db.P("@maPB", maPB), Db.P("@hoTen", nv.HoTen.Trim()),
                        Db.P("@ngaySinh", nv.NgaySinh.Date), Db.P("@gioiTinh", string.IsNullOrWhiteSpace(nv.GioiTinh) ? "Nam" : nv.GioiTinh),
                        Db.P("@cccd", string.IsNullOrWhiteSpace(nv.CCCD) ? "079" + DateTime.Now.Ticks.ToString().Substring(9) : nv.CCCD.Trim()),
                        Db.P("@dienThoai", string.IsNullOrWhiteSpace(nv.DienThoai) ? "0903000111" : nv.DienThoai.Trim()),
                        Db.P("@diaChi", nv.DiaChi), Db.P("@ngayVao", ngayKy.Date));

                    Db.ExecuteInTran(cn, tr,
                        @"INSERT INTO dbo.QuyetDinhTuyenDung(SoQDTD, MaNV, MaPB, NgayKy, ThoiGianThuViecThang, LuongThuViec, DieuKhoanHaiBen, KetQuaThuViec)
                          VALUES(@soQD, @maNV, @maPB, @ngayKy, @thang, @luongTV, @dieuKhoan, N'Đang thử việc')",
                        Db.P("@soQD", soQDTD.Trim()), Db.P("@maNV", nv.MaNV.Trim()), Db.P("@maPB", maPB),
                        Db.P("@ngayKy", ngayKy.Date), Db.P("@thang", soThangThuViec),
                        Db.P("@luongTV", luongThuViec), Db.P("@dieuKhoan", dieuKhoanHaiBen.Trim()));

                    tr.Commit();
                    return KetQuaXuLy.Ok("Đã tiếp nhận nhân viên thử việc và lập Quyết định tuyển dụng " + soQDTD + ".");
                }
                catch (Exception ex)
                {
                    tr.Rollback();
                    return KetQuaXuLy.Fail("Không thể lưu Quyết định tuyển dụng: " + ex.Message);
                }
            }
        }

        public KetQuaXuLy DanhGiaThuViec(string soQDTD, string ketQua)
        {
            if (string.IsNullOrWhiteSpace(soQDTD))
                return KetQuaXuLy.Fail("Vui lòng nhập hoặc chọn Số quyết định tuyển dụng cần đánh giá.");
            if (ketQua != "Đạt" && ketQua != "Không đạt")
                return KetQuaXuLy.Fail("Kết quả đánh giá thử việc chỉ nhận 'Đạt' hoặc 'Không đạt'.");
            using (var cn = Db.OpenConnection())
            using (var tr = cn.BeginTransaction())
            {
                try
                {
                    int rows = Db.ExecuteInTran(cn, tr,
                        "UPDATE dbo.QuyetDinhTuyenDung SET KetQuaThuViec = @kq WHERE SoQDTD = @so",
                        Db.P("@kq", ketQua), Db.P("@so", soQDTD.Trim()));
                    if (rows == 0)
                    {
                        tr.Rollback();
                        return KetQuaXuLy.Fail("Không tìm thấy Quyết định tuyển dụng " + soQDTD + ".");
                    }
                    if (ketQua == "Không đạt")
                    {
                        Db.ExecuteInTran(cn, tr,
                            @"UPDATE dbo.NhanVien SET TrangThaiLamViec = N'Nghỉ việc'
                              WHERE MaNV = (SELECT MaNV FROM dbo.QuyetDinhTuyenDung WHERE SoQDTD = @so)",
                            Db.P("@so", soQDTD.Trim()));
                    }
                    tr.Commit();
                    return KetQuaXuLy.Ok("Đã cập nhật kết quả thử việc: " + ketQua + ".");
                }
                catch (Exception ex)
                {
                    tr.Rollback();
                    return KetQuaXuLy.Fail("Lỗi cập nhật đánh giá thử việc: " + ex.Message);
                }
            }
        }
    }
}
