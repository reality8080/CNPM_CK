package nvkho.dto.response;

import lombok.Data;

@Data
public class SanPhamResponse {
	private String maSP;
    private String tenSP;
    private Double giaBan;
    private Integer soLuongTon;
    private String kichThuoc;
    private String mauSac;
    private String chatLieu;
    private String maDM;
    private String tenDanhMuc;
    private String moTa;
    private String hinhAnh;
}