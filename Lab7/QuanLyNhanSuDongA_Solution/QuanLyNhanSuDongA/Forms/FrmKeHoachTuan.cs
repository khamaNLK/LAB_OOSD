using System;
using System.Windows.Forms;
using QuanLyNhanSuDongA.Services;

namespace QuanLyNhanSuDongA.Forms
{
    public partial class FrmKeHoachTuan : Form
    {
        private readonly DanhMucService _dm = new DanhMucService();
        private readonly KeHoachTuanService _sv = new KeHoachTuanService();

        public FrmKeHoachTuan()
        {
            InitializeComponent();
            this.Load += FrmKeHoachTuan_Load;
        }

        private void FrmKeHoachTuan_Load(object sender, EventArgs e)
        {
            FormHelper.BindCombo(cboPhongBan, _dm.LayPhongBan(), "TenPB", "MaPB");
            FormHelper.BindCombo(cboNhanVien, _dm.LayNhanVienDangLamViec(), "HienThi", "MaNV");
            FormHelper.BindCombo(cboCaLamViec, _dm.LayCaLamViec(), "HienThi", "MaCa");
            TaiDanhSach();
        }

        private void TaiDanhSach() => dgvKeHoachTuan.DataSource = _sv.LayKeHoachTuan();

        private void btnGuiBanChamCong_Click(object sender, EventArgs e)
        {
            var res = _sv.LapKeHoachVaPhanCa(
                txtMaKeHoach.Text, Convert.ToString(cboPhongBan.SelectedValue),
                (int)numTuan.Value, dtpTuNgay.Value, dtpDenNgay.Value,
                "Trưởng bộ phận", Convert.ToString(cboNhanVien.SelectedValue),
                Convert.ToString(cboCaLamViec.SelectedValue), dtpNgayLamViec.Value,
                numGioPTDuKien.Value, txtGhiChu.Text);
            FormHelper.ShowResult(res);
            if (res.ThanhCong) TaiDanhSach();
        }

        private void btnDong_Click(object sender, EventArgs e) => Close();
    }
}
