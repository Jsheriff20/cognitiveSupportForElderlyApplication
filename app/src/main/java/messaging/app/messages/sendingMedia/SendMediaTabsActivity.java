package messaging.app.messages.sendingMedia;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import messaging.app.ContactingFirebase;
import messaging.app.R;
import messaging.app.messages.MessagesActivity;
import messaging.app.messages.capturingMedia.CaptureActivity;
import messaging.app.messages.sendingMedia.ui.main.SectionsPagerAdapter;

import static android.widget.Toast.LENGTH_SHORT;

public class SendMediaTabsActivity extends AppCompatActivity implements SectionsPagerAdapter.onRowSelectedListener {
    ContactingFirebase contactingFirebase = new ContactingFirebase(this);

    String pathToMedia;
    String typeOfMediaCaptured;
    String message;
    private ImageButton btnSend;
    ArrayList<String> directMessagesUUID = new ArrayList<String>();
    ArrayList<String> storyMessagesUUID = new ArrayList<String>();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_media_tabs);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), this);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        pathToMedia = getIntent().getExtras().getString("mediaPath");
        typeOfMediaCaptured = getIntent().getExtras().getString("typeOfMediaCaptured");
        message = getIntent().getExtras().getString("message");
        btnSend = findViewById(R.id.btnSend);


        btnSendOnClick();
    }



    private void sendMedia(ArrayList<String> directMessagesUUID,  ArrayList<String> storyMessagesUUID){

        contactingFirebase.sendMessages(directMessagesUUID, storyMessagesUUID, pathToMedia, typeOfMediaCaptured, message);


        Intent intent = new Intent(SendMediaTabsActivity.this, CaptureActivity.class);
        SendMediaTabsActivity.this.startActivity(intent);
    }




    private void btnSendOnClick(){
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMedia(directMessagesUUID, storyMessagesUUID);

            }
        });
    }

    @Override
    public void onSelectedListener(String UUID, String messageType) {
        if(messageType == "friends"){
            if(directMessagesUUID.contains(UUID)){
                directMessagesUUID.remove(UUID);
                return;
            }
            directMessagesUUID.add(UUID);
            return;
        }

        if(storyMessagesUUID.contains(UUID)){
            storyMessagesUUID.remove(UUID);
            return;
        }
        storyMessagesUUID.add(UUID);

    }
}