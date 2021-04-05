package messaging.app.settings.alarms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.List;

import messaging.app.R;
import messaging.app.contactingFirebase.QueryingDatabase;
import messaging.app.messages.friendsList.EditFriendActivity;
import messaging.app.messages.friendsList.ViewFriendsListActivity;
import messaging.app.messages.viewingMessages.ListOfReceivedMediaActivity;
import messaging.app.messages.viewingMessages.ViewingMessagesReceivedAdapter;
import messaging.app.settings.SettingsActivity;

public class ManageRemindersActivity extends AppCompatActivity {

    ImageButton btnBackToSettings;
    Button btnCreateNewReminder;
    RecyclerView lstCurrentReminders;

    private RemindersAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    QueryingDatabase queryingDatabase = new QueryingDatabase(null);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_reminder);

        lstCurrentReminders = findViewById(R.id.lstCurrentReminders);
        btnBackToSettings = findViewById(R.id.btnBackToSettings);
        btnCreateNewReminder = findViewById(R.id.btnCreateNewReminder);


        setupLstCurrentReminders();
        setBtnBackToSettingsOnClick();
        setBtnCreateNewReminderOnClick();

    }

    private void setBtnCreateNewReminderOnClick() {
        btnCreateNewReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManageRemindersActivity.this, CreateNewReminder.class);
                ManageRemindersActivity.this.startActivity(intent);
            }
        });
    }


    private void setupLstCurrentReminders() {
        queryingDatabase.getAllReminders(new QueryingDatabase.OnGetAllRemindersListener() {
            @Override
            public void onSuccess(List<ReminderDetails> reminderDetailsList) {

                //display to user
                mLayoutManager = new LinearLayoutManager(ManageRemindersActivity.this);
                mAdapter = new RemindersAdapter(reminderDetailsList, ManageRemindersActivity.this);

                lstCurrentReminders.setLayoutManager(mLayoutManager);
                lstCurrentReminders.setHasFixedSize(true);
                lstCurrentReminders.setAdapter(mAdapter);

            }
        });
    }



    private void setBtnBackToSettingsOnClick(){
        btnBackToSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ManageRemindersActivity.this, SettingsActivity.class);
                ManageRemindersActivity.this.startActivity(intent);
            }
        });
    }
}