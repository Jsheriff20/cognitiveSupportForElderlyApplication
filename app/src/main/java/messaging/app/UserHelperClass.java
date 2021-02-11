package messaging.app;

import android.graphics.Bitmap;

public class UserHelperClass {
    String username;
    String firstName;
    String surname;
    Bitmap profileImage;



    public UserHelperClass(String firstName, String surname, Bitmap profileImage, String username){
        this.firstName = firstName;
        this.surname = surname;
        this.profileImage = profileImage;
        this.username = username;
    }

    public UserHelperClass() {
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

    public Bitmap getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(Bitmap profileImage) {
        this.profileImage = profileImage;
    }
}
