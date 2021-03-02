package messaging.app.messages.sendingMedia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import messaging.app.MediaManagement;
import messaging.app.R;

import static android.widget.Toast.LENGTH_SHORT;

public class AddMessageToMediaActivity extends AppCompatActivity {

    private static final int REQUEST_SPEECH_INPUT_RESULT = 104;

    ImageButton btnSelectRecipientsActivity;
    ImageButton btnVoiceToText;
    ImageButton btnStopVoiceToText;
    EditText txtMessage;
    ImageView imgCapturedImagePreview;
    VideoView vidCapturedVideoPreview;

    String mTypeOfMediaCaptured;
    String mMediaPath;
    String mMessage;
    private boolean userWantsToSendTheirAudioRecording = false;
    private boolean permissionToRecordAccepted = false;
    private SpeechRecognizer speechRecognizer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_message_to_media);

        mTypeOfMediaCaptured = getIntent().getStringExtra("typeOfMediaCaptured");
        mMediaPath = getIntent().getStringExtra("mediaPath");

        btnSelectRecipientsActivity = findViewById(R.id.btnSelectRecipientsActivity);
        btnVoiceToText = findViewById(R.id.btnVoiceToText);
        btnStopVoiceToText = findViewById(R.id.btnStopVoiceToText);
        txtMessage = findViewById(R.id.txtMessage);
        imgCapturedImagePreview = findViewById(R.id.imgCapturedImagePreview);
        vidCapturedVideoPreview = findViewById(R.id.vidCapturedVideoPreview);


        if(userWantsToSendTheirAudioRecording) {
            ActivityCompat.requestPermissions(AddMessageToMediaActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_SPEECH_INPUT_RESULT);
        }
        setBtnStartVoiceToTextOnClick();
        setBtnStopVoiceToTextOnClick();
        btnStopVoiceToText.setVisibility(View.INVISIBLE);


        setBtnSelectRecipientsActivityOnClick();
        setVideoViewListener();
        previewCapturedMedia(mTypeOfMediaCaptured, mMediaPath);

    }


    private void setBtnSelectRecipientsActivityOnClick(){
        btnSelectRecipientsActivity.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mMessage = txtMessage.getText().toString();

                Intent intent = new Intent(getApplicationContext(), SendMediaTabsActivity.class);
                intent.putExtra("typeOfMediaCaptured", mTypeOfMediaCaptured);
                intent.putExtra("mediaPath", mMediaPath);
                intent.putExtra("message", mMessage);

                startActivity(intent);
            }
        });
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
                //TODO:
                //convert the file just recorded into text
                //or at the same time of recording, gather words

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

                    Picasso.with(this).load(mediaFile).into(imgCapturedImagePreview);

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