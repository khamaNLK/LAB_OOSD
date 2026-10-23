using System.Drawing;
using System.Windows.Forms;

namespace QuanLyNhanSuDongA.Forms
{
    partial class FrmMain
    {
        private System.ComponentModel.IContainer components = null;
        private Label lblTitle;
        private Label lblStatus;
        private Button btnDanhMuc;
        private Button btnTuyenDung;
        private Button btnHopDong;
        private Button btnChucVuLuong;
        private Button btnKeHoachTuan;
        private Button btnChamCongPhuTroi;
        private Button btnKhenThuongKyLuat;
        private Button btnTinhLuong;
        private Button btnThoat;

        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null)) components.Dispose();
            base.Dispose(disposing);
        }

        private void InitializeComponent()
        {
            this.lblTitle = new Label();
            this.lblStatus = new Label();
            this.btnDanhMuc = new Button();
            this.btnTuyenDung = new Button();
            this.btnHopDong = new Button();
            this.btnChucVuLuong = new Button();
            this.btnKeHoachTuan = new Button();
            this.btnChamCongPhuTroi = new Button();
            this.btnKhenThuongKyLuat = new Button();
            this.btnTinhLuong = new Button();
            this.btnThoat = new Button();
            this.SuspendLayout();

            this.lblTitle.Font = new Font("Segoe UI", 16F, FontStyle.Bold);
            this.lblTitle.ForeColor = Color.FromArgb(15, 52, 99);
            this.lblTitle.Location = new Point(40, 22);
            this.lblTitle.Size = new Size(860, 42);
            this.lblTitle.Text = "CÔNG TY SƠN ĐÔNG Á - QUẢN LÝ NHÂN SỰ & TIỀN LƯƠNG";
            this.lblTitle.TextAlign = ContentAlignment.MiddleCenter;

            Button[] buttons = {
                this.btnDanhMuc, this.btnTuyenDung,
                this.btnHopDong, this.btnChucVuLuong,
                this.btnKeHoachTuan, this.btnChamCongPhuTroi,
                this.btnKhenThuongKyLuat, this.btnTinhLuong
            };
            string[] labels = {
                "1. Danh mục Phòng ban - Chức vụ - Ca",
                "2. Tuyển dụng & Theo dõi Thử việc",
                "3. Hợp đồng Lao động & Bảo hiểm (BHXH/BHYT)",
                "4. Quyết định Chức vụ & Quyết định Lương",
                "5. Kế hoạch Làm việc Tuần & Phân ca",
                "6. Chấm công Tháng & Phiếu Phụ trội (x1.5)",
                "7. Quyết định Khen thưởng & Kỷ luật",
                "8. Tính Lương Hằng tháng & Thống kê"
            };
            for (int i = 0; i < buttons.Length; i++)
            {
                int col = i % 2;
                int row = i / 2;
                buttons[i].Font = new Font("Segoe UI", 10.5F, FontStyle.Regular);
                buttons[i].Location = new Point(65 + col * 420, 85 + row * 78);
                buttons[i].Size = new Size(390, 58);
                buttons[i].Text = labels[i];
                buttons[i].UseVisualStyleBackColor = true;
            }

            this.btnDanhMuc.Click += this.btnDanhMuc_Click;
            this.btnTuyenDung.Click += this.btnTuyenDung_Click;
            this.btnHopDong.Click += this.btnHopDong_Click;
            this.btnChucVuLuong.Click += this.btnChucVuLuong_Click;
            this.btnKeHoachTuan.Click += this.btnKeHoachTuan_Click;
            this.btnChamCongPhuTroi.Click += this.btnChamCongPhuTroi_Click;
            this.btnKhenThuongKyLuat.Click += this.btnKhenThuongKyLuat_Click;
            this.btnTinhLuong.Click += this.btnTinhLuong_Click;

            this.btnThoat.Font = new Font("Segoe UI", 10.5F, FontStyle.Bold);
            this.btnThoat.Location = new Point(275, 405);
            this.btnThoat.Size = new Size(390, 50);
            this.btnThoat.Text = "Thoát hệ thống";
            this.btnThoat.UseVisualStyleBackColor = true;
            this.btnThoat.Click += this.btnThoat_Click;

            this.lblStatus.Font = new Font("Segoe UI", 9.5F, FontStyle.Italic);
            this.lblStatus.ForeColor = Color.FromArgb(40, 90, 40);
            this.lblStatus.Location = new Point(65, 470);
            this.lblStatus.Size = new Size(810, 26);
            this.lblStatus.Text = "Trạng thái CSDL: Đang kiểm tra...";

            this.ClientSize = new Size(940, 515);
            this.Controls.AddRange(new Control[] {
                this.lblTitle, this.btnDanhMuc, this.btnTuyenDung, this.btnHopDong, this.btnChucVuLuong,
                this.btnKeHoachTuan, this.btnChamCongPhuTroi, this.btnKhenThuongKyLuat, this.btnTinhLuong,
                this.btnThoat, this.lblStatus
            });
            this.StartPosition = FormStartPosition.CenterScreen;
            this.Text = "Hệ thống Quản lý Nhân sự - Công ty Sơn Đông Á (Bài 7)";
            this.ResumeLayout(false);
        }
    }
}
