using System;
using System.Windows.Forms;
using QuanLyNhanSuDongA.Services;

namespace QuanLyNhanSuDongA.Forms
{
    public partial class FrmKhenThuongKyLuat : Form
    {
        private readonly DanhMucService _dm = new DanhMucService();
        private readonly KhenThuongKyLuatService _sv = new KhenThuongKyLuatService();

        public FrmKhenThuongKyLuat()
        {
            InitializeComponent();
            this.Load += FrmKhenThuongKyLuat_Load;
        }

        private void FrmKhenThuongKyLuat_Load(object sender, EventArgs e)
        {
            FormHelper.ConfigMoney(numSoTien, 100000000m, 500000m);
            numSoTien.Value = 2000000m;
            FormHelper.BindCombo(cboNhanVien, _dm.LayNhanVienDangLamViec(), "HienThi", "MaNV");
            FormHelper.BindCombo(cboPhongBan, _dm.LayPhongBan(), "TenPB", "MaPB");
            cboLoaiDoiTuong.Items.Clear();
            cboLoaiDoiTuong.Items.AddRange(new object[] { "Cá nhân", "Tập thể" });
            cboLoaiDoiTuong.SelectedIndex = 0;
            TaiDanhSach();
        }

        private void cboLoaiDoiTuong_SelectedIndexChanged(object sender, EventArgs e)
        {
            bool caNhan = (cboLoaiDoiTuong.Text == "Cá nhân");
            cboNhanVien.Enabled = caNhan;
            cboPhongBan.Enabled = !caNhan;
        }

        private void TaiDanhSach() => dgvKTKL.DataSource = _sv.LayDanhSachKTKL();

        private void btnLapKhenThuong_Click(object sender, EventArgs e)
        {
            var res = _sv.LapKhenThuong(
                txtSoQD.Text, cboLoaiDoiTuong.Text,
                Convert.ToString(cboNhanVien.SelectedValue),
                Convert.ToString(cboPhongBan.SelectedValue),
                dtpNgayKy.Value, txtNoiDung.Text, numSoTien.Value, txtNguoiKy.Text, txtSoPhieuChiThu.Text);
            FormHelper.ShowResult(res);
            if (res.ThanhCong) TaiDanhSach();
        }

        private void btnLapKyLuat_Click(object sender, EventArgs e)
        {
            var res = _sv.LapKyLuat(
                txtSoQD.Text, cboLoaiDoiTuong.Text,
                Convert.ToString(cboNhanVien.SelectedValue),
                Convert.ToString(cboPhongBan.SelectedValue),
                dtpNgayKy.Value, txtNoiDung.Text, numSoTien.Value, txtSoPhieuChiThu.Text, true);
            FormHelper.ShowResult(res);
            if (res.ThanhCong) TaiDanhSach();
        }

        private void btnDong_Click(object sender, EventArgs e) => Close();
    }
}
