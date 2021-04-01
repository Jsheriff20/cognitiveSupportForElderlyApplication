package messaging.app.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.HashMap;
import java.util.Map;

import messaging.app.NotifyMessageReceivedService;
import messaging.app.R;
import messaging.app.SelectAreaOfApplicationActivity;
import messaging.app.contactingFirebase.ManagingAccounts;
import messaging.app.contactingFirebase.QueryingDatabase;
import messaging.app.login.LoginActivity;

public class SettingsActivity extends AppCompatActivity {

    Button btnLogout;
    Button btnManageAdmins;

    ManagingAccounts managingAccounts = new ManagingAccounts(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        btnLogout = findViewById(R.id.btnLogout);
        btnManageAdmins = findViewById(R.id.btnManageAdmins);

        setBtnLogoutOnClick();
        setBtnAddAdminFriendOnClick();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SettingsActivity.this, SelectAreaOfApplicationActivity.class);
        SettingsActivity.this.startActivity(intent);
    }


    private void setBtnLogoutOnClick(){
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                managingAccounts.logoutUser();
                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                SettingsActivity.this.startActivity(intent);

                //stop notification service
                stopService(new Intent(getApplicationContext(), NotifyMessageReceivedService.class));
            }
        });
    }


    private void setBtnAddAdminFriendOnClick(){
        btnManageAdmins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, ManageAdminActivity.class);
                SettingsActivity.this.startActivity(intent);
            }
        });
    }
}