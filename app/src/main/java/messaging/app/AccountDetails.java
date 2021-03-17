package messaging.app;

public class AccountDetails {


    private String UUID;
    private String firstName;
    private String surname;
    private String relationship;
    private String profileImageUrl = null;
    private String username;
    private int profileImageRotation;

    public AccountDetails(String UUID, String firstName, String surname, String relationship, String profileImageUrl, int profileImageRotation, String username) {
        this.UUID = UUID;
        this.firstName = firstName;
        this.surname = surname;
        this.relationship = relationship;
        this.profileImageUrl = profileImageUrl;
        this.profileImageRotation = profileImageRotation;
        this.username = username;
    }

    public AccountDetails(){

    }

    public int getProfileImageRotation() {
        return profileImageRotation;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setProfileImageRotation(int profileImageRotation) {
        this.profileImageRotation = profileImageRotation;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
