package quanlycongtydulich.services;

import java.time.LocalDate;

public class Models {
    public static class ProcessResult {
        private boolean success;
        private String message;

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }

        public static ProcessResult ok(String msg) {
            ProcessResult r = new ProcessResult();
            r.success = true;
            r.message = msg;
            return r;
        }

        public static ProcessResult fail(String msg) {
            ProcessResult r = new ProcessResult();
            r.success = false;
            r.message = msg;
            return r;
        }
    }

    public static class ThanhVienDoanItem {
        public String hoTen;
        public LocalDate ngaySinh;
        public String soGiayTo;

        public ThanhVienDoanItem(String hoTen, LocalDate ngaySinh, String soGiayTo) {
            this.hoTen = hoTen;
            this.ngaySinh = ngaySinh;
            this.soGiayTo = soGiayTo;
        }
    }

    public static class QuyDinh {
        public static final int MOC_KHACH_DOAN = 12;
        public static final String LE = "LE";
        public static final String DOAN = "DOAN";
        public static final String MO_DANG_KY = "Mở đăng ký";
        public static final String DONG_DANG_KY = "Đóng đăng ký";
        public static final String DA_DANG_KY = "Đã đăng ký";
        public static final String HUY_MAT_COC = "Hủy - mất cọc";
        public static final String HOAN_TAT_THANH_TOAN = "Đã hoàn tất thanh toán";
    }
}
