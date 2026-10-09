package quanlycongtydulich.ui;

import java.awt.*;
import javax.swing.*;
import quanlycongtydulich.services.ChuyenLeService;
import quanlycongtydulich.services.DanhMucService;
import quanlycongtydulich.services.DangKyLeService;
import quanlycongtydulich.ui.UIHelper.*;

public class DangKyLeFrame extends JDialog {
    private final DangKyLeService svc = new DangKyLeService();
    private final ChuyenLeService chuyenSvc = new ChuyenLeService();
    private final DanhMucService dm = new DanhMucService();

    private JTextField txtSo = new JTextField(10);
    private JComboBox<ComboItem> cboChuyen = new JComboBox<>();
    private JComboBox<ComboItem> cboDiemBan = new JComboBox<>();
    private JTextField txtTen = new JTextField(14);
    private JTextField txtDT = new JTextField(10);
    private JSpinner spSoNguoi = new JSpinner(new SpinnerNumberModel(2, 1, 11, 1));
    private JTable tblDKLe = new JTable();

    public DangKyLeFrame(Frame parent) {
        super(parent, "Đăng Ký & Thanh Toán Vé Khách Lẻ (< 12 Người)", true);
        setSize(850, 520);
        setLocationRelativeTo(parent);
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel pnlTop = new JPanel(new GridLayout(2, 1, 5, 5));
        pnlTop.setBorder(BorderFactory.createTitledBorder("Thông tin đăng ký vé lẻ"));

        JPanel r1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        r1.add(new JLabel("Số phiếu:")); r1.add(txtSo);
        r1.add(new JLabel("Chuyến đang mở:")); r1.add(cboChuyen);
        r1.add(new JLabel("Điểm bán vé:")); r1.add(cboDiemBan);
        pnlTop.add(r1);

        JPanel r2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        r2.add(new JLabel("Người đăng ký:")); r2.add(txtTen);
        r2.add(new JLabel("Điện thoại:")); r2.add(txtDT);
        r2.add(new JLabel("Số người (<12):")); r2.add(spSoNguoi);
        JButton btnDangKy = new JButton("Đăng ký & thanh toán vé");
        r2.add(btnDangKy);
        pnlTop.add(r2);

        add(pnlTop, BorderLayout.NORTH);

        JPanel pnlCenter = new JPanel(new BorderLayout());
        pnlCenter.setBorder(BorderFactory.createTitledBorder("Danh sách vé khách lẻ đã bán"));
        pnlCenter.add(new JScrollPane(tblDKLe), BorderLayout.CENTER);
        add(pnlCenter, BorderLayout.CENTER);

        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnClose = new JButton("Đóng");
        btnClose.addActionListener(e -> dispose());
        pnlBottom.add(btnClose);
        add(pnlBottom, BorderLayout.SOUTH);

        btnDangKy.addActionListener(e -> {
            String so = txtSo.getText().trim();
            String maChuyen = UIHelper.getComboValue(cboChuyen);
            String maDB = UIHelper.getComboValue(cboDiemBan);
            String ten = txtTen.getText().trim();
            String dt = txtDT.getText().trim();
            int soNguoi = (Integer) spSoNguoi.getValue();

            if (UIHelper.bao(this, svc.dangKy(so, maChuyen, maDB, ten, dt, soNguoi))) {
                taiDanhSach();
            }
        });
    }

    private void loadData() {
        UIHelper.napCombo(cboChuyen, chuyenSvc.layChuyenMo(), "HienThi", "MaChuyen");
        UIHelper.napCombo(cboDiemBan, dm.layDiemBan(), "TenDiemBan", "MaDiemBan");
        taiDanhSach();
    }

    private void taiDanhSach() {
        tblDKLe.setModel(svc.layDanhSach());
    }
}
