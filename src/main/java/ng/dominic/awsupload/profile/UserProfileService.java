package ng.dominic.awsupload.profile;

import ng.dominic.awsupload.bucket.BucketName;
import ng.dominic.awsupload.filestore.FileStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;

@Service
public class UserProfileService {

    private final UserProfileDataAccessService userProfileDataAccessService;

    private final FileStore fileStore;

    @Autowired
    public UserProfileService(UserProfileDataAccessService userProfileDataAccessService, FileStore fileStore) {
        this.userProfileDataAccessService = userProfileDataAccessService;
        this.fileStore = fileStore;
    }

    List<UserProfile> getUserProfiles() {
        return userProfileDataAccessService.getUserProfiles();
    }

    public byte[] downloadUserProfileImage(UUID userProfileId) {
        UserProfile user = getUserProfileOrThrow(userProfileId);
        String path = String.format("%s/%s",
                BucketName.PROFILE_IMAGE.getBucketName(),
                user.getUserProfileId());

        return user.getUserProfileImageLink()
                .map(key -> fileStore.download(path, key))
                .orElse(new byte[0]);
    }

    public void uploadUserProfileImage(UUID userProfileId, MultipartFile file) {
        // 1. check if img is not empty
        isEmptyFileCheck(file);

        // 2. if file is an img
        isImageCheck(file);

        // 3. user exists
        UserProfile user = getUserProfileOrThrow(userProfileId);

        // 4. grab metadata from file if any
        Map<String, String> metadata = extractMetadata(file);

        // 5. store the img in s3 and update db (userProfileImageLink) with s3 img link
        String path = String.format("%s/%s", BucketName.PROFILE_IMAGE.getBucketName(), user.getUserProfileId());
        String filename = String.format("%s-%s", UUID.randomUUID(), file.getOriginalFilename());
        try (InputStream inputStream = file.getInputStream()) {
            fileStore.save(path, filename, inputStream, Optional.of(metadata));
            user.setUserProfileImageLink(filename);
        } catch (Exception e) {
            System.out.println("Oops: " + e);
        }
    }

    private void isEmptyFileCheck(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalStateException("Cannot upload empty file [" + file.getSize() + "]");
        }
    }

    private void isImageCheck(MultipartFile file) {
        if (!Arrays.asList(
                MediaType.IMAGE_JPEG_VALUE,
                MediaType.IMAGE_GIF_VALUE,
                MediaType.IMAGE_PNG_VALUE)
                .contains(file.getContentType())) {
            throw new IllegalStateException(
                    String.format("Wrong filetype, should be an image but uploaded %s", file.getContentType()));
        }
    }

    private UserProfile getUserProfileOrThrow(UUID userProfileId) {
        return userProfileDataAccessService.getUserProfiles().stream()
                .filter(userProfile -> userProfile.getUserProfileId().equals(userProfileId))
                .findAny()
                .orElseThrow(() -> new IllegalStateException(String.format("Userprofile %s not found", userProfileId)));
    }

    private Map<String, String> extractMetadata(MultipartFile file) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", file.getContentType());
        metadata.put("Content-Length", String.valueOf(file.getSize()));
        return metadata;
    }
}
