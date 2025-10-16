package nvkho.dto.response;

import lombok.Data;

@Data
public class TonKhoResponse {
    private String maSP;
    private String tenSP;
    private String kichThuoc;
    private String mauSac;
    private Integer soLuongTon;
    private String tenDanhMuc;
    private String hinhAnh;
    private Double giaBan;
}
