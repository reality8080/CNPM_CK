package vn.iotstar.exception;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;


@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception e) {
        logger.error("Ngoại lệ không mong muốn: {}", e.getMessage(), e);  // Log đầy đủ stack trace
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", "Lỗi hệ thống không xác định");
        errorResponse.put("details", e.getMessage());
        return ResponseEntity.internalServerError().body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException e) {
        logger.warn("Lỗi tham số không hợp lệ: {}", e.getMessage());
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", "Tham số không hợp lệ");
        errorResponse.put("details", e.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }
}