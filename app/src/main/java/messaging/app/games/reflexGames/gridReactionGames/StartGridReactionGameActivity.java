package messaging.app.games.reflexGames.gridReactionGames;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.HashMap;
import java.util.List;

import messaging.app.Formatting;
import messaging.app.R;
import messaging.app.contactingFirebase.ManagingGames;
import messaging.app.contactingFirebase.QueryingDatabase;
import messaging.app.games.SelectGameActivity;
import messaging.app.games.memoryGames.pairGame.PairGame12ButtonsActivity;
import messaging.app.games.memoryGames.pairGame.PairGame16ButtonsActivity;
import messaging.app.games.memoryGames.pairGame.PairGame6ButtonsActivity;
import messaging.app.games.memoryGames.pairGame.PairGame8ButtonsActivity;
import messaging.app.games.memoryGames.pairGame.StartPairsGameActivity;
import messaging.app.games.reflexGames.ButtonChangeColourActivity;
import messaging.app.games.reflexGames.SelectReactionGameActivity;
import messaging.app.games.reflexGames.stroopTest.StartStroopTestActivity;

public class StartGridReactionGameActivity extends AppCompatActivity {


    ImageButton btnBackToReactionGames;
    ImageButton btnCancel;
    Button btnWatchGridReactionGameVid;
    Button btnStartGridReactionGame;
    TextView lblGridReactionGameTitle;
    TextView lblGridReactionGameDesc;
    VideoView vidGridReactionExample;

    ManagingGames managingGames = new ManagingGames(this);
    QueryingDatabase queryingDatabase = new QueryingDatabase();
    Formatting formatting = new Formatting();
    HashMap<String, List<Long>> mHighScores = new HashMap<>();
    double highScorePercentageChange = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_grid_reaction_game);

        queryingDatabase.getHighScores(new QueryingDatabase.OnGetHighScoresListener() {
            @Override
            public void onSuccess(HashMap<String, List<Long>> highScores) {
                mHighScores = highScores;
            }
        });

        btnStartGridReactionGame = findViewById(R.id.btnStartGridReactionGame);
        btnWatchGridReactionGameVid = findViewById(R.id.btnWatchGridReactionGameVid);
        btnBackToReactionGames = findViewById(R.id.btnBackToReactionGames);
        lblGridReactionGameTitle = findViewById(R.id.lblGridReactionGameTitle);
        lblGridReactionGameDesc = findViewById(R.id.lblGridReactionGameDesc);
        vidGridReactionExample = findViewById(R.id.vidGridReactionExample);
        btnCancel = findViewById(R.id.btnCancel);

        vidGridReactionExample.setVisibility(View.INVISIBLE);
        btnCancel.setVisibility(View.INVISIBLE);



        if(mHighScores.containsKey("gridReaction")) {
            if (mHighScores.get("gridReaction").size() >= 5) {
                highScorePercentageChange = formatting.getPercentageChangeOfHighScores(mHighScores, "gridReaction");
            }
        }


        if(getIntent().getLongExtra("reactionTime", 999999999)  != 999999999){

            //display to user
            long reactionTime = getIntent().getLongExtra("reactionTime", 999999999);
            lblGridReactionGameTitle.setText("Your results:");
            lblGridReactionGameDesc.setText("Your average reaction speed was " + reactionTime +" ms");

            //store score in database
            //if high score set as their high score
            managingGames.storeGameResult("gridReaction", reactionTime);

        }


        setStartGridReactionGameOnClick();
        setBtnBackToReactionGamesOnClick();
        setBtnWatchGridReactionVidOnClick();
        setBtnCancelOnClick();
    }


    private void setStartGridReactionGameOnClick(){
        btnStartGridReactionGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                int previousLevel = 1;
                int newLevel = previousLevel;

                if(mHighScores.containsKey("gridReaction")) {
                    if (mHighScores.get("gridReaction").size() >= 5) {
                        //get the current users progression over the past 5 games
                        highScorePercentageChange = formatting.getPercentageChangeOfHighScores(mHighScores, "gridReaction");

                        //get the users previous grid size
                        if (mHighScores.get("gridReaction").get(0) < 1000 && mHighScores.get("pattern").get(0) >= 600) {
                            previousLevel = 2;
                        } else if (mHighScores.get("gridReaction").get(0) < 600) {
                            previousLevel = 3;
                        }


                        newLevel = previousLevel;
                        //find a level that is best for the user
                        if(highScorePercentageChange > -20 && previousLevel != 1){
                            //user has been struggling, make the game easier for them
                            newLevel = previousLevel -1;
                        }
                        else if(highScorePercentageChange < 30 && previousLevel != 3){
                            //user has improved so make the game harder
                            newLevel = previousLevel + 1;
                        }

                    }
                }


                //load the level that is best fit for the user
                Intent intent;
                switch (newLevel){
                    case 2:
                        intent = new Intent(StartGridReactionGameActivity.this, GridReactionGame6ButtonsActivity.class);
                        break;
                    case 3:
                        intent = new Intent(StartGridReactionGameActivity.this, GridReactionGame9ButtonsActivity.class);
                        break;
                    default:
                        intent = new Intent(StartGridReactionGameActivity.this, GridReactionGame4ButtonsActivity.class);
                }

                StartGridReactionGameActivity.this.startActivity(intent);
            }
        });
    }


    private void setBtnBackToReactionGamesOnClick(){
        btnBackToReactionGames.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartGridReactionGameActivity.this, SelectReactionGameActivity.class);
                StartGridReactionGameActivity.this.startActivity(intent);
            }
        });
    }



    private void setBtnWatchGridReactionVidOnClick(){
        btnWatchGridReactionGameVid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vidGridReactionExample.setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.VISIBLE);

                btnBackToReactionGames.setVisibility(View.INVISIBLE);
                btnWatchGridReactionGameVid.setVisibility(View.INVISIBLE);
                btnStartGridReactionGame.setVisibility(View.INVISIBLE);
                lblGridReactionGameTitle.setVisibility(View.INVISIBLE);
                lblGridReactionGameDesc.setVisibility(View.INVISIBLE);

                MediaController mediaController = new MediaController(StartGridReactionGameActivity.this);
                mediaController.setAnchorView(vidGridReactionExample);
                vidGridReactionExample.setMediaController(mediaController);
                vidGridReactionExample.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" +
                        R.raw.grid_example));
                vidGridReactionExample.start();

                vidGridReactionExample.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
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
                vidGridReactionExample.setVisibility(View.INVISIBLE);
                btnCancel.setVisibility(View.INVISIBLE);

                btnBackToReactionGames.setVisibility(View.VISIBLE);
                btnWatchGridReactionGameVid.setVisibility(View.VISIBLE);
                btnStartGridReactionGame.setVisibility(View.VISIBLE);
                lblGridReactionGameTitle.setVisibility(View.VISIBLE);
                lblGridReactionGameDesc.setVisibility(View.VISIBLE);

                vidGridReactionExample.stopPlayback();
            }
        });
    }

}