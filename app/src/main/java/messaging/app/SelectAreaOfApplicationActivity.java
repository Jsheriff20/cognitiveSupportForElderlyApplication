package messaging.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import messaging.app.messages.MessagesActivity;

public class SelectAreaOfApplicationActivity extends AppCompatActivity {

    LinearLayoutCompat llayMessages;
    LinearLayoutCompat llayMemories;
    LinearLayoutCompat llayGames;
    LinearLayoutCompat llaySettings;
    ContactingFirebase contactingFirebase = new ContactingFirebase(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_area_of_application_activity);

        llayMessages = findViewById(R.id.llayMessages);
        llayMemories = findViewById(R.id.llayMemories);
        llayGames = findViewById(R.id.llayGames);
        llaySettings = findViewById(R.id.llaySettings);

        setLayoutButtonOnClick();
    }

    @Override
    protected void onStart() {
        super.onStart();
//        contactingFirebase
        contactingFirebase.getUUIDsUsername();
    }

    private void setLayoutButtonOnClick(){
        llayMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectAreaOfApplicationActivity.this, MessagesActivity.class);
                SelectAreaOfApplicationActivity.this.startActivity(intent);
            }
        });


        llayMemories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO:
                //add activity to open
//                Intent intent = new Intent(SelectAreaOfApplicationActivity.this, .class);
//                SelectAreaOfApplicationActivity.this.startActivity(intent);
            }
        });



        llayGames.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO:
                //add activity to open
//                Intent intent = new Intent(SelectAreaOfApplicationActivity.this, .class);
//                SelectAreaOfApplicationActivity.this.startActivity(intent);
            }
        });


        llaySettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO:
                //add activity to open
//                Intent intent = new Intent(SelectAreaOfApplicationActivity.this, .class);
//                SelectAreaOfApplicationActivity.this.startActivity(intent);
            }
        });
    }
}
