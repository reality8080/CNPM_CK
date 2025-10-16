package nvkho.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "ChiTietXuatKho")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietXuatKho {
    @Id
    private String maCTXK;
    
    
    private String maPXK;
    @DBRef
    private PhieuXuatKho phieuXuatKho;
    
    @DBRef
    private SanPham sanPham;
    
    private Integer soLuong;
}