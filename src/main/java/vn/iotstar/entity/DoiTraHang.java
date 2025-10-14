package vn.iotstar.entity;


import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "DoiTraHang")
public class DoiTraHang {
    @Id
    private String maDoiTra; // ObjectId tự generate dưới dạng String

    @NotBlank(message = "Mã hóa đơn không được để trống")
    @Field(name = "MaHD")
    private String maHD; // Tham chiếu đến HoaDonBanHang

    @NotBlank(message = "Mã sản phẩm không được để trống")
    @Field(name = "MaSP")
    private String maSP; // Tham chiếu đến SanPham

    @NotNull(message = "Số lượng không được để trống")
    @Positive(message = "Số lượng phải lớn hơn 0")
    @Field(name = "SoLuong")
    private Integer soLuong;

    @NotBlank(message = "Lý do không được để trống")
    @Size(min = 5, max = 200, message = "Lý do phải từ 5 đến 200 ký tự")
    @Field(name = "LyDo")
    private String lyDo;

    @NotNull(message = "Ngày đổi trả không được để trống")
    @PastOrPresent(message = "Ngày đổi trả phải là quá khứ hoặc hiện tại")
    @Field(name = "NgayDoiTra")
    private Date ngayDoiTra;
}