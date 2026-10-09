package quanlycongtydulich.ui;

import java.awt.*;
import java.time.LocalDate;
import javax.swing.*;
import quanlycongtydulich.services.ThongKeService;

public class LuongThongKeFrame extends JDialog {
    private final ThongKeService svc = new ThongKeService();

    private JSpinner spThang = new JSpinner(new SpinnerNumberModel(LocalDate.now().getMonthValue(), 1, 12, 1));
    private JSpinner spNam = new JSpinner(new SpinnerNumberModel(LocalDate.now().getYear(), 2020, 2030, 1));
    private JTable tblLuong = new JTable();

    private JTable tblTongHop = new JTable();

    public LuongThongKeFrame(Frame parent) {
        super(parent, "Lương Hướng Dẫn Viên & Thống Kê Kinh Doanh", true);
        setSize(850, 520);
        setLocationRelativeTo(parent);
        initComponents();
    }

    private void initComponents() {
        JTabbedPane tabs = new JTabbedPane();

        JPanel pnlLuong = new JPanel(new BorderLayout(8, 8));
        pnlLuong.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel pnlLTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pnlLTop.add(new JLabel("Tháng:")); pnlLTop.add(spThang);
        pnlLTop.add(new JLabel("Năm:")); pnlLTop.add(spNam);
        JButton btnTinh = new JButton("Tính lương HDV");
        pnlLTop.add(btnTinh);
        pnlLuong.add(pnlLTop, BorderLayout.NORTH);

        pnlLuong.add(new JScrollPane(tblLuong), BorderLayout.CENTER);
        tabs.addTab("Bảng lương hướng dẫn viên", pnlLuong);

        JPanel pnlTK = new JPanel(new BorderLayout(8, 8));
        pnlTK.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel pnlTKTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton btnThongKe = new JButton("Thống kê 5 chỉ số (Toàn kỳ)");
        pnlTKTop.add(btnThongKe);
        pnlTK.add(pnlTKTop, BorderLayout.NORTH);

        pnlTK.add(new JScrollPane(tblTongHop), BorderLayout.CENTER);
        tabs.addTab("Thống kê kinh doanh tổng hợp", pnlTK);

        add(tabs, BorderLayout.CENTER);

        btnTinh.addActionListener(e -> {
            int th = (Integer) spThang.getValue();
            int na = (Integer) spNam.getValue();
            tblLuong.setModel(svc.luongHDV(th, na));
        });

        btnThongKe.addActionListener(e -> {
            tblTongHop.setModel(svc.tongHop(LocalDate.of(2025, 1, 1), LocalDate.now().plusYears(1)));
        });
    }
}
