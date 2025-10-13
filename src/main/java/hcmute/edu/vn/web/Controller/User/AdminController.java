package hcmute.edu.vn.web.Controller.User;

import hcmute.edu.vn.web.Entity.User.Admin;
import hcmute.edu.vn.web.Entity.User.UserProfile;
import hcmute.edu.vn.web.Service.GridFsService;
import hcmute.edu.vn.web.Service.implement.User.AdminService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/admin/me")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final GridFsService gridFsService;
    // [R]ead - Lấy thông tin bản thân
    @GetMapping
    public ResponseEntity<Admin> getAdminProfile(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        return adminService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // [U]pdate 1: Cập nhật thông tin Admin (ví dụ: chỉ cập nhật password hoặc active status)
    @PutMapping("/details")
    public ResponseEntity<Admin> updateMyDetails(@AuthenticationPrincipal UserDetails userDetails,
                                                 @RequestBody Admin adminDetails) {
        String email = userDetails.getUsername();

        // Không cho phép thay đổi email của bản thân
        if (adminDetails.getEmail() != null && !adminDetails.getEmail().equals(email)) {
            return ResponseEntity.badRequest().build();
        }

        return adminService.updateAdminDetails(email, adminDetails)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // [U]pdate 2: Cập nhật UserProfile của Admin
    @PutMapping("/profile")
    public ResponseEntity<Admin> updateMyProfile(@AuthenticationPrincipal UserDetails userDetails,
                                                 @RequestBody UserProfile userProfile) {
        String email = userDetails.getUsername();

        return adminService.updateAdminUserProfile(email, userProfile)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/profile/picture")
    public ResponseEntity<String> uploadProfilePicture(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("file") MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No file uploaded.");
            }
            // Kiểm tra kích thước file (5MB)
            if (file.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File size exceeds 5MB limit.");
            }
            String email = userDetails.getUsername();
            Admin admin = adminService.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Admin not found."));
            if (admin.getUserProfile() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User profile not found.");
            }
            String userProfileId = admin.getUserProfile().getId();
            ObjectId gridFsId = gridFsService.saveProfilePicture(userProfileId, file);
            return ResponseEntity.ok("Profile picture uploaded successfully. GridFS ID: " + gridFsId.toHexString());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing file: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }
    @GetMapping(value = "/profile/picture")
    public ResponseEntity<byte[]> getProfilePicture(@AuthenticationPrincipal UserDetails userDetails) throws IOException {
        String email = userDetails.getUsername();
        Admin admin = adminService.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found."));

        ObjectId gridFsId = admin.getUserProfile().getProfilePictureGridFsId();

        if (gridFsId == null) {
            // Trả về ảnh mặc định nếu không có, hoặc 404
            return ResponseEntity.notFound().build();
        }

        GridFsResource resource = gridFsService.getProfilePictureResource(gridFsId);

        if (resource == null) {
            return ResponseEntity.notFound().build();
        }

        // Trả về byte array của hình ảnh với Content-Type chính xác
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(resource.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(FileCopyUtils.copyToByteArray(resource.getInputStream()));
    }

    @DeleteMapping("/profile/picture")
    public ResponseEntity<Void> deleteProfilePicture(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            Admin admin = adminService.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Admin not found."));

            String userProfileId = admin.getUserProfile().getId();
            gridFsService.deleteProfilePicture(userProfileId); // Gọi Service để thực hiện xóa

            return ResponseEntity.noContent().build(); // Trả về 204 No Content cho thành công
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}