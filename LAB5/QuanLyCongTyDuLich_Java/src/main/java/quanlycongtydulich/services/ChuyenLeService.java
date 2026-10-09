package quanlycongtydulich.services;

import java.sql.SQLException;
import java.time.LocalDate;
import javax.swing.table.DefaultTableModel;
import quanlycongtydulich.data.Db;
import quanlycongtydulich.services.Models.*;

public class ChuyenLeService {
    public DefaultTableModel layChuyen() {
        return Db.query("SELECT c.MaChuyen, c.MaTour, t.TenTour, c.NgayDi, c.NgayVe, c.DiaDiemDon, c.TrangThai "
                      + "FROM ChuyenLe c JOIN Tour t ON c.MaTour = t.MaTour ORDER BY c.NgayDi DESC");
    }

    public DefaultTableModel layChuyenMo() {
        return Db.query("SELECT c.MaChuyen, c.MaChuyen + ' - ' + t.TenTour + ' (' + CONVERT(varchar(10), c.NgayDi, 103) + ')' AS HienThi, t.DonGiaKhach "
                      + "FROM ChuyenLe c JOIN Tour t ON c.MaTour = t.MaTour WHERE c.TrangThai = ? ORDER BY c.NgayDi", QuyDinh.MO_DANG_KY);
    }

    public ProcessResult themChuyen(String ma, String maTour, LocalDate ngayDi, String diaDiemDon) {
        if (ma.isEmpty() || maTour.isEmpty() || diaDiemDon.isEmpty())
            return ProcessResult.fail("Thông tin chuyến chưa đầy đủ.");
        Object o = Db.scalar("SELECT SoNgay FROM Tour WHERE MaTour = ? AND DangMoBan = 1", maTour);
        if (o == null) return ProcessResult.fail("Tour không tồn tại hoặc chưa mở bán.");
        int soNgay = ((Number)o).intValue();
        LocalDate ngayVe = ngayDi.plusDays(soNgay - 1);
        try {
            Db.execute("INSERT INTO ChuyenLe(MaChuyen, MaTour, NgayDi, NgayVe, DiaDiemDon, TrangThai) VALUES(?,?,?,?,?,?)",
                       ma, maTour, java.sql.Date.valueOf(ngayDi), java.sql.Date.valueOf(ngayVe), diaDiemDon, QuyDinh.MO_DANG_KY);
            return ProcessResult.ok("Đã tạo chuyến; ngày về " + ngayVe + ".");
        } catch (SQLException e) {
            return ProcessResult.fail(e.getMessage());
        }
    }

    public ProcessResult dongDangKy(String ma) {
        if (ma.isEmpty()) return ProcessResult.fail("Chưa chọn chuyến.");
        try {
            int n = Db.execute("UPDATE ChuyenLe SET TrangThai = ? WHERE MaChuyen = ?", QuyDinh.DONG_DANG_KY, ma);
            return n > 0 ? ProcessResult.ok("Đã đóng đăng ký chuyến " + ma + ".") : ProcessResult.fail("Không tìm thấy chuyến.");
        } catch (SQLException e) {
            return ProcessResult.fail(e.getMessage());
        }
    }
}
