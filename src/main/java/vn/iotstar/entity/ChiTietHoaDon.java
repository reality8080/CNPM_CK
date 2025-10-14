package vn.iotstar.entity;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "ChiTietHoaDon")
public class ChiTietHoaDon {
    @Id
    private String maCTHD; // ObjectId tự generate dưới dạng String

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

    @NotNull(message = "Đơn giá không được để trống")
    @Positive(message = "Đơn giá phải lớn hơn 0")
    @Field(name = "DonGia")
    private Double donGia;

    @Positive(message = "Chiết khấu phải lớn hơn hoặc bằng 0")
    @Field(name = "ChietKhau")
    private Double chietKhau = 0.0; // Phần trăm hoặc giá trị, mặc định 0

    @NotNull(message = "Thành tiền không được để trống")
    @Positive(message = "Thành tiền phải lớn hơn 0")
    @Field(name = "ThanhTien")
    private Double thanhTien; // Tính toán: SoLuong * DonGia * (1 - ChietKhau/100)
}