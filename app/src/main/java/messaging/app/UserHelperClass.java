package messaging.app;

public class UserHelperClass {
    String mUsername;
    String mFirstName;
    String mSurname;
    String mProfileImageUrl;
    int mProfileImageRotation;


    public UserHelperClass() {
    }

    public UserHelperClass(String username, String firstName, String surname,
                           String profileImageUrl, int profileImageRotation) {
        this.mUsername = username;
        this.mFirstName = firstName;
        this.mSurname = surname;
        this.mProfileImageUrl = profileImageUrl;
        this.mProfileImageRotation = profileImageRotation;
    }

    public int getmProfileImageRotation() {
        return mProfileImageRotation;
    }

    public void setmProfileImageRotation(int mProfileImageRotation) {
        this.mProfileImageRotation = mProfileImageRotation;
    }

    public String getmFirstName() {
        return mFirstName;
    }

    public void setmFirstName(String mFirstName) {
        this.mFirstName = mFirstName;
    }

    public String getmUsername() {
        return mUsername;
    }

    public void setmUsername(String mUsername) {
        this.mUsername = mUsername;
    }

    public String getmSurname() {
        return mSurname;
    }

    public void setmSurname(String mSurname) {
        this.mSurname = mSurname;
    }

    public String getmProfileImageUrl() {
        return mProfileImageUrl;
    }

    public void setmProfileImageUrl(String mProfileImageUrl) {
        this.mProfileImageUrl = mProfileImageUrl;
    }
}
