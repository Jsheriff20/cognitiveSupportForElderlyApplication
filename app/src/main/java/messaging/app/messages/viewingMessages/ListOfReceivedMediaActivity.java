package messaging.app.messages.viewingMessages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import java.util.HashMap;
import java.util.List;

import messaging.app.Formatting;
import messaging.app.ManagingActivityPreview;
import messaging.app.R;
import messaging.app.contactingFirebase.QueryingDatabase;
import messaging.app.messages.MessagesActivity;

public class ListOfReceivedMediaActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    ImageButton btnBackToMessagesActivity;
    ImageButton btnRefreshMessages;

    private ViewingMessagesReceivedAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    Formatting formatting = new Formatting();
    ManagingActivityPreview managingActivityPreview = new ManagingActivityPreview();
    QueryingDatabase queryingDatabase = new QueryingDatabase();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_conversations);

        recyclerView = findViewById(R.id.lstConversations);
        btnBackToMessagesActivity = findViewById(R.id.btnBackToMessagesActivity);
        btnRefreshMessages = findViewById(R.id.btnRefreshMessages);

        displayConversations();
        setBtnBackToMessagesActivity();
        setBtnRefreshMessages();
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            managingActivityPreview.hideSystemUI(getWindow().getDecorView());
        }
    }


    private void setBtnBackToMessagesActivity(){
        btnBackToMessagesActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListOfReceivedMediaActivity.this, MessagesActivity.class);
                ListOfReceivedMediaActivity.this.startActivity(intent);
            }
        });
    }

    
    private void setBtnRefreshMessages(){
        btnRefreshMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(getIntent());
            }
        });
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ListOfReceivedMediaActivity.this, MessagesActivity.class);
        ListOfReceivedMediaActivity.this.startActivity(intent);
    }

    private void displayConversations() {
        queryingDatabase.getExistingReceivedMediaDetails(this, new QueryingDatabase.OnGetExistingReceivedMediaDetailsListener() {
            @Override
            public void onSuccess(List<HashMap<String, String>> receivedMediaDetails, int numberOfStories) {

                //reorder messages so most recent is displayed first
                receivedMediaDetails = formatting.orderReceivedMediaDetails(receivedMediaDetails);

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