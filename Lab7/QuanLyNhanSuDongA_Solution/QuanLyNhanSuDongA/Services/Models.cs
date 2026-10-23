using System;

namespace QuanLyNhanSuDongA.Services
{
    public class KetQuaXuLy
    {
        public bool ThanhCong { get; set; }
        public string ThongBao { get; set; }

        public static KetQuaXuLy Ok(string msg) => new KetQuaXuLy { ThanhCong = true, ThongBao = msg };
        public static KetQuaXuLy Fail(string msg) => new KetQuaXuLy { ThanhCong = false, ThongBao = msg };
    }

    public class HoSoNhanVienMoi
    {
        public string MaNV { get; set; }
        public string HoTen { get; set; }
        public DateTime NgaySinh { get; set; }
        public string GioiTinh { get; set; }
        public string CCCD { get; set; }
        public string DienThoai { get; set; }
        public string DiaChi { get; set; }
    }

    public class ThongTinBaoHiemInput
    {
        public string MaHoSoBH { get; set; }
        public string SoSoBHXH { get; set; }
        public string SoTheBHYT { get; set; }
        public decimal MucDongHangThang { get; set; }
    }
}
