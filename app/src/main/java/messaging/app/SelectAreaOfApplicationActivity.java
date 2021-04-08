package messaging.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import messaging.app.games.SelectGameActivity;
import messaging.app.messages.MessagesActivity;
import messaging.app.messages.friendsList.ViewFriendsListActivity;
import messaging.app.settings.SettingsActivity;

public class SelectAreaOfApplicationActivity extends AppCompatActivity {

    LinearLayoutCompat llayMessages;
    LinearLayoutCompat llayFriends;
    LinearLayoutCompat llayGames;
    LinearLayoutCompat llaySettings;

    ManagingActivityPreview mManagingActivityPreview = new ManagingActivityPreview();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_area_of_application_activity);

        llayMessages = findViewById(R.id.llayMessages);
        llayFriends = findViewById(R.id.llayFriends);
        llayGames = findViewById(R.id.llayGames);
        llaySettings = findViewById(R.id.llaySettings);

        setLayoutButtonOnClick();

    }

    @Override
    protected void onStart() {
        super.onStart();
        //start notification service
        startService(new Intent(getApplicationContext(), NotifyMessageReceivedService.class));
    }

    @Override
    public void onBackPressed() {
        //exit the app
        finish();
        System.exit(0);
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            mManagingActivityPreview.hideSystemUI(getWindow().getDecorView());
        }
    }


    private void setLayoutButtonOnClick() {
        llayMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectAreaOfApplicationActivity.this
                        , MessagesActivity.class);
                SelectAreaOfApplicationActivity.this.startActivity(intent);
            }
        });


        llayFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectAreaOfApplicationActivity.this,
                        ViewFriendsListActivity.class);
                SelectAreaOfApplicationActivity.this.startActivity(intent);
            }
        });


        llayGames.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectAreaOfApplicationActivity.this,
                        SelectGameActivity.class);
                SelectAreaOfApplicationActivity.this.startActivity(intent);
            }
        });


        llaySettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectAreaOfApplicationActivity.this,
                        SettingsActivity.class);
                SelectAreaOfApplicationActivity.this.startActivity(intent);
            }
        });
    }
}
