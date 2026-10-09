package quanlycongtydulich.ui;

import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import quanlycongtydulich.services.DangKyDoanService;
import quanlycongtydulich.services.Models.ThanhVienDoanItem;
import quanlycongtydulich.services.TourService;
import quanlycongtydulich.ui.UIHelper.*;

public class DangKyDoanFrame extends JDialog {
    private final DangKyDoanService svc = new DangKyDoanService();
    private final TourService tourSvc = new TourService();

    private JTextField txtMaDoan = new JTextField(10);
    private JTextField txtTenCQ = new JTextField(16);
    private JTextField txtDiaChi = new JTextField(16);
    private JTextField txtDT = new JTextField(10);
    private JTextField txtDaiDien = new JTextField(12);

    private JTextField txtSo = new JTextField(10);
    private JComboBox<ComboItem> cboTour = new JComboBox<>();
    private JSpinner spNgayDi = new JSpinner(new SpinnerDateModel());
    private JSpinner spSoNguoi = new JSpinner(new SpinnerNumberModel(15, 1, 500, 1));
    private JTextField txtDon = new JTextField(16);
    private JSpinner spTienCoc = new JSpinner(new SpinnerNumberModel(10000000.0, 0.0, 1000000000.0, 1000000.0));
    private JCheckBox chkBH = new JCheckBox("Mua bảo hiểm");

    private JTable tblDoan = new JTable();

    public DangKyDoanFrame(Frame parent) {
        super(parent, "Đăng Ký Tour Theo Đoàn (Trên 12 Người)", true);
        setSize(920, 600);
        setLocationRelativeTo(parent);
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel pnlNorth = new JPanel(new GridLayout(2, 1, 5, 5));

        JPanel pnlDoan = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        pnlDoan.setBorder(BorderFactory.createTitledBorder("1. Thông tin đoàn khách"));
        pnlDoan.add(new JLabel("Mã đoàn:")); pnlDoan.add(txtMaDoan);
        pnlDoan.add(new JLabel("Cơ quan/Gia đình:")); pnlDoan.add(txtTenCQ);
        pnlDoan.add(new JLabel("Địa chỉ:")); pnlDoan.add(txtDiaChi);
        pnlDoan.add(new JLabel("Điện thoại:")); pnlDoan.add(txtDT);
        pnlDoan.add(new JLabel("Đại diện:")); pnlDoan.add(txtDaiDien);
        pnlNorth.add(pnlDoan);

        JPanel pnlPhieu = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        pnlPhieu.setBorder(BorderFactory.createTitledBorder("2. Thông tin đăng ký"));
        pnlPhieu.add(new JLabel("Số phiếu:")); pnlPhieu.add(txtSo);
        pnlPhieu.add(new JLabel("Tour:")); pnlPhieu.add(cboTour);
        pnlPhieu.add(new JLabel("Ngày đi:"));
        spNgayDi.setEditor(new JSpinner.DateEditor(spNgayDi, "dd/MM/yyyy"));
        spNgayDi.setValue(java.sql.Date.valueOf(LocalDate.now().plusDays(14)));
        pnlPhieu.add(spNgayDi);
        pnlPhieu.add(new JLabel("Số người:")); pnlPhieu.add(spSoNguoi);
        pnlPhieu.add(new JLabel("Tiền cọc:")); pnlPhieu.add(spTienCoc);
        pnlPhieu.add(chkBH);
        pnlNorth.add(pnlPhieu);

        add(pnlNorth, BorderLayout.NORTH);

        JPanel pnlCenter = new JPanel(new BorderLayout());
        pnlCenter.setBorder(BorderFactory.createTitledBorder("Danh sách phiếu đăng ký đoàn"));
        pnlCenter.add(new JScrollPane(tblDoan), BorderLayout.CENTER);
        add(pnlCenter, BorderLayout.CENTER);

        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 8));
        JButton btnDangKy = new JButton("Lập phiếu đăng ký");
        JButton btnHuy = new JButton("Hủy phiếu (mất cọc)");
        JButton btnClose = new JButton("Đóng");

        pnlBottom.add(btnDangKy);
        pnlBottom.add(btnHuy);
        pnlBottom.add(btnClose);
        add(pnlBottom, BorderLayout.SOUTH);

        btnDangKy.addActionListener(e -> {
            String so = txtSo.getText().trim();
            String maD = txtMaDoan.getText().trim();
            String tenCQ = txtTenCQ.getText().trim();
            String diaChi = txtDiaChi.getText().trim();
            String dt = txtDT.getText().trim();
            String daiDien = txtDaiDien.getText().trim();
            String maTour = UIHelper.getComboValue(cboTour);
            java.util.Date d = (java.util.Date) spNgayDi.getValue();
            LocalDate ngayDi = new java.sql.Date(d.getTime()).toLocalDate();
            int soNguoi = (Integer) spSoNguoi.getValue();
            BigDecimal tienCoc = BigDecimal.valueOf(((Number) spTienCoc.getValue()).doubleValue());
            boolean muaBH = chkBH.isSelected();

            List<ThanhVienDoanItem> ds = new ArrayList<>();
            if (muaBH) {
                for (int i = 1; i <= soNguoi; i++) {
                    ds.add(new ThanhVienDoanItem("Thành viên " + i, LocalDate.of(1990, 1, 1), "0790900000" + (i < 10 ? "0" + i : i)));
                }
            }

            if (UIHelper.bao(this, svc.dangKy(so, maD, tenCQ, diaChi, dt, daiDien, maTour, ngayDi, soNguoi, "TP.HCM", muaBH, tienCoc, ds))) {
                taiDanhSach();
            }
        });

        btnHuy.addActionListener(e -> {
            String so = UIHelper.getSelectedValue(tblDoan, "SoDKDoan");
            if (so.isEmpty()) { JOptionPane.showMessageDialog(this, "Chọn phiếu đoàn cần hủy."); return; }
            int ret = JOptionPane.showConfirmDialog(this, "Đoàn không đi sẽ mất tiền cọc. Hủy phiếu " + so + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (ret == JOptionPane.YES_OPTION) {
                if (UIHelper.bao(this, svc.huyDangKy(so))) taiDanhSach();
            }
        });

        btnClose.addActionListener(e -> dispose());
    }

    private void loadData() {
        UIHelper.napCombo(cboTour, tourSvc.layTourMoBan(), "HienThi", "MaTour");
        taiDanhSach();
    }

    private void taiDanhSach() {
        tblDoan.setModel(svc.layDanhSach());
    }
}
