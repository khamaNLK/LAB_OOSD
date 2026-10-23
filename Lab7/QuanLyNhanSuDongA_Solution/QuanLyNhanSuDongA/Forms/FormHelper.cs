using System.Data;
using System.Windows.Forms;
using QuanLyNhanSuDongA.Services;

namespace QuanLyNhanSuDongA.Forms
{
    public static class FormHelper
    {
        public static void BindCombo(ComboBox cbo, DataTable dt, string displayMember, string valueMember)
        {
            cbo.DataSource = dt;
            cbo.DisplayMember = displayMember;
            cbo.ValueMember = valueMember;
        }

        public static void ConfigMoney(NumericUpDown num, decimal max = 200000000m, decimal step = 500000m)
        {
            num.Minimum = 0;
            num.Maximum = max;
            num.Increment = step;
            num.ThousandsSeparator = true;
            num.DecimalPlaces = 0;
        }

        public static void ShowResult(KetQuaXuLy res)
        {
            MessageBox.Show(
                res.ThongBao,
                res.ThanhCong ? "Thông báo" : "Cảnh báo nghiệp vụ",
                MessageBoxButtons.OK,
                res.ThanhCong ? MessageBoxIcon.Information : MessageBoxIcon.Warning);
        }
    }
}
