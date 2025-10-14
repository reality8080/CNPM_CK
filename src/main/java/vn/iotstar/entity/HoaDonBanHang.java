package vn.iotstar.entity;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
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
@Document(collection = "HoaDonBanHang")
public class HoaDonBanHang {
    @Id
    private String maHD; // ObjectId tự generate dưới dạng String

    @NotBlank(message = "Mã nhân viên không được để trống")
    @Field(name = "MaNV")
    private String maNV; // Tham chiếu đến NhanVien

    @NotBlank(message = "Mã khách hàng không được để trống")
    @Field(name = "MaKH")
    private String maKH; // Tham chiếu đến KhachHang

    @NotNull(message = "Ngày bán không được để trống")
    @PastOrPresent(message = "Ngày bán phải là quá khứ hoặc hiện tại")
    @Field(name = "NgayBan")
    private Date ngayBan;

    @NotNull(message = "Tổng tiền không được để trống")
    @Positive(message = "Tổng tiền phải lớn hơn 0")
    @Field(name = "TongTien")
    private Double tongTien;

    @NotBlank(message = "Trạng thái không được để trống")
    @Field(name = "TrangThai")
    private String trangThai; // Ví dụ: "DaThanhToan", "DangXuLy", "DaHuy"

    @NotBlank(message = "Phương thức thanh toán không được để trống")
    @Field(name = "PhuongThucThanhToan")
    private String phuongThucThanhToan; // Ví dụ: "TienMat", "The", "ChuyenKhoan"
}