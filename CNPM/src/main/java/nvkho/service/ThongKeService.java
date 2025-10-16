package nvkho.service;

import nvkho.dto.response.ThongKeResponse;
import java.util.Date;

public interface ThongKeService {
    ThongKeResponse getThongKeChung();
    ThongKeResponse getThongKeTheoKhoangThoiGian(Date start, Date end);
}