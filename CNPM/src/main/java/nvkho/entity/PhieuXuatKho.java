package nvkho.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Document(collection = "PhieuXuatKho")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhieuXuatKho {
    @Id
    private String maPXK;

    // Lưu ID hóa đơn thay vì DBRef
    private String maHD;

    // Lưu ID nhân viên thay vì DBRef
    private String maNV;

    private Date ngayXuat;
    private String trangThai; // "DaXuat", "DangXuLy"
}
