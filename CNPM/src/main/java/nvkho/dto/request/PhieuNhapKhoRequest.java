package nvkho.dto.request;

import lombok.Data;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class PhieuNhapKhoRequest {
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Ho_Chi_Minh")
    private Date ngayNhap;
    private String maNV;
    private String trangThai;
    private List<ChiTietNhapKhoRequest> chiTiet;
}