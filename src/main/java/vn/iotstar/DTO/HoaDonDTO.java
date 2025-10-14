package vn.iotstar.DTO;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class HoaDonDTO {
    private String maHD;
    private String maNV;
    private String maKH;
    private String hoTenKhachHang;
    private Date ngayBan;
    private Double tongTien;
    private String trangThai;
    private String phuongThucThanhToan;
    private List<ChiTietHoaDonDTO> chiTiet;
}