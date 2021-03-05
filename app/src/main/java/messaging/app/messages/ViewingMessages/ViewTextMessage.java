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
import messaging.app.R;
import messaging.app.messages.capturingMedia.CaptureActivity;

public class ViewTextMessage extends AppCompatActivity {
    Intent intent;
    MessageData displayingMessage;

    TextView lblMessageFromName;
    TextView lblDisplayingMessage;
    TextView lblReceivedTime;
    Button btnViewMediaMessage;

    Formatting formatting = new Formatting();

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
        Log.d("test", "onCreate: " + displayingMessage.getFullName());
        lblMessageFromName.setText(displayingMessage.getFullName());
        lblDisplayingMessage.setText(displayingMessage.getTextMessage());

        String formattedTimeAgo = formatting.howLongAgo(displayingMessage.getTimeStamp());
        lblReceivedTime.setText(formattedTimeAgo + " Ago");

        setBtnViewMediaMessageOnClick();
    }

    private void setBtnViewMediaMessageOnClick(){
        btnViewMediaMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(ViewTextMessage.this, ViewMediaMessage.class);
                newIntent.putExtras(intent.getExtras());
                ViewTextMessage.this.startActivity(newIntent);
            }
        });
    }
}