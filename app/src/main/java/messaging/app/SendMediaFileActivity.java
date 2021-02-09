package messaging.app;

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

import static android.widget.Toast.LENGTH_SHORT;

public class SendMediaFileActivity extends AppCompatActivity {

    String pathToMedia;
    List<FriendsDetails> friendsDetailsList = new ArrayList<FriendsDetails>();

    private RecyclerView recyclerView;
    private FriendsListAdapter mAdapter;
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
        mAdapter = new FriendsListAdapter(friendsDetailsList, this);

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
        FriendsDetails friend1 = new FriendsDetails(0, "Jack", "Grandson", "https://www.w3schools.com/howto/img_avatar.png");
        FriendsDetails friend2 = new FriendsDetails(1, "Paul", "Child", "https://www.w3schools.com/howto/img_avatar.png");
        FriendsDetails friend3 = new FriendsDetails(2, "Brady", "Uncle", "https://www.w3schools.com/howto/img_avatar.png");
        FriendsDetails friend4 = new FriendsDetails(3, "Dawid", "Dad", "https://www.w3schools.com/howto/img_avatar.png");
        FriendsDetails friend5 = new FriendsDetails(4, "Sam", "Grandson", "https://www.w3schools.com/howto/img_avatar.png");
        FriendsDetails friend6 = new FriendsDetails(5, "Mel", "Child", "https://www.w3schools.com/howto/img_avatar.png");

        friendsDetailsList.addAll(Arrays.asList(new FriendsDetails[] {friend1, friend2, friend3, friend4, friend5, friend6}));
    }



}
