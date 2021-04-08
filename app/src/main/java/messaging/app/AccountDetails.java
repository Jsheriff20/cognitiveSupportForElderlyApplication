package messaging.app;

public class AccountDetails {


    private String mUUID;
    private String mFirstName;
    private String mSurname;
    private String mRelationship;
    private String mProfileImageUrl = null;
    private String mUsername;
    private int mProfileImageRotation;

    public AccountDetails(String UUID, String firstName, String surname, String relationship,
                          String profileImageUrl, int profileImageRotation, String username) {
        this.mUUID = UUID;
        this.mFirstName = firstName;
        this.mSurname = surname;
        this.mRelationship = relationship;
        this.mProfileImageUrl = profileImageUrl;
        this.mProfileImageRotation = profileImageRotation;
        this.mUsername = username;
    }

    public AccountDetails() {

    }

    public int getmProfileImageRotation() {
        return mProfileImageRotation;
    }

    public String getmUsername() {
        return mUsername;
    }

    public void setmUsername(String mUsername) {
        this.mUsername = mUsername;
    }

    public void setmProfileImageRotation(int mProfileImageRotation) {
        this.mProfileImageRotation = mProfileImageRotation;
    }

    public String getmUUID() {
        return mUUID;
    }

    public void setmUUID(String mUUID) {
        this.mUUID = mUUID;
    }

    public String getmFirstName() {
        return mFirstName;
    }

    public void setmFirstName(String mFirstName) {
        this.mFirstName = mFirstName;
    }

    public String getmSurname() {
        return mSurname;
    }

    public void setmSurname(String mSurname) {
        this.mSurname = mSurname;
    }

    public String getmRelationship() {
        return mRelationship;
    }

    public void setmRelationship(String mRelationship) {
        this.mRelationship = mRelationship;
    }

    public String getmProfileImageUrl() {
        return mProfileImageUrl;
    }

    public void setmProfileImageUrl(String mProfileImageUrl) {
        this.mProfileImageUrl = mProfileImageUrl;
    }
}
