package quanlycongtydulich.ui;

import java.awt.*;
import java.math.BigDecimal;
import javax.swing.*;
import quanlycongtydulich.data.Db;
import quanlycongtydulich.services.DanhMucService;
import quanlycongtydulich.services.Models.QuyDinh;
import quanlycongtydulich.services.PhanCongService;
import quanlycongtydulich.ui.UIHelper.*;

public class PhanCongHDVFrame extends JDialog {
    private final PhanCongService svc = new PhanCongService();
    private final DanhMucService dm = new DanhMucService();

    private JTextField txtMaPC = new JTextField(10);
    private JComboBox<ComboItem> cboHDV = new JComboBox<>();
    private JComboBox<String> cboLoai = new JComboBox<>(new String[] { QuyDinh.LE, QuyDinh.DOAN });
    private JComboBox<ComboItem> cboDoiTuong = new JComboBox<>();
    private JSpinner spThuLao = new JSpinner(new SpinnerNumberModel(1500000.0, 0.0, 50000000.0, 100000.0));
    private JTable tblPC = new JTable();

    public PhanCongHDVFrame(Frame parent) {
        super(parent, "Phân Công Hướng Dẫn Viên (Không Chồng Chéo Lịch)", true);
        setSize(850, 520);
        setLocationRelativeTo(parent);
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel pnlTop = new JPanel(new GridLayout(2, 1, 5, 5));
        pnlTop.setBorder(BorderFactory.createTitledBorder("Thông tin phân công"));

        JPanel r1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        r1.add(new JLabel("Mã PC:")); r1.add(txtMaPC);
        r1.add(new JLabel("Hướng dẫn viên:")); r1.add(cboHDV);
        r1.add(new JLabel("Loại tour:")); r1.add(cboLoai);
        pnlTop.add(r1);

        JPanel r2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        r2.add(new JLabel("Chuyến / Đoàn:")); r2.add(cboDoiTuong);
        r2.add(new JLabel("Thù lao tour:")); r2.add(spThuLao);
        JButton btnPhanCong = new JButton("Phân công");
        r2.add(btnPhanCong);
        pnlTop.add(r2);

        add(pnlTop, BorderLayout.NORTH);

        JPanel pnlCenter = new JPanel(new BorderLayout());
        pnlCenter.setBorder(BorderFactory.createTitledBorder("Danh sách phân công HDV"));
        pnlCenter.add(new JScrollPane(tblPC), BorderLayout.CENTER);
        add(pnlCenter, BorderLayout.CENTER);

        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnClose = new JButton("Đóng");
        btnClose.addActionListener(e -> dispose());
        pnlBottom.add(btnClose);
        add(pnlBottom, BorderLayout.SOUTH);

        cboLoai.addActionListener(e -> napDoiTuong());

        btnPhanCong.addActionListener(e -> {
            String maPC = txtMaPC.getText().trim();
            String maHDV = UIHelper.getComboValue(cboHDV);
            String loai = (String) cboLoai.getSelectedItem();
            String maDT = UIHelper.getComboValue(cboDoiTuong);
            BigDecimal thùLao = BigDecimal.valueOf(((Number) spThuLao.getValue()).doubleValue());

            if (UIHelper.bao(this, svc.phanCong(maPC, maHDV, loai, maDT, thùLao))) {
                taiDanhSach();
                napDoiTuong();
            }
        });
    }

    private void loadData() {
        UIHelper.napCombo(cboHDV, dm.layHDV(), "HoTen", "MaHDV");
        napDoiTuong();
        taiDanhSach();
    }

    private void napDoiTuong() {
        String loai = (String) cboLoai.getSelectedItem();
        if (loai == null) return;
        if (loai.equals(QuyDinh.LE)) {
            String sql = "SELECT MaChuyen AS Ma, MaChuyen + ' - ' + DiaDiemDon AS HienThi FROM ChuyenLe "
                       + "WHERE NOT EXISTS(SELECT 1 FROM PhanCongHDV p WHERE p.MaChuyen = ChuyenLe.MaChuyen)";
            UIHelper.napCombo(cboDoiTuong, Db.query(sql), "HienThi", "Ma");
        } else {
            String sql = "SELECT d.SoDKDoan AS Ma, d.SoDKDoan + ' - ' + k.TenCoQuanDaiDien AS HienThi FROM DangKyDoan d "
                       + "JOIN DoanKhach k ON d.MaDoan = k.MaDoan WHERE d.TrangThai = N'" + QuyDinh.DA_DANG_KY + "'";
            UIHelper.napCombo(cboDoiTuong, Db.query(sql), "HienThi", "Ma");
        }
    }

    private void taiDanhSach() {
        String sql = "SELECT p.MaPC, p.MaHDV, h.HoTen, p.LoaiDoiTuong, ISNULL(p.MaChuyen, p.SoDKDoan) AS DoiTuong, "
                   + "p.NgayBatDau, p.NgayKetThuc, p.ThuLaoTour FROM PhanCongHDV p JOIN HuongDanVien h ON p.MaHDV = h.MaHDV "
                   + "ORDER BY p.NgayBatDau DESC";
        tblPC.setModel(Db.query(sql));
    }
}
