package messaging.app;

public class UserHelperClass {
    String username;
    String firstName;
    String surname;
    String profileImageUrl;
    int profileImageRotation;


    public UserHelperClass() {
    }

    public UserHelperClass(String username, String firstName, String surname, String profileImageUrl, int profileImageRotation) {
        this.username = username;
        this.firstName = firstName;
        this.surname = surname;
        this.profileImageUrl = profileImageUrl;
        this.profileImageRotation = profileImageRotation;
    }

    public int getProfileImageRotation() {
        return profileImageRotation;
    }

    public void setProfileImageRotation(int profileImageRotation) {
        this.profileImageRotation = profileImageRotation;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
