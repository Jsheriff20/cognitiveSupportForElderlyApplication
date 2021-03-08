package messaging.app.messages.ViewingMessages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import messaging.app.ContactingFirebase;
import messaging.app.Formatting;
import messaging.app.R;
import messaging.app.login.LoginActivity;
import messaging.app.login.ResetPasswordActivity;
import messaging.app.messages.MessagesActivity;

public class ListOfReceivedMediaActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ViewingMessagesReceivedAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ContactingFirebase contactingFirebase = new ContactingFirebase(this);
    Formatting formatting = new Formatting();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_conversations);

        recyclerView = findViewById(R.id.lstConversations);

        displayConversations();
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ListOfReceivedMediaActivity.this, MessagesActivity.class);
        ListOfReceivedMediaActivity.this.startActivity(intent);
    }

    private void displayConversations() {
        contactingFirebase.getExistingReceivedMediaDetails(new ContactingFirebase.OnGetExistingReceivedMediaDetailsListener() {
            @Override
            public void onSuccess(List<HashMap<String, String>> receivedMediaDetails, int numberOfStories) {
                Log.d("test", "receivedMediaDetails: " + receivedMediaDetails);

                //reorder messages so most recent is displayed first
//                receivedMediaDetails = formatting.orderReceivedMediaDetails(receivedMediaDetails);

                //display to user
                mLayoutManager = new LinearLayoutManager(getApplicationContext());
                mAdapter = new ViewingMessagesReceivedAdapter(receivedMediaDetails, numberOfStories, getApplicationContext());

                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(mAdapter);

            }
        });
    }

}