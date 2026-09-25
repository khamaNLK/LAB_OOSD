import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class FrmPhongTienNghi extends JFrame {
    private DefaultTableModel model;
    private JTable table;
    private JTextField txtSoPhong, txtMaKhuVuc, txtSoNguoi, txtDonGia;
    private JComboBox<String> cboTrangThai, cboLoaiPhong;

    public FrmPhongTienNghi() {
        setTitle("Quản Lý Phòng - Tiện Nghi");
        setSize(850, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel pnlTop = new JPanel(new GridLayout(3, 4, 10, 10));
        pnlTop.setBorder(BorderFactory.createTitledBorder("Thông tin Phòng"));

        pnlTop.add(new JLabel("Số Phòng:"));
        txtSoPhong = new JTextField();
        pnlTop.add(txtSoPhong);

        pnlTop.add(new JLabel("Mã Khu Vực:"));
        txtMaKhuVuc = new JTextField();
        pnlTop.add(txtMaKhuVuc);

        pnlTop.add(new JLabel("Sức Chứa:"));
        txtSoNguoi = new JTextField();
        pnlTop.add(txtSoNguoi);

        pnlTop.add(new JLabel("Đơn Giá:"));
        txtDonGia = new JTextField();
        pnlTop.add(txtDonGia);

        pnlTop.add(new JLabel("Loại Phòng:"));
        cboLoaiPhong = new JComboBox<>(new String[] { "Phòng Đơn", "Phòng Đôi", "Phòng VIP", "Phòng Gia Đình" });
        pnlTop.add(cboLoaiPhong);

        pnlTop.add(new JLabel("Trạng Thái:"));
        cboTrangThai = new JComboBox<>(new String[] { "Trống", "Đang ở", "Đã đặt", "Bảo trì" });
        pnlTop.add(cboTrangThai);

        add(pnlTop, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new String[] { "Số Phòng", "Khu Vực", "Loại Phòng", "Sức Chứa", "Đơn Giá", "Trạng Thái" }, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                txtSoPhong.setText(model.getValueAt(row, 0).toString());
                txtSoPhong.setEditable(false);
                txtMaKhuVuc.setText(model.getValueAt(row, 1) != null ? model.getValueAt(row, 1).toString() : "");
                cboLoaiPhong.setSelectedItem(model.getValueAt(row, 2).toString());
                txtSoNguoi.setText(model.getValueAt(row, 3).toString());
                txtDonGia.setText(model.getValueAt(row, 4).toString());
                cboTrangThai.setSelectedItem(model.getValueAt(row, 5).toString());
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

        btnThem.addActionListener(e -> themPhong());
        btnSua.addActionListener(e -> suaPhong());
        btnXoa.addActionListener(e -> xoaPhong());
        btnLamMoi.addActionListener(e -> {
            txtSoPhong.setText("");
            txtSoPhong.setEditable(true);
            txtMaKhuVuc.setText("");
            txtSoNguoi.setText("");
            txtDonGia.setText("");
            cboLoaiPhong.setSelectedIndex(0);
            cboTrangThai.setSelectedIndex(0);
            table.clearSelection();
        });

        loadData();
    }

    private void loadData() {
        model.setRowCount(0);
        try (Connection conn = DbConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(
                        "SELECT SoPhong, MaKhuVuc, LoaiPhong, SoNguoiToiDa, DonGiaNgay, TrangThai FROM Phong")) {
            while (rs.next()) {
                model.addRow(new Object[] { rs.getString(1), rs.getString(2), rs.getString(3), rs.getInt(4),
                        rs.getDouble(5), rs.getString(6) });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi nạp Phòng: " + ex.getMessage());
        }
    }

    private void themPhong() {
        String soPhong = txtSoPhong.getText().trim();
        String maKV = txtMaKhuVuc.getText().trim();
        String nguoi = txtSoNguoi.getText().trim();
        String gia = txtDonGia.getText().trim();
        String loaiPhong = cboLoaiPhong.getSelectedItem().toString();
        String trangThai = cboTrangThai.getSelectedItem().toString();

        if (soPhong.isEmpty() || maKV.isEmpty() || nguoi.isEmpty() || gia.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đủ thông tin!");
            return;
        }

        try (Connection conn = DbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO Phong(SoPhong, MaKhuVuc, SoNguoiToiDa, DonGiaNgay, LoaiPhong, TrangThai) VALUES(?, ?, ?, ?, ?, ?)")) {
            stmt.setString(1, soPhong);
            stmt.setString(2, maKV);
            stmt.setInt(3, Integer.parseInt(nguoi));
            stmt.setDouble(4, Double.parseDouble(gia));
            stmt.setString(5, loaiPhong);
            stmt.setString(6, trangThai);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Thêm thành công!");
            loadData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi thêm: " + ex.getMessage());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số người và Đơn giá phải là số hợp lệ!");
        }
    }

    private void suaPhong() {
        String soPhong = txtSoPhong.getText().trim();
        String maKV = txtMaKhuVuc.getText().trim();
        String nguoi = txtSoNguoi.getText().trim();
        String gia = txtDonGia.getText().trim();
        String loaiPhong = cboLoaiPhong.getSelectedItem().toString();
        String trangThai = cboTrangThai.getSelectedItem().toString();

        if (soPhong.isEmpty() || maKV.isEmpty() || nguoi.isEmpty() || gia.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phòng và nhập thông tin mới!");
            return;
        }

        try (Connection conn = DbConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(
                        "UPDATE Phong SET MaKhuVuc=?, SoNguoiToiDa=?, DonGiaNgay=?, LoaiPhong=?, TrangThai=? WHERE SoPhong=?")) {
            stmt.setString(1, maKV);
            stmt.setInt(2, Integer.parseInt(nguoi));
            stmt.setDouble(3, Double.parseDouble(gia));
            stmt.setString(4, loaiPhong);
            stmt.setString(5, trangThai);
            stmt.setString(6, soPhong);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Sửa thành công!");
            loadData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi sửa: " + ex.getMessage());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số người và Đơn giá phải là số hợp lệ!");
        }
    }

    private void xoaPhong() {
        String soPhong = txtSoPhong.getText().trim();
        if (soPhong.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phòng cần xóa!");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa?", "Xác nhận",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DbConnection.getConnection();
                    PreparedStatement stmt = conn.prepareStatement("DELETE FROM Phong WHERE SoPhong=?")) {
                stmt.setString(1, soPhong);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Xóa thành công!");
                loadData();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi xóa: " + ex.getMessage());
            }
        }
    }
}