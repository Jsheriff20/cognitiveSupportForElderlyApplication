package messaging.app.games.reflexGames.stroopTest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import messaging.app.R;
import messaging.app.contactingFirebase.ManagingGames;
import messaging.app.games.reflexGames.SelectReactionGameActivity;
import messaging.app.games.reflexGames.gridReactionGames.StartGridReactionGameActivity;

public class StartStroopTestActivity extends AppCompatActivity {


    ImageButton btnBackToReactionGame;
    ImageButton btnCancel;
    Button btnWatchStoopTestVid;
    Button btnStartStoopTest;
    TextView lblStoopTestTitle;
    TextView lblStoopTestDesc;
    VideoView vidStoopTextExample;

    ManagingGames managingGames = new ManagingGames(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_stroop_test);

        btnBackToReactionGame = findViewById(R.id.btnBackToReactionGame);
        btnWatchStoopTestVid = findViewById(R.id.btnWatchStoopTestVid);
        btnStartStoopTest = findViewById(R.id.btnStartStoopTest);
        lblStoopTestTitle = findViewById(R.id.lblStoopTestTitle);
        lblStoopTestDesc = findViewById(R.id.lblStoopTestDesc);
        vidStoopTextExample = findViewById(R.id.vidStoopTextExample);
        btnCancel = findViewById(R.id.btnCancel);

        vidStoopTextExample.setVisibility(View.INVISIBLE);
        btnCancel.setVisibility(View.INVISIBLE);


        if(getIntent().getLongExtra("reactionTime", 999999999)  != 999999999){

            //display to user
            long reactionTime = getIntent().getLongExtra("reactionTime", 999999999);
            lblStoopTestTitle.setText("You results:");
            lblStoopTestDesc.setText("Your average reaction speed was " + reactionTime +" ms");

            //store score in database
            //if high score set as their high score
            managingGames.storeGameResult("stroopTest", reactionTime);
        }


        setBtnBackToReactionGamesOnClick();
        setBtnStartStoopTestOnClick();
        setBtnWatchStoopTestVidOnClick();
        setBtnCancelOnClick();
    }



    private void setBtnBackToReactionGamesOnClick(){
        btnBackToReactionGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartStroopTestActivity.this, SelectReactionGameActivity.class);
                StartStroopTestActivity.this.startActivity(intent);
            }
        });
    }


    private void setBtnWatchStoopTestVidOnClick(){
        btnWatchStoopTestVid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vidStoopTextExample.setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.VISIBLE);

                btnBackToReactionGame.setVisibility(View.INVISIBLE);
                btnWatchStoopTestVid.setVisibility(View.INVISIBLE);
                btnStartStoopTest.setVisibility(View.INVISIBLE);
                lblStoopTestTitle.setVisibility(View.INVISIBLE);
                lblStoopTestDesc.setVisibility(View.INVISIBLE);

                MediaController mediaController = new MediaController(StartStroopTestActivity.this);
                mediaController.setAnchorView(vidStoopTextExample);
                vidStoopTextExample.setMediaController(mediaController);
                vidStoopTextExample.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" +
                        R.raw.stoop_test_example));
                vidStoopTextExample.start();

                vidStoopTextExample.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.setLooping(true);
                    }
                });
            }
        });
    }


    private void setBtnCancelOnClick(){
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vidStoopTextExample.setVisibility(View.INVISIBLE);
                btnCancel.setVisibility(View.INVISIBLE);

                btnBackToReactionGame.setVisibility(View.VISIBLE);
                btnWatchStoopTestVid.setVisibility(View.VISIBLE);
                btnStartStoopTest.setVisibility(View.VISIBLE);
                lblStoopTestTitle.setVisibility(View.VISIBLE);
                lblStoopTestDesc.setVisibility(View.VISIBLE);

                vidStoopTextExample.stopPlayback();
            }
        });
    }



    private void setBtnStartStoopTestOnClick(){
        btnStartStoopTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartStroopTestActivity.this, StroopTestActivity.class);
                StartStroopTestActivity.this.startActivity(intent);
            }
        });
    }
}