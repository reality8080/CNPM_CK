package nvkho.dto.request;

import lombok.Data;

@Data
public class SanPhamRequest {
    private String tenSP;
    private Double giaBan;
    private String kichThuoc;
    private String mauSac;
    private String chatLieu;
    private String maDM;
    private String moTa;
}