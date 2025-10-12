package nvkho.service;

import nvkho.dto.request.KiemKeRequest;
import nvkho.entity.KiemKe;
import java.util.List;

public interface KiemKeService {
    KiemKe createKiemKe(KiemKeRequest request);
    KiemKe xacNhanKiemKe(String maKiemKe);
    KiemKe getKiemKeById(String maKiemKe);
    List<KiemKe> getAllKiemKe();
}