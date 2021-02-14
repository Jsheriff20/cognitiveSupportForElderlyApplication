package messaging.app.messages.friendsList;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import messaging.app.R;
import messaging.app.messages.MessagesActivity;

public class ViewFriendsListActivity extends AppCompatActivity {

    ImageButton btnLoadAddFriendActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_friends_list);

        btnLoadAddFriendActivity = findViewById(R.id.btnAddFriend);

        setBtnLoadAddFriendActivityOnClick();

    }

    private void setBtnLoadAddFriendActivityOnClick(){
        btnLoadAddFriendActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewFriendsListActivity.this, AddFriendActivity.class);
                ViewFriendsListActivity.this.startActivity(intent);
            }
        });
    }
}