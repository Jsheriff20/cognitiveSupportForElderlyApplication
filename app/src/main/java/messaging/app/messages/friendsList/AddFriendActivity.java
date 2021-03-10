package messaging.app.messages.friendsList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.List;

import messaging.app.CheckInputsValidity;
import messaging.app.ContactingFirebase;
import messaging.app.R;
import messaging.app.login.LoginActivity;
import messaging.app.login.ResetPasswordActivity;

public class AddFriendActivity extends AppCompatActivity {

    EditText txtAddingUsername;
    EditText txtRelationship;
    ImageButton btnSearchFriend;
    ImageButton btnBackToFriendsList;
    ImageButton btnRefreshFriendRequests;
    private RecyclerView recyclerView;

    CheckInputsValidity checkInputsValidity = new CheckInputsValidity(this);
    ContactingFirebase contactingFirebase = new ContactingFirebase(this);

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        txtAddingUsername = findViewById(R.id.txtAddingUsername);
        btnSearchFriend = findViewById(R.id.btnSearchFriend);
        txtRelationship = findViewById(R.id.txtReceivedFriendRequestRelationship);
        recyclerView = findViewById(R.id.lstAddFriend);
        btnBackToFriendsList = findViewById(R.id.btnBackToFriendsList);
        btnRefreshFriendRequests = findViewById(R.id.btnRefreshFriendRequests);

        setBtnSearchFriendOnClick();
        displayFriendRequests();
        setBtnBackToFriendsList();
        setBtnRefreshFriendRequests();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AddFriendActivity.this, ViewFriendsListActivity.class);
        AddFriendActivity.this.startActivity(intent);
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
                AddFriendActivity.this.startActivity(intent);
            }
        });
    }

    private void displayFriendRequests() {
        contactingFirebase.getReceivedFriendRequests(new ContactingFirebase.OnGetReceivedFriendRequestsListener() {
            @Override
            public void onSuccess(final List receivedFriendRequests) {
                contactingFirebase.getSentFriendRequests(new ContactingFirebase.OnGetSentFriendRequestsListener() {
                    @Override
                    public void onSuccess(List sentFriendRequests) {

                        //display to user
                        mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        mAdapter = new AddFriendsAdapter(sentFriendRequests, receivedFriendRequests, getApplicationContext());

                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setAdapter(mAdapter);
                    }
                });
            }
        });
    }

    private void setBtnSearchFriendOnClick(){
        btnSearchFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = txtAddingUsername.getText().toString();
                String relationship = txtRelationship.getText().toString();

                if(checkInputsValidity.isUsernameValid(username) && checkInputsValidity.isRelationshipValid(relationship)){
                    contactingFirebase.addFriend(username, relationship);
                }
            }
        });
    }

}