import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.util.List;

public class FrmDatPhong extends JFrame {
    private JComboBox<String> cboKhachHang, cboPhong;
    private JTextField txtSoPhieu, txtSoNguoi, txtTienCoc;
    private JButton btnLapPhieu, btnCapNhat, btnHuy, btnNhanPhong, btnTraPhong;
    private JTable tblPhieuDat;
    private DatPhongService service;

    public FrmDatPhong() {
        service = new DatPhongService();

        setTitle("Quản Lý Khách Sạn - Nghiệp vụ Đặt & Thuê Phòng");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel pnlTop = new JPanel(new GridLayout(6, 2, 10, 10));
        pnlTop.setBorder(BorderFactory.createTitledBorder("Thông tin Phiếu Đặt"));

        pnlTop.add(new JLabel("  Số phiếu đặt (Tạo/Sửa):"));
        txtSoPhieu = new JTextField();
        pnlTop.add(txtSoPhieu);

        pnlTop.add(new JLabel("  Khách hàng:"));
        cboKhachHang = new JComboBox<>();
        loadKhachHangData();
        pnlTop.add(cboKhachHang);

        pnlTop.add(new JLabel("  Chọn Phòng (Trống):"));
        cboPhong = new JComboBox<>();
        loadPhongData();
        pnlTop.add(cboPhong);

        pnlTop.add(new JLabel("  Số người lưu trú:"));
        txtSoNguoi = new JTextField("2");
        pnlTop.add(txtSoNguoi);

        pnlTop.add(new JLabel("  Tiền cọc (Sửa được):"));
        txtTienCoc = new JTextField("500000");
        pnlTop.add(txtTienCoc);

        btnLapPhieu = new JButton("Thêm Mới (Lập Phiếu)");
        btnCapNhat = new JButton("Sửa (Cập Nhật)");

        JPanel pnlButtonsTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlButtonsTop.add(btnLapPhieu);
        pnlButtonsTop.add(btnCapNhat);
        pnlTop.add(new JLabel("  Thao tác Dữ liệu:"));
        pnlTop.add(pnlButtonsTop);

        add(pnlTop, BorderLayout.NORTH);

        tblPhieuDat = new JTable();
        add(new JScrollPane(tblPhieuDat), BorderLayout.CENTER);
        loadTableData();

        tblPhieuDat.getSelectionModel().addListSelectionListener(e -> {
            int row = tblPhieuDat.getSelectedRow();
            if (row >= 0) {
                txtSoPhieu.setText(tblPhieuDat.getValueAt(row, 0).toString());
            }
        });

        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        btnHuy = new JButton("Hủy Đặt Phòng");
        btnNhanPhong = new JButton("Khách Nhận Phòng");
        btnTraPhong = new JButton("Khách Trả Phòng");

        btnHuy.setBackground(new Color(255, 102, 102));
        btnNhanPhong.setBackground(new Color(102, 204, 255));
        btnTraPhong.setBackground(new Color(153, 255, 153));

        pnlBottom.add(btnHuy);
        pnlBottom.add(btnNhanPhong);
        pnlBottom.add(btnTraPhong);
        add(pnlBottom, BorderLayout.SOUTH);

        btnLapPhieu.addActionListener(e -> themMoi());
        btnCapNhat.addActionListener(e -> capNhat());
        btnHuy.addActionListener(e -> xuLyNghiepVu("HỦY"));
        btnNhanPhong.addActionListener(e -> xuLyNghiepVu("NHẬN"));
        btnTraPhong.addActionListener(e -> xuLyNghiepVu("TRẢ"));
    }

    private void loadPhongData() {
        List<String> dsPhong = service.layDanhSachPhong();
        for (String p : dsPhong) {
            cboPhong.addItem(p);
        }
    }

    private void loadKhachHangData() {
        List<String> dsKhach = service.layDanhSachKhachHang();
        for (String k : dsKhach) {
            cboKhachHang.addItem(k);
        }
    }

    private void loadTableData() {
        tblPhieuDat.setModel(service.layDanhSachPhieuDat());
    }

    private void themMoi() {
        if (cboPhong.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Không có phòng trống để đặt!");
            return;
        }
        try {
            String soPhieu = txtSoPhieu.getText().trim();
            String maKhach = cboKhachHang.getSelectedItem().toString().split(" - ")[0];
            String soPhong = cboPhong.getSelectedItem().toString().split(" - ")[0];
            int soNguoi = Integer.parseInt(txtSoNguoi.getText().trim());
            double tienCoc = Double.parseDouble(txtTienCoc.getText().trim());

            long millis = System.currentTimeMillis();
            Date ngayNhan = new Date(millis);
            Date ngayTra = new Date(millis + (2L * 24 * 60 * 60 * 1000));

            StringBuilder tb = new StringBuilder();
            if (service.taoDatPhong(soPhieu, maKhach, "NV01", ngayNhan, ngayTra, tienCoc, soPhong, soNguoi, tb)) {
                JOptionPane.showMessageDialog(this, tb.toString());
                loadTableData(); // Refresh bảng
            } else {
                JOptionPane.showMessageDialog(this, tb.toString(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi nhập liệu!");
        }
    }

    // 2. CHỨC NĂNG SỬA (Cập nhật)
    private void capNhat() {
        int row = tblPhieuDat.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 dòng dưới bảng để sửa!");
            return;
        }
        try {
            String soPhieu = tblPhieuDat.getValueAt(row, 0).toString();
            double tienCocMoi = Double.parseDouble(txtTienCoc.getText().trim());
            Date ngayTraMoi = new Date(System.currentTimeMillis() + (5L * 24 * 60 * 60 * 1000)); // Ví dụ gia hạn thêm
                                                                                                 // ngày

            StringBuilder tb = new StringBuilder();
            if (service.capNhatDatPhong(soPhieu, ngayTraMoi, tienCocMoi, tb)) {
                JOptionPane.showMessageDialog(this, tb.toString());
                loadTableData();
            } else {
                JOptionPane.showMessageDialog(this, tb.toString(), "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi nhập liệu tiền cọc!");
        }
    }

    // 3. XỬ LÝ HỦY / NHẬN / TRẢ (Cần lấy thông tin dòng đang chọn trên bảng)
    private void xuLyNghiepVu(String loaiThaoTac) {
        int row = tblPhieuDat.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 phiếu đặt từ danh sách bên dưới!");
            return;
        }

        String soPhieu = tblPhieuDat.getValueAt(row, 0).toString();
        String soPhong = tblPhieuDat.getValueAt(row, 2).toString();
        StringBuilder tb = new StringBuilder();
        boolean thanhCong = false;

        if (loaiThaoTac.equals("HỦY")) {
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn chắc chắn muốn hủy phiếu " + soPhieu + "?");
            if (confirm == JOptionPane.YES_OPTION) {
                thanhCong = service.huyDatPhong(soPhieu, soPhong, tb);
            } else
                return;
        } else if (loaiThaoTac.equals("NHẬN")) {
            thanhCong = service.nhậnPhong(soPhieu, soPhong, tb);
        } else if (loaiThaoTac.equals("TRẢ")) {
            thanhCong = service.traPhong(soPhieu, soPhong, tb);
        }

        if (thanhCong) {
            JOptionPane.showMessageDialog(this, tb.toString());
            loadTableData();
        } else {
            JOptionPane.showMessageDialog(this, tb.toString(), "Thao tác thất bại", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FrmDatPhong().setVisible(true));
    }
}