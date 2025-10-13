package hcmute.edu.vn.web.Service;

import com.mongodb.client.gridfs.model.GridFSFile;
import hcmute.edu.vn.web.Entity.User.UserProfile;
import hcmute.edu.vn.web.Repository.User.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class GridFsService {

    private final GridFsTemplate gridFsTemplate;
    private final UserProfileRepository userProfileRepository;

    /**
     * Lưu file ảnh vào GridFS, xóa file cũ và cập nhật UserProfile.
     * @return ObjectId của file mới được lưu.
     */
    public ObjectId saveProfilePicture(String userProfileId, MultipartFile file) throws IOException {
        UserProfile profile = userProfileRepository.findById(userProfileId)
                .orElseThrow(() -> new IllegalArgumentException("User profile not found with ID: " + userProfileId));

        // 1. Xóa hình ảnh cũ (Nếu có)
        ObjectId oldId = profile.getProfilePictureGridFsId();
        if (oldId != null) {
            // Xóa file bằng ObjectId
            gridFsTemplate.delete(new Query(Criteria.where("_id").is(oldId)));
        }

        // 2. Lưu tệp mới vào GridFS
        ObjectId fileId = gridFsTemplate.store(
                file.getInputStream(),
                file.getOriginalFilename(),
                file.getContentType()
        );

        // 3. Cập nhật UserProfile với ObjectId mới
        profile.setProfilePictureGridFsId(fileId); // Set ObjectId
        userProfileRepository.save(profile);

        return fileId;
    }

    /**
     * Lấy Resource từ GridFS dựa trên GridFS ID (ObjectId).
     */
    public GridFsResource getProfilePictureResource(ObjectId gridFsId) {
        // Truy vấn bằng ObjectId
        GridFSFile gridFSFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(gridFsId)));

        if (gridFSFile == null) {
            return null;
        }

        return gridFsTemplate.getResource(gridFSFile);
    }

    public boolean deleteProfilePicture(String userProfileId) {
        UserProfile profile = userProfileRepository.findById(userProfileId)
                .orElseThrow(() -> new IllegalArgumentException("User profile not found with ID: " + userProfileId));

        ObjectId oldId = profile.getProfilePictureGridFsId();
        if (oldId != null) {
            // 1. Xóa file khỏi GridFS
            gridFsTemplate.delete(new Query(Criteria.where("_id").is(oldId)));

            // 2. Cập nhật UserProfile (Set ID về null)
            profile.setProfilePictureGridFsId(null);
            userProfileRepository.save(profile);
            return true;
        }
        return false; // Không có ảnh để xóa
    }
}
