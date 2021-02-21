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
import messaging.app.R;

import static android.widget.Toast.LENGTH_SHORT;

public class SendMediaFileActivity extends AppCompatActivity {

    String pathToMedia;
    List<AccountDetails> friendsDetailsList = new ArrayList<AccountDetails>();

    private RecyclerView recyclerView;
    private SendMediaFriendsListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ImageButton btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_media_friends_list);

        pathToMedia = getIntent().getExtras().getString("mediaPath");

        btnSend = (ImageButton) findViewById(R.id.btnPendingFriends);
        recyclerView = findViewById(R.id.lstFriendsList);

        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new SendMediaFriendsListAdapter(friendsDetailsList, this);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);

        getFriendsDetailsList();

        btnSendOnClick();
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


    private void getFriendsDetailsList() {
        //TODO:
        //replace this with a for loop that gets all details from a database
        AccountDetails friend1 = new AccountDetails("0", "Jack", "Sheriff", "Grandson", "https://www.w3schools.com/howto/img_avatar.png", 0, "Test" );
        AccountDetails friend2 = new AccountDetails("0", "Jack", "Sheriff", "Grandson", "https://www.w3schools.com/howto/img_avatar.png", 0, "Test" );
        AccountDetails friend3 = new AccountDetails("0", "Jack", "Sheriff", "Grandson", "https://www.w3schools.com/howto/img_avatar.png", 0, "Test" );
        AccountDetails friend4 = new AccountDetails("0", "Jack", "Sheriff", "Grandson", "https://www.w3schools.com/howto/img_avatar.png", 0, "Test" );
        AccountDetails friend5 = new AccountDetails("0", "Jack", "Sheriff", "Grandson", "https://www.w3schools.com/howto/img_avatar.png", 0, "Test" );
        AccountDetails friend6 = new AccountDetails("0", "Jack", "Sheriff", "Grandson", "https://www.w3schools.com/howto/img_avatar.png", 0, "Test" );
        friendsDetailsList.addAll(Arrays.asList(new AccountDetails[] {friend1, friend2, friend3, friend4, friend5, friend6}));
    }



}
