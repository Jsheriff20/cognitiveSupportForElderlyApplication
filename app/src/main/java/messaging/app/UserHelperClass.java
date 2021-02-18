package messaging.app;

import android.graphics.Bitmap;
import android.net.Uri;

public class UserHelperClass {
    String username;
    String firstName;
    String surname;
    String profileImage;
    int profileImageRotation;



    public UserHelperClass(String firstName, String surname, String profileImage, int profileImageRotation, String username){
        this.firstName = firstName;
        this.surname = surname;
        this.profileImage = profileImage;
        this.username = username;
        this.profileImageRotation = profileImageRotation;
    }

    public UserHelperClass() {
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

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
