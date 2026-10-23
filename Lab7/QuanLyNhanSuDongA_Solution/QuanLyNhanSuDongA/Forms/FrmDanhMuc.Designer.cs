using System.Drawing;
using System.Windows.Forms;

namespace QuanLyNhanSuDongA.Forms
{
    partial class FrmDanhMuc
    {
        private System.ComponentModel.IContainer components = null;
        private TabControl tabDanhMuc;
        private TabPage tabPB, tabCV, tabCa, tabNV;
        private TextBox txtMaPB, txtTenPB, txtDienThoaiPB;
        private CheckBox chkNhuCauTD;
        private Button btnLuuPB;
        private DataGridView dgvPhongBan;
        private TextBox txtMaCV, txtTenCV;
        private NumericUpDown numPhuCapMacDinh;
        private Button btnLuuCV;
        private DataGridView dgvChucVu;
        private TextBox txtMaCa, txtTenCa, txtGioBD, txtGioKT;
        private NumericUpDown numSoGioChuan;
        private Button btnLuuCa;
        private DataGridView dgvCaLamViec;
        private DataGridView dgvNhanVien;
        private Button btnDong;

        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null)) components.Dispose();
            base.Dispose(disposing);
        }

        private void InitializeComponent()
        {
            this.tabDanhMuc = new TabControl();
            this.tabPB = new TabPage("1. Phòng ban");
            this.tabCV = new TabPage("2. Chức vụ");
            this.tabCa = new TabPage("3. Ca làm việc");
            this.tabNV = new TabPage("4. Danh sách Nhân viên");

            this.txtMaPB = new TextBox { Location = new Point(115, 18), Size = new Size(120, 26), Text = "PB_RD" };
            this.txtTenPB = new TextBox { Location = new Point(345, 18), Size = new Size(250, 26), Text = "Phòng Nghiên cứu Sơn" };
            this.txtDienThoaiPB = new TextBox { Location = new Point(685, 18), Size = new Size(140, 26), Text = "02838991109" };
            this.chkNhuCauTD = new CheckBox { Location = new Point(115, 52), Size = new Size(280, 24), Text = "Đang có nhu cầu tuyển dụng", Checked = true };
            this.btnLuuPB = new Button { Location = new Point(685, 48), Size = new Size(140, 32), Text = "Thêm / Cập nhật" };
            this.btnLuuPB.Click += this.btnLuuPB_Click;
            this.dgvPhongBan = new DataGridView { Location = new Point(16, 90), Size = new Size(925, 340), ReadOnly = true, AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill };
            this.tabPB.Controls.AddRange(new Control[] {
                new Label { Text = "Mã PB:", Location = new Point(18, 21), AutoSize = true }, this.txtMaPB,
                new Label { Text = "Tên phòng ban:", Location = new Point(250, 21), AutoSize = true }, this.txtTenPB,
                new Label { Text = "Điện thoại:", Location = new Point(608, 21), AutoSize = true }, this.txtDienThoaiPB,
                this.chkNhuCauTD, this.btnLuuPB, this.dgvPhongBan
            });

            this.txtMaCV = new TextBox { Location = new Point(115, 22), Size = new Size(130, 26), Text = "CV_GS" };
            this.txtTenCV = new TextBox { Location = new Point(350, 22), Size = new Size(230, 26), Text = "Giám sát chất lượng" };
            this.numPhuCapMacDinh = new NumericUpDown { Location = new Point(710, 22), Size = new Size(140, 26), Value = 1200000 };
            this.btnLuuCV = new Button { Location = new Point(710, 54), Size = new Size(140, 32), Text = "Thêm / Cập nhật" };
            this.btnLuuCV.Click += this.btnLuuCV_Click;
            this.dgvChucVu = new DataGridView { Location = new Point(16, 95), Size = new Size(925, 335), ReadOnly = true, AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill };
            this.tabCV.Controls.AddRange(new Control[] {
                new Label { Text = "Mã chức vụ:", Location = new Point(18, 25), AutoSize = true }, this.txtMaCV,
                new Label { Text = "Tên chức vụ:", Location = new Point(260, 25), AutoSize = true }, this.txtTenCV,
                new Label { Text = "Phụ cấp mặc định:", Location = new Point(595, 25), AutoSize = true }, this.numPhuCapMacDinh,
                this.btnLuuCV, this.dgvChucVu
            });

            this.txtMaCa = new TextBox { Location = new Point(95, 22), Size = new Size(100, 26), Text = "CA3" };
            this.txtTenCa = new TextBox { Location = new Point(280, 22), Size = new Size(180, 26), Text = "Ca đêm SX" };
            this.txtGioBD = new TextBox { Location = new Point(540, 22), Size = new Size(80, 26), Text = "22:00" };
            this.txtGioKT = new TextBox { Location = new Point(695, 22), Size = new Size(80, 26), Text = "06:00" };
            this.numSoGioChuan = new NumericUpDown { Location = new Point(865, 22), Size = new Size(70, 26), Minimum = 1, Maximum = 24, DecimalPlaces = 1, Value = 8 };
            this.btnLuuCa = new Button { Location = new Point(795, 54), Size = new Size(140, 32), Text = "Thêm / Cập nhật" };
            this.btnLuuCa.Click += this.btnLuuCa_Click;
            this.dgvCaLamViec = new DataGridView { Location = new Point(16, 95), Size = new Size(925, 335), ReadOnly = true, AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill };
            this.tabCa.Controls.AddRange(new Control[] {
                new Label { Text = "Mã ca:", Location = new Point(18, 25), AutoSize = true }, this.txtMaCa,
                new Label { Text = "Tên ca:", Location = new Point(215, 25), AutoSize = true }, this.txtTenCa,
                new Label { Text = "Giờ BĐ:", Location = new Point(475, 25), AutoSize = true }, this.txtGioBD,
                new Label { Text = "Giờ KT:", Location = new Point(635, 25), AutoSize = true }, this.txtGioKT,
                new Label { Text = "Số giờ:", Location = new Point(795, 25), AutoSize = true }, this.numSoGioChuan,
                this.btnLuuCa, this.dgvCaLamViec
            });

            this.dgvNhanVien = new DataGridView { Location = new Point(16, 18), Size = new Size(925, 412), ReadOnly = true, AutoSizeColumnsMode = DataGridViewAutoSizeColumnsMode.Fill };
            this.tabNV.Controls.Add(this.dgvNhanVien);

            this.tabDanhMuc.Font = new Font("Segoe UI", 9.5F);
            this.tabDanhMuc.Location = new Point(16, 14);
            this.tabDanhMuc.Size = new Size(965, 470);
            this.tabDanhMuc.TabPages.AddRange(new TabPage[] { this.tabPB, this.tabCV, this.tabCa, this.tabNV });

            this.btnDong = new Button { Location = new Point(850, 494), Size = new Size(130, 36), Text = "Đóng" };
            this.btnDong.Click += this.btnDong_Click;

            this.ClientSize = new Size(1000, 545);
            this.Controls.AddRange(new Control[] { this.tabDanhMuc, this.btnDong });
            this.StartPosition = FormStartPosition.CenterParent;
            this.Text = "Quản lý Danh mục: Phòng ban - Chức vụ - Ca làm việc - Nhân viên";
        }
    }
}
