package nvkho.dto.request;

import lombok.Data;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class PhieuXuatKhoRequest {
    private String maHD;
    private String maNV;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Ho_Chi_Minh")
    private Date ngayXuat;
    private String trangThai;
    private List<ChiTietXuatKhoRequest> chiTiet;
}