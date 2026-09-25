import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class FrmMuonTra extends JFrame {
    private JTable tableSach;
    private DefaultTableModel tableModel;
    private JTextField txtMaDocGia;
    private JButton btnLapPhieu;

    public FrmMuonTra() {
        setTitle("Quản Lý Thư Viện - Lập Phiếu Mượn");
        setSize(700, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Panel phía trên: Nhập thông tin mượn
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Mã độc giả (VD: DG001):"));
        
        txtMaDocGia = new JTextField(10);
        txtMaDocGia.setText("DG001"); 
        topPanel.add(txtMaDocGia);
        
        btnLapPhieu = new JButton("Xác nhận Lập phiếu");
        topPanel.add(btnLapPhieu);
        add(topPanel, BorderLayout.NORTH);

        // Panel ở giữa: Danh sách sách
        tableModel = new DefaultTableModel(new String[]{"Mã Sách", "Tên Sách", "Số Lượng Tồn"}, 0);
        tableSach = new JTable(tableModel);
        tableSach.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(tableSach), BorderLayout.CENTER);

        // Nạp dữ liệu lần đầu
        taiDanhSachKho();

        // Xử lý sự kiện click nút Lập phiếu
        btnLapPhieu.addActionListener(e -> lapPhieuMuon());
    }

        private void taiDanhSachKho() {
                tableModel.setRowCount(0); // Xóa dữ liệu cũ
                String sql = "SELECT MaDauSach, TenSach, SoLuongHienCo FROM DauSach WHERE SoLuongHienCo > 0";
                
                try (Connection conn = DbConnection.getConnection();
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(sql)) {
                    
                    while (rs.next()) {
                        tableModel.addRow(new Object[]{
                            rs.getString("MaDauSach"), 
                            rs.getString("TenSach"), 
                            rs.getInt("SoLuongHienCo")
                        });
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi kết nối CSDL: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }

    private void lapPhieuMuon() {
        int selectedRow = tableSach.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 cuốn sách từ danh sách bên dưới!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maDocGia = txtMaDocGia.getText().trim();
        String maSach = tableModel.getValueAt(selectedRow, 0).toString();
        
        // Gọi Stored Procedure đã thiết kế trong CSDL
        String spSql = "{call sp_LapPhieuMuon(?, ?, ?, ?)}";

        try (Connection conn = DbConnection.getConnection();
             CallableStatement cstmt = conn.prepareCall(spSql)) {

            cstmt.setString(1, maDocGia);
            cstmt.setString(2, "NV001"); // Mặc định nhân viên trực
            cstmt.setString(3, maSach);
            
            // Set ngày hẹn trả là 7 ngày tính từ hôm nay
            long bayNgay = System.currentTimeMillis() + (7L * 24 * 60 * 60 * 1000);
            cstmt.setDate(4, new java.sql.Date(bayNgay));

            ResultSet rs = cstmt.executeQuery();
            if (rs.next()) {
                String thongBao = rs.getString("ThongBao");
                JOptionPane.showMessageDialog(this, thongBao, "Kết quả", JOptionPane.INFORMATION_MESSAGE);
                taiDanhSachKho(); // Tải lại bảng để cập nhật số lượng tồn kho giảm đi
            }
        } catch (SQLException ex) {
            // Hiển thị mã lỗi được ném ra từ lệnh THROW trong SQL
            JOptionPane.showMessageDialog(this, "Nghiệp vụ thất bại:\n" + ex.getMessage(), "Lỗi nghiệp vụ", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // Khởi chạy UI
        SwingUtilities.invokeLater(() -> {
            new FrmMuonTra().setVisible(true);
        });
    }
}