package nvkho.util;

import java.util.List;

public class TinhToanUtil {
    
    public static Double tinhThanhTien(Integer soLuong, Double donGia) {
        if (soLuong == null || donGia == null) {
            return 0.0;
        }
        return soLuong * donGia;
    }
    
    public static Double tinhTongTien(List<Double> danhSachThanhTien) {
        return danhSachThanhTien.stream()
                .mapToDouble(Double::doubleValue)
                .sum();
    }
}