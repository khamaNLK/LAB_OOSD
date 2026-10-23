using System;
using System.Data;
using QuanLyNhanSuDongA.Data;

namespace QuanLyNhanSuDongA.Services
{
    public class ChamCongPhuTroiService
    {
        public DataTable LayPhieuPhuTroi() =>
            Db.Query(@"SELECT pt.SoPhieuPT, pt.NgayPhuTroi, pt.HinhThucPhuTroi, ct.MaNV, nv.HoTen,
                              ct.SoGioThamGia AS SoGioPT, pt.HeSoPhuTroi AS HeSo, ct.DonGiaGioThuong, ct.ThanhTienPhuTroi
                       FROM dbo.PhieuPhuTroi pt
                       JOIN dbo.ChiTietPhuTroi ct ON ct.SoPhieuPT = pt.SoPhieuPT
                       JOIN dbo.NhanVien nv ON nv.MaNV = ct.MaNV
                       ORDER BY pt.NgayPhuTroi DESC");

        public DataTable LayBangChamCong() =>
            Db.Query(@"SELECT cc.MaChamCong, cc.MaNV, nv.HoTen, pb.TenPB, cc.Thang, cc.Nam,
                              cc.SoNgayCongChuan, cc.SoNgayCongThucTe, cc.NguoiChamCong
                       FROM dbo.BangChamCong cc
                       JOIN dbo.NhanVien nv ON nv.MaNV = cc.MaNV
                       JOIN dbo.PhongBan pb ON pb.MaPB = nv.MaPB
                       ORDER BY cc.Nam DESC, cc.Thang DESC, cc.MaNV ASC");

        public KetQuaXuLy GhiNhanPhuTroi(string soPhieuPT, DateTime ngayPT, string hinhThuc,
            string maNV, decimal soGioThamGia, string nguoiLap = "Ban chấm công")
        {
            if (string.IsNullOrWhiteSpace(soPhieuPT) || string.IsNullOrWhiteSpace(maNV))
                return KetQuaXuLy.Fail("Vui lòng nhập Số phiếu phụ trội và chọn Nhân viên tham gia.");
            if (soGioThamGia <= 0)
                return KetQuaXuLy.Fail("Số giờ phụ trội phải lớn hơn 0.");
            if (hinhThuc != "Tăng ca" && hinhThuc != "Ngày lễ" && hinhThuc != "Ngày nghỉ")
                return KetQuaXuLy.Fail("Hình thức phụ trội chỉ nhận: Tăng ca, Ngày lễ hoặc Ngày nghỉ (BR07).");

            object lcbObj = Db.Scalar(
                "SELECT TOP 1 LuongCoBan FROM dbo.QuyetDinhLuong WHERE MaNV = @nv ORDER BY NgayHieuLuc DESC",
                Db.P("@nv", maNV));
            if (lcbObj == null)
            {
                lcbObj = Db.Scalar(
                    "SELECT TOP 1 LuongThuViec FROM dbo.QuyetDinhTuyenDung WHERE MaNV = @nv ORDER BY NgayKy DESC",
                    Db.P("@nv", maNV));
            }
            if (lcbObj == null)
                return KetQuaXuLy.Fail("Nhân viên chưa có mức lương cơ bản hoặc lương thử việc để tính phụ trội.");

            decimal luongCB = Convert.ToDecimal(lcbObj);
            decimal donGiaGioThuong = Math.Round(luongCB / 208m, 2);
            decimal thanhTienPT = Math.Round(soGioThamGia * donGiaGioThuong * 1.5m, 0);

            using (var cn = Db.OpenConnection())
            using (var tr = cn.BeginTransaction())
            {
                try
                {
                    int daCoPhieu = Convert.ToInt32(Db.Scalar(
                        "SELECT COUNT(*) FROM dbo.PhieuPhuTroi WHERE SoPhieuPT = @so", Db.P("@so", soPhieuPT.Trim())));
                    if (daCoPhieu == 0)
                    {
                        Db.ExecuteInTran(cn, tr,
                            @"INSERT INTO dbo.PhieuPhuTroi(SoPhieuPT, NgayPhuTroi, SoGioPhuTroi, HinhThucPhuTroi, HeSoPhuTroi, NguoiLap)
                              VALUES(@so, @ngay, @gio, @ht, 1.5, @nl)",
                            Db.P("@so", soPhieuPT.Trim()), Db.P("@ngay", ngayPT.Date),
                            Db.P("@gio", soGioThamGia), Db.P("@ht", hinhThuc), Db.P("@nl", nguoiLap));
                    }

                    Db.ExecuteInTran(cn, tr,
                        @"INSERT INTO dbo.ChiTietPhuTroi(SoPhieuPT, MaNV, SoGioThamGia, DonGiaGioThuong, ThanhTienPhuTroi)
                          VALUES(@so, @nv, @gio, @dg, @tt)",
                        Db.P("@so", soPhieuPT.Trim()), Db.P("@nv", maNV),
                        Db.P("@gio", soGioThamGia), Db.P("@dg", donGiaGioThuong), Db.P("@tt", thanhTienPT));

                    tr.Commit();
                    return KetQuaXuLy.Ok($"Đã ghi nhận phụ trội (hệ số 1.5). Thành tiền: {thanhTienPT:N0} đ.");
                }
                catch (Exception ex)
                {
                    tr.Rollback();
                    return KetQuaXuLy.Fail("Lỗi ghi phiếu phụ trội: " + ex.Message);
                }
            }
        }

        public KetQuaXuLy ChamCongThang(string maCC, string maNV, int thang, int nam, decimal ngayCongChuan, decimal ngayCongThucTe)
        {
            if (string.IsNullOrWhiteSpace(maCC) || string.IsNullOrWhiteSpace(maNV))
                return KetQuaXuLy.Fail("Mã chấm công và nhân viên không được để trống.");
            if (ngayCongThucTe < 0 || ngayCongChuan <= 0)
                return KetQuaXuLy.Fail("Số ngày công không hợp lệ.");
            try
            {
                int daCo = Convert.ToInt32(Db.Scalar(
                    "SELECT COUNT(*) FROM dbo.BangChamCong WHERE MaNV = @nv AND Thang = @t AND Nam = @n",
                    Db.P("@nv", maNV), Db.P("@t", thang), Db.P("@n", nam)));
                if (daCo > 0)
                {
                    Db.Execute(
                        "UPDATE dbo.BangChamCong SET SoNgayCongChuan=@chuan, SoNgayCongThucTe=@thucTe WHERE MaNV=@nv AND Thang=@t AND Nam=@n",
                        Db.P("@chuan", ngayCongChuan), Db.P("@thucTe", ngayCongThucTe),
                        Db.P("@nv", maNV), Db.P("@t", thang), Db.P("@n", nam));
                    return KetQuaXuLy.Ok($"Đã cập nhật bảng chấm công tháng {thang}/{nam} cho nhân viên {maNV}.");
                }
                Db.Execute(
                    @"INSERT INTO dbo.BangChamCong(MaChamCong, MaNV, Thang, Nam, SoNgayCongChuan, SoNgayCongThucTe, NguoiChamCong)
                      VALUES(@ma, @nv, @thang, @nam, @chuan, @thucTe, N'Ban chấm công')",
                    Db.P("@ma", maCC.Trim()), Db.P("@nv", maNV), Db.P("@thang", thang),
                    Db.P("@nam", nam), Db.P("@chuan", ngayCongChuan), Db.P("@thucTe", ngayCongThucTe));
                return KetQuaXuLy.Ok($"Đã chốt bảng chấm công tháng {thang}/{nam} cho nhân viên {maNV}.");
            }
            catch (Exception ex) { return KetQuaXuLy.Fail("Lỗi chấm công: " + ex.Message); }
        }
    }
}
