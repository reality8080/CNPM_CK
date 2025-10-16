package nvkho.dto.response;

import lombok.Data;
import java.util.Date;

@Data
public class DoiTraHangResponse {
	private String maDoiTra;
    private String maHD;
    private String maSP;
    private String tenSP;
    private Integer soLuong;
    private String lyDo;
    private Date ngayDoiTra;
    private String trangThai;
}
