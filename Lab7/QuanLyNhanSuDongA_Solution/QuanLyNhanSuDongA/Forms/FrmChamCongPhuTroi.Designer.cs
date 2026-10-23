using System;
using System.Drawing;
using System.Windows.Forms;

namespace QuanLyNhanSuDongA.Forms
{
    partial class FrmChamCongPhuTroi
    {
        private System.ComponentModel.IContainer components = null;
        private TextBox txtSoPhieuPT;
        private DateTimePicker dtpNgayPT;
        private ComboBox cboHinhThucPT, cboNhanVien;
        private NumericUpDown numSoGioPT, numNgayCongThucTe;
        private Button btnGhiPhuTroi, btnChotChamCong, btnDong;
        private DataGridView dgvPhuTroi, dgvChamCong;

        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null)) components.Dispose();
            base.Dispose(disposing);
        }

        private void InitializeComponent()
        {
            this.Font = new Font("Segoe UI", 9.5F);
            this.txtSoPhieuPT = new TextBox { Location = new Point(135, 18), Size = new Size(135, 26), Text = "PPT2026_10_04" };
            this.dtpNgayPT = new DateTimePicker { Location = new Point(385, 18), Size = new Size(125, 26), Format = DateTimePickerFormat.Short, Value = new DateTime(2026, 10, 25) };
            this.cboHinhThucPT = new ComboBox { Location = new Point(620, 18), Size = new Size(155, 26), DropDownStyle = ComboBoxStyle.DropDownList };
            this.numSoGioPT = new NumericUpDown { Location = new Point(915, 18), Size = new Size(105, 26), Minimum = 0.5m, Maximum = 24m, DecimalPlaces = 1, Increment = 0.5m, Value = 4.0m };

            this.cboNhanVien = new ComboBox { Location = new Point(135, 56), Size = new Size(245, 26), DropDownStyle = ComboBoxStyle.DropDownList };
            this.numNgayCongThucTe = new NumericUpDown { Location = new Point(545, 56), Size = new Size(90, 26), Minimum = 0m, Maximum = 31m, DecimalPlaces = 1, Increment = 0.5m, Value = 26.0m };
            this.btnGhiPhuTroi = new Button { Location = new Point(665, 52), Size = new Size(175, 34), Text = "Ghi Phiếu Phụ trội (x1.5)" };
            this.btnGhiPhuTroi.Click += this.btnGhiPhuTroi_Click;
            this.btnChotChamCong = new Button { Location = new Point(850, 52), Size = new Size(170, 34), Text = "Chốt Chấm công Tháng" };
            this.btnChotChamCong.Click += this.btnChotChamCong_Click;

            this.dgvPhuTroi = new DataGridView { Location = new Point(18, 100), Size = new Size(1002, 210), ReadOnly = true, AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill };
            this.dgvChamCong = new DataGridView { Location = new Point(18, 320), Size = new Size(1002, 192), ReadOnly = true, AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill };

            this.btnDong = new Button { Location = new Point(890, 524), Size = new Size(130, 36), Text = "Đóng" };
            this.btnDong.Click += this.btnDong_Click;

            this.ClientSize = new Size(1040, 575);
            this.Controls.AddRange(new Control[] {
                new Label { Text = "Số phiếu phụ trội:", Location = new Point(18, 21), AutoSize = true }, this.txtSoPhieuPT,
                new Label { Text = "Ngày phụ trội:", Location = new Point(285, 21), AutoSize = true }, this.dtpNgayPT,
                new Label { Text = "Hình thức PT:", Location = new Point(525, 21), AutoSize = true }, this.cboHinhThucPT,
                new Label { Text = "Số giờ PT (x1.5):", Location = new Point(795, 21), AutoSize = true }, this.numSoGioPT,
                new Label { Text = "Nhân viên:", Location = new Point(18, 59), AutoSize = true }, this.cboNhanVien,
                new Label { Text = "Ngày công thực tế:", Location = new Point(405, 59), AutoSize = true }, this.numNgayCongThucTe,
                this.btnGhiPhuTroi, this.btnChotChamCong, this.dgvPhuTroi, this.dgvChamCong, this.btnDong
            });
            this.StartPosition = FormStartPosition.CenterParent;
            this.Text = "Ban Chấm công: Bảng Chấm công Tháng & Phiếu Ghi nhận Giờ Phụ trội (Hệ số 1.5)";
        }
    }
}
