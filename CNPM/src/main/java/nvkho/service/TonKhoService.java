package nvkho.service;

import nvkho.dto.response.TonKhoResponse;
import java.util.List;

public interface TonKhoService {
    List<TonKhoResponse> getAllTonKho();
    TonKhoResponse getTonKhoBySanPham(String maSP);
    List<TonKhoResponse> getSanPhamSapHetHang(Integer nguongCanhBao);
}