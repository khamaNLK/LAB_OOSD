using System;
using System.Data;
using QuanLyNhanSuDongA.Data;

namespace QuanLyNhanSuDongA.Services
{
    public class TinhLuongService
    {
        public DataTable LayBangLuongThang(int thang, int nam) =>
            Db.Query(@"SELECT bl.MaBangLuong, bl.MaNV, nv.HoTen, pb.TenPB,
                              bl.LuongCoBan, (bl.PhuCapChucVu + bl.PhuCapLuongDocHai) AS TongPhuCap,
                              bl.SoNgayCong, bl.TienLuongNgayCong, bl.TienPhuTroi,
                              bl.TienKhenThuong, bl.TienKyLuatTru, bl.TrichTruBHXH_BHYT, bl.ThucLanh, bl.TrangThai
                       FROM dbo.BangLuongThang bl
                       JOIN dbo.NhanVien nv ON nv.MaNV = bl.MaNV
                       JOIN dbo.PhongBan pb ON pb.MaPB = nv.MaPB
                       WHERE bl.Thang = @thang AND bl.Nam = @nam
                       ORDER BY bl.MaNV",
                     Db.P("@thang", thang), Db.P("@nam", nam));

        public KetQuaXuLy TinhBangLuongThang(int thang, int nam)
        {
            var dtCC = Db.Query("SELECT * FROM dbo.BangChamCong WHERE Thang = @thang AND Nam = @nam",
                Db.P("@thang", thang), Db.P("@nam", nam));
            if (dtCC.Rows.Count == 0)
                return KetQuaXuLy.Fail($"Chưa có dữ liệu Bảng chấm công cho tháng {thang}/{nam}.");

            using (var cn = Db.OpenConnection())
            using (var tr = cn.BeginTransaction())
            {
                try
                {
                    foreach (DataRow rCC in dtCC.Rows)
                    {
                        string maNV = rCC["MaNV"].ToString();
                        decimal congChuan = Convert.ToDecimal(rCC["SoNgayCongChuan"]);
                        decimal congThucTe = Convert.ToDecimal(rCC["SoNgayCongThucTe"]);

                        decimal luongCB = 0, pcLuong = 0, pcDocHai = 0, pcChucVu = 0;
                        var dtQDL = Db.Query("SELECT TOP 1 * FROM dbo.QuyetDinhLuong WHERE MaNV = @nv ORDER BY NgayHieuLuc DESC", Db.P("@nv", maNV));
                        if (dtQDL.Rows.Count > 0)
                        {
                            luongCB = Convert.ToDecimal(dtQDL.Rows[0]["LuongCoBan"]);
                            pcLuong = Convert.ToDecimal(dtQDL.Rows[0]["PhuCapLuong"]);
                            pcDocHai = Convert.ToDecimal(dtQDL.Rows[0]["PhuCapDocHai"]);
                        }
                        else
                        {
                            object ltv = Db.Scalar("SELECT TOP 1 LuongThuViec FROM dbo.QuyetDinhTuyenDung WHERE MaNV = @nv ORDER BY NgayKy DESC", Db.P("@nv", maNV));
                            luongCB = ltv != null ? Convert.ToDecimal(ltv) : 0;
                        }

                        object pccvObj = Db.Scalar("SELECT TOP 1 PhuCapChucVu FROM dbo.QuyetDinhChucVu WHERE MaNV = @nv ORDER BY NgayHieuLuc DESC", Db.P("@nv", maNV));
                        if (pccvObj != null && pccvObj != DBNull.Value) pcChucVu = Convert.ToDecimal(pccvObj);

                        decimal tienPhuTroi = Convert.ToDecimal(Db.Scalar(
                            @"SELECT ISNULL(SUM(ct.ThanhTienPhuTroi), 0)
                              FROM dbo.ChiTietPhuTroi ct
                              JOIN dbo.PhieuPhuTroi pt ON pt.SoPhieuPT = ct.SoPhieuPT
                              WHERE ct.MaNV = @nv AND MONTH(pt.NgayPhuTroi) = @thang AND YEAR(pt.NgayPhuTroi) = @nam",
                            Db.P("@nv", maNV), Db.P("@thang", thang), Db.P("@nam", nam)));

                        decimal tienThuong = Convert.ToDecimal(Db.Scalar(
                            @"SELECT ISNULL(SUM(pc.SoTienChi), 0)
                              FROM dbo.PhieuChiKhenThuong pc
                              JOIN dbo.QuyetDinhKhenThuong kt ON kt.SoQDKT = pc.SoQDKT
                              WHERE kt.MaNV = @nv AND pc.ThangTinhLuong = @thang AND pc.NamTinhLuong = @nam",
                            Db.P("@nv", maNV), Db.P("@thang", thang), Db.P("@nam", nam)));

                        decimal tienPhat = Convert.ToDecimal(Db.Scalar(
                            @"SELECT ISNULL(SUM(pt.SoTienThuPhat), 0)
                              FROM dbo.PhieuThuNopPhat pt
                              JOIN dbo.QuyetDinhKyLuat kl ON kl.SoQDKL = pt.SoQDKL
                              WHERE kl.MaNV = @nv AND pt.TruVaoBangLuong = 1 AND pt.ThangTruLuong = @thang AND pt.NamTruLuong = @nam",
                            Db.P("@nv", maNV), Db.P("@thang", thang), Db.P("@nam", nam)));

                        decimal trichBH = Convert.ToDecimal(Db.Scalar(
                            @"SELECT ISNULL(SUM(bh.MucDongHangThang), 0)
                              FROM dbo.HoSoBaoHiem bh
                              JOIN dbo.HopDongLaoDong hd ON hd.SoHD = bh.SoHD
                              WHERE bh.MaNV = @nv AND hd.LoaiHopDong = N'Dài hạn' AND hd.TrangThaiHD = N'Đang hiệu lực'",
                            Db.P("@nv", maNV)));

                        decimal luongNgayCong = Math.Round((luongCB / congChuan) * congThucTe, 0);
                        decimal pcLuongDocHai = pcLuong + pcDocHai;
                        decimal thucLanh = luongNgayCong + pcChucVu + pcLuongDocHai + tienPhuTroi + tienThuong - tienPhat - trichBH;
                        string maBL = $"BL{nam}{thang:D2}_{maNV}";

                        Db.ExecuteInTran(cn, tr,
                            "DELETE FROM dbo.BangLuongThang WHERE MaNV = @nv AND Thang = @thang AND Nam = @nam",
                            Db.P("@nv", maNV), Db.P("@thang", thang), Db.P("@nam", nam));

                        Db.ExecuteInTran(cn, tr,
                            @"INSERT INTO dbo.BangLuongThang(MaBangLuong, MaNV, Thang, Nam, LuongCoBan, PhuCapChucVu,
                                  PhuCapLuongDocHai, SoNgayCong, TienLuongNgayCong, TienPhuTroi,
                                  TienKhenThuong, TienKyLuatTru, TrichTruBHXH_BHYT, ThucLanh, TrangThai)
                              VALUES(@maBL, @nv, @thang, @nam, @lcb, @pccv, @pcldh, @cong, @luongCong, @pt, @kt, @kl, @bh, @thucLanh, N'Đã tính')",
                            Db.P("@maBL", maBL), Db.P("@nv", maNV), Db.P("@thang", thang), Db.P("@nam", nam),
                            Db.P("@lcb", luongCB), Db.P("@pccv", pcChucVu), Db.P("@pcldh", pcLuongDocHai),
                            Db.P("@cong", congThucTe), Db.P("@luongCong", luongNgayCong), Db.P("@pt", tienPhuTroi),
                            Db.P("@kt", tienThuong), Db.P("@kl", tienPhat), Db.P("@bh", trichBH), Db.P("@thucLanh", thucLanh));
                    }

                    tr.Commit();
                    return KetQuaXuLy.Ok($"Đã tính xong Bảng lương tháng {thang}/{nam} cho {dtCC.Rows.Count} nhân viên.");
                }
                catch (Exception ex)
                {
                    tr.Rollback();
                    return KetQuaXuLy.Fail("Lỗi tính bảng lương tháng: " + ex.Message);
                }
            }
        }

        public KetQuaXuLy ChotBangLuongThang(int thang, int nam)
        {
            int rows = Db.Execute(
                "UPDATE dbo.BangLuongThang SET TrangThai = N'Đã chốt' WHERE Thang = @thang AND Nam = @nam",
                Db.P("@thang", thang), Db.P("@nam", nam));
            if (rows == 0)
                return KetQuaXuLy.Fail($"Chưa có bảng lương tháng {thang}/{nam} để chốt.");
            return KetQuaXuLy.Ok($"Đã chốt bảng lương tháng {thang}/{nam} ({rows} nhân viên).");
        }
    }
}
