package messaging.app.register;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;

import messaging.app.ManagingActivityPreview;
import messaging.app.MediaManagement;
import messaging.app.contactingFirebase.ManagingAccounts;
import messaging.app.login.LoginActivity;
import messaging.app.R;
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
    String mProfileImagePath = null;
    int mProfileImageRotation = 0;
    private boolean mButtonPressProcessing = false;

    ManagingAccounts mangingAccounts = new ManagingAccounts(this);
    MediaManagement mediaManagement = new MediaManagement();
    ManagingActivityPreview managingActivityPreview = new ManagingActivityPreview();

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
            mProfileImagePath = getIntent().getStringExtra("profileImage");
            mProfileImageRotation = getIntent().getIntExtra("profileImageRotation", 0);

            btnRegister.setText("Register");

            mProfileImage = Uri.fromFile(new File(mProfileImagePath));

            try {

                ExifInterface exif = null;
                //display the media in the correct rotation
                exif = new ExifInterface(mProfileImagePath);
                int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                Bitmap myBitmap = BitmapFactory.decodeFile(new File(mProfileImagePath).getAbsolutePath());

                Bitmap adjustedBitmapImage = mediaManagement.adjustBitmapImage(exifOrientation, myBitmap);

                imgProfileImage.setImageBitmap(adjustedBitmapImage);

            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        setBtnCapturePhotoOnClick();
        setBtnUploadPhotoOnClick();

        setBtnLoadLoginOnClick();
        setBtnRegisterOnClick();

        setBtnBackToRegisterPasswordOnClick();
    }


    @Override
    public void onBackPressed() {
        btnBackToRegisterPersonalInfo.callOnClick();
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            managingActivityPreview.hideSystemUI(getWindow().getDecorView());
        }
    }


    private void setBtnBackToRegisterPasswordOnClick(){
        btnBackToRegisterPersonalInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mProfileImagePath.equals(null)) {
                    mediaManagement.deleteMediaFile(mProfileImagePath, getApplicationContext());
                }

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
                if(!mButtonPressProcessing) {
                    mButtonPressProcessing = true;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            //open the storage if permissions are granted
                            openFileSelector();
                        } else {
                            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                                //if permissions are denied say why permission is needed
                                Toast.makeText(getApplicationContext(), "Please enable Storage Access", LENGTH_SHORT).show();
                            }
                            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE_PERMISSION_RESULT);
                        }
                    } else {
                        openFileSelector();
                    }
                }
            }
        });
    }


    private void setBtnCapturePhotoOnClick(){
        btnCapturePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mButtonPressProcessing) {
                    //do not need to set button press processing to false as a new activity will be created instead
                    mButtonPressProcessing = true;
                    Intent intent = new Intent(RegisterProfileImageActivity.this, CaptureActivity.class);
                    intent.putExtra("captureForProfileImage", true);
                    intent.putExtras(getIntent().getExtras());
                    RegisterProfileImageActivity.this.startActivity(intent);
                }

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
        //cancel button process for selecting an image
        mButtonPressProcessing = false;
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
                mangingAccounts.createUserWithEmailAndPassword(mEmail, mPassword, mFirstName, mSurname, mProfileImage, mUsername);
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