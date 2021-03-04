package messaging.app.messages.ViewingMessages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import messaging.app.ContactingFirebase;
import messaging.app.R;

public class ListOfReceivedMediaActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ViewingMessagesAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ContactingFirebase contactingFirebase = new ContactingFirebase(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_conversations);

        recyclerView = findViewById(R.id.lstConversations);

        displayConversations();
    }

    private void displayConversations() {
        contactingFirebase.getExistingReceivedMediaDetails(new ContactingFirebase.OnGetExistingReceivedMediaDetailsListener() {
            @Override
            public void onSuccess(List<HashMap<String, String>> receivedMediaDetails, int numberOfStories) {

                //display to user
                mLayoutManager = new LinearLayoutManager(getApplicationContext());
                mAdapter = new ViewingMessagesAdapter(receivedMediaDetails, numberOfStories, getApplicationContext());

                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(mAdapter);

            }
        });
    }
}