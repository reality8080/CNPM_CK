package nvkho.repository;


import nvkho.entity.DanhMuc;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DanhMucRepository extends MongoRepository<DanhMuc, String> {
}
