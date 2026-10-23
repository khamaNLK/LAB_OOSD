using System;
using System.Drawing;
using System.Windows.Forms;

namespace QuanLyNhanSuDongA.Forms
{
    partial class FrmTuyenDungThuViec
    {
        private System.ComponentModel.IContainer components = null;
        private TextBox txtSoQDTD, txtMaNV, txtHoTen, txtCCCD, txtDienThoai, txtDieuKhoan;
        private ComboBox cboPhongBan, cboKetQua;
        private DateTimePicker dtpNgayKy;
        private NumericUpDown numThangTV, numLuongTV;
        private Button btnLapQDTuyenDung, btnDanhGiaTV, btnDong;
        private DataGridView dgvThuViec;

        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null)) components.Dispose();
            base.Dispose(disposing);
        }

        private void InitializeComponent()
        {
            this.Font = new Font("Segoe UI", 9.5F);
            this.txtSoQDTD = new TextBox { Location = new Point(135, 18), Size = new Size(120, 26), Text = "QDTD005" };
            this.txtMaNV = new TextBox { Location = new Point(345, 18), Size = new Size(95, 26), Text = "NV007" };
            this.txtHoTen = new TextBox { Location = new Point(515, 18), Size = new Size(190, 26), Text = "Đặng Quốc Khánh" };
            this.cboPhongBan = new ComboBox { Location = new Point(805, 18), Size = new Size(215, 26), DropDownStyle = ComboBoxStyle.DropDownList };

            this.txtCCCD = new TextBox { Location = new Point(135, 54), Size = new Size(120, 26), Text = "079099008899" };
            this.txtDienThoai = new TextBox { Location = new Point(345, 54), Size = new Size(110, 26), Text = "0903777888" };
            this.dtpNgayKy = new DateTimePicker { Location = new Point(555, 54), Size = new Size(130, 26), Format = DateTimePickerFormat.Short, Value = new DateTime(2026, 10, 1) };
            this.numThangTV = new NumericUpDown { Location = new Point(805, 54), Size = new Size(65, 26), Minimum = 1, Maximum = 12, Value = 2 };
            this.numLuongTV = new NumericUpDown { Location = new Point(135, 90), Size = new Size(150, 26) };
            this.cboKetQua = new ComboBox { Location = new Point(405, 90), Size = new Size(130, 26), DropDownStyle = ComboBoxStyle.DropDownList };

            this.txtDieuKhoan = new TextBox { Location = new Point(135, 126), Size = new Size(575, 26), Text = "Bên A (Sơn Đông Á) đào tạo quy trình pha màu; Bên B tuân thủ ATLĐ, hưởng lương thử việc." };
            this.btnLapQDTuyenDung = new Button { Location = new Point(725, 122), Size = new Size(145, 34), Text = "Lập QĐ Tuyển dụng" };
            this.btnLapQDTuyenDung.Click += this.btnLapQDTuyenDung_Click;
            this.btnDanhGiaTV = new Button { Location = new Point(880, 122), Size = new Size(140, 34), Text = "Đánh giá Thử việc" };
            this.btnDanhGiaTV.Click += this.btnDanhGiaTV_Click;

            this.dgvThuViec = new DataGridView { Location = new Point(18, 172), Size = new Size(1002, 340), ReadOnly = true, AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill };
            this.dgvThuViec.CellClick += this.dgvThuViec_CellClick;

            this.btnDong = new Button { Location = new Point(890, 524), Size = new Size(130, 36), Text = "Đóng" };
            this.btnDong.Click += this.btnDong_Click;

            this.ClientSize = new Size(1040, 575);
            this.Controls.AddRange(new Control[] {
                new Label { Text = "Số QĐ tuyển:", Location = new Point(18, 21), AutoSize = true }, this.txtSoQDTD,
                new Label { Text = "Mã NV:", Location = new Point(275, 21), AutoSize = true }, this.txtMaNV,
                new Label { Text = "Họ tên:", Location = new Point(455, 21), AutoSize = true }, this.txtHoTen,
                new Label { Text = "Phòng ban:", Location = new Point(718, 21), AutoSize = true }, this.cboPhongBan,
                new Label { Text = "Số CCCD:", Location = new Point(18, 57), AutoSize = true }, this.txtCCCD,
                new Label { Text = "Điện thoại:", Location = new Point(268, 57), AutoSize = true }, this.txtDienThoai,
                new Label { Text = "Ngày tuyển:", Location = new Point(468, 57), AutoSize = true }, this.dtpNgayKy,
                new Label { Text = "Tháng thử việc:", Location = new Point(698, 57), AutoSize = true }, this.numThangTV,
                new Label { Text = "Lương thử việc:", Location = new Point(18, 93), AutoSize = true }, this.numLuongTV,
                new Label { Text = "Kết quả TV:", Location = new Point(310, 93), AutoSize = true }, this.cboKetQua,
                new Label { Text = "Điều khoản 2 bên:", Location = new Point(18, 129), AutoSize = true }, this.txtDieuKhoan,
                this.btnLapQDTuyenDung, this.btnDanhGiaTV, this.dgvThuViec, this.btnDong
            });
            this.StartPosition = FormStartPosition.CenterParent;
            this.Text = "Lập Quyết định Tuyển dụng & Theo dõi Thử việc (Công ty Sơn Đông Á)";
        }
    }
}
