package messaging.app.messages.friendsList;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import messaging.app.CheckInputsValidity;
import messaging.app.ManagingActivityPreview;
import messaging.app.R;
import messaging.app.contactingFirebase.ManagingFriends;
import messaging.app.contactingFirebase.QueryingDatabase;
import messaging.app.messages.capturingMedia.CaptureActivity;

public class EditFriendActivity extends AppCompatActivity {

    String mFriendsName;
    String mFriendsRelationship;
    String mFriendsUUID;
    String mFriendsUsername;
    String mFriendsProfileImageUrl;
    int mFriendsProfileImageRotation;

    ImageView imgProfileImage;
    TextView lblFullName;
    TextView lblUsername;
    EditText txtRelationship;
    Button btnUpdate;
    Button btnRemove;
    Button btnBlock;
    ImageButton btnBackToFriendsList;

    CheckInputsValidity checkInputsValidity = new CheckInputsValidity(this);
    ManagingActivityPreview managingActivityPreview = new ManagingActivityPreview();;
    ManagingFriends managingFriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_friend);

        mFriendsName = getIntent().getStringExtra("name");
        mFriendsRelationship = getIntent().getStringExtra("relationship");
        mFriendsUUID = getIntent().getStringExtra("UUID");
        mFriendsUsername = getIntent().getStringExtra("username");
        mFriendsProfileImageUrl = getIntent().getStringExtra("profileImageUrl");
        mFriendsProfileImageRotation = getIntent().getIntExtra("profileImageRotation", 0);


        if(getIntent().getStringExtra("adminUUID") != null){
            String friendsUUID = getIntent().getStringExtra("adminUUID");
            managingFriends = new ManagingFriends(this, friendsUUID);
        }
        else{
            managingFriends = new ManagingFriends(this, null);
        }

        imgProfileImage = findViewById(R.id.imgEditFriendsProfileImage);
        lblFullName = findViewById(R.id.lblFriendsFullName);
        lblUsername = findViewById(R.id.lblUsername);
        txtRelationship = findViewById(R.id.txtEditRelationship);
        btnUpdate = findViewById(R.id.btnUpdateFriend);
        btnRemove = findViewById(R.id.btnRemoveFriend);
        btnBlock = findViewById(R.id.btnBlockFriend);
        btnBackToFriendsList = findViewById(R.id.btnBackToFriendsList);

        lblFullName.setText(mFriendsName);
        lblUsername.setText(mFriendsUsername);
        txtRelationship.setText(mFriendsRelationship);
        Picasso.with(this).load(mFriendsProfileImageUrl).rotate(mFriendsProfileImageRotation).into(imgProfileImage);

        setBtnRemoveOnClick();
        setBtnUpdateOnClick();
        setBtnBlockOnClick();
        setBtnBackToFriendsList();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EditFriendActivity.this, ViewFriendsListActivity.class);
        intent.putExtra("adminUUID", getIntent().getStringExtra("adminUUID"));
        EditFriendActivity.this.startActivity(intent);
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            managingActivityPreview.hideSystemUI(getWindow().getDecorView());
        }
    }


    private void setBtnBackToFriendsList(){
        btnBackToFriendsList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditFriendActivity.this, ViewFriendsListActivity.class);
                intent.putExtra("adminUUID", getIntent().getStringExtra("adminUUID"));
                EditFriendActivity.this.startActivity(intent);
            }
        });
    }


    private void setBtnRemoveOnClick() {
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                managingFriends.removeFriend(mFriendsUUID);
                //take user back
                btnBackToFriendsList.callOnClick();
            }
        });
    }


    private void setBtnUpdateOnClick() {
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String updatedRelationship = txtRelationship.getText().toString();
                if (updatedRelationship.equals(mFriendsRelationship)) {
                    Toast.makeText(EditFriendActivity.this, "You have not updated the relationship", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!checkInputsValidity.isRelationshipValid(mFriendsRelationship)) {
                    return;
                }
                managingFriends.updateFriendRelationship(mFriendsUUID, updatedRelationship, new ManagingFriends.OnUpdateFriendRelationshipListener() {
                    @Override
                    public void onSuccess(boolean success) {
                        if (success) {
                            Toast.makeText(EditFriendActivity.this, "The relationship has been updated", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Toast.makeText(EditFriendActivity.this, "The relationship failed to update", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void setBtnBlockOnClick(){
        btnBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                managingFriends.blockFriend(mFriendsUUID, mFriendsUsername);
                //take user back
                btnBackToFriendsList.callOnClick();
            }
        });
    }
}