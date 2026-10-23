using System;
using System.Windows.Forms;
using QuanLyNhanSuDongA.Services;

namespace QuanLyNhanSuDongA.Forms
{
    public partial class FrmTuyenDungThuViec : Form
    {
        private readonly DanhMucService _dm = new DanhMucService();
        private readonly TuyenDungService _sv = new TuyenDungService();

        public FrmTuyenDungThuViec()
        {
            InitializeComponent();
            this.Load += FrmTuyenDungThuViec_Load;
        }

        private void FrmTuyenDungThuViec_Load(object sender, EventArgs e)
        {
            FormHelper.ConfigMoney(numLuongTV, 100000000m, 500000m);
            numLuongTV.Value = 8500000m;
            FormHelper.BindCombo(cboPhongBan, _dm.LayPhongBanCoNhuCau(), "HienThi", "MaPB");
            cboKetQua.Items.Clear();
            cboKetQua.Items.AddRange(new object[] { "Đạt", "Không đạt" });
            cboKetQua.SelectedIndex = 0;
            TaiDanhSach();
        }

        private void TaiDanhSach() => dgvThuViec.DataSource = _sv.LayDanhSachThuViec();

        private void btnLapQDTuyenDung_Click(object sender, EventArgs e)
        {
            var nv = new HoSoNhanVienMoi
            {
                MaNV = txtMaNV.Text,
                HoTen = txtHoTen.Text,
                NgaySinh = new DateTime(1999, 5, 15),
                GioiTinh = "Nam",
                CCCD = txtCCCD.Text,
                DienThoai = txtDienThoai.Text,
                DiaChi = "TP.HCM"
            };
            var res = _sv.TiepNhanThuViec(
                nv, txtSoQDTD.Text, Convert.ToString(cboPhongBan.SelectedValue),
                dtpNgayKy.Value, (int)numThangTV.Value, numLuongTV.Value, txtDieuKhoan.Text);
            FormHelper.ShowResult(res);
            if (res.ThanhCong) TaiDanhSach();
        }

        private void btnDanhGiaTV_Click(object sender, EventArgs e)
        {
            var res = _sv.DanhGiaThuViec(txtSoQDTD.Text, cboKetQua.Text);
            FormHelper.ShowResult(res);
            if (res.ThanhCong) TaiDanhSach();
        }

        private void dgvThuViec_CellClick(object sender, DataGridViewCellEventArgs e)
        {
            if (e.RowIndex >= 0 && dgvThuViec.Rows[e.RowIndex].Cells["SoQDTD"].Value != null)
            {
                txtSoQDTD.Text = dgvThuViec.Rows[e.RowIndex].Cells["SoQDTD"].Value.ToString();
                txtMaNV.Text = dgvThuViec.Rows[e.RowIndex].Cells["MaNV"].Value.ToString();
                txtHoTen.Text = dgvThuViec.Rows[e.RowIndex].Cells["HoTen"].Value.ToString();
            }
        }

        private void btnDong_Click(object sender, EventArgs e) => Close();
    }
}
