package nvkho.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "SanPham")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SanPham {
    @Id
    private String maSP;
    
    private String tenSP;
    private Double giaBan;
    private Integer soLuongTon;
    private String kichThuoc;
    private String mauSac;
    private String chatLieu;
    
    @DBRef
    private DanhMuc danhMuc;
    
    private String moTa;
    private String hinhAnh; // URL từ Cloudinary
}