using System;
using System.Data;
using QuanLyNhanSuDongA.Data;

namespace QuanLyNhanSuDongA.Services
{
    public class KeHoachTuanService
    {
        public DataTable LayKeHoachTuan() =>
            Db.Query(@"SELECT kh.MaKeHoach, pb.TenPB, kh.Tuan, kh.TuNgay, kh.DenNgay,
                              ct.MaNV, nv.HoTen, ca.TenCa, ct.NgayLamViec, ct.GioPhuTroiDuKien, ct.GhiChu
                       FROM dbo.KeHoachTuan kh
                       JOIN dbo.PhongBan pb ON pb.MaPB = kh.MaPB
                       JOIN dbo.ChiTietKeHoachTuan ct ON ct.MaKeHoach = kh.MaKeHoach
                       JOIN dbo.NhanVien nv ON nv.MaNV = ct.MaNV
                       JOIN dbo.CaLamViec ca ON ca.MaCa = ct.MaCa
                       ORDER BY kh.TuNgay DESC, ct.NgayLamViec ASC");

        public KetQuaXuLy LapKeHoachVaPhanCa(string maKH, string maPB, int tuan, DateTime tuNgay, DateTime denNgay,
            string nguoiLap, string maNV, string maCa, DateTime ngayLamViec, decimal gioPTDuKien, string ghiChu)
        {
            if (string.IsNullOrWhiteSpace(maKH) || string.IsNullOrWhiteSpace(maPB) || string.IsNullOrWhiteSpace(maNV) || string.IsNullOrWhiteSpace(maCa))
                return KetQuaXuLy.Fail("Vui lòng nhập đầy đủ mã kế hoạch, phòng ban, nhân viên và ca làm việc.");
            if (denNgay.Date < tuNgay.Date)
                return KetQuaXuLy.Fail("Ngày kết thúc tuần phải lớn hơn hoặc bằng ngày bắt đầu.");
            if (ngayLamViec.Date < tuNgay.Date || ngayLamViec.Date > denNgay.Date)
                return KetQuaXuLy.Fail("Ngày phân ca làm việc phải nằm trong khoảng [Từ ngày, Đến ngày] của tuần kế hoạch.");

            using (var cn = Db.OpenConnection())
            using (var tr = cn.BeginTransaction())
            {
                try
                {
                    int daCoKH = Convert.ToInt32(Db.Scalar("SELECT COUNT(*) FROM dbo.KeHoachTuan WHERE MaKeHoach = @ma", Db.P("@ma", maKH.Trim())));
                    if (daCoKH == 0)
                    {
                        Db.ExecuteInTran(cn, tr,
                            @"INSERT INTO dbo.KeHoachTuan(MaKeHoach, MaPB, Tuan, TuNgay, DenNgay, NguoiLap)
                              VALUES(@ma, @pb, @tuan, @tu, @den, @nl)",
                            Db.P("@ma", maKH.Trim()), Db.P("@pb", maPB), Db.P("@tuan", tuan),
                            Db.P("@tu", tuNgay.Date), Db.P("@den", denNgay.Date),
                            Db.P("@nl", string.IsNullOrWhiteSpace(nguoiLap) ? "Trưởng bộ phận" : nguoiLap.Trim()));
                    }
                    Db.ExecuteInTran(cn, tr,
                        @"INSERT INTO dbo.ChiTietKeHoachTuan(MaKeHoach, MaNV, MaCa, NgayLamViec, GioPhuTroiDuKien, GhiChu)
                          VALUES(@ma, @nv, @ca, @ngay, @gioPT, @gc)",
                        Db.P("@ma", maKH.Trim()), Db.P("@nv", maNV), Db.P("@ca", maCa),
                        Db.P("@ngay", ngayLamViec.Date), Db.P("@gioPT", gioPTDuKien), Db.P("@gc", ghiChu));

                    tr.Commit();
                    return KetQuaXuLy.Ok("Đã lưu phân ca làm việc tuần và chuyển Ban chấm công theo dõi.");
                }
                catch (Exception ex)
                {
                    tr.Rollback();
                    return KetQuaXuLy.Fail("Lỗi lưu kế hoạch tuần: " + ex.Message);
                }
            }
        }
    }
}
