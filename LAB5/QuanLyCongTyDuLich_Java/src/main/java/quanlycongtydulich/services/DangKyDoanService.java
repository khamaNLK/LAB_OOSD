package quanlycongtydulich.services;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import quanlycongtydulich.data.Db;
import quanlycongtydulich.services.Models.*;

public class DangKyDoanService {

    public DefaultTableModel layDanhSach() {
        String sql = "SELECT d.SoDKDoan, k.TenCoQuanDaiDien, t.TenTour, d.NgayDi, d.NgayKetThucDuKien, "
                   + "d.SoNguoi, d.MuaBaoHiem, d.TienCoc, d.TongTienDuKien, d.TrangThai "
                   + "FROM DangKyDoan d JOIN DoanKhach k ON d.MaDoan = k.MaDoan JOIN Tour t ON d.MaTour = t.MaTour "
                   + "ORDER BY d.NgayDangKy DESC";
        return Db.query(sql);
    }

    // Nghiệp vụ lập phiếu đoàn chạy trong 1 Database Transaction
    public ProcessResult dangKy(String soDK, String maDoan, String tenCQ, String diaChi, String dienThoai,
                                String nguoiDaiDien, String maTour, LocalDate ngayDi, int soNguoi,
                                String diaDiemDon, boolean muaBaoHiem, BigDecimal tienCoc,
                                List<ThanhVienDoanItem> thanhVien) {
        if (soDK.isEmpty() || maDoan.isEmpty() || tenCQ.isEmpty() || maTour.isEmpty())
            return ProcessResult.fail("Thông tin phiếu đăng ký đoàn chưa đầy đủ.");
        if (soNguoi <= QuyDinh.MOC_KHACH_DOAN)
            return ProcessResult.fail("Khách theo đoàn phải trên 12 người. Dưới 12 người đăng ký khách lẻ.");
        if (ngayDi.isBefore(LocalDate.now().plusDays(1)))
            return ProcessResult.fail("Ngày đi phải sau ngày đăng ký.");
        if (tienCoc.compareTo(BigDecimal.ZERO) <= 0)
            return ProcessResult.fail("Khách đoàn phải đặt cọc trước một khoản tiền.");
        if (muaBaoHiem && (thanhVien == null || thanhVien.size() != soNguoi))
            return ProcessResult.fail("Đoàn mua bảo hiểm phải kèm danh sách đủ " + soNguoi + " người cùng đi.");

        try (Connection cn = Db.openConnection()) {
            cn.setAutoCommit(false); // Bắt đầu Transaction
            try {
                // Lấy thông tin số ngày và đơn giá tour
                int soNgay; BigDecimal donGia;
                try (PreparedStatement ps = cn.prepareStatement("SELECT SoNgay, DonGiaKhach FROM Tour WHERE MaTour = ? AND DangMoBan = 1")) {
                    ps.setString(1, maTour);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) { cn.rollback(); return ProcessResult.fail("Tour không tồn tại hoặc chưa mở bán."); }
                        soNgay = rs.getInt(1); donGia = rs.getBigDecimal(2);
                    }
                }

                LocalDate ketThuc = ngayDi.plusDays(soNgay - 1);
                BigDecimal tongDuKien = donGia.multiply(BigDecimal.valueOf(soNguoi));
                if (tienCoc.compareTo(tongDuKien) > 0) {
                    cn.rollback(); return ProcessResult.fail("Tiền cọc không được vượt tổng tiền dự kiến.");
                }

                // 1. Lưu thông tin Đoàn Khách
                String sqlDK = "IF EXISTS(SELECT 1 FROM DoanKhach WHERE MaDoan = ?) "
                             + "UPDATE DoanKhach SET TenCoQuanDaiDien=?, DiaChi=?, DienThoai=?, NguoiDaiDien=? WHERE MaDoan=? "
                             + "ELSE INSERT INTO DoanKhach(MaDoan, TenCoQuanDaiDien, DiaChi, DienThoai, NguoiDaiDien) VALUES(?,?,?,?,?)";
                try (PreparedStatement ps = cn.prepareStatement(sqlDK)) {
                    ps.setString(1, maDoan); ps.setString(2, tenCQ); ps.setString(3, diaChi);
                    ps.setString(4, dienThoai); ps.setString(5, nguoiDaiDien); ps.setString(6, maDoan);
                    ps.setString(7, maDoan); ps.setString(8, tenCQ); ps.setString(9, diaChi);
                    ps.setString(10, dienThoai); ps.setString(11, nguoiDaiDien);
                    ps.executeUpdate();
                }

                // 2. Lưu Phiếu Đăng Ký Đoàn
                String sqlPK = "INSERT INTO DangKyDoan(SoDKDoan, MaDoan, MaTour, NgayDangKy, NgayDi, NgayKetThucDuKien, "
                             + "SoNguoi, DiaDiemDon, MuaBaoHiem, TienCoc, DaThanhToanCoc, TongTienDuKien, TrangThai) "
                             + "VALUES(?, ?, ?, SYSDATETIME(), ?, ?, ?, ?, ?, ?, 1, ?, ?)";
                try (PreparedStatement ps = cn.prepareStatement(sqlPK)) {
                    ps.setString(1, soDK); ps.setString(2, maDoan); ps.setString(3, maTour);
                    ps.setDate(4, Date.valueOf(ngayDi)); ps.setDate(5, Date.valueOf(ketThuc));
                    ps.setInt(6, soNguoi); ps.setString(7, diaDiemDon); ps.setBoolean(8, muaBaoHiem);
                    ps.setBigDecimal(9, tienCoc); ps.setBigDecimal(10, tongDuKien); ps.setString(11, QuyDinh.DA_DANG_KY);
                    ps.executeUpdate();
                }

                // 3. Lưu Danh sách Thành viên (nếu có mua bảo hiểm)
                if (muaBaoHiem && thanhVien != null) {
                    String sqlTV = "INSERT INTO ThanhVienDoan(SoDKDoan, STT, HoTen, NgaySinh, SoGiayTo) VALUES(?, ?, ?, ?, ?)";
                    try (PreparedStatement ps = cn.prepareStatement(sqlTV)) {
                        for (int i = 0; i < thanhVien.size(); i++) {
                            ThanhVienDoanItem tv = thanhVien.get(i);
                            ps.setString(1, soDK); ps.setInt(2, i + 1); ps.setString(3, tv.hoTen);
                            ps.setDate(4, tv.ngaySinh != null ? Date.valueOf(tv.ngaySinh) : null);
                            ps.setString(5, tv.soGiayTo);
                            ps.addBatch();
                        }
                        ps.executeBatch();
                    }
                }

                cn.commit(); // Hoàn tất giao dịch
                return ProcessResult.ok("Đã lập phiếu thành công. Kết thúc dự kiến: " + ketThuc + "; Tổng tiền: " + tongDuKien + " đ.");
            } catch (SQLException ex) {
                cn.rollback();
                return ProcessResult.fail("Lỗi CSDL: " + ex.getMessage());
            }
        } catch (SQLException ex) {
            return ProcessResult.fail("Lỗi kết nối: " + ex.getMessage());
        }
    }

    // Nghiệp vụ Hủy phiếu đoàn mất cọc và gỡ phân công HDV
    public ProcessResult huyDangKy(String soDK) {
        if (soDK.isEmpty()) return ProcessResult.fail("Chưa chọn phiếu đoàn.");
        try (Connection cn = Db.openConnection()) {
            cn.setAutoCommit(false);
            try {
                // Xóa phân công HDV của đoàn
                try (PreparedStatement ps = cn.prepareStatement("DELETE FROM PhanCongHDV WHERE SoDKDoan = ?")) {
                    ps.setString(1, soDK); ps.executeUpdate();
                }
                // Cập nhật trạng thái phiếu
                try (PreparedStatement ps = cn.prepareStatement("UPDATE DangKyDoan SET TrangThai = ? WHERE SoDKDoan = ?")) {
                    ps.setString(1, QuyDinh.HUY_MAT_COC); ps.setString(2, soDK); ps.executeUpdate();
                }
                cn.commit();
                return ProcessResult.ok("Đã hủy phiếu " + soDK + ". Đoàn không đi bị mất tiền cọc.");
            } catch (SQLException ex) {
                cn.rollback();
                return ProcessResult.fail(ex.getMessage());
            }
        } catch (SQLException ex) {
            return ProcessResult.fail(ex.getMessage());
        }
    }
}