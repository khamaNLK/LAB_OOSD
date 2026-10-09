package quanlycongtydulich.ui;

import java.awt.*;
import javax.swing.*;

public class MainFrame extends JFrame {
    public MainFrame() {
        setTitle("CÔNG TY DU LỊCH VĂN HÓA VIỆT - HỆ THỐNG QUẢN LÝ");
        setSize(750, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        JLabel lblTitle = new JLabel("CÔNG TY DU LỊCH VĂN HÓA VIỆT TP.HCM", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 19));
        lblTitle.setForeground(new Color(27, 54, 93));
        mainPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel btnPanel = new JPanel(new GridLayout(4, 2, 15, 15));

        JButton btnDanhMuc = new JButton("Danh mục (Phương tiện, Điểm bán, HDV, Điểm TQ)");
        JButton btnTour = new JButton("Quản lý Tour & Lộ trình hành trình");
        JButton btnChuyenLe = new JButton("Lịch chuyến khách lẻ & Đóng đăng ký");
        JButton btnDangKyLe = new JButton("Đăng ký & Bán vé khách lẻ");
        JButton btnDangKyDoan = new JButton("Đăng ký theo đoàn, Bảo hiểm, Hủy cọc");
        JButton btnPhanCong = new JButton("Phân công Hướng dẫn viên");
        JButton btnKetThuc = new JButton("Kết thúc tour, Thanh toán & Khảo sát");
        JButton btnLuong = new JButton("Lương HDV & Thống kê kinh doanh");

        // Action Listeners to open child frames!
        btnDanhMuc.addActionListener(e -> new DanhMucFrame(this).setVisible(true));
        btnTour.addActionListener(e -> new TourFrame(this).setVisible(true));
        btnChuyenLe.addActionListener(e -> new ChuyenLeFrame(this).setVisible(true));
        btnDangKyLe.addActionListener(e -> new DangKyLeFrame(this).setVisible(true));
        btnDangKyDoan.addActionListener(e -> new DangKyDoanFrame(this).setVisible(true));
        btnPhanCong.addActionListener(e -> new PhanCongHDVFrame(this).setVisible(true));
        btnKetThuc.addActionListener(e -> new KetThucKhaoSatFrame(this).setVisible(true));
        btnLuong.addActionListener(e -> new LuongThongKeFrame(this).setVisible(true));

        btnPanel.add(btnDanhMuc);
        btnPanel.add(btnTour);
        btnPanel.add(btnChuyenLe);
        btnPanel.add(btnDangKyLe);
        btnPanel.add(btnDangKyDoan);
        btnPanel.add(btnPhanCong);
        btnPanel.add(btnKetThuc);
        btnPanel.add(btnLuong);

        mainPanel.add(btnPanel, BorderLayout.CENTER);

        JButton btnExit = new JButton("Thoát chương trình");
        btnExit.addActionListener(e -> {
            int ret = JOptionPane.showConfirmDialog(this, "Bạn có thực sự muốn thoát?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (ret == JOptionPane.YES_OPTION) System.exit(0);
        });
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        southPanel.add(btnExit);
        mainPanel.add(southPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}
