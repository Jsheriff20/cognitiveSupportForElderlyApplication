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

    private ImageButton btnSend;


    String mPathToMedia;
    String mTypeOfMediaCaptured;
    String mMessage;
    int mDeviceOrientationMode;
    ArrayList<String> mDirectMessagesUUID = new ArrayList<String>();
    ArrayList<String> mStoryMessagesUUID = new ArrayList<String>();
    ;


    ManagingActivityPreview mManagingActivityPreview = new ManagingActivityPreview();
    ManagingMessages mManagingMessages = new ManagingMessages(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_media_tabs);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this,
                getSupportFragmentManager(), this);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        mPathToMedia = getIntent().getStringExtra("mediaPath");
        mTypeOfMediaCaptured = getIntent().getStringExtra("typeOfMediaCaptured");
        mMessage = getIntent().getStringExtra("message");
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
            mManagingActivityPreview.hideSystemUI(getWindow().getDecorView());
        }
    }


    private void sendMedia(ArrayList<String> directMessagesUUID, ArrayList<String> storyMessagesUUID) {

        try {

            mManagingMessages.sendMessages(directMessagesUUID, storyMessagesUUID,
                    mPathToMedia, mTypeOfMediaCaptured, mMessage, mDeviceOrientationMode);
        } catch (IOException e) {
            e.printStackTrace();
        }


        Intent intent = new Intent(SendMediaTabsActivity.this, CaptureActivity.class);
        SendMediaTabsActivity.this.startActivity(intent);
    }


    private void btnSendOnClick() {
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMedia(mDirectMessagesUUID, mStoryMessagesUUID);

            }
        });
    }

    @Override
    public void onSelectedListener(String UUID, String messageType) {
        if (messageType == "friends") {
            if (mDirectMessagesUUID.contains(UUID)) {
                mDirectMessagesUUID.remove(UUID);
                return;
            }
            mDirectMessagesUUID.add(UUID);
            return;
        }

        if (mStoryMessagesUUID.contains(UUID)) {
            mStoryMessagesUUID.remove(UUID);
            return;
        }
        mStoryMessagesUUID.add(UUID);

    }
}