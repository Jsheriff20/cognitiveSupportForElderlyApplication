package messaging.app.messages.viewingMessages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import messaging.app.Formatting;
import messaging.app.ManagingActivityPreview;
import messaging.app.R;
import messaging.app.messages.friendsList.ViewFriendsListActivity;

public class ViewTextMessageActivity extends AppCompatActivity {
    Intent intent;
    MessageData displayingMessage;

    TextView lblMessageFromName;
    TextView lblDisplayingMessage;
    TextView lblReceivedTime;
    Button btnViewMediaMessage;
    ImageButton btnTextToSpeech;
    TextToSpeech textToSpeech;

    Formatting formatting = new Formatting();
    ManagingActivityPreview managingActivityPreview = new ManagingActivityPreview();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_text_message);

        lblMessageFromName = findViewById(R.id.lblMessageFromName);
        lblDisplayingMessage = findViewById(R.id.lblDisplayingMessage);
        lblReceivedTime = findViewById(R.id.lblReceivedTime);
        btnViewMediaMessage = findViewById(R.id.btnViewMediaMessage);
        btnTextToSpeech = findViewById(R.id.btnTextToSpeech);


        intent = getIntent();
        int messageNum = intent.getIntExtra("messageNum", -1);

        if(messageNum == -1){
            messageNum = 0;
            intent.putExtra("messageNum", 0);
        }


        ArrayList<Parcelable> messageList = intent.getParcelableArrayListExtra("messagesList");
        displayingMessage = (MessageData) messageList.get(messageNum);


        //display the "displayingMessage" data to the user
        lblMessageFromName.setText(displayingMessage.getFullName());
        lblDisplayingMessage.setText(displayingMessage.getTextMessage());

        String formattedTimeAgo = formatting.howLongAgo(displayingMessage.getTimeStamp());
        lblReceivedTime.setText(formattedTimeAgo + " Ago");

        setBtnViewMediaMessageOnClick();
        setBtnTextToSpeechOnClick();
        setupTextToSpeech();
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ViewTextMessageActivity.this, ListOfReceivedMediaActivity.class);
        ViewTextMessageActivity.this.startActivity(intent);
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            managingActivityPreview.hideSystemUI(getWindow().getDecorView());
        }
    }


    private void setupTextToSpeech(){
        textToSpeech = new TextToSpeech(ViewTextMessageActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i == TextToSpeech.SUCCESS){
                     textToSpeech.setLanguage(Locale.ENGLISH);
                }
            }
        });
    }


    private void setBtnTextToSpeechOnClick(){
        btnTextToSpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = lblDisplayingMessage.getText().toString();

                //convert text to speech
               textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            }
        });
    }


    private void setBtnViewMediaMessageOnClick(){
        btnViewMediaMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(ViewTextMessageActivity.this, ViewMediaMessageActivity.class);
                newIntent.putExtras(intent.getExtras());
                ViewTextMessageActivity.this.startActivity(newIntent);
            }
        });
    }
}