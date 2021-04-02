package messaging.app.messages.friendsList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.List;

import messaging.app.CheckInputsValidity;
import messaging.app.ManagingActivityPreview;
import messaging.app.R;
import messaging.app.contactingFirebase.ManagingFriends;
import messaging.app.contactingFirebase.QueryingDatabase;
import messaging.app.messages.capturingMedia.CaptureActivity;

public class AddFriendActivity extends AppCompatActivity {

    EditText txtAddingUsername;
    EditText txtRelationship;
    ImageButton btnSendFriendRequest;
    ImageButton btnBackToFriendsList;
    ImageButton btnRefreshFriendRequests;
    private RecyclerView recyclerView;

    CheckInputsValidity checkInputsValidity = new CheckInputsValidity(this);
    ManagingActivityPreview managingActivityPreview = new ManagingActivityPreview();
    QueryingDatabase queryingDatabase;
    ManagingFriends managingFriends;

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        txtAddingUsername = findViewById(R.id.txtAddingUsername);
        btnSendFriendRequest = findViewById(R.id.btnSendFriendRequest);
        txtRelationship = findViewById(R.id.txtReceivedFriendRequestRelationship);
        recyclerView = findViewById(R.id.lstAddFriend);
        btnBackToFriendsList = findViewById(R.id.btnBackToFriendsList);
        btnRefreshFriendRequests = findViewById(R.id.btnRefreshFriendRequests);

        if(getIntent().getStringExtra("adminUUID") != null){
            String friendsUUID = getIntent().getStringExtra("adminUUID");

            managingFriends = new ManagingFriends(this, friendsUUID);
            queryingDatabase = new QueryingDatabase(friendsUUID);
        }
        else{
            managingFriends = new ManagingFriends(this, null);
            queryingDatabase = new QueryingDatabase(null);
        }

        setBtnSearchFriendOnClick();
        displayFriendRequests();
        setBtnBackToFriendsList();
        setBtnRefreshFriendRequests();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AddFriendActivity.this, ViewFriendsListActivity.class);
        if(getIntent().getStringExtra("adminUUID") != null){
            intent.putExtra("adminUUID", getIntent().getStringExtra("adminUUID"));
        }
        AddFriendActivity.this.startActivity(intent);
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            managingActivityPreview.hideSystemUI(getWindow().getDecorView());
        }
    }


    private void setBtnRefreshFriendRequests(){
        btnRefreshFriendRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(getIntent());
            }
        });
    }

    private void setBtnBackToFriendsList(){
        btnBackToFriendsList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddFriendActivity.this, ViewFriendsListActivity.class);
                if(getIntent().getStringExtra("adminUUID") != null){
                    intent.putExtra("adminUUID", getIntent().getStringExtra("adminUUID"));
                }
                AddFriendActivity.this.startActivity(intent);
            }
        });
    }

    private void displayFriendRequests() {
        queryingDatabase.getReceivedFriendRequests(new QueryingDatabase.OnGetReceivedFriendRequestsListener() {
            @Override
            public void onSuccess(final List receivedFriendRequests) {
                queryingDatabase.getSentFriendRequests(new QueryingDatabase.OnGetSentFriendRequestsListener() {
                    @Override
                    public void onSuccess(List sentFriendRequests) {

                        //display to user
                        mLayoutManager = new LinearLayoutManager(AddFriendActivity.this);
                        mAdapter = new AddFriendsAdapter(sentFriendRequests, receivedFriendRequests, AddFriendActivity.this);

                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setAdapter(mAdapter);
                    }
                });
            }
        });
    }

    private void setBtnSearchFriendOnClick(){
        btnSendFriendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = txtAddingUsername.getText().toString();
                String relationship = txtRelationship.getText().toString();

                if(checkInputsValidity.isUsernameValid(username) && checkInputsValidity.isRelationshipValid(relationship)){
                    managingFriends.addFriend(username, relationship, new ManagingFriends.OnAddFriendListener() {
                        @Override
                        public void onSuccess(boolean requestSentSuccessfully) {
                            if(requestSentSuccessfully){
                                finish();
                                startActivity(getIntent());
                            }
                            else{
                                Toast.makeText(AddFriendActivity.this, "Failed to send Friend Request", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

}