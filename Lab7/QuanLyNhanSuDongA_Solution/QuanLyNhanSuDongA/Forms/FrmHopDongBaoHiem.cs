using System;
using System.Windows.Forms;
using QuanLyNhanSuDongA.Services;

namespace QuanLyNhanSuDongA.Forms
{
    public partial class FrmHopDongBaoHiem : Form
    {
        private readonly DanhMucService _dm = new DanhMucService();
        private readonly HopDongBaoHiemService _sv = new HopDongBaoHiemService();

        public FrmHopDongBaoHiem()
        {
            InitializeComponent();
            this.Load += FrmHopDongBaoHiem_Load;
        }

        private void FrmHopDongBaoHiem_Load(object sender, EventArgs e)
        {
            FormHelper.ConfigMoney(numMucDongBH, 50000000m, 100000m);
            numMucDongBH.Value = 1260000m;
            FormHelper.BindCombo(cboNhanVien, _dm.LayNhanVienDangLamViec(), "HienThi", "MaNV");
            cboLoaiHD.Items.Clear();
            cboLoaiHD.Items.AddRange(new object[] { "Dài hạn", "Ngắn hạn" });
            cboLoaiHD.SelectedIndex = 0;
            TaiDanhSach();
        }

        private void cboLoaiHD_SelectedIndexChanged(object sender, EventArgs e)
        {
            grpBaoHiem.Enabled = (cboLoaiHD.Text == "Dài hạn");
        }

        private void TaiDanhSach() => dgvHopDong.DataSource = _sv.LayDanhSachHopDong();

        private void btnKyHopDong_Click(object sender, EventArgs e)
        {
            var bh = cboLoaiHD.Text == "Dài hạn"
                ? new ThongTinBaoHiemInput
                {
                    MaHoSoBH = txtMaHoSoBH.Text,
                    SoSoBHXH = txtSoSoBHXH.Text,
                    SoTheBHYT = txtSoTheBHYT.Text,
                    MucDongHangThang = numMucDongBH.Value
                }
                : null;

            var res = _sv.KyHopDongChinhThuc(
                txtSoHD.Text, Convert.ToString(cboNhanVien.SelectedValue), cboLoaiHD.Text,
                dtpNgayKy.Value, dtpNgayBatDau.Value, dtpNgayKetThuc.Value, txtNoiDungHD.Text, bh);
            FormHelper.ShowResult(res);
            if (res.ThanhCong) TaiDanhSach();
        }

        private void btnDong_Click(object sender, EventArgs e) => Close();
    }
}
