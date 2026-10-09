package quanlycongtydulich.services;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import quanlycongtydulich.data.Db;
import quanlycongtydulich.services.Models.*;

public class PhanCongService {

    public ProcessResult phanCong(String maPC, String maHDV, String loai, String maDoiTuong, BigDecimal thuLao) {
        if (maPC.isEmpty() || maHDV.isEmpty() || maDoiTuong.isEmpty() || thuLao.compareTo(BigDecimal.ZERO) < 0)
            return ProcessResult.fail("Thông tin phân công không hợp lệ.");

        try {
            LocalDate bd, kt;
            if (loai.equals(QuyDinh.LE)) {
                // Kiểm tra chuyến lẻ đã có HDV chưa (quy tắc BR06: đúng 1 HDV)
                Object count = Db.scalar("SELECT COUNT(*) FROM PhanCongHDV WHERE MaChuyen = ?", maDoiTuong);
                if (count != null && ((Number)count).intValue() > 0)
                    return ProcessResult.fail("Mỗi chuyến khách lẻ chỉ được phân công một hướng dẫn viên.");

                Object[] row = (Object[]) Db.scalar("SELECT NgayDi FROM ChuyenLe WHERE MaChuyen = ?", maDoiTuong);
                Date dDi = (Date) Db.scalar("SELECT NgayDi FROM ChuyenLe WHERE MaChuyen = ?", maDoiTuong);
                Date dVe = (Date) Db.scalar("SELECT NgayVe FROM ChuyenLe WHERE MaChuyen = ?", maDoiTuong);
                if (dDi == null) return ProcessResult.fail("Không tìm thấy chuyến khách lẻ.");
                bd = dDi.toLocalDate(); kt = dVe.toLocalDate();
            } else {
                Date dDi = (Date) Db.scalar("SELECT NgayDi FROM DangKyDoan WHERE SoDKDoan = ? AND TrangThai = ?", maDoiTuong, QuyDinh.DA_DANG_KY);
                Date dKt = (Date) Db.scalar("SELECT NgayKetThucDuKien FROM DangKyDoan WHERE SoDKDoan = ? AND TrangThai = ?", maDoiTuong, QuyDinh.DA_DANG_KY);
                if (dDi == null) return ProcessResult.fail("Không tìm thấy phiếu đoàn đang hiệu lực.");
                bd = dDi.toLocalDate(); kt = dKt.toLocalDate();
            }

            // Kiểm tra trùng lịch: NgayBatDau <= kt AND NgayKetThuc >= bd
            String sqlCheck = "SELECT COUNT(*) FROM PhanCongHDV WHERE MaHDV = ? AND NgayBatDau <= ? AND NgayKetThuc >= ?";
            Object trung = Db.scalar(sqlCheck, maHDV, Date.valueOf(kt), Date.valueOf(bd));
            if (trung != null && ((Number)trung).intValue() > 0) {
                return ProcessResult.fail("Hướng dẫn viên bị chồng chéo lịch từ " + bd + " đến " + kt + ".");
            }

            // Thực hiện ghi nhận phân công
            String sqlInsert = "INSERT INTO PhanCongHDV(MaPC, MaHDV, LoaiDoiTuong, MaChuyen, SoDKDoan, NgayBatDau, NgayKetThuc, ThuLaoTour) "
                             + "VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
            Db.execute(sqlInsert, maPC, maHDV, loai, 
                       loai.equals(QuyDinh.LE) ? maDoiTuong : null,
                       loai.equals(QuyDinh.DOAN) ? maDoiTuong : null,
                       Date.valueOf(bd), Date.valueOf(kt), thuLao);
            return ProcessResult.ok("Đã phân công hướng dẫn viên.");
        } catch (SQLException ex) {
            return ProcessResult.fail(ex.getMessage());
        }
    }
}