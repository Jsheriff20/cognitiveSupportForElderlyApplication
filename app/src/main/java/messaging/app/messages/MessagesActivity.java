package messaging.app.messages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import messaging.app.ManagingActivityPreview;
import messaging.app.R;
import messaging.app.SelectAreaOfApplicationActivity;
import messaging.app.messages.ViewingMessages.ListOfReceivedMediaActivity;
import messaging.app.messages.ViewingMessages.ViewTextMessageActivity;
import messaging.app.messages.capturingMedia.CaptureActivity;
import messaging.app.messages.friendsList.ViewFriendsListActivity;

public class MessagesActivity extends AppCompatActivity {

    LinearLayoutCompat llayFriends;
    LinearLayoutCompat llaySendMessages;
    LinearLayoutCompat llayViewMessages;
    LinearLayoutCompat llayAppOptions;

    ManagingActivityPreview managingActivityPreview = new ManagingActivityPreview();

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

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(MessagesActivity.this, SelectAreaOfApplicationActivity.class);
        MessagesActivity.this.startActivity(intent);
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            managingActivityPreview.hideSystemUI(getWindow().getDecorView());
        }
    }


    private void setLayoutButtonOnClick(){
        llaySendMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessagesActivity.this, CaptureActivity.class);
                intent.putExtra("captureForProfileImage", false);
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
                Intent intent = new Intent(MessagesActivity.this, ListOfReceivedMediaActivity.class);
                MessagesActivity.this.startActivity(intent);
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