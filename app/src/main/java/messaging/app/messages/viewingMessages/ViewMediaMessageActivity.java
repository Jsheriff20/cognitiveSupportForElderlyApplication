package messaging.app.messages.viewingMessages;

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

import messaging.app.ManagingActivityPreview;
import messaging.app.MediaManagement;
import messaging.app.R;
import messaging.app.contactingFirebase.ManagingMessages;

public class ViewMediaMessageActivity extends AppCompatActivity {

    VideoView vidViewMediaMessage;
    ImageView imgViewMediaMessage;
    Button btnViewTextMessage;

    Intent mIntent;
    int mMessageNum;
    int mNumberOfMessages;
    int mNewMessageNum;
    String mFriendsUUID;
    String mViewingType;
    String mMessageUrl;
    int mDeviceOrientationMode;
    ArrayList<Parcelable> mMessageList;

    private File mImageFolder;
    private String mImageFilePath;

    ManagingMessages mManagingMessages = new ManagingMessages(this);
    ManagingActivityPreview mManagingActivityPreview = new ManagingActivityPreview();
    MessageData mDisplayingMessage;
    MediaManagement mMediaManagement;

    public ViewMediaMessageActivity() {
        mMediaManagement = new MediaManagement(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_media_message);

        vidViewMediaMessage = findViewById(R.id.vidViewMediaMessage);
        imgViewMediaMessage = findViewById(R.id.imgViewMediaMessage);
        btnViewTextMessage = findViewById(R.id.btnViewTextMessage);

        mIntent = getIntent();
        mMessageNum = mIntent.getIntExtra("messageNum", -1);

        //if this is the first message, set he message number to 0
        if (mMessageNum == -1) {
            mMessageNum = 0;
            mIntent.putExtra("messageNum", 0);
        }

        mMessageList = mIntent.getParcelableArrayListExtra("messagesList");
        mDisplayingMessage = (MessageData) mMessageList.get(mMessageNum);
        mNumberOfMessages = mIntent.getIntExtra("numOfMessages", 0);
        mFriendsUUID = mIntent.getStringExtra("friendsUUID");
        mViewingType = mIntent.getStringExtra("viewingType");
        mDeviceOrientationMode = mDisplayingMessage.getmDeviceOrientationMode();

        mNewMessageNum = mMessageNum + 1;
        if (mNewMessageNum >= mNumberOfMessages) {
            btnViewTextMessage.setText("Exit");
        }


        lockOrientation(true);

        mMessageUrl = mDisplayingMessage.getmMediaMessageUrl();
        String fileExtension = mDisplayingMessage.getmFileExtension();

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

        }

        if (fileExtension.equals(".jpg")) {
            displayReceivedImage();
        } else {
            displayReceivedVideo();
        }

        setBtnViewTextMessageOnClick();
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            mManagingActivityPreview.hideSystemUI(getWindow().getDecorView());
        }
    }


    private void displayReceivedImage() {
        vidViewMediaMessage.setVisibility(View.INVISIBLE);

        if (mMessageUrl != null && !mMessageUrl.equals("")) {
            //create directories for files
            File[] mediaFolders = mMediaManagement.createMediaFolders();
            mImageFolder = mediaFolders[1];

            try {
                mImageFilePath = mMediaManagement.createImageFileName(mImageFolder).getAbsolutePath();
                try (BufferedInputStream inputStream =
                             new BufferedInputStream(new URL(mMessageUrl).openStream());
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


                int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL);
                Bitmap myBitmap = BitmapFactory.decodeFile(new File(mImageFilePath).getAbsolutePath());

                //pre rotate bitmap to account for the device rotation
                int currentDeviceOrientationMode = getWindowManager().getDefaultDisplay().getRotation();
                if (currentDeviceOrientationMode != mDeviceOrientationMode) {
                    myBitmap = mMediaManagement.rotateBitmap(myBitmap, 90);
                }

                Bitmap adjustedBitmapImage = mMediaManagement.adjustBitmapImage(exifOrientation, myBitmap);

                imgViewMediaMessage.setImageBitmap(adjustedBitmapImage);


                mMediaManagement.deleteMediaFile(mImageFilePath, ViewMediaMessageActivity.this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void displayReceivedVideo() {
        imgViewMediaMessage.setVisibility(View.INVISIBLE);
        vidViewMediaMessage.setVideoPath(mMessageUrl);
        vidViewMediaMessage.start();
        setVideoViewListener();
    }

    @Override
    public void onBackPressed() {

        lockOrientation(false);
        if (mViewingType.equals("directMessages")) {
            mManagingMessages.deleteMessage(mDisplayingMessage.getmTimeStamp(), mFriendsUUID);
            mManagingMessages.logMediaMessageViewed(mMessageUrl);
        }

        Intent intent = new Intent(ViewMediaMessageActivity.this,
                ListOfReceivedMediaActivity.class);
        ViewMediaMessageActivity.this.startActivity(intent);
    }


    private void setBtnViewTextMessageOnClick() {
        btnViewTextMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //check if there is another message to view
                Intent newIntent;
                lockOrientation(false);

                if (mViewingType.equals("directMessages")) {
                    mManagingMessages.deleteMessage(mDisplayingMessage.getmTimeStamp(), mFriendsUUID);
                    mManagingMessages.logMediaMessageViewed(mMessageUrl);
                }


                if (mNewMessageNum >= mNumberOfMessages) {

                    //if there are no more messages, load the list of messages from friends
                    newIntent = new Intent(ViewMediaMessageActivity.this,
                            ListOfReceivedMediaActivity.class);
                } else {
                    MessageData tempMessageData = (MessageData) mMessageList.get(mNewMessageNum);
                    mIntent.putExtra("messageNum", mNewMessageNum);

                    //check to see if there is a text message
                    if (tempMessageData.getmTextMessage().equals("")) {
                        newIntent = new Intent(ViewMediaMessageActivity.this,
                                ViewMediaMessageActivity.class);
                        newIntent.putExtras(mIntent.getExtras());
                    } else {
                        newIntent = new Intent(ViewMediaMessageActivity.this,
                                ViewTextMessageActivity.class);
                        newIntent.putExtras(mIntent.getExtras());
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
            boolean inLandscapeMode =
                    ((int) getWindowManager().getDefaultDisplay().getRotation() == landscape);
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