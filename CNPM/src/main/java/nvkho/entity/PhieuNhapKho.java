package nvkho.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Document(collection = "PhieuNhapKho")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhieuNhapKho {
    @Id
    private String maPNK;

    private Date ngayNhap;

    // Chỉ lưu ID thay vì tham chiếu đến entity NhanVien
    private String maNV;  

    private Double tongTien;
    private String trangThai; // "DaNhap", "DangXuLy"
}
