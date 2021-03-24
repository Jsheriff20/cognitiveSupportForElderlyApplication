package messaging.app.messages.friendsList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.List;

import messaging.app.ManagingActivityPreview;
import messaging.app.R;
import messaging.app.contactingFirebase.QueryingDatabase;
import messaging.app.messages.MessagesActivity;

public class ViewFriendsListActivity extends AppCompatActivity {

    Button btnLoadAddFriendActivity;
    ImageButton btnBackToMessagesActivity;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ManagingActivityPreview managingActivityPreview = new ManagingActivityPreview();
    QueryingDatabase queryingDatabase = new QueryingDatabase();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_friends_list);

        btnLoadAddFriendActivity = findViewById(R.id.btnAddFriend);
        btnBackToMessagesActivity = findViewById(R.id.btnBackToMessagesActivity);
        recyclerView = findViewById(R.id.lstFriendsList);

        setBtnLoadAddFriendActivityOnClick();
        displayFriends();
        setBtnBackToMessagesActivity();
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            managingActivityPreview.hideSystemUI(getWindow().getDecorView());
        }
    }


    private void setBtnBackToMessagesActivity(){
        btnBackToMessagesActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewFriendsListActivity.this, MessagesActivity.class);
                ViewFriendsListActivity.this.startActivity(intent);
            }
        });
    }


    private void displayFriends() {
        queryingDatabase.getFriendsDetails(new QueryingDatabase.OnGetFriendsDetailsListener() {
            @Override
            public void onSuccess(List friendsDetails) {
                //display to user
                mLayoutManager = new LinearLayoutManager(getApplicationContext());
                mAdapter = new ViewFriendsListAdapter(friendsDetails, getApplicationContext());

                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(mAdapter);
            }
        });
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