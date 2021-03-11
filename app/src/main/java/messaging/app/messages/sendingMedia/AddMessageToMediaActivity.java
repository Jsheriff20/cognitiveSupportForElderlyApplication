package messaging.app.messages.sendingMedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import messaging.app.ManagingActivityPreview;
import messaging.app.MediaManagement;
import messaging.app.R;
import messaging.app.contactingFirebase.ManagingMessages;
import messaging.app.messages.ViewingMessages.ListOfReceivedMediaActivity;

import static android.widget.Toast.LENGTH_SHORT;

public class AddMessageToMediaActivity extends AppCompatActivity {

    private static final int REQUEST_SPEECH_INPUT_RESULT = 104;

    ImageButton btnSelectRecipientsActivity;
    ImageButton btnVoiceToText;
    ImageButton btnStopVoiceToText;
    ImageButton btnBackToCaptureMedia;
    EditText txtMessage;
    ImageView imgCapturedImagePreview;
    VideoView vidCapturedVideoPreview;

    String mTypeOfMediaCaptured;
    String mMediaPath;
    String mMessage;
    int mDeviceOrientationMode;
    private boolean userWantsToSendTheirAudioRecording = false;
    private boolean permissionToRecordAccepted = false;
    private SpeechRecognizer speechRecognizer;

    private String mReplyingToUUID = null;

    MediaManagement mediaManagement = new MediaManagement();
    ManagingActivityPreview managingActivityPreview = new ManagingActivityPreview();
    ManagingMessages managingMessages = new ManagingMessages(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_message_to_media);

        mTypeOfMediaCaptured = getIntent().getStringExtra("typeOfMediaCaptured");
        mMediaPath = getIntent().getStringExtra("mediaPath");
        mDeviceOrientationMode = getIntent().getIntExtra("deviceOrientationMode", 0);


        btnSelectRecipientsActivity = findViewById(R.id.btnSelectRecipientsActivity);
        btnVoiceToText = findViewById(R.id.btnVoiceToText);
        btnStopVoiceToText = findViewById(R.id.btnStopVoiceToText);
        txtMessage = findViewById(R.id.txtMessage);
        imgCapturedImagePreview = findViewById(R.id.imgCapturedImagePreview);
        vidCapturedVideoPreview = findViewById(R.id.vidCapturedVideoPreview);
        btnBackToCaptureMedia = findViewById(R.id.btnBackToCaptureMedia);

        if(getIntent().getStringExtra("replyingTo") != null){
            mReplyingToUUID = getIntent().getStringExtra("replyingTo");
        }


        if(userWantsToSendTheirAudioRecording) {
            ActivityCompat.requestPermissions(AddMessageToMediaActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_SPEECH_INPUT_RESULT);
        }
        setBtnStartVoiceToTextOnClick();
        setBtnStopVoiceToTextOnClick();
        btnStopVoiceToText.setVisibility(View.INVISIBLE);

        //hide the navigation controls
        View decorView = getWindow().getDecorView();

        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        setBtnSelectRecipientsActivityOnClick();
        setVideoViewListener();
        previewCapturedMedia(mTypeOfMediaCaptured, mMediaPath);
        setBtnBackToCaptureMedia();

    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            managingActivityPreview.hideSystemUI(getWindow().getDecorView());
        }
    }


    private void setBtnBackToCaptureMedia(){
        btnBackToCaptureMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Activity) AddMessageToMediaActivity.this).finish();
            }
        });
    }


    private void setBtnSelectRecipientsActivityOnClick(){
        btnSelectRecipientsActivity.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mMessage = txtMessage.getText().toString();

                if(mReplyingToUUID == null) {
                    Intent intent = new Intent(getApplicationContext(), SendMediaTabsActivity.class);
                    intent.putExtra("typeOfMediaCaptured", mTypeOfMediaCaptured);
                    intent.putExtra("mediaPath", mMediaPath);
                    intent.putExtra("message", mMessage);
                    intent.putExtra("deviceOrientationMode", mDeviceOrientationMode);


                    startActivity(intent);
                }
                else{
                    sendMedia(mReplyingToUUID, mMediaPath, mTypeOfMediaCaptured, mMessage, mDeviceOrientationMode);
                }
            }
        });
    }

    private void sendMedia(String friendsUUID, String pathToMedia, String typeOfMediaCaptured, String message, int deviceOrientationMode ){

        ArrayList<String> directMessagesUUID = new ArrayList<String>();
        ArrayList<String> storyMessagesUUID = new ArrayList<String>();;

        directMessagesUUID.add(friendsUUID);

        try {
            managingMessages.sendMessages(directMessagesUUID, storyMessagesUUID, pathToMedia, typeOfMediaCaptured, message, deviceOrientationMode);
        } catch (IOException e) {
            e.printStackTrace();
        }


        Intent intent = new Intent(AddMessageToMediaActivity.this, ListOfReceivedMediaActivity.class);
        AddMessageToMediaActivity.this.startActivity(intent);
    }


    private void setBtnStartVoiceToTextOnClick(){
        btnVoiceToText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                recordAudioForText(true);
            }
        });
    }


    private void setBtnStopVoiceToTextOnClick(){
        btnStopVoiceToText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordAudioForText(false);

            }
        });
    }


    private void recordAudioForText(boolean start) {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(AddMessageToMediaActivity.this);

        //load intent to record voice
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        getIntent().putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());


        //todo:
        //need to set a time limit

        //increase the amount of time before stopping

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                Toast.makeText(getApplicationContext(), "Listening", LENGTH_SHORT).show();

                btnVoiceToText.setVisibility(View.INVISIBLE);
                btnStopVoiceToText.setVisibility(View.VISIBLE);

            }

            @Override
            public void onBeginningOfSpeech() {
            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {
            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results) {
                Toast.makeText(getApplicationContext(), "Stopped listening", LENGTH_SHORT).show();

                btnVoiceToText.setVisibility(View.VISIBLE);
                btnStopVoiceToText.setVisibility(View.INVISIBLE);

                ArrayList<String> matches = results
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                if (matches != null)
                    txtMessage.setText(matches.get(0));
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {
            }
        });


        if(start){
            speechRecognizer.startListening(intent);
        }
        else{
            speechRecognizer.stopListening();
        }
    }


    private void previewCapturedMedia(String typeOfCapturedMedia, String mediaPath){

        File mediaFile;

        //display the captured media
        switch (typeOfCapturedMedia){
            case "Image":
                //get image file
                mediaFile = new File(mediaPath);

                //update image view
                if(mediaFile.exists()){
                    try {

                        ExifInterface exif = null;
                        //display the media in the correct rotation
                        exif = new ExifInterface(mediaFile.getPath());
                        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                        Bitmap myBitmap = BitmapFactory.decodeFile(mediaFile.getAbsolutePath());

                        Bitmap adjustedBitmapImage = mediaManagement.adjustBitmapImage(exifOrientation, myBitmap);

                        imgCapturedImagePreview.setImageBitmap(adjustedBitmapImage);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }else{
                    Toast.makeText(getApplicationContext(), "Could not find image", LENGTH_SHORT).show();
                }

                //display image
                imgCapturedImagePreview.setVisibility(View.VISIBLE);
                vidCapturedVideoPreview.setVisibility(View.INVISIBLE);
                break;

            case "Video":
                //get image file
                mediaFile = new File(mediaPath);

                //update image view
                if(mediaFile.exists()){

                    vidCapturedVideoPreview.setVideoPath(mediaPath);
                    vidCapturedVideoPreview.start();


                }else{
                    Toast.makeText(getApplicationContext(), "Could not find video", LENGTH_SHORT).show();
                }

                //display image
                imgCapturedImagePreview.setVisibility(View.INVISIBLE);
                vidCapturedVideoPreview.setVisibility(View.VISIBLE);


                break;
        }
    }


    public void setVideoViewListener(){
        vidCapturedVideoPreview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_SPEECH_INPUT_RESULT:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();
    }
}