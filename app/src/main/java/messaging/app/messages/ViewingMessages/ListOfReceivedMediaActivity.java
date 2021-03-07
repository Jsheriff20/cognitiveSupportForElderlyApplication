package messaging.app.messages.ViewingMessages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import messaging.app.ContactingFirebase;
import messaging.app.R;

public class ListOfReceivedMediaActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ViewingMessagesReceivedAdapter mAdapter;
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

                //reorder messages so most recent is displayed first
                receivedMediaDetails = orderReceivedMediaDetails(receivedMediaDetails);

                //display to user
                mLayoutManager = new LinearLayoutManager(getApplicationContext());
                mAdapter = new ViewingMessagesReceivedAdapter(receivedMediaDetails, numberOfStories, getApplicationContext());

                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(mAdapter);

            }
        });
    }

    private List<HashMap<String, String>> orderReceivedMediaDetails(List<HashMap<String, String>> receivedMediaDetails) {

        HashMap<Long, HashMap<String, String>> unorderedMap = new HashMap<>();
        List<HashMap<String, String>> sortedList = new ArrayList<>();

        for (Map<String, String> kvPair : receivedMediaDetails) {
            Long timestamp = Long.valueOf(kvPair.get("lastMessageTimeStamp"));
            unorderedMap.put(timestamp, (HashMap<String, String>) kvPair);
        }

        // TreeMap to store values of HashMap
        TreeMap<Long, Map<String, String>> sorted = new TreeMap<>();

        // Copy all data from hashMap into TreeMap
        sorted.putAll(unorderedMap);

        // Display the TreeMap which is naturally sorted
        for (Map.Entry<Long, Map<String, String>> entry : sorted.entrySet()) {
            sortedList.add((HashMap<String, String>) entry.getValue());
        }

        return sortedList;
    }

}