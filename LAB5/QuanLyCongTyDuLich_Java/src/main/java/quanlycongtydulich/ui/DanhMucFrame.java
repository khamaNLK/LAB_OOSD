package quanlycongtydulich.ui;

import java.awt.*;
import javax.swing.*;
import quanlycongtydulich.services.DanhMucService;

public class DanhMucFrame extends JDialog {
    private final DanhMucService svc = new DanhMucService();

    private JTable tblPT = new JTable();
    private JTable tblDB = new JTable();
    private JTable tblHDV = new JTable();
    private JTable tblDTQ = new JTable();

    public DanhMucFrame(Frame parent) {
        super(parent, "Danh Mục Dùng Chung", true);
        setSize(800, 480);
        setLocationRelativeTo(parent);
        initComponents();
        loadData();
    }

    private void initComponents() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Phương tiện", new JScrollPane(tblPT));
        tabs.addTab("Điểm bán vé", new JScrollPane(tblDB));
        tabs.addTab("Hướng dẫn viên", new JScrollPane(tblHDV));
        tabs.addTab("Điểm tham quan", new JScrollPane(tblDTQ));
        add(tabs, BorderLayout.CENTER);
    }

    private void loadData() {
        tblPT.setModel(svc.layPhuongTien());
        tblDB.setModel(svc.layDiemBan());
        tblHDV.setModel(svc.layHDV());
        tblDTQ.setModel(svc.layDiemThamQuan());
    }
}
