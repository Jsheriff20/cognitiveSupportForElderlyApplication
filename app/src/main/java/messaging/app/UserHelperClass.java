package messaging.app;

import android.graphics.Bitmap;

public class UserHelperClass {
    String UUID;
    String firstName;
    String surname;
    Bitmap profileImage;



    public UserHelperClass(String firstName, String surname, Bitmap profileImage) {
        this.firstName = firstName;
        this.surname = surname;
        this.profileImage = profileImage;
    }

    public UserHelperClass() {
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

    public Bitmap getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(Bitmap profileImage) {
        this.profileImage = profileImage;
    }
}
