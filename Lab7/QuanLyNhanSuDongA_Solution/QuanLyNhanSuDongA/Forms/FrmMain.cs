using System;
using System.Windows.Forms;
using QuanLyNhanSuDongA.Data;

namespace QuanLyNhanSuDongA.Forms
{
    public partial class FrmMain : Form
    {
        public FrmMain()
        {
            InitializeComponent();
            this.Load += FrmMain_Load;
        }

        private void FrmMain_Load(object sender, EventArgs e)
        {
            try
            {
                Db.EnsureDatabaseReady();
                lblStatus.Text = "Trạng thái CSDL: Đã kết nối SQL Server (QuanLyNhanSuDongA) thành công.";
            }
            catch (Exception ex)
            {
                lblStatus.Text = "Lỗi kết nối SQL Server: " + ex.Message;
                MessageBox.Show(
                    "Không kết nối được SQL Server.\nHãy đảm bảo SQL Server LocalDB ((localdb)\\MSSQLLocalDB) hoặc SQLEXPRESS đang chạy.\nChi tiết: " + ex.Message,
                    "Lỗi kết nối CSDL",
                    MessageBoxButtons.OK,
                    MessageBoxIcon.Error);
            }
        }

        private void btnDanhMuc_Click(object sender, EventArgs e) { using (var f = new FrmDanhMuc()) f.ShowDialog(this); }
        private void btnTuyenDung_Click(object sender, EventArgs e) { using (var f = new FrmTuyenDungThuViec()) f.ShowDialog(this); }
        private void btnHopDong_Click(object sender, EventArgs e) { using (var f = new FrmHopDongBaoHiem()) f.ShowDialog(this); }
        private void btnChucVuLuong_Click(object sender, EventArgs e) { using (var f = new FrmQuyetDinhChucVuLuong()) f.ShowDialog(this); }
        private void btnKeHoachTuan_Click(object sender, EventArgs e) { using (var f = new FrmKeHoachTuan()) f.ShowDialog(this); }
        private void btnChamCongPhuTroi_Click(object sender, EventArgs e) { using (var f = new FrmChamCongPhuTroi()) f.ShowDialog(this); }
        private void btnKhenThuongKyLuat_Click(object sender, EventArgs e) { using (var f = new FrmKhenThuongKyLuat()) f.ShowDialog(this); }
        private void btnTinhLuong_Click(object sender, EventArgs e) { using (var f = new FrmTinhLuongThang()) f.ShowDialog(this); }
        private void btnThoat_Click(object sender, EventArgs e) => Close();
    }
}
