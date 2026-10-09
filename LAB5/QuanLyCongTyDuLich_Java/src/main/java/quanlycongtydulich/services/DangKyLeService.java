package quanlycongtydulich.services;

import java.math.BigDecimal;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;
import quanlycongtydulich.data.Db;
import quanlycongtydulich.services.Models.*;

public class DangKyLeService {
    public DefaultTableModel layDanhSach() {
        return Db.query("SELECT d.SoDKLe, d.MaChuyen, t.TenTour, c.NgayDi, c.NgayVe, b.TenDiemBan, "
                      + "d.TenNguoiDangKy, d.DienThoai, d.SoNguoi, d.ThanhTien, d.TrangThai "
                      + "FROM DangKyLe d JOIN ChuyenLe c ON d.MaChuyen = c.MaChuyen "
                      + "JOIN Tour t ON c.MaTour = t.MaTour "
                      + "JOIN DiemBanVe b ON d.MaDiemBan = b.MaDiemBan ORDER BY d.NgayDangKy DESC");
    }

    public ProcessResult dangKy(String soDK, String maChuyen, String maDiemBan, String ten, String dienThoai, int soNguoi) {
        if (soDK.isEmpty() || maChuyen.isEmpty() || maDiemBan.isEmpty() || ten.isEmpty() || dienThoai.isEmpty())
            return ProcessResult.fail("Thông tin đăng ký khách lẻ chưa đầy đủ.");
        if (soNguoi <= 0) return ProcessResult.fail("Số người phải lớn hơn 0.");
        if (soNguoi >= QuyDinh.MOC_KHACH_DOAN)
            return ProcessResult.fail("Khách lẻ phải dưới 12 người. Từ 13 người trở lên đăng ký theo đoàn; đúng 12 người đề không quy định.");

        Object o = Db.scalar("SELECT t.DonGiaKhach FROM ChuyenLe c JOIN Tour t ON c.MaTour = t.MaTour WHERE c.MaChuyen = ? AND c.TrangThai = ?",
                             maChuyen, QuyDinh.MO_DANG_KY);
        if (o == null) return ProcessResult.fail("Chuyến không tồn tại hoặc đã đóng đăng ký.");
        BigDecimal donGia = (BigDecimal)o;
        BigDecimal thanhTien = donGia.multiply(BigDecimal.valueOf(soNguoi));

        try {
            Db.execute("INSERT INTO DangKyLe(SoDKLe, MaChuyen, MaDiemBan, NgayDangKy, TenNguoiDangKy, DienThoai, SoNguoi, ThanhTien, DaThanhToan, TrangThai) "
                     + "VALUES(?,?,?,SYSDATETIME(),?,?,?,?,1,?)",
                     soDK, maChuyen, maDiemBan, ten, dienThoai, soNguoi, thanhTien, QuyDinh.DA_DANG_KY);
            return ProcessResult.ok("Đã đăng ký và thanh toán vé. Thành tiền: " + thanhTien + " đ.");
        } catch (SQLException e) {
            return ProcessResult.fail(e.getMessage());
        }
    }
}
