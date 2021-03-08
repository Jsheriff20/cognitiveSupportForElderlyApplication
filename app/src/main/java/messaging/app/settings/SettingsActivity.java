package messaging.app.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import messaging.app.ContactingFirebase;
import messaging.app.R;
import messaging.app.SelectAreaOfApplicationActivity;
import messaging.app.login.LoginActivity;
import messaging.app.register.RegisterEmailActivity;

public class SettingsActivity extends AppCompatActivity {

    Button btnLogout;

    ContactingFirebase contactingFirebase = new ContactingFirebase(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        btnLogout = findViewById(R.id.btnLogout);
        setBtnLogoutOnClick();
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
                contactingFirebase.logoutUser();
                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                SettingsActivity.this.startActivity(intent);
            }
        });
    }
}