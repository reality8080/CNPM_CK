package nvkho.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

@Document(collection = "DoiTraHang")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoiTraHang {
    @Id
    private String maDoiTra;
    
    
    @DBRef
    @JsonIgnoreProperties({"danhMuc", "nhaCungCap", "doiTraHang"})

    private SanPham sanPham;
    
    private Integer soLuong;
    private String lyDo;
    private Date ngayDoiTra;
    private String maHD;
    private String trangThai; // "ChuaXuLy", "DaXuLy"
}