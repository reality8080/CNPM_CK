package vn.iotstar.entity;


import jakarta.validation.constraints.NotBlank;
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
@Document(collection = "TaiKhoan")
public class TaiKhoan {
    @Id
    private String maTK; // ObjectId tự generate dưới dạng String

    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Size(min = 3, max = 50, message = "Tên đăng nhập phải từ 3 đến 50 ký tự")
    @Field(name = "TenDangNhap")
    private String tenDangNhap; // Unique, nhưng kiểm tra unique ở repository/service

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu phải ít nhất 6 ký tự")
    @Field(name = "MatKhau")
    private String matKhau; // Lưu mật khẩu đã mã hóa bằng BCrypt

    @NotBlank(message = "Vai trò không được để trống")
    @Field(name = "VaiTro")
    private String vaiTro; // Ví dụ: "NhanVienBanHang"
}