package nvkho.service;

import nvkho.dto.request.DoiTraHangRequest;
import nvkho.dto.response.DoiTraHangResponse;
import java.util.List;

public interface DoiTraHangService {
    DoiTraHangResponse createDoiTra(DoiTraHangRequest request);
    DoiTraHangResponse xuLyDoiTra(String maDoiTra);
    DoiTraHangResponse getDoiTraById(String maDoiTra);
    List<DoiTraHangResponse> getAllDoiTra();
    DoiTraHangResponse updateDoiTra(String maDoiTra, DoiTraHangRequest request);
    void deleteDoiTra(String maDoiTra);
}