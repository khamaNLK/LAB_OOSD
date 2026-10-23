using System;
using System.Windows.Forms;
using QuanLyNhanSuDongA.Services;

namespace QuanLyNhanSuDongA.Forms
{
    public partial class FrmTinhLuongThang : Form
    {
        private readonly TinhLuongService _sv = new TinhLuongService();

        public FrmTinhLuongThang()
        {
            InitializeComponent();
            this.Load += FrmTinhLuongThang_Load;
        }

        private void FrmTinhLuongThang_Load(object sender, EventArgs e)
        {
            numThang.Value = 10;
            numNam.Value = 2026;
            TaiBangLuong();
        }

        private void TaiBangLuong() =>
            dgvBangLuong.DataSource = _sv.LayBangLuongThang((int)numThang.Value, (int)numNam.Value);

        private void btnTinhLuong_Click(object sender, EventArgs e)
        {
            var res = _sv.TinhBangLuongThang((int)numThang.Value, (int)numNam.Value);
            FormHelper.ShowResult(res);
            if (res.ThanhCong) TaiBangLuong();
        }

        private void btnChotBangLuong_Click(object sender, EventArgs e)
        {
            var res = _sv.ChotBangLuongThang((int)numThang.Value, (int)numNam.Value);
            FormHelper.ShowResult(res);
            if (res.ThanhCong) TaiBangLuong();
        }

        private void btnDong_Click(object sender, EventArgs e) => Close();
    }
}
