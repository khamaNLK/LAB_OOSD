import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class FrmDanhMuc extends JFrame {
    private DefaultTableModel model;
    private JTable table;
    private JTextField txtMaKhuVuc, txtTenKhuVuc;

    public FrmDanhMuc() {
        setTitle("Quản Lý Danh Mục");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel pnlTop = new JPanel(new GridLayout(2, 2, 10, 10));
        pnlTop.setBorder(BorderFactory.createTitledBorder("Thông tin Khu Vực"));
        pnlTop.add(new JLabel("Mã Khu Vực:"));
        txtMaKhuVuc = new JTextField();
        pnlTop.add(txtMaKhuVuc);
        pnlTop.add(new JLabel("Tên Khu Vực:"));
        txtTenKhuVuc = new JTextField();
        pnlTop.add(txtTenKhuVuc);
        add(pnlTop, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[] { "Mã Khu Vực", "Tên Khu Vực" }, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                txtMaKhuVuc.setText(model.getValueAt(row, 0).toString());
                txtMaKhuVuc.setEditable(false);
                txtTenKhuVuc.setText(model.getValueAt(row, 1).toString());
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

        btnThem.addActionListener(e -> themKhuVuc());
        btnSua.addActionListener(e -> suaKhuVuc());
        btnXoa.addActionListener(e -> xoaKhuVuc());
        btnLamMoi.addActionListener(e -> {
            txtMaKhuVuc.setText("");
            txtMaKhuVuc.setEditable(true);
            txtTenKhuVuc.setText("");
            table.clearSelection();
        });

        loadData();
    }

    private void loadData() {
        model.setRowCount(0);
        try (Connection conn = DbConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT MaKhuVuc, TenKhuVuc FROM KhuVuc")) {
            while (rs.next()) {
                model.addRow(new Object[] { rs.getString("MaKhuVuc"), rs.getString("TenKhuVuc") });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi nạp Danh Mục: " + ex.getMessage());
        }
    }

    private void themKhuVuc() {
        String ma = txtMaKhuVuc.getText().trim();
        String ten = txtTenKhuVuc.getText().trim();
        if (ma.isEmpty() || ten.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đủ thông tin!");
            return;
        }
        try (Connection conn = DbConnection.getConnection();
                PreparedStatement stmt = conn
                        .prepareStatement("INSERT INTO KhuVuc(MaKhuVuc, TenKhuVuc) VALUES(?, ?)")) {
            stmt.setString(1, ma);
            stmt.setString(2, ten);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Thêm thành công!");
            loadData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi thêm: " + ex.getMessage());
        }
    }

    private void suaKhuVuc() {
        String ma = txtMaKhuVuc.getText().trim();
        String ten = txtTenKhuVuc.getText().trim();
        if (ma.isEmpty() || ten.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khu vực cần sửa và nhập tên mới!");
            return;
        }
        try (Connection conn = DbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement("UPDATE KhuVuc SET TenKhuVuc=? WHERE MaKhuVuc=?")) {
            stmt.setString(1, ten);
            stmt.setString(2, ma);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Sửa thành công!");
            loadData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi sửa: " + ex.getMessage());
        }
    }

    private void xoaKhuVuc() {
        String ma = txtMaKhuVuc.getText().trim();
        if (ma.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khu vực cần xóa!");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa?", "Xác nhận",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DbConnection.getConnection();
                    PreparedStatement stmt = conn.prepareStatement("DELETE FROM KhuVuc WHERE MaKhuVuc=?")) {
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