package nvkho.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MaPhieuGenerator {
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
    
    public static String generateMaPNK() {
        return "PNK" + DATE_FORMAT.format(new Date()) + System.currentTimeMillis() % 10000;
    }
    
    public static String generateMaPXK() {
        return "PXK" + DATE_FORMAT.format(new Date()) + System.currentTimeMillis() % 10000;
    }
    
    public static String generateMaKiemKe() {
        return "KK" + DATE_FORMAT.format(new Date()) + System.currentTimeMillis() % 10000;
    }
    
    public static String generateMaSP() {
        return "SP" + System.currentTimeMillis();
    }
}