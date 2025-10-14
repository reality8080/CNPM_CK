package vn.iotstar.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.iotstar.entity.ChiTietHoaDon;
import vn.iotstar.entity.DoiTraHang;
import vn.iotstar.entity.SanPham;
import vn.iotstar.repository.ChiTietHoaDonRepository;
import vn.iotstar.repository.DoiTraHangRepository;
import vn.iotstar.repository.SanPhamRepository;

import java.util.List;

@Service
public class DoiTraHangService {
    @Autowired
    private DoiTraHangRepository doiTraRepository;
    @Autowired
    private ChiTietHoaDonRepository chiTietRepository;
    @Autowired
    private SanPhamRepository sanPhamRepository;

    public DoiTraHang saveDoiTra(DoiTraHang doiTra) {
        List<ChiTietHoaDon> chiTietList = chiTietRepository.findByMaHD(doiTra.getMaHD());
        ChiTietHoaDon chiTiet = chiTietList.stream()
                .filter(ct -> ct.getMaSP().equals(doiTra.getMaSP()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Sản phẩm không có trong hóa đơn"));
        if (chiTiet.getSoLuong() < doiTra.getSoLuong()) {
            throw new RuntimeException("Số lượng đổi trả vượt quá số lượng trong hóa đơn");
        }
        SanPham sp = sanPhamRepository.findById(doiTra.getMaSP())
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
        sp.setSoLuongTon(sp.getSoLuongTon() + doiTra.getSoLuong());
        sanPhamRepository.save(sp);
        return doiTraRepository.save(doiTra);
    }

    public List<DoiTraHang> findByMaHD(String maHD) {
        return doiTraRepository.findByMaHD(maHD);
    }
}
