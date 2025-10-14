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
@Document(collection = "DanhMuc")
public class DanhMuc {
    @Id
    private String maDM; // ObjectId tự generate dưới dạng String

    @NotBlank(message = "Tên danh mục không được để trống")
    @Size(min = 2, max = 50, message = "Tên danh mục phải từ 2 đến 50 ký tự")
    @Field(name = "TenDM")
    private String tenDM; // Ví dụ: "Ao", "Quan", "Vay"
}