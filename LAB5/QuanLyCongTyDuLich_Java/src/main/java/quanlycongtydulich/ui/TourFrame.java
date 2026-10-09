package quanlycongtydulich.ui;

import java.awt.*;
import java.math.BigDecimal;
import javax.swing.*;
import quanlycongtydulich.services.TourService;

public class TourFrame extends JDialog {
    private final TourService svc = new TourService();

    private JTextField txtMa = new JTextField(8);
    private JTextField txtTen = new JTextField(16);
    private JSpinner spNgay = new JSpinner(new SpinnerNumberModel(3, 1, 30, 1));
    private JSpinner spDem = new JSpinner(new SpinnerNumberModel(2, 0, 30, 1));
    private JSpinner spGia = new JSpinner(new SpinnerNumberModel(2500000.0, 0.0, 100000000.0, 500000.0));
    private JTable tblTour = new JTable();

    public TourFrame(Frame parent) {
        super(parent, "Quản Lý Tour & Lộ Trình Hành Trình", true);
        setSize(850, 500);
        setLocationRelativeTo(parent);
        initComponents();
        taiTour();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel pnlTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        pnlTop.setBorder(BorderFactory.createTitledBorder("Thêm tour mới"));
        pnlTop.add(new JLabel("Mã tour:")); pnlTop.add(txtMa);
        pnlTop.add(new JLabel("Tên tour:")); pnlTop.add(txtTen);
        pnlTop.add(new JLabel("Ngày:")); pnlTop.add(spNgay);
        pnlTop.add(new JLabel("Đêm:")); pnlTop.add(spDem);
        pnlTop.add(new JLabel("Đơn giá/khách:")); pnlTop.add(spGia);

        JButton btnThem = new JButton("Thêm tour");
        pnlTop.add(btnThem);
        add(pnlTop, BorderLayout.NORTH);

        JPanel pnlCenter = new JPanel(new BorderLayout());
        pnlCenter.setBorder(BorderFactory.createTitledBorder("Danh sách tour đang mở bán"));
        pnlCenter.add(new JScrollPane(tblTour), BorderLayout.CENTER);
        add(pnlCenter, BorderLayout.CENTER);

        btnThem.addActionListener(e -> {
            String ma = txtMa.getText().trim();
            String ten = txtTen.getText().trim();
            int n = (Integer) spNgay.getValue();
            int d = (Integer) spDem.getValue();
            BigDecimal gia = BigDecimal.valueOf(((Number) spGia.getValue()).doubleValue());
            if (UIHelper.bao(this, svc.themTour(ma, ten, n, d, gia, "TP.HCM xuất phát"))) {
                taiTour();
            }
        });
    }

    private void taiTour() { tblTour.setModel(svc.layTour()); }
}
