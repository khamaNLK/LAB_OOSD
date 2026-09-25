import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class FrmThongKe extends JFrame {
    public FrmThongKe() {
        setTitle("Thống Kê Tổng Hợp");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 1, 10, 10));

        try (Connection conn = DbConnection.getConnection();
                Statement stmt = conn.createStatement()) {

            ResultSet rsDat = stmt.executeQuery("SELECT COUNT(*) FROM PhieuDatPhong");
            if (rsDat.next())
                add(new JLabel("  Tổng số phiếu đặt phòng: " + rsDat.getInt(1)));

            ResultSet rsHoaDon = stmt.executeQuery("SELECT COUNT(*) FROM HoaDon");
            if (rsHoaDon.next())
                add(new JLabel("  Tổng số hóa đơn đã xuất: " + rsHoaDon.getInt(1)));

            ResultSet rsDoanhThu = stmt.executeQuery(
                    "SELECT ISNULL(SUM(TienPhong + TienDichVu), 0) FROM HoaDon WHERE TrangThai = N'Đã thanh toán'");
            if (rsDoanhThu.next())
                add(new JLabel("  Tổng doanh thu (VNĐ): " + rsDoanhThu.getDouble(1)));

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi thống kê: " + ex.getMessage());
        }
    }
}