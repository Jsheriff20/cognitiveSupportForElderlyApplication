package messaging.app.messages.ViewingMessages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.VideoView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import messaging.app.ContactingFirebase;
import messaging.app.MediaManagement;
import messaging.app.R;
import messaging.app.login.LoginActivity;
import messaging.app.login.ResetPasswordActivity;

public class ViewMediaMessageActivity extends AppCompatActivity {

    VideoView vidViewMediaMessage;
    ImageView imgViewMediaMessage;
    Button btnViewTextMessage;

    Intent intent;
    MessageData displayingMessage;
    MediaManagement mediaManagement = new MediaManagement();
    int messageNum;
    int numberOfMessages;
    int newMessageNum;
    String friendsUUID;
    String viewingType;
    String messageUrl;
    int deviceOrientationMode;
    ArrayList<Parcelable> messageList;

    private File mImageFolder;
    private String mImageFilePath;

    ContactingFirebase contactingFirebase = new ContactingFirebase(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_media_message);

        vidViewMediaMessage = findViewById(R.id.vidViewMediaMessage);
        imgViewMediaMessage = findViewById(R.id.imgViewMediaMessage);
        btnViewTextMessage = findViewById(R.id.btnViewTextMessage);

        intent = getIntent();
        messageNum = intent.getIntExtra("messageNum", -1);

        //if this is the first message, set he message number to 0
        if (messageNum == -1) {
            messageNum = 0;
            intent.putExtra("messageNum", 0);
        }

        messageList = intent.getParcelableArrayListExtra("messagesList");
        displayingMessage = (MessageData) messageList.get(messageNum);
        numberOfMessages = intent.getIntExtra("numOfMessages", 0);
        friendsUUID = intent.getStringExtra("friendsUUID");
        viewingType = intent.getStringExtra("viewingType");
        deviceOrientationMode = displayingMessage.getDeviceOrientationMode();

        newMessageNum = messageNum + 1;
        if (newMessageNum >= numberOfMessages) {
            btnViewTextMessage.setText("Exit");
        }


        lockOrientation(true);

        messageUrl = displayingMessage.getMediaMessageUrl();
        String fileExtension = displayingMessage.getFileExtension();

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

        }

        if (fileExtension.equals(".jpg")) {
            displayReceivedImage();
        }
        else {
            displayReceivedVideo();
        }

        setBtnViewTextMessageOnClick();
    }


    private void displayReceivedImage(){
        vidViewMediaMessage.setVisibility(View.INVISIBLE);

        if(messageUrl != null && !messageUrl.equals("") ) {
            //create directories for files
            File[] mediaFolders = mediaManagement.createMediaFolders();
            mImageFolder = mediaFolders[1];

            try {
                mImageFilePath = mediaManagement.createImageFileName(mImageFolder).getAbsolutePath();
                try (BufferedInputStream inputStream = new BufferedInputStream(new URL(messageUrl).openStream());
                     FileOutputStream fileOS = new FileOutputStream(mImageFilePath)) {
                    byte data[] = new byte[1024];
                    int byteContent;
                    while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
                        fileOS.write(data, 0, byteContent);
                    }

                } catch (IOException e) {
                    // handles IO exceptions
                }


                ExifInterface exif = null;
                //display the media in the correct rotation
                exif = new ExifInterface(mImageFilePath);


                int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                Bitmap myBitmap = BitmapFactory.decodeFile(new File(mImageFilePath).getAbsolutePath());

                //pre rotate bitmap to account for the device rotation
                int currentDeviceOrientationMode = getWindowManager().getDefaultDisplay().getRotation();
                if (currentDeviceOrientationMode != deviceOrientationMode) {
                    myBitmap = mediaManagement.rotateBitmap(myBitmap, 90);
                }

                Bitmap adjustedBitmapImage = mediaManagement.adjustBitmapImage(exifOrientation, myBitmap);

                imgViewMediaMessage.setImageBitmap(adjustedBitmapImage);


                mediaManagement.deleteMediaFile(mImageFilePath, getApplicationContext());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void displayReceivedVideo(){
        imgViewMediaMessage.setVisibility(View.INVISIBLE);
        vidViewMediaMessage.setVideoPath(messageUrl);
        vidViewMediaMessage.start();
        setVideoViewListener();
    }

    @Override
    public void onBackPressed() {

        lockOrientation(false);
        if(viewingType.equals("directMessages")) {
            contactingFirebase.deleteMessage(displayingMessage.getTimeStamp(), friendsUUID);
            contactingFirebase.logMediaMessageViewed(messageUrl);
        }

        Intent intent = new Intent(ViewMediaMessageActivity.this, ListOfReceivedMediaActivity.class);
        ViewMediaMessageActivity.this.startActivity(intent);
    }


    private void setBtnViewTextMessageOnClick() {
        btnViewTextMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //check if there is another message to view
                Intent newIntent;
                lockOrientation(false);

                if(viewingType.equals("directMessages")) {
                    contactingFirebase.deleteMessage(displayingMessage.getTimeStamp(), friendsUUID);
                    contactingFirebase.logMediaMessageViewed(messageUrl);
                }


                if (newMessageNum >= numberOfMessages) {

                    //if there are no more messages, load the list of messages from friends
                    newIntent = new Intent(ViewMediaMessageActivity.this, ListOfReceivedMediaActivity.class);
                }
                else {
                    MessageData tempMessageData = (MessageData) messageList.get(newMessageNum);
                    intent.putExtra("messageNum", newMessageNum);

                    //check to see if there is a text message
                    if (tempMessageData.getTextMessage().equals("")) {
                        newIntent = new Intent(ViewMediaMessageActivity.this, ViewMediaMessageActivity.class);
                        newIntent.putExtras(intent.getExtras());
                    }
                    else {
                        newIntent = new Intent(ViewMediaMessageActivity.this, ViewTextMessageActivity.class);
                        newIntent.putExtras(intent.getExtras());
                    }
                }
                ViewMediaMessageActivity.this.startActivity(newIntent);
            }
        });
    }

    public void setVideoViewListener() {
        vidViewMediaMessage.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
    }


    private void lockOrientation(boolean lock) {

        if (lock) {
            int landscape = 1;
            boolean inLandscapeMode = ((int) getWindowManager().getDefaultDisplay().getRotation() == landscape);
            if (inLandscapeMode) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }
}