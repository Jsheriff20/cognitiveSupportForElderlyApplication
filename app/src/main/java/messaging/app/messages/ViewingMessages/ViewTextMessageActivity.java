package messaging.app.messages.ViewingMessages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import messaging.app.Formatting;
import messaging.app.ManagingActivityPreview;
import messaging.app.R;

public class ViewTextMessageActivity extends AppCompatActivity {
    Intent intent;
    MessageData displayingMessage;

    TextView lblMessageFromName;
    TextView lblDisplayingMessage;
    TextView lblReceivedTime;
    Button btnViewMediaMessage;

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