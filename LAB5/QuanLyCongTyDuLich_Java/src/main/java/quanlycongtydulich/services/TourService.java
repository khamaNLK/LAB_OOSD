package quanlycongtydulich.services;

import java.math.BigDecimal;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;
import quanlycongtydulich.data.Db;
import quanlycongtydulich.services.Models.*;

public class TourService {
    public DefaultTableModel layTour() {
        return Db.query("SELECT MaTour, TenTour, SoNgay, SoDem, DonGiaKhach, MoTa, DangMoBan FROM Tour ORDER BY MaTour");
    }

    public DefaultTableModel layTourMoBan() {
        return Db.query("SELECT MaTour, MaTour + ' - ' + TenTour AS HienThi, SoNgay, DonGiaKhach FROM Tour WHERE DangMoBan = 1 ORDER BY MaTour");
    }

    public DefaultTableModel layDiemDung(String maTour) {
        return Db.query("SELECT ThuTu, TenDiemDung, DoiPhuongTien, CoNoiAn, CoKhachSan, HangSaoKhachSan, GhiChu FROM TourDiemDung WHERE MaTour = ? ORDER BY ThuTu", maTour);
    }

    public DefaultTableModel layChang(String maTour) {
        return Db.query("SELECT t.ThuTuChang, p.TenPT, t.MaPT, t.GhiChu FROM TourPhuongTien t JOIN PhuongTien p ON t.MaPT = p.MaPT WHERE t.MaTour = ? ORDER BY t.ThuTuChang", maTour);
    }

    public DefaultTableModel layDiemTQTour(String maTour) {
        return Db.query("SELECT t.ThuTu, d.MaDiemTQ, d.TenDiemTQ, d.DiaDiem FROM TourDiemThamQuan t JOIN DiemThamQuan d ON t.MaDiemTQ = d.MaDiemTQ WHERE t.MaTour = ? ORDER BY t.ThuTu", maTour);
    }

    public ProcessResult themTour(String ma, String ten, int soNgay, int soDem, BigDecimal donGia, String moTa) {
        if (ma.isEmpty() || ten.isEmpty()) return ProcessResult.fail("Mã tour và tên tour không được để trống.");
        if (soNgay <= 0 || soDem < 0 || soDem > soNgay || donGia.compareTo(BigDecimal.ZERO) < 0)
            return ProcessResult.fail("Số ngày, số đêm hoặc đơn giá không hợp lệ.");
        try {
            Db.execute("INSERT INTO Tour(MaTour, TenTour, SoNgay, SoDem, DonGiaKhach, MoTa, DangMoBan) VALUES(?,?,?,?,?,?,1)",
                       ma, ten, soNgay, soDem, donGia, moTa);
            return ProcessResult.ok("Đã thêm tour.");
        } catch (SQLException e) {
            return ProcessResult.fail(e.getMessage());
        }
    }

    public ProcessResult themDiemDung(String maTour, int thuTu, String ten, boolean doiPT, boolean coAn, boolean coKS, Integer hangSao, String ghiChu) {
        if (maTour.isEmpty() || thuTu <= 0 || ten.isEmpty()) return ProcessResult.fail("Thông tin điểm dừng chưa đầy đủ.");
        if (coKS && (hangSao == null || hangSao < 2 || hangSao > 5)) return ProcessResult.fail("Khách sạn phải có hạng từ 2 đến 5 sao.");
        try {
            Db.execute("INSERT INTO TourDiemDung(MaTour, ThuTu, TenDiemDung, DoiPhuongTien, CoNoiAn, CoKhachSan, HangSaoKhachSan, GhiChu) VALUES(?,?,?,?,?,?,?,?)",
                       maTour, thuTu, ten, doiPT, coAn, coKS, coKS ? hangSao : null, ghiChu);
            return ProcessResult.ok("Đã thêm điểm dừng.");
        } catch (SQLException e) {
            return ProcessResult.fail(e.getMessage());
        }
    }
}
