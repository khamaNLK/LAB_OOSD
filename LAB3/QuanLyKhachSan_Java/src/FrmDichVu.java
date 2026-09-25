import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class FrmDichVu extends JFrame {
    private DefaultTableModel model;
    private JTable table;
    private JTextField txtMaDV, txtTenDV, txtDonGia;

    public FrmDichVu() {
        setTitle("Quản Lý Dịch Vụ");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel pnlTop = new JPanel(new GridLayout(3, 2, 10, 10));
        pnlTop.setBorder(BorderFactory.createTitledBorder("Thông tin Dịch Vụ"));
        pnlTop.add(new JLabel("Mã DV:"));
        txtMaDV = new JTextField();
        pnlTop.add(txtMaDV);
        pnlTop.add(new JLabel("Tên Dịch Vụ:"));
        txtTenDV = new JTextField();
        pnlTop.add(txtTenDV);
        pnlTop.add(new JLabel("Đơn Giá (VNĐ):"));
        txtDonGia = new JTextField();
        pnlTop.add(txtDonGia);
        add(pnlTop, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[] { "Mã DV", "Tên Dịch Vụ", "Đơn Giá (VNĐ)" }, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                txtMaDV.setText(model.getValueAt(row, 0).toString());
                txtMaDV.setEditable(false);
                txtTenDV.setText(model.getValueAt(row, 1).toString());
                txtDonGia.setText(model.getValueAt(row, 2).toString());
            }
        });

        JPanel pnlBottom = new JPanel();
        JButton btnThem = new JButton("Thêm");
        JButton btnSua = new JButton("Sửa");
        JButton btnXoa = new JButton("Xóa");
        JButton btnLamMoi = new JButton("Làm mới");
        pnlBottom.add(btnThem);
        pnlBottom.add(btnSua);
        pnlBottom.add(btnXoa);
        pnlBottom.add(btnLamMoi);
        add(pnlBottom, BorderLayout.SOUTH);

        btnThem.addActionListener(e -> themDV());
        btnSua.addActionListener(e -> suaDV());
        btnXoa.addActionListener(e -> xoaDV());
        btnLamMoi.addActionListener(e -> {
            txtMaDV.setText("");
            txtMaDV.setEditable(true);
            txtTenDV.setText("");
            txtDonGia.setText("");
            table.clearSelection();
        });

        loadData();
    }

    private void loadData() {
        model.setRowCount(0);
        try (Connection conn = DbConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT MaDV, TenDV, DonGia FROM DichVu")) {
            while (rs.next()) {
                model.addRow(new Object[] { rs.getString(1), rs.getString(2), rs.getDouble(3) });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi nạp Dịch Vụ: " + ex.getMessage());
        }
    }

    private void themDV() {
        String ma = txtMaDV.getText().trim();
        String ten = txtTenDV.getText().trim();
        String giaStr = txtDonGia.getText().trim();
        if (ma.isEmpty() || ten.isEmpty() || giaStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đủ thông tin!");
            return;
        }
        try (Connection conn = DbConnection.getConnection();
                PreparedStatement stmt = conn
                        .prepareStatement("INSERT INTO DichVu(MaDV, TenDV, DonGia) VALUES(?, ?, ?)")) {
            stmt.setString(1, ma);
            stmt.setString(2, ten);
            stmt.setDouble(3, Double.parseDouble(giaStr));
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Thêm thành công!");
            loadData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi thêm: " + ex.getMessage());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Đơn giá phải là số hợp lệ!");
        }
    }

    private void suaDV() {
        String ma = txtMaDV.getText().trim();
        String ten = txtTenDV.getText().trim();
        String giaStr = txtDonGia.getText().trim();
        if (ma.isEmpty() || ten.isEmpty() || giaStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dịch vụ và nhập thông tin mới!");
            return;
        }
        try (Connection conn = DbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement("UPDATE DichVu SET TenDV=?, DonGia=? WHERE MaDV=?")) {
            stmt.setString(1, ten);
            stmt.setDouble(2, Double.parseDouble(giaStr));
            stmt.setString(3, ma);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Sửa thành công!");
            loadData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi sửa: " + ex.getMessage());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Đơn giá phải là số hợp lệ!");
        }
    }

    private void xoaDV() {
        String ma = txtMaDV.getText().trim();
        if (ma.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dịch vụ cần xóa!");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa?", "Xác nhận",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DbConnection.getConnection();
                    PreparedStatement stmt = conn.prepareStatement("DELETE FROM DichVu WHERE MaDV=?")) {
                stmt.setString(1, ma);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Xóa thành công!");
                loadData();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi xóa: " + ex.getMessage());
            }
        }
    }
}