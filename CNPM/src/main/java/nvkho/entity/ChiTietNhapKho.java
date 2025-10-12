package nvkho.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "ChiTietNhapKho")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietNhapKho {
    
    @Id
    private String maCTNK;
    
    @DBRef
    private PhieuNhapKho phieuNhapKho;
    
    @DBRef
    private SanPham sanPham;
    
    private Integer soLuong;
    
    private Double donGiaNhap; // Giá nhập từ nhà cung cấp
    
    private Double thanhTien; // soLuong * donGiaNhap
}