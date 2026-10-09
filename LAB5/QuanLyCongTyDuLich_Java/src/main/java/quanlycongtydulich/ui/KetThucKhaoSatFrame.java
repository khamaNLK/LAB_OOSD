package quanlycongtydulich.ui;

import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.swing.*;
import quanlycongtydulich.services.KetThucService;
import quanlycongtydulich.services.Models.QuyDinh;
import quanlycongtydulich.ui.UIHelper.*;

public class KetThucKhaoSatFrame extends JDialog {
    private final KetThucService svc = new KetThucService();

    private JTable tblDoan = new JTable();
    private JTextField txtSoTT = new JTextField(8);
    private JTextField txtSoDK = new JTextField(8);
    private JSpinner spTienTT = new JSpinner(new SpinnerNumberModel(40000000.0, 0.0, 1000000000.0, 1000000.0));

    private JComboBox<String> cboLoaiKS = new JComboBox<>(new String[] { QuyDinh.LE, QuyDinh.DOAN });
    private JComboBox<ComboItem> cboDangKy = new JComboBox<>();
    private JTextField txtMaKS = new JTextField(8);
    private JTable tblKS = new JTable();
    private JTextField txtKSChon = new JTextField(8);
    private JSpinner spDiem = new JSpinner(new SpinnerNumberModel(5, 1, 5, 1));
    private JTextField txtGopY = new JTextField(20);

    public KetThucKhaoSatFrame(Frame parent) {
        super(parent, "Kết Thúc Tour, Thanh Toán Sau Tour & Khảo Sát", true);
        setSize(880, 560);
        setLocationRelativeTo(parent);
        initComponents();
        loadData();
    }

    private void initComponents() {
        JTabbedPane tabs = new JTabbedPane();

        JPanel pnlTT = new JPanel(new BorderLayout(8, 8));
        pnlTT.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnlTT.add(new JScrollPane(tblDoan), BorderLayout.CENTER);

        JPanel pnlTTForm = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pnlTTForm.add(new JLabel("Mã thanh toán:")); pnlTTForm.add(txtSoTT);
        pnlTTForm.add(new JLabel("Số phiếu đoàn:")); pnlTTForm.add(txtSoDK);
        pnlTTForm.add(new JLabel("Số tiền trả:")); pnlTTForm.add(spTienTT);
        JButton btnThanhToan = new JButton("Ghi nhận thanh toán");
        pnlTTForm.add(btnThanhToan);
        pnlTT.add(pnlTTForm, BorderLayout.SOUTH);
        tabs.addTab("Thanh toán kinh phí đoàn sau tour", pnlTT);

        JPanel pnlKS = new JPanel(new BorderLayout(8, 8));
        pnlKS.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel pnlKSGui = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        pnlKSGui.add(new JLabel("Loại:")); pnlKSGui.add(cboLoaiKS);
        pnlKSGui.add(new JLabel("Đăng ký đã kết thúc:")); pnlKSGui.add(cboDangKy);
        pnlKSGui.add(new JLabel("Mã KS:")); pnlKSGui.add(txtMaKS);
        JButton btnGuiKS = new JButton("Gửi khảo sát");
        pnlKSGui.add(btnGuiKS);
        pnlKS.add(pnlKSGui, BorderLayout.NORTH);

        pnlKS.add(new JScrollPane(tblKS), BorderLayout.CENTER);

        JPanel pnlKSPhanHoi = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        pnlKSPhanHoi.add(new JLabel("Phiếu chọn:")); pnlKSPhanHoi.add(txtKSChon);
        pnlKSPhanHoi.add(new JLabel("Điểm (1-5):")); pnlKSPhanHoi.add(spDiem);
        pnlKSPhanHoi.add(new JLabel("Góp ý:")); pnlKSPhanHoi.add(txtGopY);
        JButton btnGhiPH = new JButton("Ghi nhận góp ý");
        pnlKSPhanHoi.add(btnGhiPH);
        pnlKS.add(pnlKSPhanHoi, BorderLayout.SOUTH);

        tabs.addTab("Khảo sát ý kiến khách hàng", pnlKS);

        add(tabs, BorderLayout.CENTER);

        tblDoan.getSelectionModel().addListSelectionListener(e -> {
            String so = UIHelper.getSelectedValue(tblDoan, "SoDKDoan");
            txtSoDK.setText(so);
        });

        tblKS.getSelectionModel().addListSelectionListener(e -> {
            String ma = UIHelper.getSelectedValue(tblKS, "MaKhaoSat");
            txtKSChon.setText(ma);
        });

        cboLoaiKS.addActionListener(e -> napDangKyKS());

        btnThanhToan.addActionListener(e -> {
            String soTT = txtSoTT.getText().trim();
            String soDK = txtSoDK.getText().trim();
            BigDecimal soTien = BigDecimal.valueOf(((Number) spTienTT.getValue()).doubleValue());
            if (UIHelper.bao(this, svc.thanhToanDoan(soTT, soDK, LocalDate.now(), soTien, "Chuyển khoản"))) {
                taiThanhToan();
            }
        });

        btnGuiKS.addActionListener(e -> {
            String ma = txtMaKS.getText().trim();
            String loai = (String) cboLoaiKS.getSelectedItem();
            String soDK = UIHelper.getComboValue(cboDangKy);
            if (UIHelper.bao(this, svc.guiKhaoSat(ma, loai, soDK, LocalDate.now()))) {
                taiKhaoSat();
                napDangKyKS();
            }
        });

        btnGhiPH.addActionListener(e -> {
            String ma = txtKSChon.getText().trim();
            int diem = (Integer) spDiem.getValue();
            String gopY = txtGopY.getText().trim();
            if (UIHelper.bao(this, svc.ghiPhanHoi(ma, LocalDate.now(), diem, gopY))) {
                taiKhaoSat();
            }
        });
    }

    private void loadData() {
        taiThanhToan();
        taiKhaoSat();
        napDangKyKS();
    }

    private void taiThanhToan() { tblDoan.setModel(svc.doanCanThanhToan()); }
    private void taiKhaoSat() { tblKS.setModel(svc.layKhaoSat()); }
    private void napDangKyKS() {
        String loai = (String) cboLoaiKS.getSelectedItem();
        if (loai != null) UIHelper.napCombo(cboDangKy, svc.layDangKyChoKhaoSat(loai), "HienThi", "Ma");
    }
}
