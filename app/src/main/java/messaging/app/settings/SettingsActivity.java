package messaging.app.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import messaging.app.NotifyMessageReceivedService;
import messaging.app.R;
import messaging.app.SelectAreaOfApplicationActivity;
import messaging.app.contactingFirebase.ManagingAccounts;
import messaging.app.contactingFirebase.QueryingDatabase;
import messaging.app.login.LoginActivity;
import messaging.app.messages.friendsList.ViewFriendsListActivity;
import messaging.app.settings.alarms.ManageRemindersActivity;

public class SettingsActivity extends AppCompatActivity {

    Button btnLogout;
    Button btnManageAdmins;
    Button btnManageAccount;
    Button btnManageMedicationReminders;
    TextView lblManageAnAccount;
    Spinner spnSelectAdminAccount;

    String mFriendsName = "";
    HashMap<String, String> mAdministeringFriends;

    ManagingAccounts mManagingAccounts = new ManagingAccounts(this);
    QueryingDatabase mQueryingDatabase = new QueryingDatabase(null);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        btnLogout = findViewById(R.id.btnLogout);
        btnManageAdmins = findViewById(R.id.btnManageAdmins);
        btnManageAccount = findViewById(R.id.btnManageAccount);
        btnManageMedicationReminders = findViewById(R.id.btnManageMedicationReminders);
        spnSelectAdminAccount = findViewById(R.id.spnSelectAdminAccount);
        lblManageAnAccount = findViewById(R.id.lblManageAnAccount);


        if (getIntent().getStringExtra("adminUUID") != null) {

            btnLogout.setVisibility(View.INVISIBLE);
            btnManageAccount.setVisibility(View.INVISIBLE);
            spnSelectAdminAccount.setVisibility(View.INVISIBLE);
            lblManageAnAccount.setVisibility(View.INVISIBLE);

            btnManageAdmins.setText("Exit account manager mode");

        }

        setBtnLogoutOnClick();
        setBtnAddAdminFriendOnClick();
        setBtnManageAccountOnClick();
        setBtnManageMedicationRemindersOnClick();
        setupSpinner();
    }

    @Override
    public void onBackPressed() {
        if (getIntent().getStringExtra("adminUUID") != null) {
            //get the UUID of the users name
            String friendsUUID = getIntent().getStringExtra("adminUUID");

            //load the friends activity (the only place an admin can currently moderate)
            Intent intent = new Intent(SettingsActivity.this, ViewFriendsListActivity.class);
            intent.putExtra("adminUUID", friendsUUID);
            SettingsActivity.this.startActivity(intent);
        } else {
            Intent intent = new Intent(SettingsActivity.this, SelectAreaOfApplicationActivity.class);
            SettingsActivity.this.startActivity(intent);
        }
    }


    public void setBtnManageMedicationRemindersOnClick() {
        btnManageMedicationReminders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, ManageRemindersActivity.class);
                SettingsActivity.this.startActivity(intent);
            }
        });
    }


    private void setBtnLogoutOnClick() {
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mManagingAccounts.logoutUser();
                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                SettingsActivity.this.startActivity(intent);

                //stop notification service
                stopService(new Intent(SettingsActivity.this, NotifyMessageReceivedService.class));
            }
        });
    }


    private void setBtnAddAdminFriendOnClick() {
        btnManageAdmins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getIntent().getStringExtra("adminUUID") != null) {

                    btnLogout.setVisibility(View.VISIBLE);
                    btnManageAccount.setVisibility(View.VISIBLE);
                    spnSelectAdminAccount.setVisibility(View.VISIBLE);
                    lblManageAnAccount.setVisibility(View.VISIBLE);

                    btnManageAdmins.setText("Manage Account Admins");

                    Toast.makeText(SettingsActivity.this, "Exited account manager mode", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(SettingsActivity.this, ManageAdminActivity.class);
                    SettingsActivity.this.startActivity(intent);
                }
            }
        });
    }


    private void setBtnManageAccountOnClick() {
        btnManageAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //get the UUID of the users name
                String friendsUUID = mAdministeringFriends.get(mFriendsName);

                //load the friends activity (the only place an admin can currently moderate)
                Intent intent = new Intent(SettingsActivity.this, ViewFriendsListActivity.class);
                intent.putExtra("adminUUID", friendsUUID);
                SettingsActivity.this.startActivity(intent);

            }
        });
    }


    private void setupSpinner() {

        mQueryingDatabase.getAdministeringAccounts(new QueryingDatabase.OnGetAdministeringAccountsListener() {
            @Override
            public void onSuccess(HashMap<String, String> administeringFriends) {
                List<String> friendsNames = new ArrayList<>();
                for (String friend : administeringFriends.keySet()) {
                    friendsNames.add(friend);
                }

                mAdministeringFriends = administeringFriends;

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(SettingsActivity.this, R.layout.spinner_item, friendsNames);
                arrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                spnSelectAdminAccount.setAdapter(arrayAdapter);
                spnSelectAdminAccount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position,
                                               long id) {
                        mFriendsName = parent.getItemAtPosition(position).toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }
        });
    }
}