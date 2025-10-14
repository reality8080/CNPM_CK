package vn.iotstar.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import vn.iotstar.entity.DanhMuc;

public interface DanhMucRepository extends MongoRepository<DanhMuc, String> {
    // Không cần custom query, vì chỉ cần CRUD cơ bản
}