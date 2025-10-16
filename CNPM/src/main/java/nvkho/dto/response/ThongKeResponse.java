package nvkho.dto.response;

import lombok.Data;
import java.util.Map;

@Data
public class ThongKeResponse {
    private Long tongSanPham;
    private Integer tongSoLuongTon;
    private Double giaTriTonKho;
    private Long soPhieuNhap;
    private Long soPhieuXuat;
    private Double tongGiaTriNhap;
    private Double tongGiaTriXuat;
    private Map<String, Long> thongKeTheoDanhMuc;
    private Map<String, Integer> sanPhamSapHetHang;
}