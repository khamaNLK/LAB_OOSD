using System;
using System.Drawing;
using System.Windows.Forms;

namespace QuanLyNhanSuDongA.Forms
{
    partial class FrmHopDongBaoHiem
    {
        private System.ComponentModel.IContainer components = null;
        private TextBox txtSoHD, txtNoiDungHD, txtMaHoSoBH, txtSoSoBHXH, txtSoTheBHYT;
        private ComboBox cboNhanVien, cboLoaiHD;
        private DateTimePicker dtpNgayKy, dtpNgayBatDau, dtpNgayKetThuc;
        private NumericUpDown numMucDongBH;
        private GroupBox grpBaoHiem;
        private Button btnKyHopDong, btnDong;
        private DataGridView dgvHopDong;

        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null)) components.Dispose();
            base.Dispose(disposing);
        }

        private void InitializeComponent()
        {
            this.Font = new Font("Segoe UI", 9.5F);
            this.txtSoHD = new TextBox { Location = new Point(120, 18), Size = new Size(135, 26), Text = "HD2026_004" };
            this.cboNhanVien = new ComboBox { Location = new Point(350, 18), Size = new Size(230, 26), DropDownStyle = ComboBoxStyle.DropDownList };
            this.cboLoaiHD = new ComboBox { Location = new Point(690, 18), Size = new Size(140, 26), DropDownStyle = ComboBoxStyle.DropDownList };
            this.cboLoaiHD.SelectedIndexChanged += this.cboLoaiHD_SelectedIndexChanged;
            this.dtpNgayKy = new DateTimePicker { Location = new Point(905, 18), Size = new Size(115, 26), Format = DateTimePickerFormat.Short, Value = new DateTime(2026, 10, 15) };

            this.dtpNgayBatDau = new DateTimePicker { Location = new Point(120, 54), Size = new Size(135, 26), Format = DateTimePickerFormat.Short, Value = new DateTime(2026, 10, 15) };
            this.dtpNgayKetThuc = new DateTimePicker { Location = new Point(350, 54), Size = new Size(135, 26), Format = DateTimePickerFormat.Short, Value = new DateTime(2029, 10, 15) };
            this.txtNoiDungHD = new TextBox { Location = new Point(580, 54), Size = new Size(440, 26), Text = "Hợp đồng lao động chính thức tại Công ty Sơn Đông Á" };

            this.grpBaoHiem = new GroupBox { Location = new Point(18, 90), Size = new Size(1002, 72), Text = "Hồ sơ Bảo hiểm Y tế & Bảo hiểm Xã hội (Bắt buộc đối với Hợp đồng dài hạn - trích trừ bảng lương)" };
            this.txtMaHoSoBH = new TextBox { Location = new Point(95, 30), Size = new Size(100, 26), Text = "BH004" };
            this.txtSoSoBHXH = new TextBox { Location = new Point(295, 30), Size = new Size(145, 26), Text = "7926001148" };
            this.txtSoTheBHYT = new TextBox { Location = new Point(535, 30), Size = new Size(155, 26), Text = "DN4797926001148" };
            this.numMucDongBH = new NumericUpDown { Location = new Point(845, 30), Size = new Size(140, 26) };
            this.grpBaoHiem.Controls.AddRange(new Control[] {
                new Label { Text = "Mã HS BH:", Location = new Point(14, 33), AutoSize = true }, this.txtMaHoSoBH,
                new Label { Text = "Số sổ BHXH:", Location = new Point(205, 33), AutoSize = true }, this.txtSoSoBHXH,
                new Label { Text = "Số thẻ BHYT:", Location = new Point(450, 33), AutoSize = true }, this.txtSoTheBHYT,
                new Label { Text = "Mức trích trừ/tháng:", Location = new Point(705, 33), AutoSize = true }, this.numMucDongBH
            });

            this.btnKyHopDong = new Button { Location = new Point(835, 170), Size = new Size(185, 36), Text = "Ký HĐ & Lập Bảo hiểm" };
            this.btnKyHopDong.Click += this.btnKyHopDong_Click;

            this.dgvHopDong = new DataGridView { Location = new Point(18, 214), Size = new Size(1002, 300), ReadOnly = true, AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill };
            this.btnDong = new Button { Location = new Point(890, 524), Size = new Size(130, 36), Text = "Đóng" };
            this.btnDong.Click += this.btnDong_Click;

            this.ClientSize = new Size(1040, 575);
            this.Controls.AddRange(new Control[] {
                new Label { Text = "Số hợp đồng:", Location = new Point(18, 21), AutoSize = true }, this.txtSoHD,
                new Label { Text = "Nhân viên:", Location = new Point(268, 21), AutoSize = true }, this.cboNhanVien,
                new Label { Text = "Loại hợp đồng:", Location = new Point(592, 21), AutoSize = true }, this.cboLoaiHD,
                new Label { Text = "Ngày ký:", Location = new Point(840, 21), AutoSize = true }, this.dtpNgayKy,
                new Label { Text = "Ngày BĐ:", Location = new Point(18, 57), AutoSize = true }, this.dtpNgayBatDau,
                new Label { Text = "Ngày KT:", Location = new Point(275, 57), AutoSize = true }, this.dtpNgayKetThuc,
                new Label { Text = "Điều khoản:", Location = new Point(495, 57), AutoSize = true }, this.txtNoiDungHD,
                this.grpBaoHiem, this.btnKyHopDong, this.dgvHopDong, this.btnDong
            });
            this.StartPosition = FormStartPosition.CenterParent;
            this.Text = "Ký Hợp đồng Lao động Chính thức & Lập Hồ sơ BHXH, BHYT";
        }
    }
}
