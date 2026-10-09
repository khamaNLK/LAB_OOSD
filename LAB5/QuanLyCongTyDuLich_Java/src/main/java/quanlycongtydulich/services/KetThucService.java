package quanlycongtydulich.services;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import javax.swing.table.DefaultTableModel;
import quanlycongtydulich.data.Db;
import quanlycongtydulich.services.Models.*;

public class KetThucService {
    public DefaultTableModel doanCanThanhToan() {
        String sql = "SELECT d.SoDKDoan, k.TenCoQuanDaiDien, t.TenTour, d.NgayKetThucDuKien, "
                   + "d.TongTienDuKien, d.TienCoc, "
                   + "ISNULL(SUM(tt.SoTien), 0) AS DaTraSauTour, "
                   + "d.TongTienDuKien - d.TienCoc - ISNULL(SUM(tt.SoTien), 0) AS ConLai, d.TrangThai "
                   + "FROM DangKyDoan d JOIN DoanKhach k ON d.MaDoan = k.MaDoan JOIN Tour t ON d.MaTour = t.MaTour "
                   + "LEFT JOIN ThanhToanDoan tt ON d.SoDKDoan = tt.SoDKDoan "
                   + "WHERE d.TrangThai <> ? "
                   + "GROUP BY d.SoDKDoan, k.TenCoQuanDaiDien, t.TenTour, d.NgayKetThucDuKien, "
                   + "d.TongTienDuKien, d.TienCoc, d.TrangThai "
                   + "ORDER BY d.NgayKetThucDuKien DESC";
        return Db.query(sql, QuyDinh.HUY_MAT_COC);
    }

    public ProcessResult thanhToanDoan(String soTT, String soDK, LocalDate ngay, BigDecimal soTien, String ghiChu) {
        if (soTT.isEmpty() || soDK.isEmpty() || soTien.compareTo(BigDecimal.ZERO) <= 0)
            return ProcessResult.fail("Thông tin thanh toán không hợp lệ.");
        DefaultTableModel dt = Db.query("SELECT NgayKetThucDuKien, TongTienDuKien, TienCoc, TrangThai FROM DangKyDoan WHERE SoDKDoan = ?", soDK);
        if (dt.getRowCount() == 0) return ProcessResult.fail("Không tìm thấy phiếu đăng ký đoàn.");

        String trangThai = String.valueOf(dt.getValueAt(0, 3));
        if (!trangThai.equals(QuyDinh.DA_DANG_KY)) return ProcessResult.fail("Phiếu đã hủy hoặc đã thanh toán đủ.");

        Date dKt = (Date) dt.getValueAt(0, 0);
        if (!ngay.isAfter(dKt.toLocalDate()))
            return ProcessResult.fail("Kinh phí đoàn chỉ thanh toán sau khi kết thúc chuyến tham quan.");

        BigDecimal tong = new BigDecimal(dt.getValueAt(0, 1).toString());
        BigDecimal coc = new BigDecimal(dt.getValueAt(0, 2).toString());
        Object daTraObj = Db.scalar("SELECT ISNULL(SUM(SoTien), 0) FROM ThanhToanDoan WHERE SoDKDoan = ?", soDK);
        BigDecimal daTra = daTraObj == null ? BigDecimal.ZERO : new BigDecimal(daTraObj.toString());
        BigDecimal conLai = tong.subtract(coc).subtract(daTra);

        if (soTien.compareTo(conLai) > 0)
            return ProcessResult.fail("Số tiền vượt số còn phải thanh toán (" + conLai + " đ).");

        try {
            Db.execute("INSERT INTO ThanhToanDoan(SoTT, SoDKDoan, NgayThanhToan, SoTien, GhiChu) VALUES(?,?,?,?,?)",
                       soTT, soDK, Date.valueOf(ngay), soTien, ghiChu);
            if (soTien.compareTo(conLai) == 0) {
                Db.execute("UPDATE DangKyDoan SET TrangThai = ? WHERE SoDKDoan = ?", QuyDinh.HOAN_TAT_THANH_TOAN, soDK);
                return ProcessResult.ok("Đã ghi nhận thanh toán; đoàn đã hoàn tất thanh toán.");
            }
            return ProcessResult.ok("Đã ghi nhận thanh toán; còn lại " + conLai.subtract(soTien) + " đ.");
        } catch (SQLException e) { return ProcessResult.fail(e.getMessage()); }
    }

    public DefaultTableModel layDangKyChoKhaoSat(String loai) {
        if (loai.equals(QuyDinh.LE)) {
            String sql = "SELECT d.SoDKLe AS Ma, d.SoDKLe + ' - ' + d.TenNguoiDangKy AS HienThi FROM DangKyLe d "
                       + "JOIN ChuyenLe c ON d.MaChuyen = c.MaChuyen "
                       + "WHERE c.NgayVe < CAST(GETDATE() AS date) AND NOT EXISTS(SELECT 1 FROM KhaoSat k WHERE k.SoDKLe = d.SoDKLe) "
                       + "ORDER BY d.SoDKLe";
            return Db.query(sql);
        } else {
            String sql = "SELECT d.SoDKDoan AS Ma, d.SoDKDoan + ' - ' + k.TenCoQuanDaiDien AS HienThi FROM DangKyDoan d "
                       + "JOIN DoanKhach k ON d.MaDoan = k.MaDoan "
                       + "WHERE d.TrangThai <> ? AND d.NgayKetThucDuKien < CAST(GETDATE() AS date) "
                       + "AND NOT EXISTS(SELECT 1 FROM KhaoSat s WHERE s.SoDKDoan = d.SoDKDoan) ORDER BY d.SoDKDoan";
            return Db.query(sql, QuyDinh.HUY_MAT_COC);
        }
    }

    public DefaultTableModel layKhaoSat() {
        return Db.query("SELECT MaKhaoSat, LoaiKhach, ISNULL(SoDKLe, SoDKDoan) AS SoDangKy, NgayGui, NgayPhanHoi, DiemDanhGia, GopY FROM KhaoSat ORDER BY NgayGui DESC");
    }

    public ProcessResult guiKhaoSat(String maKS, String loai, String soDK, LocalDate ngayGui) {
        if (maKS.isEmpty() || soDK.isEmpty()) return ProcessResult.fail("Thông tin khảo sát chưa đầy đủ.");
        try {
            Date dKt;
            if (loai.equals(QuyDinh.LE)) {
                dKt = (Date) Db.scalar("SELECT c.NgayVe FROM DangKyLe d JOIN ChuyenLe c ON d.MaChuyen = c.MaChuyen WHERE d.SoDKLe = ?", soDK);
            } else {
                dKt = (Date) Db.scalar("SELECT NgayKetThucDuKien FROM DangKyDoan WHERE SoDKDoan = ? AND TrangThai <> ?", soDK, QuyDinh.HUY_MAT_COC);
            }
            if (dKt == null) return ProcessResult.fail("Không tìm thấy đăng ký hợp lệ.");
            if (!ngayGui.isAfter(dKt.toLocalDate()))
                return ProcessResult.fail("Phiếu khảo sát chỉ gửi sau khi kết thúc tour.");

            Db.execute("INSERT INTO KhaoSat(MaKhaoSat, LoaiKhach, SoDKLe, SoDKDoan, NgayGui) VALUES(?,?,?,?,?)",
                       maKS, loai, loai.equals(QuyDinh.LE) ? soDK : null, loai.equals(QuyDinh.DOAN) ? soDK : null, Date.valueOf(ngayGui));
            return ProcessResult.ok("Đã gửi phiếu khảo sát " + maKS + ".");
        } catch (SQLException e) { return ProcessResult.fail(e.getMessage()); }
    }

    public ProcessResult ghiPhanHoi(String maKS, LocalDate ngayPhanHoi, int diem, String gopY) {
        if (maKS.isEmpty()) return ProcessResult.fail("Chưa chọn phiếu khảo sát.");
        if (diem < 1 || diem > 5) return ProcessResult.fail("Điểm đánh giá phải từ 1 đến 5.");
        try {
            Date dGui = (Date) Db.scalar("SELECT NgayGui FROM KhaoSat WHERE MaKhaoSat = ?", maKS);
            if (dGui == null) return ProcessResult.fail("Không tìm thấy phiếu khảo sát.");
            if (ngayPhanHoi.isBefore(dGui.toLocalDate()))
                return ProcessResult.fail("Ngày phản hồi không được trước ngày gửi.");

            Db.execute("UPDATE KhaoSat SET NgayPhanHoi = ?, DiemDanhGia = ?, GopY = ? WHERE MaKhaoSat = ?",
                       Date.valueOf(ngayPhanHoi), diem, gopY, maKS);
            return ProcessResult.ok("Đã ghi nhận góp ý của khách hàng.");
        } catch (SQLException e) { return ProcessResult.fail(e.getMessage()); }
    }
}
