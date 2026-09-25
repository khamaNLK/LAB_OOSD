import javax.swing.*;
import java.awt.*;

public class FrmMain extends JFrame {

    public FrmMain() {
        setTitle("Hệ Thống Quản Lý Khách Sạn");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 20));

        JLabel lblTitle = new JLabel("HỆ THỐNG QUẢN LÝ KHÁCH SẠN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(new Color(0, 51, 153));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(lblTitle, BorderLayout.NORTH);

        JPanel pnlButtons = new JPanel(new GridLayout(3, 2, 20, 20));
        pnlButtons.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));

        JButton btnDanhMuc = new JButton("Danh mục");
        JButton btnPhongTN = new JButton("Phòng - Tiện nghi");
        JButton btnDatPhong = new JButton("Đặt / Nhận phòng");
        JButton btnDichVu = new JButton("Sử dụng dịch vụ");
        JButton btnTraPhong = new JButton("Trả phòng - Thanh toán");
        JButton btnThongKe = new JButton("Thống kê");

        pnlButtons.add(btnDanhMuc);
        pnlButtons.add(btnPhongTN);
        pnlButtons.add(btnDichVu);
        pnlButtons.add(btnTraPhong);
        pnlButtons.add(btnDatPhong);
        pnlButtons.add(btnThongKe);
        add(pnlButtons, BorderLayout.CENTER);

        JPanel pnlExit = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlExit.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        JButton btnThoat = new JButton("Thoát");
        btnThoat.setPreferredSize(new Dimension(200, 40));
        pnlExit.add(btnThoat);
        add(pnlExit, BorderLayout.SOUTH);

        btnDanhMuc.addActionListener(e -> new FrmDanhMuc().setVisible(true));
        btnPhongTN.addActionListener(e -> new FrmPhongTienNghi().setVisible(true));
        btnDatPhong.addActionListener(e -> new FrmDatPhong().setVisible(true));
        btnDichVu.addActionListener(e -> new FrmDichVu().setVisible(true));
        btnTraPhong.addActionListener(e -> new FrmTraPhong().setVisible(true));
        btnThongKe.addActionListener(e -> new FrmThongKe().setVisible(true));

        btnThoat.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn thoát?", "Xác nhận",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FrmMain().setVisible(true));
    }
}