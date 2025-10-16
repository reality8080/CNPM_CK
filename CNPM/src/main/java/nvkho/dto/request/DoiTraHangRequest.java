package nvkho.dto.request;

import lombok.Data;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class DoiTraHangRequest {
    private String maHD;
    private String maSP;
    private Integer soLuong;
    private String lyDo;
    private String trangThai;
    
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Ho_Chi_Minh")
    private Date ngayDoiTra;
}