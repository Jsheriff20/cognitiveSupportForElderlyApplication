package messaging.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SendMediaFile extends AppCompatActivity {

    String pathToMedia;
    List<friendsDetails> friendsDetailsList = new ArrayList<friendsDetails>();

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public void sendMediaFile(String pathToCapturedMedia){
        pathToMedia = pathToCapturedMedia;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_media_file);

        recyclerView = findViewById(R.id.lstFriendsList);
        recyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new friendsListAdapter(friendsDetailsList, this);
        recyclerView.setAdapter(mAdapter);

        getFriendsDetailsList();
    }


    private void getFriendsDetailsList() {
        //TODO:
        //replace this with a for loop that gets all details from a database
        friendsDetails friend1 = new friendsDetails(0, "Jack", "Grandson", "https://www.w3schools.com/howto/img_avatar.png");
        friendsDetails friend2 = new friendsDetails(0, "Paul", "Child", "https://www.w3schools.com/howto/img_avatar.png");
        friendsDetails friend3 = new friendsDetails(0, "Brady", "Uncle", "https://www.w3schools.com/howto/img_avatar.png");
        friendsDetails friend4 = new friendsDetails(0, "Dawid", "Dad", "https://www.w3schools.com/howto/img_avatar.png");
        friendsDetails friend5 = new friendsDetails(0, "Sam", "Grandson", "https://www.w3schools.com/howto/img_avatar.png");
        friendsDetails friend6 = new friendsDetails(0, "Mel", "Child", "https://www.w3schools.com/howto/img_avatar.png");

        friendsDetailsList.addAll(Arrays.asList(new friendsDetails[] {friend1, friend2, friend3, friend4, friend5, friend6}));
    }
}
