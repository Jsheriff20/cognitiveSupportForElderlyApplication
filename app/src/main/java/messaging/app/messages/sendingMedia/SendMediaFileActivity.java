package messaging.app.messages.sendingMedia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import messaging.app.AccountDetails;
import messaging.app.ContactingFirebase;
import messaging.app.R;
import messaging.app.messages.friendsList.ViewFriendsListAdapter;

import static android.widget.Toast.LENGTH_SHORT;

public class SendMediaFileActivity extends AppCompatActivity {

    String pathToMedia;

    private RecyclerView recyclerView;
    private SendMediaFriendsListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ImageButton btnSend;

    ContactingFirebase contactingFirebase = new ContactingFirebase(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_media_friends_list);

        pathToMedia = getIntent().getExtras().getString("mediaPath");

        btnSend = (ImageButton) findViewById(R.id.btnPendingFriends);
        recyclerView = findViewById(R.id.lstFriendsList);

        mLayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);

        displayFriendsDetailsList();

        btnSendOnClick();
    }


    private void displayFriendsDetailsList() {
        contactingFirebase.getFriendsDetails(new ContactingFirebase.OnGetFriendsDetailsListener() {
            @Override
            public void onSuccess(List friendsDetailsList) {
                //display to user
                mAdapter = new SendMediaFriendsListAdapter(friendsDetailsList, getApplicationContext());

                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(mAdapter);
            }
        });
    }


    private void sendMedia(List<Integer> recipientIDs){

        Log.d("Test", "sent Media to : " + recipientIDs);
        deleteMediaFile(pathToMedia);
    }


    public void deleteMediaFile(String path){
        File file = new File(path);
        boolean deleted = file.delete();

        if(!deleted){
            Toast.makeText(this, "Error Deleting file", LENGTH_SHORT).show();
        }
        return;
    }


    private void btnSendOnClick(){
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //check that recipients have been selected
                if(mAdapter.mSelectedFriends.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please select recipients", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendMedia(mAdapter.mSelectedFriends);
            }
        });
    }




}
