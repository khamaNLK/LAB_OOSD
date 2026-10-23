using System.Drawing;
using System.Windows.Forms;

namespace QuanLyNhanSuDongA.Forms
{
    partial class FrmTinhLuongThang
    {
        private System.ComponentModel.IContainer components = null;
        private NumericUpDown numThang, numNam;
        private Button btnTinhLuong, btnChotBangLuong, btnDong;
        private Label lblCongThuc;
        private DataGridView dgvBangLuong;

        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null)) components.Dispose();
            base.Dispose(disposing);
        }

        private void InitializeComponent()
        {
            this.Font = new Font("Segoe UI", 9.5F);
            this.numThang = new NumericUpDown { Location = new Point(140, 20), Size = new Size(75, 26), Minimum = 1, Maximum = 12, Value = 10 };
            this.numNam = new NumericUpDown { Location = new Point(280, 20), Size = new Size(95, 26), Minimum = 2020, Maximum = 2100, Value = 2026 };
            this.btnTinhLuong = new Button { Location = new Point(620, 16), Size = new Size(195, 36), Text = "Tính Bảng Lương Tháng" };
            this.btnTinhLuong.Click += this.btnTinhLuong_Click;
            this.btnChotBangLuong = new Button { Location = new Point(825, 16), Size = new Size(195, 36), Text = "Chốt Bảng Lương" };
            this.btnChotBangLuong.Click += this.btnChotBangLuong_Click;

            this.lblCongThuc = new Label
            {
                Location = new Point(18, 62),
                Size = new Size(1002, 24),
                Font = new Font("Segoe UI", 9.5F, FontStyle.Bold),
                ForeColor = Color.FromArgb(15, 52, 99),
                Text = "Công thức: Thực lãnh = Lương ngày công + Phụ cấp (Chức vụ, Lương, Độc hại) + Phụ trội (x1.5) + Thưởng - Phạt KL - Trích BHXH/BHYT"
            };

            this.dgvBangLuong = new DataGridView { Location = new Point(18, 92), Size = new Size(1002, 418), ReadOnly = true, AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill };
            this.btnDong = new Button { Location = new Point(890, 524), Size = new Size(130, 36), Text = "Đóng" };
            this.btnDong.Click += this.btnDong_Click;

            this.ClientSize = new Size(1040, 575);
            this.Controls.AddRange(new Control[] {
                new Label { Text = "Tháng tính lương:", Location = new Point(18, 23), AutoSize = true }, this.numThang,
                new Label { Text = "Năm:", Location = new Point(230, 23), AutoSize = true }, this.numNam,
                this.btnTinhLuong, this.btnChotBangLuong, this.lblCongThuc, this.dgvBangLuong, this.btnDong
            });
            this.StartPosition = FormStartPosition.CenterParent;
            this.Text = "Tính Bảng Lương Hằng Tháng & Thống kê Quỹ Lương (Công ty Sơn Đông Á)";
        }
    }
}
