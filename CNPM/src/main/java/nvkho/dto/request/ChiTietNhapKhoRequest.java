package nvkho.dto.request;

import lombok.Data;

@Data
public class ChiTietNhapKhoRequest {
    private String maSP;
    private Integer soLuong;
    private Double donGiaNhap;
}