package messaging.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SendMediaFile extends AppCompatActivity {

    String pathToMedia;
    List<friendsDetails> friendsDetailsList = new ArrayList<friendsDetails>();

    private RecyclerView recyclerView;
    private friendsListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ImageButton btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_media_file);

        pathToMedia = getIntent().getExtras().getString("mediaPath");

        btnSend = (ImageButton) findViewById(R.id.btnSend);
        recyclerView = findViewById(R.id.lstFriendsList);

        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new friendsListAdapter(friendsDetailsList, this);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);

        getFriendsDetailsList();

        btnSendOnClick();
    }

    private void sendMedia(List<Integer> recipientIDs){

        Log.d("Test", "sent Media to : " + recipientIDs);

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
        friendsDetails friend1 = new friendsDetails(0, "Jack", "Grandson", "https://www.w3schools.com/howto/img_avatar.png");
        friendsDetails friend2 = new friendsDetails(1, "Paul", "Child", "https://www.w3schools.com/howto/img_avatar.png");
        friendsDetails friend3 = new friendsDetails(2, "Brady", "Uncle", "https://www.w3schools.com/howto/img_avatar.png");
        friendsDetails friend4 = new friendsDetails(3, "Dawid", "Dad", "https://www.w3schools.com/howto/img_avatar.png");
        friendsDetails friend5 = new friendsDetails(4, "Sam", "Grandson", "https://www.w3schools.com/howto/img_avatar.png");
        friendsDetails friend6 = new friendsDetails(5, "Mel", "Child", "https://www.w3schools.com/howto/img_avatar.png");

        friendsDetailsList.addAll(Arrays.asList(new friendsDetails[] {friend1, friend2, friend3, friend4, friend5, friend6}));
    }



}
