using System;
using System.Drawing;
using System.Windows.Forms;

namespace QuanLyNhanSuDongA.Forms
{
    partial class FrmQuyetDinhChucVuLuong
    {
        private System.ComponentModel.IContainer components = null;
        private ComboBox cboNhanVien, cboChucVu;
        private TextBox txtSoQDCV, txtSoQDL, txtLyDo;
        private NumericUpDown numPhuCapCV, numLuongCB, numPhuCapLuong, numPhuCapDocHai;
        private DateTimePicker dtpNgayHieuLuc;
        private Button btnBanHanhQDCV, btnBanHanhQDLuong, btnDong;
        private DataGridView dgvQuyetDinh;

        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null)) components.Dispose();
            base.Dispose(disposing);
        }

        private void InitializeComponent()
        {
            this.Font = new Font("Segoe UI", 9.5F);
            this.cboNhanVien = new ComboBox { Location = new Point(110, 18), Size = new Size(230, 26), DropDownStyle = ComboBoxStyle.DropDownList };
            this.txtSoQDCV = new TextBox { Location = new Point(445, 18), Size = new Size(110, 26), Text = "QDCV005" };
            this.cboChucVu = new ComboBox { Location = new Point(635, 18), Size = new Size(195, 26), DropDownStyle = ComboBoxStyle.DropDownList };
            this.numPhuCapCV = new NumericUpDown { Location = new Point(915, 18), Size = new Size(105, 26) };

            this.txtSoQDL = new TextBox { Location = new Point(110, 56), Size = new Size(110, 26), Text = "QDL005" };
            this.numLuongCB = new NumericUpDown { Location = new Point(325, 56), Size = new Size(140, 26) };
            this.numPhuCapLuong = new NumericUpDown { Location = new Point(580, 56), Size = new Size(130, 26) };
            this.numPhuCapDocHai = new NumericUpDown { Location = new Point(855, 56), Size = new Size(165, 26) };

            this.dtpNgayHieuLuc = new DateTimePicker { Location = new Point(110, 94), Size = new Size(125, 26), Format = DateTimePickerFormat.Short, Value = new DateTime(2026, 10, 15) };
            this.txtLyDo = new TextBox { Location = new Point(315, 94), Size = new Size(375, 26), Text = "Bổ nhiệm chức vụ & điều chỉnh mức lương chính thức" };
            this.btnBanHanhQDCV = new Button { Location = new Point(705, 90), Size = new Size(150, 34), Text = "Ban hành QĐ Chức vụ" };
            this.btnBanHanhQDCV.Click += this.btnBanHanhQDCV_Click;
            this.btnBanHanhQDLuong = new Button { Location = new Point(865, 90), Size = new Size(155, 34), Text = "Ban hành QĐ Lương" };
            this.btnBanHanhQDLuong.Click += this.btnBanHanhQDLuong_Click;

            this.dgvQuyetDinh = new DataGridView { Location = new Point(18, 140), Size = new Size(1002, 370), ReadOnly = true, AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill };
            this.btnDong = new Button { Location = new Point(890, 524), Size = new Size(130, 36), Text = "Đóng" };
            this.btnDong.Click += this.btnDong_Click;

            this.ClientSize = new Size(1040, 575);
            this.Controls.AddRange(new Control[] {
                new Label { Text = "Nhân viên:", Location = new Point(18, 21), AutoSize = true }, this.cboNhanVien,
                new Label { Text = "Số QĐ CV:", Location = new Point(355, 21), AutoSize = true }, this.txtSoQDCV,
                new Label { Text = "Chức vụ:", Location = new Point(568, 21), AutoSize = true }, this.cboChucVu,
                new Label { Text = "PC chức vụ:", Location = new Point(838, 21), AutoSize = true }, this.numPhuCapCV,
                new Label { Text = "Số QĐ Lương:", Location = new Point(18, 59), AutoSize = true }, this.txtSoQDL,
                new Label { Text = "Lương cơ bản:", Location = new Point(230, 59), AutoSize = true }, this.numLuongCB,
                new Label { Text = "Phụ cấp lương:", Location = new Point(475, 59), AutoSize = true }, this.numPhuCapLuong,
                new Label { Text = "PC độc hại (Sơn):", Location = new Point(725, 59), AutoSize = true }, this.numPhuCapDocHai,
                new Label { Text = "Ngày hiệu lực:", Location = new Point(18, 97), AutoSize = true }, this.dtpNgayHieuLuc,
                new Label { Text = "Lý do:", Location = new Point(255, 97), AutoSize = true }, this.txtLyDo,
                this.btnBanHanhQDCV, this.btnBanHanhQDLuong, this.dgvQuyetDinh, this.btnDong
            });
            this.StartPosition = FormStartPosition.CenterParent;
            this.Text = "Quản lý Quyết định Chức vụ & Quyết định Lương - Phụ cấp Độc hại";
        }
    }
}
