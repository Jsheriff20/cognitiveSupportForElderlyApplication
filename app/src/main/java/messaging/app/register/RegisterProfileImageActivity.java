package messaging.app.register;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;

import messaging.app.ContactingFirebase;
import messaging.app.login.LoginActivity;
import messaging.app.R;
import messaging.app.messages.MessagesActivity;
import messaging.app.messages.capturingMedia.CaptureActivity;

import static android.widget.Toast.LENGTH_SHORT;


//TODO:
//when user uploads or captures a photo the "Skip" button needs to change to "Register"
public class RegisterProfileImageActivity extends AppCompatActivity {

    private static final int REQUEST_EXTERNAL_STORAGE_PERMISSION_RESULT = 102;

    Button btnUploadPhoto;
    Button btnCapturePhoto;
    Button btnRegister;
    Button btnLoadLogin;
    Button btnBackToRegisterPersonalInfo;
    ImageView imgProfileImage;

    String mEmail;
    String mPassword;
    String mUsername;
    String mFirstName;
    String mSurname;
    Uri mProfileImage = null;
    int mProfileImageRotation = 0;
    ContactingFirebase contactingFirebase = new ContactingFirebase(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_profile_image);

        btnUploadPhoto = findViewById(R.id.btnUploadProfileImage);
        btnCapturePhoto = findViewById(R.id.btnCaptureProfileImage);
        btnRegister = findViewById(R.id.btnRegister);
        btnLoadLogin = findViewById(R.id.btnLoadLogin);
        btnBackToRegisterPersonalInfo = findViewById(R.id.btnBackToRegisterPersonalInfo);
        imgProfileImage = findViewById(R.id.imgProfileImage);

        mEmail = getIntent().getStringExtra("email");
        mPassword = getIntent().getStringExtra("password");
        mUsername = getIntent().getStringExtra("username");
        mFirstName = getIntent().getStringExtra("firstName");
        mSurname = getIntent().getStringExtra("surname");

        if(getIntent().getStringExtra("profileImage") != null){
            String profileImagePath = getIntent().getStringExtra("profileImage");
            mProfileImageRotation = getIntent().getIntExtra("profileImageRotation", 0);

            btnRegister.setText("Register");


            Bitmap profileImage = BitmapFactory.decodeFile(profileImagePath);
            profileImage = RotateBitmap(profileImage, mProfileImageRotation);


            mProfileImage = Uri.fromFile(new File(profileImagePath));

            imgProfileImage.setImageBitmap(profileImage);
        }

        setBtnCapturePhotoOnClick();
        setBtnUploadPhotoOnClick();

        setBtnLoadLoginOnClick();
        setBtnRegisterOnClick();

        setBtnBackToRegisterPasswordOnClick();
    }


    public static Bitmap RotateBitmap(Bitmap source, float angle){
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }


    private void setBtnBackToRegisterPasswordOnClick(){
        btnBackToRegisterPersonalInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterProfileImageActivity.this, RegisterPersonalInfoActivity.class);
                intent.putExtra("email", mEmail);
                intent.putExtra("password", mPassword);
                intent.putExtra("username", mUsername);
                intent.putExtra("firstName", mFirstName);
                intent.putExtra("surname", mSurname);
                RegisterProfileImageActivity.this.startActivity(intent);
                return;
            }
        });
    }


    private void setBtnUploadPhotoOnClick(){
        btnUploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                        //open the storage if permissions are granted
                        openFileSelector();
                    }
                    else{
                        if(shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){
                            //if permissions are denied say why permission is needed
                            Toast.makeText(getApplicationContext(), "Please enable Storage Access", LENGTH_SHORT).show();
                        }
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE_PERMISSION_RESULT);
                    }
                }
                else{
                    openFileSelector();
                }
            }
        });
    }


    private void setBtnCapturePhotoOnClick(){
        btnCapturePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterProfileImageActivity.this, CaptureActivity.class);
                intent.putExtra("captureForProfileImage", true);
                intent.putExtras(getIntent().getExtras());
                RegisterProfileImageActivity.this.startActivity(intent);

            }
        });
    }


    private void openFileSelector(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent.createChooser(intent, "Select Profile Picture"), REQUEST_EXTERNAL_STORAGE_PERMISSION_RESULT);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK){
            Toast.makeText(this, "No new profile image", LENGTH_SHORT).show();
            return;
        }


        mProfileImage = data.getData();
        imgProfileImage.setImageURI(mProfileImage);

        if(requestCode == REQUEST_EXTERNAL_STORAGE_PERMISSION_RESULT){
            //store the captured image
            mProfileImage = data.getData();

            // display the captured image
            imgProfileImage.setImageURI(mProfileImage);
            btnRegister.setText("Register");
        }

    }


    private void setBtnRegisterOnClick(){
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactingFirebase.createUserWithEmailAndPassword(mEmail, mPassword, mFirstName, mSurname, mProfileImage, mUsername);
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