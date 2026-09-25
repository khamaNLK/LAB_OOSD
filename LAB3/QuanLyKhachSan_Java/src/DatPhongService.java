import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

public class DatPhongService {

    public List<String> layDanhSachPhong() {
        List<String> dsPhong = new ArrayList<>();
        String sql = "SELECT SoPhong, LoaiPhong FROM Phong WHERE TrangThai = N'Trống'";
        try (Connection conn = DbConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                dsPhong.add(rs.getString("SoPhong") + " - " + rs.getString("LoaiPhong"));
            }
        } catch (SQLException ex) {
            javax.swing.JOptionPane.showMessageDialog(null,
                    "Lỗi tải danh sách phòng. Chi tiết lỗi SQL:\n" + ex.getMessage(),
                    "Lỗi Kết Nối CSDL",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
        }
        return dsPhong;
    }

    public boolean taoDatPhong(String soPhieu, String maKhach, String maNV,
            java.sql.Date ngayNhan, java.sql.Date ngayTra,
            double tienCoc, String soPhong, int soNguoi,
            StringBuilder thongBao) {
        String sql = "{call sp_TaoDatPhong(?, ?, ?, ?, ?, ?, ?, ?)}";
        try (Connection conn = DbConnection.getConnection();
                CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setString(1, soPhieu);
            stmt.setString(2, maKhach);
            stmt.setString(3, maNV);
            stmt.setDate(4, ngayNhan);
            stmt.setDate(5, ngayTra);
            stmt.setDouble(6, tienCoc);
            stmt.setString(7, soPhong);
            stmt.setInt(8, soNguoi);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                thongBao.append(rs.getString("ThongBao"));
                return true;
            }
        } catch (SQLException ex) {
            thongBao.append("Lỗi nghiệp vụ:\n").append(ex.getMessage());
        }
        return false;
    }

    public DefaultTableModel layDanhSachPhieuDat() {
        DefaultTableModel model = new DefaultTableModel(
                new String[] { "Số Phiếu", "Khách Hàng", "Phòng", "Ngày Nhận", "Ngày Trả", "Trạng Thái" }, 0);
        String sql = "SELECT d.SoPhieuDat, k.HoTen, c.SoPhong, d.NgayNhan, d.NgayTraDuKien, d.TrangThai " +
                "FROM PhieuDatPhong d " +
                "JOIN KhachHang k ON d.MaKhach = k.MaKhach " +
                "JOIN ChiTietDatPhong c ON d.SoPhieuDat = c.SoPhieuDat " +
                "ORDER BY d.NgayLap DESC";
        try (Connection conn = DbConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                model.addRow(new Object[] {
                        rs.getString(1), rs.getString(2), rs.getString(3),
                        rs.getDate(4), rs.getDate(5), rs.getString(6)
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return model;
    }

    public List<String> layDanhSachKhachHang() {
        List<String> dsKhach = new ArrayList<>();
        String sql = "SELECT MaKhach, HoTen FROM KhachHang";
        try (Connection conn = DbConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                dsKhach.add(rs.getString("MaKhach") + " - " + rs.getString("HoTen"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return dsKhach;
    }

    public boolean capNhatDatPhong(String soPhieu, java.sql.Date ngayTraMoi, double tienCocMoi,
            StringBuilder thongBao) {
        String sql = "UPDATE PhieuDatPhong SET NgayTraDuKien = ?, TienCoc = ? WHERE SoPhieuDat = ? AND TrangThai = N'Đã đặt'";
        try (Connection conn = DbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, ngayTraMoi);
            stmt.setDouble(2, tienCocMoi);
            stmt.setString(3, soPhieu);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                thongBao.append("Cập nhật thông tin phiếu đặt thành công.");
                return true;
            } else {
                thongBao.append("Chỉ có thể sửa phiếu đang ở trạng thái 'Đã đặt'.");
            }
        } catch (SQLException ex) {
            thongBao.append("Lỗi cập nhật:\n").append(ex.getMessage());
        }
        return false;
    }

    public boolean huyDatPhong(String soPhieu, String soPhong, StringBuilder thongBao) {
        String sqlHuyPhieu = "UPDATE PhieuDatPhong SET TrangThai = N'Hủy' WHERE SoPhieuDat = ? AND TrangThai = N'Đã đặt'";
        String sqlGiaiPhong = "UPDATE Phong SET TrangThai = N'Trống' WHERE SoPhong = ?";

        try (Connection conn = DbConnection.getConnection()) {
            conn.setAutoCommit(false); // Bắt đầu Transaction
            try (PreparedStatement st1 = conn.prepareStatement(sqlHuyPhieu);
                    PreparedStatement st2 = conn.prepareStatement(sqlGiaiPhong)) {

                st1.setString(1, soPhieu);
                if (st1.executeUpdate() == 0) {
                    thongBao.append("Không thể hủy phiếu này (Phiếu đã nhận phòng hoặc đã hủy).");
                    conn.rollback();
                    return false;
                }

                st2.setString(1, soPhong);
                st2.executeUpdate();

                conn.commit();
                thongBao.append("Đã hủy phiếu và giải phóng phòng thành công.");
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        } catch (SQLException ex) {
            thongBao.append("Lỗi hệ thống:\n").append(ex.getMessage());
        }
        return false;
    }

    public boolean nhậnPhong(String soPhieu, String soPhong, StringBuilder thongBao) {
        String sqlNhanPhieu = "UPDATE PhieuDatPhong SET TrangThai = N'Đang ở', NgayNhanThucTe = GETDATE() WHERE SoPhieuDat = ? AND TrangThai = N'Đã đặt'";
        String sqlCapNhatPhong = "UPDATE Phong SET TrangThai = N'Đang ở' WHERE SoPhong = ?";

        try (Connection conn = DbConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement st1 = conn.prepareStatement(sqlNhanPhieu);
                    PreparedStatement st2 = conn.prepareStatement(sqlCapNhatPhong)) {

                st1.setString(1, soPhieu);
                if (st1.executeUpdate() == 0) {
                    thongBao.append("Phiếu không hợp lệ để nhận phòng.");
                    conn.rollback();
                    return false;
                }

                st2.setString(1, soPhong);
                st2.executeUpdate();

                conn.commit();
                thongBao.append("Khách đã nhận phòng thành công.");
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        } catch (SQLException ex) {
            thongBao.append("Lỗi nhận phòng:\n").append(ex.getMessage());
        }
        return false;
    }

    public boolean traPhong(String soPhieu, String soPhong, StringBuilder thongBao) {
        String sqlTraPhieu = "UPDATE PhieuDatPhong SET TrangThai = N'Đã trả', NgayTraThucTe = GETDATE() WHERE SoPhieuDat = ? AND TrangThai = N'Đang ở'";
        String sqlTraPhong = "UPDATE Phong SET TrangThai = N'Trống' WHERE SoPhong = ?";

        try (Connection conn = DbConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement st1 = conn.prepareStatement(sqlTraPhieu);
                    PreparedStatement st2 = conn.prepareStatement(sqlTraPhong)) {

                st1.setString(1, soPhieu);
                if (st1.executeUpdate() == 0) {
                    thongBao.append("Phòng này chưa nhận, không thể trả.");
                    conn.rollback();
                    return false;
                }

                st2.setString(1, soPhong);
                st2.executeUpdate();

                conn.commit();
                thongBao.append("Đã hoàn tất trả phòng. Phòng đang trống.");
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        } catch (SQLException ex) {
            thongBao.append("Lỗi trả phòng:\n").append(ex.getMessage());
        }
        return false;
    }
}
