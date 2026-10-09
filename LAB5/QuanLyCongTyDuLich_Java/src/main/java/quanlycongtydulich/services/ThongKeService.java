package quanlycongtydulich.services;

import java.sql.Date;
import java.time.LocalDate;
import javax.swing.table.DefaultTableModel;
import quanlycongtydulich.data.Db;
import quanlycongtydulich.services.Models.QuyDinh;

public class ThongKeService {
    public DefaultTableModel luongHDV(int thang, int nam) {
        String sql = "SELECT h.MaHDV, h.HoTen, h.LuongCoBan, "
                   + "COUNT(p.MaPC) AS SoTour, ISNULL(SUM(p.ThuLaoTour), 0) AS LuongTheoTour, "
                   + "h.LuongCoBan + ISNULL(SUM(p.ThuLaoTour), 0) AS TongLuong "
                   + "FROM HuongDanVien h "
                   + "LEFT JOIN PhanCongHDV p ON h.MaHDV = p.MaHDV AND MONTH(p.NgayKetThuc) = ? AND YEAR(p.NgayKetThuc) = ? "
                   + "WHERE h.DangLamViec = 1 "
                   + "GROUP BY h.MaHDV, h.HoTen, h.LuongCoBan ORDER BY h.HoTen";
        return Db.query(sql, thang, nam);
    }

    public DefaultTableModel tongHop(LocalDate tu, LocalDate den) {
        String sql = "SELECT N'Đăng ký khách lẻ' AS ChiSo, COUNT(*) AS SoLuong, ISNULL(SUM(ThanhTien), 0) AS GiaTri "
                   + "FROM DangKyLe WHERE CAST(NgayDangKy AS date) BETWEEN ? AND ? "
                   + "UNION ALL SELECT N'Đăng ký đoàn (không tính phiếu hủy)', COUNT(*), ISNULL(SUM(TongTienDuKien), 0) "
                   + "FROM DangKyDoan WHERE CAST(NgayDangKy AS date) BETWEEN ? AND ? AND TrangThai <> ? "
                   + "UNION ALL SELECT N'Phiếu đoàn hủy - mất cọc', COUNT(*), ISNULL(SUM(TienCoc), 0) "
                   + "FROM DangKyDoan WHERE CAST(NgayDangKy AS date) BETWEEN ? AND ? AND TrangThai = ? "
                   + "UNION ALL SELECT N'Thanh toán sau tour của đoàn', COUNT(*), ISNULL(SUM(SoTien), 0) "
                   + "FROM ThanhToanDoan WHERE CAST(NgayThanhToan AS date) BETWEEN ? AND ? "
                   + "UNION ALL SELECT N'Khảo sát có phản hồi (giá trị = điểm trung bình)', COUNT(*), "
                   + "CAST(ISNULL(AVG(CAST(DiemDanhGia AS decimal(5,2))), 0) AS decimal(18,2)) "
                   + "FROM KhaoSat WHERE NgayPhanHoi BETWEEN ? AND ?";
        return Db.query(sql, Date.valueOf(tu), Date.valueOf(den),
                             Date.valueOf(tu), Date.valueOf(den), QuyDinh.HUY_MAT_COC,
                             Date.valueOf(tu), Date.valueOf(den), QuyDinh.HUY_MAT_COC,
                             Date.valueOf(tu), Date.valueOf(den),
                             Date.valueOf(tu), Date.valueOf(den));
    }
}
