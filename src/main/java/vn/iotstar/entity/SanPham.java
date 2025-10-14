package vn.iotstar.entity;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
@Document(collection = "SanPham")
public class SanPham {
    @Id
    private String maSP; // ObjectId tự generate dưới dạng String

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(min = 2, max = 100, message = "Tên sản phẩm phải từ 2 đến 100 ký tự")
    @Field(name = "TenSP")
    private String tenSP;

    @NotNull(message = "Giá bán không được để trống")
    @Positive(message = "Giá bán phải lớn hơn 0")
    @Field(name = "GiaBan")
    private Double giaBan;

    @NotNull(message = "Số lượng tồn kho không được để trống")
    @Positive(message = "Số lượng tồn kho phải lớn hơn 0")
    @Field(name = "SoLuongTon")
    private Integer soLuongTon;

    @NotBlank(message = "Kích thước không được để trống")
    @Field(name = "KichThuoc")
    private String kichThuoc;

    @NotBlank(message = "Màu sắc không được để trống")
    @Field(name = "MauSac")
    private String mauSac;

    @NotBlank(message = "Chất liệu không được để trống")
    @Field(name = "ChatLieu")
    private String chatLieu;

    @NotBlank(message = "Mã danh mục không được để trống")
    @Field(name = "MaDanhMuc")
    private String maDanhMuc; // Tham chiếu đến DanhMuc

    @Size(max = 500, message = "Mô tả không được vượt quá 500 ký tự")
    @Field(name = "MoTa")
    private String moTa;

    @NotBlank(message = "URL hình ảnh không được để trống")
    @Field(name = "HinhAnh")
    private String hinhAnh; // URL từ Cloudinary
}