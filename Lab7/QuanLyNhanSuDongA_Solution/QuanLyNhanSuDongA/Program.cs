using System;
using System.Windows.Forms;
using QuanLyNhanSuDongA.Forms;

namespace QuanLyNhanSuDongA
{
    internal static class Program
    {
        [STAThread]
        static void Main()
        {
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            Application.Run(new FrmMain());
        }
    }
}
