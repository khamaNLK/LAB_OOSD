using System;
using System.Windows.Forms;
using QuanLyNhanSuDongA.Services;

namespace QuanLyNhanSuDongA.Forms
{
    public partial class FrmDanhMuc : Form
    {
        private readonly DanhMucService _sv = new DanhMucService();

        public FrmDanhMuc()
        {
            InitializeComponent();
            this.Load += FrmDanhMuc_Load;
        }

        private void FrmDanhMuc_Load(object sender, EventArgs e)
        {
            FormHelper.ConfigMoney(numPhuCapMacDinh, 50000000m, 100000m);
            TaiDuLieu();
        }

        private void TaiDuLieu()
        {
            dgvPhongBan.DataSource = _sv.LayPhongBan();
            dgvChucVu.DataSource = _sv.LayChucVu();
            dgvCaLamViec.DataSource = _sv.LayCaLamViec();
            dgvNhanVien.DataSource = _sv.LayTatCaNhanVien();
        }

        private void btnLuuPB_Click(object sender, EventArgs e)
        {
            var res = _sv.ThemHoacCapNhatPhongBan(txtMaPB.Text, txtTenPB.Text, txtDienThoaiPB.Text, chkNhuCauTD.Checked);
            FormHelper.ShowResult(res);
            if (res.ThanhCong) TaiDuLieu();
        }

        private void btnLuuCV_Click(object sender, EventArgs e)
        {
            var res = _sv.ThemHoacCapNhatChucVu(txtMaCV.Text, txtTenCV.Text, numPhuCapMacDinh.Value);
            FormHelper.ShowResult(res);
            if (res.ThanhCong) TaiDuLieu();
        }

        private void btnLuuCa_Click(object sender, EventArgs e)
        {
            var res = _sv.ThemHoacCapNhatCa(txtMaCa.Text, txtTenCa.Text, txtGioBD.Text, txtGioKT.Text, numSoGioChuan.Value);
            FormHelper.ShowResult(res);
            if (res.ThanhCong) TaiDuLieu();
        }

        private void btnDong_Click(object sender, EventArgs e) => Close();
    }
}
