package messaging.app.messages.sendingMedia;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ImageButton;

import java.io.IOException;
import java.util.ArrayList;

import messaging.app.ManagingActivityPreview;
import messaging.app.R;
import messaging.app.contactingFirebase.ManagingMessages;
import messaging.app.messages.capturingMedia.CaptureActivity;
import messaging.app.messages.sendingMedia.ui.main.SectionsPagerAdapter;

public class SendMediaTabsActivity extends AppCompatActivity implements SectionsPagerAdapter.onRowSelectedListener {
    ManagingActivityPreview managingActivityPreview = new ManagingActivityPreview();
    ManagingMessages managingMessages = new ManagingMessages(this);

    String pathToMedia;
    String typeOfMediaCaptured;
    String message;
    int mDeviceOrientationMode;
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

        pathToMedia = getIntent().getStringExtra("mediaPath");
        typeOfMediaCaptured = getIntent().getStringExtra("typeOfMediaCaptured");
        message = getIntent().getStringExtra("message");
        mDeviceOrientationMode = getIntent().getIntExtra("deviceOrientationMode", 0);
        btnSend = findViewById(R.id.btnSend);


        //hide the navigation controls
        View decorView = getWindow().getDecorView();

        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);


        btnSendOnClick();
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            managingActivityPreview.hideSystemUI(getWindow().getDecorView());
        }
    }


    private void sendMedia(ArrayList<String> directMessagesUUID,  ArrayList<String> storyMessagesUUID){

        try {

            managingMessages.sendMessages(directMessagesUUID, storyMessagesUUID, pathToMedia, typeOfMediaCaptured, message, mDeviceOrientationMode);
        } catch (IOException e) {
            e.printStackTrace();
        }


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