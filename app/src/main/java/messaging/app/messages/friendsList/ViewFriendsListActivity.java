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
import messaging.app.settings.SettingsActivity;

public class ViewFriendsListActivity extends AppCompatActivity {

    Button btnLoadAddFriendActivity;
    ImageButton btnBackToMessagesActivity;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    ManagingActivityPreview mManagingActivityPreview = new ManagingActivityPreview();
    QueryingDatabase mQueryingDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_friends_list);

        btnLoadAddFriendActivity = findViewById(R.id.btnAddFriend);
        btnBackToMessagesActivity = findViewById(R.id.btnBackToMessagesActivity);
        mRecyclerView = findViewById(R.id.lstFriendsList);

        if (getIntent().getStringExtra("adminUUID") != null) {
            String friendsUUID = getIntent().getStringExtra("adminUUID");
            mQueryingDatabase = new QueryingDatabase(friendsUUID);
        } else {
            mQueryingDatabase = new QueryingDatabase(null);
        }

        setBtnLoadAddFriendActivityOnClick();
        displayFriends();
        setBtnBackToMessagesActivity();
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            mManagingActivityPreview.hideSystemUI(getWindow().getDecorView());
        }
    }


    private void setBtnBackToMessagesActivity() {
        btnBackToMessagesActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (getIntent().getStringExtra("adminUUID") != null) {
                    Intent intent = new Intent(ViewFriendsListActivity.this, SettingsActivity.class);
                    if (getIntent().getStringExtra("adminUUID") != null) {
                        intent.putExtra("adminUUID", getIntent().getStringExtra("adminUUID"));
                    }
                    ViewFriendsListActivity.this.startActivity(intent);
                } else {
                    Intent intent = new Intent(ViewFriendsListActivity.this, MessagesActivity.class);
                    intent.putExtra("adminUUID", getIntent().getStringExtra("adminUUID"));
                    ViewFriendsListActivity.this.startActivity(intent);
                }
            }
        });
    }


    private void displayFriends() {
        mQueryingDatabase.getFriendsDetails(new QueryingDatabase.OnGetFriendsDetailsListener() {
            @Override
            public void onSuccess(List friendsDetails) {
                //display to user
                mLayoutManager = new LinearLayoutManager(ViewFriendsListActivity.this);
                mAdapter = new ViewFriendsListAdapter(friendsDetails, ViewFriendsListActivity.this);

                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setAdapter(mAdapter);
            }
        });
    }

    private void setBtnLoadAddFriendActivityOnClick() {
        btnLoadAddFriendActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewFriendsListActivity.this, AddFriendActivity.class);
                if (getIntent().getStringExtra("adminUUID") != null) {
                    intent.putExtra("adminUUID", getIntent().getStringExtra("adminUUID"));
                }
                ViewFriendsListActivity.this.startActivity(intent);
            }
        });
    }
}