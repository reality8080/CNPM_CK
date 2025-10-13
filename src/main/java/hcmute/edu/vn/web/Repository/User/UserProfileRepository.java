package hcmute.edu.vn.web.Repository.User;

import hcmute.edu.vn.web.Entity.User.UserProfile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileRepository extends MongoRepository<UserProfile, String> {
    UserProfile findByPhoneNumber(String phoneNumber);
//    void deleteById(String email);
}
