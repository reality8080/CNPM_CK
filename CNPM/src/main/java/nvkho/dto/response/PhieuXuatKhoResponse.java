package nvkho.dto.response;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class PhieuXuatKhoResponse {
    private String maPXK;
    private String maHD;
    private String maNV;
    private Date ngayXuat;
    private String trangThai;
    private List<ChiTietXuatKhoResponse> chiTiet;
    
    @Data
    public static class ChiTietXuatKhoResponse {
        private String maCTXK;
        private String maSP;
        private String tenSP;
        private Integer soLuong;
    }
}