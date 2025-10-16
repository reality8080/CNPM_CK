package nvkho.dto.response;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class KiemKeResponse {
    private String maKiemKe;
    private Date ngayKiemKe;
    private String maNV;
    private String trangThai;
    private String ghiChu;
    private List<ChiTietKiemKeResponse> chiTietKiemKe;

    @Data
    public static class ChiTietKiemKeResponse {
        private String maSP;
        private String tenSP;
        private Integer soLuongHeThong;
        private Integer soLuongThucTe;
        private Integer chenhLech;
        private String ghiChu;
    }
}