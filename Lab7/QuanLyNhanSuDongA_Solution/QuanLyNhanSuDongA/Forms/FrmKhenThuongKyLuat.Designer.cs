using System;
using System.Drawing;
using System.Windows.Forms;

namespace QuanLyNhanSuDongA.Forms
{
    partial class FrmKhenThuongKyLuat
    {
        private System.ComponentModel.IContainer components = null;
        private TextBox txtSoQD, txtNoiDung, txtSoPhieuChiThu, txtNguoiKy;
        private ComboBox cboLoaiDoiTuong, cboNhanVien, cboPhongBan;
        private DateTimePicker dtpNgayKy;
        private NumericUpDown numSoTien;
        private Button btnLapKhenThuong, btnLapKyLuat, btnDong;
        private DataGridView dgvKTKL;

        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null)) components.Dispose();
            base.Dispose(disposing);
        }

        private void InitializeComponent()
        {
            this.Font = new Font("Segoe UI", 9.5F);
            this.txtSoQD = new TextBox { Location = new Point(110, 18), Size = new Size(125, 26), Text = "QDKT2026_03" };
            this.cboLoaiDoiTuong = new ComboBox { Location = new Point(325, 18), Size = new Size(120, 26), DropDownStyle = ComboBoxStyle.DropDownList };
            this.cboLoaiDoiTuong.SelectedIndexChanged += this.cboLoaiDoiTuong_SelectedIndexChanged;
            this.cboNhanVien = new ComboBox { Location = new Point(535, 18), Size = new Size(205, 26), DropDownStyle = ComboBoxStyle.DropDownList };
            this.cboPhongBan = new ComboBox { Location = new Point(825, 18), Size = new Size(195, 26), DropDownStyle = ComboBoxStyle.DropDownList, Enabled = false };

            this.txtNoiDung = new TextBox { Location = new Point(110, 56), Size = new Size(335, 26), Text = "Thưởng ngày lễ / Thưởng cuối năm / Lương tháng 13" };
            this.numSoTien = new NumericUpDown { Location = new Point(535, 56), Size = new Size(145, 26) };
            this.txtSoPhieuChiThu = new TextBox { Location = new Point(810, 56), Size = new Size(100, 26), Text = "PCKT003" };
            this.dtpNgayKy = new DateTimePicker { Location = new Point(920, 56), Size = new Size(100, 26), Format = DateTimePickerFormat.Short, Value = new DateTime(2026, 10, 22) };

            this.txtNguoiKy = new TextBox { Location = new Point(110, 94), Size = new Size(150, 26), Text = "Giám đốc" };
            this.btnLapKhenThuong = new Button { Location = new Point(620, 90), Size = new Size(195, 34), Text = "Lập QĐ Khen thưởng & Phiếu chi" };
            this.btnLapKhenThuong.Click += this.btnLapKhenThuong_Click;
            this.btnLapKyLuat = new Button { Location = new Point(825, 90), Size = new Size(195, 34), Text = "Lập QĐ Kỷ luật & Phiếu thu" };
            this.btnLapKyLuat.Click += this.btnLapKyLuat_Click;

            this.dgvKTKL = new DataGridView { Location = new Point(18, 138), Size = new Size(1002, 372), ReadOnly = true, AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill };
            this.btnDong = new Button { Location = new Point(890, 524), Size = new Size(130, 36), Text = "Đóng" };
            this.btnDong.Click += this.btnDong_Click;

            this.ClientSize = new Size(1040, 575);
            this.Controls.AddRange(new Control[] {
                new Label { Text = "Số Quyết định:", Location = new Point(18, 21), AutoSize = true }, this.txtSoQD,
                new Label { Text = "Đối tượng:", Location = new Point(248, 21), AutoSize = true }, this.cboLoaiDoiTuong,
                new Label { Text = "Nhân viên:", Location = new Point(458, 21), AutoSize = true }, this.cboNhanVien,
                new Label { Text = "Phòng ban:", Location = new Point(748, 21), AutoSize = true }, this.cboPhongBan,
                new Label { Text = "Nội dung / Dịp:", Location = new Point(18, 59), AutoSize = true }, this.txtNoiDung,
                new Label { Text = "Số tiền:", Location = new Point(468, 59), AutoSize = true }, this.numSoTien,
                new Label { Text = "Số Phiếu Chi/Thu:", Location = new Point(692, 59), AutoSize = true }, this.txtSoPhieuChiThu,
                this.dtpNgayKy,
                new Label { Text = "Người ký QĐ:", Location = new Point(18, 97), AutoSize = true }, this.txtNguoiKy,
                this.btnLapKhenThuong, this.btnLapKyLuat, this.dgvKTKL, this.btnDong
            });
            this.StartPosition = FormStartPosition.CenterParent;
            this.Text = "Quản lý Quyết định Khen thưởng (Phiếu chi) & Quyết định Kỷ luật (Phiếu thu nộp phạt)";
        }
    }
}
