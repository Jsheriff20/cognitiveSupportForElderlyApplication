package messaging.app.messages.ViewingMessages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.VideoView;

import com.squareup.picasso.Picasso;

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
        if (fileExtension.equals(".jpg")) {



            vidViewMediaMessage.setVisibility(View.INVISIBLE);
            int rotation = mediaManagement.exifToDegrees(displayingMessage.getMediaMessageRotation());
            int currentDeviceOrientationMode = getWindowManager().getDefaultDisplay().getRotation();
            if(currentDeviceOrientationMode != deviceOrientationMode){
                rotation += 90;
            }

            Picasso.with(this).load(messageUrl)
                    .rotate(rotation)
                    .into(imgViewMediaMessage);
        }
        else {
            imgViewMediaMessage.setVisibility(View.INVISIBLE);
            vidViewMediaMessage.setVideoPath(messageUrl);
            vidViewMediaMessage.start();
            setVideoViewListener();
        }

        setBtnViewTextMessageOnClick();
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