package nvkho.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "DanhMuc")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DanhMuc {
    @Id
    private String maDM;
    
    private String tenDM; // "Ao", "Quan", "Vay", "PhuKien"
}