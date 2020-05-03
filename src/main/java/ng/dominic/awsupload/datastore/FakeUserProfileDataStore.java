package ng.dominic.awsupload.datastore;

import ng.dominic.awsupload.profile.UserProfile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class FakeUserProfileDataStore {
    private static final List<UserProfile> USER_PROFILES = new ArrayList<>();

    /**
     * Bij een nieuwe start wordt de userProfileImageLink telkens op null gezet, daardoor zie je broken images in het begin
     */
    static {
        USER_PROFILES.add(new UserProfile(UUID.fromString("2c6d2f45-e1a9-4ec1-826b-b385ca299a95"), "Anne", null));
        USER_PROFILES.add(new UserProfile(UUID.fromString("67f60c03-0abd-4bec-b5a2-1f6260232d49"), "Marie", null));
    }

    public List<UserProfile> getUserProfiles() {
        return USER_PROFILES;
    }
}
