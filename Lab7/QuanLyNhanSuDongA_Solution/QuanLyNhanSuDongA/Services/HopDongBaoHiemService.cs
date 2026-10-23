using System;
using System.Data;
using QuanLyNhanSuDongA.Data;

namespace QuanLyNhanSuDongA.Services
{
    public class HopDongBaoHiemService
    {
        public DataTable LayDanhSachHopDong() =>
            Db.Query(@"SELECT hd.SoHD, hd.MaNV, nv.HoTen, hd.LoaiHopDong, hd.NgayKy, hd.NgayBatDau, hd.NgayKetThuc,
                              ISNULL(bh.MaHoSoBH, N'-') AS MaHoSoBH,
                              ISNULL(bh.SoSoBHXH, N'(Không bắt buộc)') AS SoSoBHXH,
                              ISNULL(bh.SoTheBHYT, N'(Không bắt buộc)') AS SoTheBHYT,
                              ISNULL(bh.MucDongHangThang, 0) AS MucTrichTruBH, hd.TrangThaiHD
                       FROM dbo.HopDongLaoDong hd
                       JOIN dbo.NhanVien nv ON nv.MaNV = hd.MaNV
                       LEFT JOIN dbo.HoSoBaoHiem bh ON bh.SoHD = hd.SoHD
                       ORDER BY hd.NgayKy DESC");

        public KetQuaXuLy KyHopDongChinhThuc(string soHD, string maNV, string loaiHopDong,
            DateTime ngayKy, DateTime ngayBatDau, DateTime? ngayKetThuc, string noiDung,
            ThongTinBaoHiemInput hoSoBH)
        {
            if (string.IsNullOrWhiteSpace(soHD) || string.IsNullOrWhiteSpace(maNV))
                return KetQuaXuLy.Fail("Số hợp đồng và nhân viên không được để trống.");
            if (loaiHopDong != "Dài hạn" && loaiHopDong != "Ngắn hạn")
                return KetQuaXuLy.Fail("Loại hợp đồng phải là 'Dài hạn' hoặc 'Ngắn hạn'.");

            int datThuViec = Convert.ToInt32(Db.Scalar(
                "SELECT COUNT(*) FROM dbo.QuyetDinhTuyenDung WHERE MaNV = @nv AND KetQuaThuViec = N'Đạt'",
                Db.P("@nv", maNV)));
            if (datThuViec == 0)
                return KetQuaXuLy.Fail("Nhân viên chưa có kết quả thử việc 'Đạt', chưa thể ký hợp đồng chính thức (BR02).");

            if (loaiHopDong == "Dài hạn")
            {
                if (hoSoBH == null || string.IsNullOrWhiteSpace(hoSoBH.MaHoSoBH) ||
                    string.IsNullOrWhiteSpace(hoSoBH.SoSoBHXH) || string.IsNullOrWhiteSpace(hoSoBH.SoTheBHYT) ||
                    hoSoBH.MucDongHangThang <= 0)
                {
                    return KetQuaXuLy.Fail("Đối với Hợp đồng dài hạn, bắt buộc phải lập Hồ sơ BHXH, BHYT và mức đóng > 0 (BR03).");
                }
            }

            using (var cn = Db.OpenConnection())
            using (var tr = cn.BeginTransaction())
            {
                try
                {
                    Db.ExecuteInTran(cn, tr,
                        @"INSERT INTO dbo.HopDongLaoDong(SoHD, MaNV, LoaiHopDong, NgayKy, NgayBatDau, NgayKetThuc, NoiDungHopDong, TrangThaiHD)
                          VALUES(@soHD, @maNV, @loai, @ngayKy, @ngayBD, @ngayKT, @nd, N'Đang hiệu lực')",
                        Db.P("@soHD", soHD.Trim()), Db.P("@maNV", maNV), Db.P("@loai", loaiHopDong),
                        Db.P("@ngayKy", ngayKy.Date), Db.P("@ngayBD", ngayBatDau.Date),
                        Db.P("@ngayKT", ngayKetThuc),
                        Db.P("@nd", string.IsNullOrWhiteSpace(noiDung) ? "Hợp đồng lao động chính thức tại Công ty Sơn Đông Á" : noiDung.Trim()));

                    if (loaiHopDong == "Dài hạn")
                    {
                        Db.ExecuteInTran(cn, tr,
                            @"INSERT INTO dbo.HoSoBaoHiem(MaHoSoBH, SoHD, MaNV, SoSoBHXH, SoTheBHYT, TyLeBHXH, TyLeBHYT, MucDongHangThang)
                              VALUES(@maBH, @soHD, @maNV, @bhxh, @bhyt, 8.0, 1.5, @mucDong)",
                            Db.P("@maBH", hoSoBH.MaHoSoBH.Trim()), Db.P("@soHD", soHD.Trim()), Db.P("@maNV", maNV),
                            Db.P("@bhxh", hoSoBH.SoSoBHXH.Trim()), Db.P("@bhyt", hoSoBH.SoTheBHYT.Trim()),
                            Db.P("@mucDong", hoSoBH.MucDongHangThang));
                    }

                    Db.ExecuteInTran(cn, tr,
                        "UPDATE dbo.NhanVien SET TrangThaiLamViec = N'Chính thức' WHERE MaNV = @maNV",
                        Db.P("@maNV", maNV));

                    tr.Commit();
                    return KetQuaXuLy.Ok(loaiHopDong == "Dài hạn"
                        ? "Đã ký Hợp đồng dài hạn và lập Hồ sơ BHXH, BHYT thành công."
                        : "Đã ký Hợp đồng ngắn hạn thành công.");
                }
                catch (Exception ex)
                {
                    tr.Rollback();
                    return KetQuaXuLy.Fail("Lỗi khi ký hợp đồng: " + ex.Message);
                }
            }
        }
    }
}
