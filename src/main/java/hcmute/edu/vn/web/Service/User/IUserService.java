package hcmute.edu.vn.web.Service.User;

import hcmute.edu.vn.web.Entity.User.User;

import java.util.Optional;

public interface IUserService<T extends User> {
    // Phương thức chung để tìm kiếm theo email
    Optional<T> findByEmail(String email);

    // Phương thức chung để lưu/cập nhật người dùng (ví dụ: cập nhật profile)
    T save(T user);

    // Lưu UserProfile
//    UserProfile saveUserProfile(UserProfile userProfile);

    void saveRefreshToken(String email, String token);

    boolean isRefreshTokenValid(String email, String token);

}
