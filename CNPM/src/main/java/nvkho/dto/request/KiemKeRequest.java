package nvkho.dto.request;

import lombok.Data;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class KiemKeRequest {
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Ho_Chi_Minh")
    private Date ngayKiemKe;
    private String maNV;
    private String trangThai;
    private String ghiChu;
    private List<ChiTietKiemKeRequest> chiTiet;
    
    @Data
    public static class ChiTietKiemKeRequest {
        private String maSP;
        private Integer soLuongThucTe;
        private String ghiChu;
    }
}