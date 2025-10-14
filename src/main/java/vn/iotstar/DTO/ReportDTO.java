package vn.iotstar.DTO;

import lombok.Data;

import java.util.Date;

@Data
public class ReportDTO {
    private Date date;
    private Double revenue;
    private Integer quantitySold;
    private Integer quantityReturned;
}