import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class FrmTraPhong extends JFrame {
    private DefaultTableModel model;
    private JTable table;
    private JTextField txtSoHD, txtSoPhieu, txtTienPhong, txtTienDV;
    private JButton btnThanhToan, btnTaoHoaDon;

    public FrmTraPhong() {
        setTitle("Trả Phòng - Thanh Toán Hóa Đơn");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel pnlTop = new JPanel(new GridLayout(2, 4, 10, 10));
        pnlTop.setBorder(BorderFactory.createTitledBorder("Thông tin Hóa Đơn"));

        pnlTop.add(new JLabel("Số HD:"));
        txtSoHD = new JTextField();
        pnlTop.add(txtSoHD);

        pnlTop.add(new JLabel("Số Phiếu Đặt:"));
        txtSoPhieu = new JTextField();
        pnlTop.add(txtSoPhieu);

        pnlTop.add(new JLabel("Tiền Phòng:"));
        txtTienPhong = new JTextField();
        pnlTop.add(txtTienPhong);

        pnlTop.add(new JLabel("Tiền DV:"));
        txtTienDV = new JTextField();
        pnlTop.add(txtTienDV);

        add(pnlTop, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[] { "Số HĐ", "Số Phiếu Đặt", "Tiền Phòng", "Tiền DV", "Trạng Thái" },
                0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                txtSoHD.setText(model.getValueAt(row, 0).toString());
                txtSoPhieu.setText(model.getValueAt(row, 1).toString());
                txtTienPhong.setText(model.getValueAt(row, 2).toString());
                txtTienDV.setText(model.getValueAt(row, 3).toString());
            }
        });

        JPanel pnlBottom = new JPanel();
        btnTaoHoaDon = new JButton("Tạo Hóa Đơn (Tính Tiền)");
        btnThanhToan = new JButton("Thanh Toán (Xác nhận)");
        JButton btnLamMoi = new JButton("Làm mới");
        pnlBottom.add(btnTaoHoaDon);
        pnlBottom.add(btnThanhToan);
        pnlBottom.add(btnLamMoi);
        add(pnlBottom, BorderLayout.SOUTH);

        btnTaoHoaDon.addActionListener(e -> taoHoaDon());
        btnThanhToan.addActionListener(e -> thanhToan());
        btnLamMoi.addActionListener(e -> {
            txtSoHD.setText("");
            txtSoPhieu.setText("");
            txtTienPhong.setText("");
            txtTienDV.setText("");
            table.clearSelection();
            loadData();
        });

        loadData();
    }

    private void loadData() {
        model.setRowCount(0);
        try (Connection conn = DbConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt
                        .executeQuery("SELECT SoHoaDon, SoPhieuDat, TienPhong, TienDichVu, TrangThai FROM HoaDon")) {
            while (rs.next()) {
                model.addRow(new Object[] { rs.getString(1), rs.getString(2), rs.getDouble(3), rs.getDouble(4),
                        rs.getString(5) });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi nạp Hóa Đơn: " + ex.getMessage());
        }
    }

    private void taoHoaDon() {
        String soPhieu = txtSoPhieu.getText().trim();
        String soHD = txtSoHD.getText().trim();

        if (soPhieu.isEmpty() || soHD.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Số Phiếu Đặt và Số HĐ để tạo!");
            return;
        }

        try (Connection conn = DbConnection.getConnection()) {
            String sqlTienPhong = "SELECT p.DonGiaNgay * DATEDIFF(day, d.NgayNhan, GETDATE()) as TienPhong FROM PhieuDatPhong d JOIN ChiTietDatPhong c ON d.SoPhieuDat = c.SoPhieuDat JOIN Phong p ON c.SoPhong = p.SoPhong WHERE d.SoPhieuDat = ?";
            PreparedStatement stmt1 = conn.prepareStatement(sqlTienPhong);
            stmt1.setString(1, soPhieu);
            ResultSet rs1 = stmt1.executeQuery();
            double tienPhong = 0;
            if (rs1.next()) {
                tienPhong = rs1.getDouble("TienPhong");
                if (tienPhong <= 0)
                    tienPhong = 100000; // default nếu cùng ngày
            }

            // Tính tiền dịch vụ
            String sqlTienDV = "SELECT SUM(ThanhTien) as TienDV FROM ChiTietSuDungDV WHERE SoPhieuDat = ?";
            PreparedStatement stmt2 = conn.prepareStatement(sqlTienDV);
            stmt2.setString(1, soPhieu);
            ResultSet rs2 = stmt2.executeQuery();
            double tienDV = 0;
            if (rs2.next()) {
                tienDV = rs2.getDouble("TienDV");
            }

            String sqlInsert = "INSERT INTO HoaDon (SoHoaDon, SoPhieuDat, NhanVienLap, NgayLap, TienPhong, TienDichVu, TrangThai) VALUES (?, ?, 'NV01', GETDATE(), ?, ?, N'Chưa thanh toán')";
            PreparedStatement stmt3 = conn.prepareStatement(sqlInsert);
            stmt3.setString(1, soHD);
            stmt3.setString(2, soPhieu);
            stmt3.setDouble(3, tienPhong);
            stmt3.setDouble(4, tienDV);
            stmt3.executeUpdate();

            JOptionPane.showMessageDialog(this, "Tạo hóa đơn thành công!");
            loadData();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tạo hóa đơn: " + ex.getMessage());
        }
    }

    private void thanhToan() {
        String soHD = txtSoHD.getText().trim();
        if (soHD.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn cần thanh toán!");
            return;
        }

        try (Connection conn = DbConnection.getConnection();
                PreparedStatement stmt = conn
                        .prepareStatement("UPDATE HoaDon SET TrangThai = N'Đã thanh toán' WHERE SoHoaDon = ?")) {
            stmt.setString(1, soHD);
            int updated = stmt.executeUpdate();
            if (updated > 0) {
                JOptionPane.showMessageDialog(this, "Thanh toán thành công!");
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn!");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi thanh toán: " + ex.getMessage());
        }
    }
}