package nvkho.service;

import nvkho.dto.request.KiemKeRequest;
import nvkho.dto.response.KiemKeResponse;
import java.util.List;

public interface KiemKeService {
    KiemKeResponse createKiemKe(KiemKeRequest request);
    KiemKeResponse xacNhanKiemKe(String maKiemKe);
    KiemKeResponse getKiemKeById(String maKiemKe);
    List<KiemKeResponse> getAllKiemKe();
}