package messaging.app.messages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import messaging.app.R;
import messaging.app.SelectAreaOfApplicationActivity;
import messaging.app.messages.capturingMedia.CaptureActivity;
import messaging.app.messages.friendsList.ViewFriendsListActivity;
import messaging.app.messages.sendingMedia.SendMediaFriendsListActivity;

public class MessagesActivity extends AppCompatActivity {

    LinearLayoutCompat llayFriends;
    LinearLayoutCompat llaySendMessages;
    LinearLayoutCompat llayViewMessages;
    LinearLayoutCompat llayAppOptions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        llayFriends = findViewById(R.id.llayFriends);
        llaySendMessages = findViewById(R.id.llaySendMessages);
        llayViewMessages = findViewById(R.id.llayViewMessages);
        llayAppOptions = findViewById(R.id.llayAppOptions);

        setLayoutButtonOnClick();
    }


    private void setLayoutButtonOnClick(){
        llaySendMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessagesActivity.this, CaptureActivity.class);
                MessagesActivity.this.startActivity(intent);
            }
        });

        llayFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessagesActivity.this, ViewFriendsListActivity.class);
                MessagesActivity.this.startActivity(intent);
            }
        });

        llayViewMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO:
                //add activity to open
//                Intent intent = new Intent(MessagesActivity.this, .class);
//                MessagesActivity.this.startActivity(intent);
            }
        });

        llayAppOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessagesActivity.this, SelectAreaOfApplicationActivity.class);
                MessagesActivity.this.startActivity(intent);
            }
        });
    }
}