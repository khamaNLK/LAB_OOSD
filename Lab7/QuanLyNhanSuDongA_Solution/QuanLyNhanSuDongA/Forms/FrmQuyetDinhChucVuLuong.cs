using System;
using System.Windows.Forms;
using QuanLyNhanSuDongA.Services;

namespace QuanLyNhanSuDongA.Forms
{
    public partial class FrmQuyetDinhChucVuLuong : Form
    {
        private readonly DanhMucService _dm = new DanhMucService();
        private readonly ChucVuLuongService _sv = new ChucVuLuongService();

        public FrmQuyetDinhChucVuLuong()
        {
            InitializeComponent();
            this.Load += FrmQuyetDinhChucVuLuong_Load;
        }

        private void FrmQuyetDinhChucVuLuong_Load(object sender, EventArgs e)
        {
            FormHelper.ConfigMoney(numPhuCapCV, 50000000m, 100000m);
            FormHelper.ConfigMoney(numLuongCB, 200000000m, 500000m);
            FormHelper.ConfigMoney(numPhuCapLuong, 50000000m, 100000m);
            FormHelper.ConfigMoney(numPhuCapDocHai, 50000000m, 100000m);
            numPhuCapCV.Value = 800000m;
            numLuongCB.Value = 12000000m;
            numPhuCapLuong.Value = 1000000m;
            numPhuCapDocHai.Value = 800000m;

            FormHelper.BindCombo(cboNhanVien, _dm.LayNhanVienDangLamViec(), "HienThi", "MaNV");
            FormHelper.BindCombo(cboChucVu, _dm.LayChucVu(), "HienThi", "MaCV");
            TaiDanhSach();
        }

        private void TaiDanhSach() => dgvQuyetDinh.DataSource = _sv.LayDanhSachQuyetDinh();

        private void btnBanHanhQDCV_Click(object sender, EventArgs e)
        {
            var res = _sv.BanHanhQDChucVu(
                txtSoQDCV.Text, Convert.ToString(cboNhanVien.SelectedValue),
                Convert.ToString(cboChucVu.SelectedValue), dtpNgayHieuLuc.Value,
                numPhuCapCV.Value, txtLyDo.Text);
            FormHelper.ShowResult(res);
            if (res.ThanhCong) TaiDanhSach();
        }

        private void btnBanHanhQDLuong_Click(object sender, EventArgs e)
        {
            var res = _sv.BanHanhQDLuong(
                txtSoQDL.Text, Convert.ToString(cboNhanVien.SelectedValue),
                dtpNgayHieuLuc.Value, numLuongCB.Value, numPhuCapLuong.Value,
                numPhuCapDocHai.Value, txtLyDo.Text);
            FormHelper.ShowResult(res);
            if (res.ThanhCong) TaiDanhSach();
        }

        private void btnDong_Click(object sender, EventArgs e) => Close();
    }
}
