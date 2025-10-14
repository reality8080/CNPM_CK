package vn.iotstar.entity;


import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "KhuyenMai")
public class KhuyenMai {
    @Id
    private String maKM; // ObjectId tự generate dưới dạng String

    @NotEmpty(message = "Danh sách mã sản phẩm không được để trống")
    @Field(name = "MaSP")
    private List<String> maSP; // Array of ObjectId (String), tham chiếu đến SanPham

    @NotBlank(message = "Mã giảm giá không được để trống")
    @Field(name = "MaGiamGia")
    private String maGiamGia;

    @NotNull(message = "Phần trăm giảm không được để trống")
    @Positive(message = "Phần trăm giảm phải lớn hơn 0")
    @Field(name = "PhanTramGiam")
    private Double phanTramGiam;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    @FutureOrPresent(message = "Ngày bắt đầu phải là hiện tại hoặc tương lai")
    @Field(name = "NgayBatDau")
    private Date ngayBatDau;

    @NotNull(message = "Ngày kết thúc không được để trống")
    @FutureOrPresent(message = "Ngày kết thúc phải là hiện tại hoặc tương lai")
    @Field(name = "NgayKetThuc")
    private Date ngayKetThuc;
}