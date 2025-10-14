package vn.iotstar.entity;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "KhachHang")
public class KhachHang {
    @Id
    private String maKH; // ObjectId tự generate dưới dạng String

    @NotBlank(message = "Họ tên không được để trống")
    @Size(min = 2, max = 100, message = "Họ tên phải từ 2 đến 100 ký tự")
    @Field(name = "HoTen")
    private String hoTen;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(0[3-9][0-9]{8})$", message = "Số điện thoại phải là số Việt Nam hợp lệ (10 chữ số, bắt đầu bằng 0)")
    @Field(name = "SoDienThoai")
    private String soDienThoai;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email phải đúng định dạng")
    @Field(name = "Email")
    private String email;

    @NotBlank(message = "Địa chỉ không được để trống")
    @Size(min = 5, max = 200, message = "Địa chỉ phải từ 5 đến 200 ký tự")
    @Field(name = "DiaChi")
    private String diaChi;
}