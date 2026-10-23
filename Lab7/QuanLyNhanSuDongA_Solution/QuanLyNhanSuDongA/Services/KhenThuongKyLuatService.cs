using System;
using System.Data;
using QuanLyNhanSuDongA.Data;

namespace QuanLyNhanSuDongA.Services
{
    public class KhenThuongKyLuatService
    {
        public DataTable LayDanhSachKTKL() =>
            Db.Query(@"SELECT kt.SoQDKT AS SoQD, N'Khen thưởng' AS LoaiQD, kt.LoaiDoiTuong,
                              ISNULL(kt.MaNV, kt.MaPB) AS MaDoiTuong, kt.LoaiKhenThuong AS NoiDung,
                              pc.SoPhieuChi AS SoPhieuChiThu, kt.SoTienThuong AS SoTien, kt.NgayKy, kt.NguoiKy
                       FROM dbo.QuyetDinhKhenThuong kt
                       JOIN dbo.PhieuChiKhenThuong pc ON pc.SoQDKT = kt.SoQDKT
                       UNION ALL
                       SELECT kl.SoQDKL AS SoQD, N'Kỷ luật' AS LoaiQD, kl.LoaiDoiTuong,
                              ISNULL(kl.MaNV, kl.MaPB) AS MaDoiTuong, kl.HinhThucKyLuat AS NoiDung,
                              pt.SoPhieuThu AS SoPhieuChiThu, -kl.SoTienPhat AS SoTien, kl.NgayKy, N'Giám đốc' AS NguoiKy
                       FROM dbo.QuyetDinhKyLuat kl
                       JOIN dbo.PhieuThuNopPhat pt ON pt.SoQDKL = kl.SoQDKL
                       ORDER BY NgayKy DESC");

        public KetQuaXuLy LapKhenThuong(string soQDKT, string loaiDoiTuong, string maNV, string maPB,
            DateTime ngayKy, string loaiKhenThuong, decimal soTienThuong, string nguoiKy, string soPhieuChi)
        {
            if (string.IsNullOrWhiteSpace(soQDKT) || string.IsNullOrWhiteSpace(soPhieuChi))
                return KetQuaXuLy.Fail("Số quyết định khen thưởng và Số phiếu chi không được để trống.");
            if (string.IsNullOrWhiteSpace(loaiKhenThuong))
                return KetQuaXuLy.Fail("Vui lòng nhập nội dung / dịp khen thưởng.");
            if (soTienThuong <= 0)
                return KetQuaXuLy.Fail("Số tiền khen thưởng phải lớn hơn 0.");
            if (loaiDoiTuong == "Cá nhân" && string.IsNullOrWhiteSpace(maNV))
                return KetQuaXuLy.Fail("Khen thưởng cá nhân bắt buộc phải chọn Mã nhân viên.");
            if (loaiDoiTuong == "Tập thể" && string.IsNullOrWhiteSpace(maPB))
                return KetQuaXuLy.Fail("Khen thưởng tập thể bắt buộc phải chọn Mã phòng ban.");

            using (var cn = Db.OpenConnection())
            using (var tr = cn.BeginTransaction())
            {
                try
                {
                    Db.ExecuteInTran(cn, tr,
                        @"INSERT INTO dbo.QuyetDinhKhenThuong(SoQDKT, LoaiDoiTuong, MaNV, MaPB, NgayKy, LoaiKhenThuong, SoTienThuong, NguoiKy)
                          VALUES(@soQD, @loaiDT, @nv, @pb, @ngay, @loaiKT, @tien, @nk)",
                        Db.P("@soQD", soQDKT.Trim()), Db.P("@loaiDT", loaiDoiTuong),
                        Db.P("@nv", loaiDoiTuong == "Cá nhân" ? maNV : null),
                        Db.P("@pb", loaiDoiTuong == "Tập thể" ? maPB : null),
                        Db.P("@ngay", ngayKy.Date), Db.P("@loaiKT", loaiKhenThuong.Trim()),
                        Db.P("@tien", soTienThuong), Db.P("@nk", string.IsNullOrWhiteSpace(nguoiKy) ? "Giám đốc" : nguoiKy));

                    Db.ExecuteInTran(cn, tr,
                        @"INSERT INTO dbo.PhieuChiKhenThuong(SoPhieuChi, SoQDKT, NgayChi, SoTienChi, ThangTinhLuong, NamTinhLuong)
                          VALUES(@soPC, @soQD, @ngay, @tien, @thang, @nam)",
                        Db.P("@soPC", soPhieuChi.Trim()), Db.P("@soQD", soQDKT.Trim()),
                        Db.P("@ngay", ngayKy.Date), Db.P("@tien", soTienThuong),
                        Db.P("@thang", ngayKy.Month), Db.P("@nam", ngayKy.Year));

                    tr.Commit();
                    return KetQuaXuLy.Ok($"Đã lập Quyết định khen thưởng {soQDKT} và Phiếu chi {soPhieuChi} ({soTienThuong:N0} đ).");
                }
                catch (Exception ex)
                {
                    tr.Rollback();
                    return KetQuaXuLy.Fail("Lỗi lập quyết định khen thưởng: " + ex.Message);
                }
            }
        }

        public KetQuaXuLy LapKyLuat(string soQDKL, string loaiDoiTuong, string maNV, string maPB,
            DateTime ngayKy, string hinhThucKyLuat, decimal soTienPhat, string soPhieuThu, bool truVaoLuong = true)
        {
            if (string.IsNullOrWhiteSpace(soQDKL) || string.IsNullOrWhiteSpace(soPhieuThu))
                return KetQuaXuLy.Fail("Số quyết định kỷ luật và Số phiếu thu nộp phạt không được để trống.");
            if (string.IsNullOrWhiteSpace(hinhThucKyLuat))
                return KetQuaXuLy.Fail("Vui lòng nhập hình thức / lý do kỷ luật.");
            if (soTienPhat <= 0)
                return KetQuaXuLy.Fail("Số tiền nộp phạt phải lớn hơn 0.");

            using (var cn = Db.OpenConnection())
            using (var tr = cn.BeginTransaction())
            {
                try
                {
                    Db.ExecuteInTran(cn, tr,
                        @"INSERT INTO dbo.QuyetDinhKyLuat(SoQDKL, LoaiDoiTuong, MaNV, MaPB, NgayKy, HinhThucKyLuat, SoTienPhat)
                          VALUES(@soQD, @loaiDT, @nv, @pb, @ngay, @ht, @tien)",
                        Db.P("@soQD", soQDKL.Trim()), Db.P("@loaiDT", loaiDoiTuong),
                        Db.P("@nv", loaiDoiTuong == "Cá nhân" ? maNV : null),
                        Db.P("@pb", loaiDoiTuong == "Tập thể" ? maPB : null),
                        Db.P("@ngay", ngayKy.Date), Db.P("@ht", hinhThucKyLuat.Trim()), Db.P("@tien", soTienPhat));

                    Db.ExecuteInTran(cn, tr,
                        @"INSERT INTO dbo.PhieuThuNopPhat(SoPhieuThu, SoQDKL, NgayThu, SoTienThuPhat, TruVaoBangLuong, ThangTruLuong, NamTruLuong)
                          VALUES(@soPT, @soQD, @ngay, @tien, @tru, @thang, @nam)",
                        Db.P("@soPT", soPhieuThu.Trim()), Db.P("@soQD", soQDKL.Trim()),
                        Db.P("@ngay", ngayKy.Date), Db.P("@tien", soTienPhat),
                        Db.P("@tru", truVaoLuong), Db.P("@thang", ngayKy.Month), Db.P("@nam", ngayKy.Year));

                    tr.Commit();
                    return KetQuaXuLy.Ok($"Đã lập Quyết định kỷ luật {soQDKL} và Phiếu thu {soPhieuThu} (-{soTienPhat:N0} đ).");
                }
                catch (Exception ex)
                {
                    tr.Rollback();
                    return KetQuaXuLy.Fail("Lỗi lập quyết định kỷ luật: " + ex.Message);
                }
            }
        }
    }
}
