package quanlycongtydulich.services;

import java.math.BigDecimal;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;
import quanlycongtydulich.data.Db;
import quanlycongtydulich.services.Models.ProcessResult;

public class DanhMucService {
    public DefaultTableModel layPhuongTien() {
        return Db.query("SELECT MaPT, TenPT, GhiChu FROM PhuongTien ORDER BY TenPT");
    }
    public DefaultTableModel layDiemBan() {
        return Db.query("SELECT MaDiemBan, TenDiemBan, DiaChi, DienThoai FROM DiemBanVe ORDER BY TenDiemBan");
    }
    public DefaultTableModel layHDV() {
        return Db.query("SELECT MaHDV, HoTen, DienThoai, LuongCoBan, DangLamViec FROM HuongDanVien ORDER BY HoTen");
    }
    public DefaultTableModel layDiemThamQuan() {
        return Db.query("SELECT MaDiemTQ, TenDiemTQ, DiaDiem, NoiDung, YNghia FROM DiemThamQuan ORDER BY TenDiemTQ");
    }

    public ProcessResult themPhuongTien(String ma, String ten, String ghiChu) {
        if (ma.isEmpty() || ten.isEmpty()) return ProcessResult.fail("Mã và tên phương tiện không được để trống.");
        try {
            Db.execute("INSERT INTO PhuongTien(MaPT, TenPT, GhiChu) VALUES(?,?,?)", ma, ten, ghiChu);
            return ProcessResult.ok("Đã thêm phương tiện.");
        } catch (SQLException e) { return ProcessResult.fail(e.getMessage()); }
    }

    public ProcessResult themDiemBan(String ma, String ten, String diaChi, String dienThoai) {
        if (ma.isEmpty() || ten.isEmpty() || diaChi.isEmpty()) return ProcessResult.fail("Thông tin điểm bán vé chưa đầy đủ.");
        try {
            Db.execute("INSERT INTO DiemBanVe(MaDiemBan, TenDiemBan, DiaChi, DienThoai) VALUES(?,?,?,?)", ma, ten, diaChi, dienThoai);
            return ProcessResult.ok("Đã thêm điểm bán vé.");
        } catch (SQLException e) { return ProcessResult.fail(e.getMessage()); }
    }

    public ProcessResult themHDV(String ma, String ten, String dienThoai, BigDecimal luongCoBan) {
        if (ma.isEmpty() || ten.isEmpty() || luongCoBan.compareTo(BigDecimal.ZERO) < 0)
            return ProcessResult.fail("Thông tin hướng dẫn viên không hợp lệ.");
        try {
            Db.execute("INSERT INTO HuongDanVien(MaHDV, HoTen, DienThoai, LuongCoBan, DangLamViec) VALUES(?,?,?,?,1)", ma, ten, dienThoai, luongCoBan);
            return ProcessResult.ok("Đã thêm hướng dẫn viên.");
        } catch (SQLException e) { return ProcessResult.fail(e.getMessage()); }
    }

    public ProcessResult themDiemThamQuan(String ma, String ten, String diaDiem, String noiDung, String yNghia) {
        if (ma.isEmpty() || ten.isEmpty() || diaDiem.isEmpty()) return ProcessResult.fail("Thông tin điểm tham quan chưa đầy đủ.");
        try {
            Db.execute("INSERT INTO DiemThamQuan(MaDiemTQ, TenDiemTQ, DiaDiem, NoiDung, YNghia) VALUES(?,?,?,?,?)", ma, ten, diaDiem, noiDung, yNghia);
            return ProcessResult.ok("Đã thêm điểm tham quan.");
        } catch (SQLException e) { return ProcessResult.fail(e.getMessage()); }
    }
}
