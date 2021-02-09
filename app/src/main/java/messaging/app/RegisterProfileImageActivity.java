package messaging.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;


//TODO:
//when user uploads or captures a photo the "Skip" button needs to change to "Register"
public class RegisterProfileImageActivity extends AppCompatActivity {

    Button btnUploadPhoto;
    Button btnCapturePhoto;
    Button btnRegister;
    Button btnLoadLogin;

    String mEmail;
    String mPassword;
    String mFirstName;
    String mSurname;
    ContactingFirebase contactingFirebase = new ContactingFirebase(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_profile_image);

        btnUploadPhoto = findViewById(R.id.btnUploadProfileImage);
        btnCapturePhoto = findViewById(R.id.btnCaptureProfileImage);
        btnRegister = findViewById(R.id.btnRegister);
        btnLoadLogin = findViewById(R.id.btnLoadLogin);

        mEmail = getIntent().getStringExtra("email");
        mPassword = getIntent().getStringExtra("password");
        mFirstName = getIntent().getStringExtra("firstName");
        mSurname = getIntent().getStringExtra("surname");

        setBtnCapturePhotoOnClick();
        setBtnUploadPhotoOnClick();
        setBtnLoadLoginOnClick();
        setBtnRegisterOnClick();
    }


    private void setBtnUploadPhotoOnClick(){
        btnUploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    private void setBtnCapturePhotoOnClick(){
        btnCapturePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    private void setBtnRegisterOnClick(){
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                contactingFirebase.createUserWithEmailAndPassword(mEmail, mPassword);
            }
        });
    }


    private void setBtnLoadLoginOnClick(){
        btnLoadLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterProfileImageActivity.this, LoginActivity.class);
                RegisterProfileImageActivity.this.startActivity(intent);
            }
        });
    }
}