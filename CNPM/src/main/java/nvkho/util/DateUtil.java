package nvkho.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    
    public static String formatDate(Date date) {
        return date != null ? DATE_FORMAT.format(date) : "";
    }
    
    public static String formatDateTime(Date date) {
        return date != null ? DATETIME_FORMAT.format(date) : "";
    }
}