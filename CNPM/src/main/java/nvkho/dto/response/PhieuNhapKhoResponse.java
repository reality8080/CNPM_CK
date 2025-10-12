package nvkho.dto.response;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class PhieuNhapKhoResponse {
    private String maPNK;
    private Date ngayNhap;
    private String maNV;
    private Double tongTien;
    private String trangThai;
    private List<ChiTietNhapKhoResponse> chiTiet;
    
    @Data
    public static class ChiTietNhapKhoResponse {
        private String maCTNK;
        private String maSP;
        private String tenSP;
        private Integer soLuong;
        private Double donGiaNhap;
        private Double thanhTien;
    }
}