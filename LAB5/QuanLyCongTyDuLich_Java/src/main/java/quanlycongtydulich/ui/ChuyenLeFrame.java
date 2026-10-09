package quanlycongtydulich.ui;

import java.awt.*;
import java.time.LocalDate;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import quanlycongtydulich.services.ChuyenLeService;
import quanlycongtydulich.services.TourService;
import quanlycongtydulich.ui.UIHelper.*;

public class ChuyenLeFrame extends JDialog {
    private final ChuyenLeService svc = new ChuyenLeService();
    private final TourService tourSvc = new TourService();

    private JTextField txtMa = new JTextField(10);
    private JComboBox<ComboItem> cboTour = new JComboBox<>();
    private JSpinner spNgayDi = new JSpinner(new SpinnerDateModel());
    private JLabel lblNgayVe = new JLabel("-");
    private JTextField txtDon = new JTextField(20);
    private JTable tblChuyen = new JTable();

    public ChuyenLeFrame(Frame parent) {
        super(parent, "Lịch Chuyến Khách Lẻ & Đóng Đăng Ký", true);
        setSize(850, 520);
        setLocationRelativeTo(parent);
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel pnlTop = new JPanel(new GridLayout(3, 1, 8, 8));
        pnlTop.setBorder(BorderFactory.createTitledBorder("Thông tin chuyến khách lẻ"));

        JPanel r1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        r1.add(new JLabel("Mã chuyến:"));
        r1.add(txtMa);
        r1.add(new JLabel("Tour:"));
        r1.add(cboTour);
        pnlTop.add(r1);

        JPanel r2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        r2.add(new JLabel("Ngày đi:"));
        spNgayDi.setEditor(new JSpinner.DateEditor(spNgayDi, "dd/MM/yyyy"));
        spNgayDi.setValue(java.sql.Date.valueOf(LocalDate.now().plusDays(7)));
        r2.add(spNgayDi);
        r2.add(new JLabel("Ngày về (tự tính):"));
        lblNgayVe.setFont(new Font("Arial", Font.BOLD, 12));
        lblNgayVe.setForeground(new Color(27, 54, 93));
        r2.add(lblNgayVe);
        pnlTop.add(r2);

        JPanel r3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        r3.add(new JLabel("Địa điểm đón:"));
        r3.add(txtDon);
        JButton btnThem = new JButton("Tạo chuyến mới");
        JButton btnDongDK = new JButton("Đóng đăng ký chuyến");
        r3.add(btnThem);
        r3.add(btnDongDK);
        pnlTop.add(r3);

        add(pnlTop, BorderLayout.NORTH);

        JPanel pnlCenter = new JPanel(new BorderLayout());
        pnlCenter.setBorder(BorderFactory.createTitledBorder("Danh sách chuyến khách lẻ"));
        pnlCenter.add(new JScrollPane(tblChuyen), BorderLayout.CENTER);
        add(pnlCenter, BorderLayout.CENTER);

        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnClose = new JButton("Đóng");
        btnClose.addActionListener(e -> dispose());
        pnlBottom.add(btnClose);
        add(pnlBottom, BorderLayout.SOUTH);

        btnThem.addActionListener(e -> {
            String ma = txtMa.getText().trim();
            String maTour = UIHelper.getComboValue(cboTour);
            java.util.Date d = (java.util.Date) spNgayDi.getValue();
            LocalDate ngayDi = new java.sql.Date(d.getTime()).toLocalDate();
            String don = txtDon.getText().trim();
            if (UIHelper.bao(this, svc.themChuyen(ma, maTour, ngayDi, don))) {
                taiDanhSach();
            }
        });

        btnDongDK.addActionListener(e -> {
            String ma = UIHelper.getSelectedValue(tblChuyen, "MaChuyen");
            if (UIHelper.bao(this, svc.dongDangKy(ma))) {
                taiDanhSach();
            }
        });
    }

    private void loadData() {
        UIHelper.napCombo(cboTour, tourSvc.layTourMoBan(), "HienThi", "MaTour");
        taiDanhSach();
    }

    private void taiDanhSach() {
        tblChuyen.setModel(svc.layChuyen());
    }
}
