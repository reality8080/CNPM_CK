package nvkho.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection = "KiemKe")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KiemKe {
    @Id
    private String maKiemKe;
    private Date ngayKiemKe;
    private String maNV;
    private String trangThai;
    private String ghiChu;
    private List<ChiTietKiemKe> chiTietKiemKe;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChiTietKiemKe {
        @DBRef
        private SanPham sanPham;
        private Integer soLuongHeThong;
        private Integer soLuongThucTe;
        private Integer chenhLech;
        private String ghiChu;
    }
}