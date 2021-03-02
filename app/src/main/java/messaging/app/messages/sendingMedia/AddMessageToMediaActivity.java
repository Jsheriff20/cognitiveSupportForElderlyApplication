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

    private static final int REQUEST_RECORD_AUDIO_PERMISSION_RESULT = 103;
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
    String mFileName;
    private boolean userWantsToSendTheirAudioRecording = false;
    private boolean permissionToRecordAccepted = false;
    private SpeechRecognizer speechRecognizer;

    MediaRecorder mediaRecorder = null;

    MediaManagement mediaManagement = new MediaManagement();

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
            ActivityCompat.requestPermissions(AddMessageToMediaActivity.this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_RECORD_AUDIO_PERMISSION_RESULT);
            setBtnStartVoiceToTextUsingCustomOnClick();
        }
        else{
            setBtnVoiceToTextUsingGoogleOnClick();
        }

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


    private void setBtnVoiceToTextUsingGoogleOnClick(){
        btnVoiceToText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordAudioForText();
            }
        });
    }

    private void setBtnStartVoiceToTextUsingCustomOnClick(){
        btnVoiceToText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                recordVoice(true);

                btnVoiceToText.setVisibility(View.INVISIBLE);
                btnStopVoiceToText.setVisibility(View.INVISIBLE);


            }
        });
    }


    private void setupSpeechRecorder(){
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(AddMessageToMediaActivity.this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

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
                ArrayList<String> matches = results.getStringArrayList(speechRecognizer.RESULTS_RECOGNITION);
                String string = "";
                txtMessage.setText("");
                if (matches != null){
                    string = matches.get(0);
                    txtMessage.setText(string);
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });
    }


    private void setBtnStopVoiceToTextOnClick(){
        btnStopVoiceToText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordVoice(false);
                //TODO:
                //convert the file just recorded into text
                //or at the same time of recording, gather words

            }
        });
    }


    private void recordAudioForText() {
        //load intent to record voice
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        getIntent().putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say your message");

        //todo:
        //need to set a time limit

        //increase the amount of time before stopping
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, new Long(2500));

        //start intent
        try{
            startActivityForResult(intent, REQUEST_SPEECH_INPUT_RESULT);
        } catch (Exception exception) {
            Toast.makeText(this, "Error occurred", LENGTH_SHORT).show();
            exception.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_SPEECH_INPUT_RESULT){
            if(resultCode == RESULT_OK && data != null){

                //get text from audio and display
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                txtMessage.setText(result.get(0));
            }
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
                        int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                        int rotationInDegrees = mediaManagement.exifToDegrees(rotation);

                        Log.d("Test", "rotationInDegrees: " + rotationInDegrees);

                        Picasso.with(this).load(mediaFile).into(imgCapturedImagePreview);

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
            case REQUEST_RECORD_AUDIO_PERMISSION_RESULT:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();
    }


    private void recordVoice(boolean start) {
        if (start) {
            startRecordingVoice();
        } else {
            stopRecordingVoice();
        }
    }


    private void startRecordingVoice() {

        mFileName = getExternalCacheDir().getAbsolutePath();
        mFileName += "/tempAudioRecording.3gp";

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setAudioSamplingRate(44100);
        mediaRecorder.setAudioEncodingBitRate(96000); //change to 128000 if needed
        mediaRecorder.setOutputFile(mFileName);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaRecorder.start();
    }


    private void stopRecordingVoice() {
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
    }
}