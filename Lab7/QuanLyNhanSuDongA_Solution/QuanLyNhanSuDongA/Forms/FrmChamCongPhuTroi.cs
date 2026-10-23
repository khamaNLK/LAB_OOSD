using System;
using System.Windows.Forms;
using QuanLyNhanSuDongA.Services;

namespace QuanLyNhanSuDongA.Forms
{
    public partial class FrmChamCongPhuTroi : Form
    {
        private readonly DanhMucService _dm = new DanhMucService();
        private readonly ChamCongPhuTroiService _sv = new ChamCongPhuTroiService();

        public FrmChamCongPhuTroi()
        {
            InitializeComponent();
            this.Load += FrmChamCongPhuTroi_Load;
        }

        private void FrmChamCongPhuTroi_Load(object sender, EventArgs e)
        {
            FormHelper.BindCombo(cboNhanVien, _dm.LayNhanVienDangLamViec(), "HienThi", "MaNV");
            cboHinhThucPT.Items.Clear();
            cboHinhThucPT.Items.AddRange(new object[] { "Tăng ca", "Ngày lễ", "Ngày nghỉ" });
            cboHinhThucPT.SelectedIndex = 0;
            TaiDanhSach();
        }

        private void TaiDanhSach()
        {
            dgvPhuTroi.DataSource = _sv.LayPhieuPhuTroi();
            dgvChamCong.DataSource = _sv.LayBangChamCong();
        }

        private void btnGhiPhuTroi_Click(object sender, EventArgs e)
        {
            var res = _sv.GhiNhanPhuTroi(
                txtSoPhieuPT.Text, dtpNgayPT.Value, cboHinhThucPT.Text,
                Convert.ToString(cboNhanVien.SelectedValue), numSoGioPT.Value);
            FormHelper.ShowResult(res);
            if (res.ThanhCong) TaiDanhSach();
        }

        private void btnChotChamCong_Click(object sender, EventArgs e)
        {
            string maNV = Convert.ToString(cboNhanVien.SelectedValue);
            string maCC = $"CC{dtpNgayPT.Value:yyyyMM}_{maNV}";
            var res = _sv.ChamCongThang(maCC, maNV, dtpNgayPT.Value.Month, dtpNgayPT.Value.Year, 26m, numNgayCongThucTe.Value);
            FormHelper.ShowResult(res);
            if (res.ThanhCong) TaiDanhSach();
        }

        private void btnDong_Click(object sender, EventArgs e) => Close();
    }
}
