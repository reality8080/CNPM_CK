package nvkho.util;

public class ValidationUtil {
    
    public static boolean isValidSoLuong(Integer soLuong) {
        return soLuong != null && soLuong > 0;
    }
    
    public static boolean isValidGia(Double gia) {
        return gia != null && gia > 0;
    }
    
    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }
}