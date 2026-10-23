using System;
using System.Drawing;
using System.Windows.Forms;

namespace QuanLyNhanSuDongA.Forms
{
    partial class FrmKeHoachTuan
    {
        private System.ComponentModel.IContainer components = null;
        private TextBox txtMaKeHoach, txtGhiChu;
        private ComboBox cboPhongBan, cboNhanVien, cboCaLamViec;
        private NumericUpDown numTuan, numGioPTDuKien;
        private DateTimePicker dtpTuNgay, dtpDenNgay, dtpNgayLamViec;
        private Button btnGuiBanChamCong, btnDong;
        private DataGridView dgvKeHoachTuan;

        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null)) components.Dispose();
            base.Dispose(disposing);
        }

        private void InitializeComponent()
        {
            this.Font = new Font("Segoe UI", 9.5F);
            this.txtMaKeHoach = new TextBox { Location = new Point(115, 18), Size = new Size(130, 26), Text = "KHT2026_W43" };
            this.cboPhongBan = new ComboBox { Location = new Point(340, 18), Size = new Size(220, 26), DropDownStyle = ComboBoxStyle.DropDownList };
            this.numTuan = new NumericUpDown { Location = new Point(625, 18), Size = new Size(65, 26), Minimum = 1, Maximum = 53, Value = 43 };
            this.dtpTuNgay = new DateTimePicker { Location = new Point(755, 18), Size = new Size(115, 26), Format = DateTimePickerFormat.Short, Value = new DateTime(2026, 10, 19) };
            this.dtpDenNgay = new DateTimePicker { Location = new Point(905, 18), Size = new Size(115, 26), Format = DateTimePickerFormat.Short, Value = new DateTime(2026, 10, 25) };

            this.cboNhanVien = new ComboBox { Location = new Point(115, 56), Size = new Size(220, 26), DropDownStyle = ComboBoxStyle.DropDownList };
            this.cboCaLamViec = new ComboBox { Location = new Point(415, 56), Size = new Size(200, 26), DropDownStyle = ComboBoxStyle.DropDownList };
            this.dtpNgayLamViec = new DateTimePicker { Location = new Point(705, 56), Size = new Size(115, 26), Format = DateTimePickerFormat.Short, Value = new DateTime(2026, 10, 21) };
            this.numGioPTDuKien = new NumericUpDown { Location = new Point(940, 56), Size = new Size(80, 26), Minimum = 0, Maximum = 16, DecimalPlaces = 1, Increment = 0.5m, Value = 3.0m };

            this.txtGhiChu = new TextBox { Location = new Point(115, 94), Size = new Size(685, 26), Text = "Phân công ca sản xuất sơn tuần 43 & đăng ký phụ trội dự kiến" };
            this.btnGuiBanChamCong = new Button { Location = new Point(820, 90), Size = new Size(200, 34), Text = "Lưu & Gửi Ban Chấm công" };
            this.btnGuiBanChamCong.Click += this.btnGuiBanChamCong_Click;

            this.dgvKeHoachTuan = new DataGridView { Location = new Point(18, 138), Size = new Size(1002, 372), ReadOnly = true, AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill };
            this.btnDong = new Button { Location = new Point(890, 524), Size = new Size(130, 36), Text = "Đóng" };
            this.btnDong.Click += this.btnDong_Click;

            this.ClientSize = new Size(1040, 575);
            this.Controls.AddRange(new Control[] {
                new Label { Text = "Mã kế hoạch:", Location = new Point(18, 21), AutoSize = true }, this.txtMaKeHoach,
                new Label { Text = "Phòng ban:", Location = new Point(258, 21), AutoSize = true }, this.cboPhongBan,
                new Label { Text = "Tuần:", Location = new Point(575, 21), AutoSize = true }, this.numTuan,
                new Label { Text = "Từ ngày:", Location = new Point(698, 21), AutoSize = true }, this.dtpTuNgay,
                new Label { Text = "-", Location = new Point(882, 21), AutoSize = true }, this.dtpDenNgay,
                new Label { Text = "Nhân viên:", Location = new Point(18, 59), AutoSize = true }, this.cboNhanVien,
                new Label { Text = "Ca làm:", Location = new Point(350, 59), AutoSize = true }, this.cboCaLamViec,
                new Label { Text = "Ngày làm:", Location = new Point(630, 59), AutoSize = true }, this.dtpNgayLamViec,
                new Label { Text = "Giờ PT dự kiến:", Location = new Point(835, 59), AutoSize = true }, this.numGioPTDuKien,
                new Label { Text = "Ghi chú:", Location = new Point(18, 97), AutoSize = true }, this.txtGhiChu,
                this.btnGuiBanChamCong, this.dgvKeHoachTuan, this.btnDong
            });
            this.StartPosition = FormStartPosition.CenterParent;
            this.Text = "Lập Kế hoạch Phân công Làm việc Tuần & Dự kiến Phụ trội";
        }
    }
}
