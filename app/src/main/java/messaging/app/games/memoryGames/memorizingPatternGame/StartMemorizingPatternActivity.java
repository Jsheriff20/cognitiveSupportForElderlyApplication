package messaging.app.games.memoryGames.memorizingPatternGame;

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

import java.util.HashMap;
import java.util.List;

import messaging.app.Formatting;
import messaging.app.R;
import messaging.app.contactingFirebase.ManagingGames;
import messaging.app.contactingFirebase.QueryingDatabase;
import messaging.app.games.memoryGames.SelectMemoryGameActivity;

public class StartMemorizingPatternActivity extends AppCompatActivity {


    ImageButton btnBackToMemoryGames;
    ImageButton btnCancel;
    Button btnWatchPatternGameVid;
    Button btnStartPatternGame;
    TextView lblPatternGameTitle;
    TextView lblPatternGameDesc;
    VideoView vidPatternExample;

    HashMap<String, List<Long>> mHighScores = new HashMap<>();
    double mHighScorePercentageChange = 0;

    ManagingGames mManagingGames = new ManagingGames(this);
    QueryingDatabase mQueryingDatabase = new QueryingDatabase(null);
    Formatting mFormatting = new Formatting();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_memorizing_pattern);

        btnBackToMemoryGames = findViewById(R.id.btnBackToMemoryGames);
        btnWatchPatternGameVid = findViewById(R.id.btnWatchPatternGameVid);
        btnStartPatternGame = findViewById(R.id.btnStartPatternGame);
        lblPatternGameTitle = findViewById(R.id.lblPatternGameTitle);
        lblPatternGameDesc = findViewById(R.id.lblPatternGameDesc);
        vidPatternExample = findViewById(R.id.vidPatternExample);
        btnCancel = findViewById(R.id.btnCancel);

        vidPatternExample.setVisibility(View.INVISIBLE);
        btnCancel.setVisibility(View.INVISIBLE);

        mQueryingDatabase.getHighScores(new QueryingDatabase.OnGetHighScoresListener() {
            @Override
            public void onSuccess(HashMap<String, List<Long>> highScores) {
                mHighScores = highScores;
            }
        });


        if (getIntent().getStringExtra("level") != null) {
            //display scores to user
            String level = getIntent().getStringExtra("level");
            int highScore = getIntent().getIntExtra("highScore", 0);

            int userScore = 0;
            float maxScoreForLevel = 0f;
            float bonusPointsForLevel = 0f;
            float pointsPerHighestSquare = 0f; //this is how many squares were in the pattern


            if (level.equals("2x2")) {
                maxScoreForLevel = (float) (4f / 9f) * 100f;
                //8 is the target number for this level
                pointsPerHighestSquare = maxScoreForLevel / 8f;

            } else if (level.equals("2x3")) {
                maxScoreForLevel = (float) (6f / 9f) * 100f;
                bonusPointsForLevel = 20f;
                //6 is the target number for this level
                pointsPerHighestSquare = (maxScoreForLevel - bonusPointsForLevel) / 6f;
            } else if (level.equals("3x3")) {
                //no max score for final level
                bonusPointsForLevel = 40f;
                pointsPerHighestSquare = (float) (100f - bonusPointsForLevel) / 5f;
            }

            userScore = Math.round(highScore * pointsPerHighestSquare);


            lblPatternGameTitle.setText("You scored:");
            lblPatternGameDesc.setText(userScore + " on level " + level);

            //store score in database
            //if high score set as their high score
            mManagingGames.storeGameResult("pattern", userScore);
        }

        setBtnBackToMemoryGamesOnClick();
        setBtnStartPatternGameOnClick();
        setBtnWatchPatternVidOnClick();
        setBtnCancelOnClick();
    }


    private void setBtnBackToMemoryGamesOnClick() {
        btnBackToMemoryGames.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartMemorizingPatternActivity.this,
                        SelectMemoryGameActivity.class);
                StartMemorizingPatternActivity.this.startActivity(intent);
            }
        });
    }


    private void setBtnStartPatternGameOnClick() {
        btnStartPatternGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                int previousLevel = 1;
                int newLevel = previousLevel;

                if (mHighScores.containsKey("pattern")) {
                    if (mHighScores.get("pattern").size() >= 5) {
                        //get the current users progression over the past 5 games
                        mHighScorePercentageChange = mFormatting
                                .getPercentageChangeOfHighScores(mHighScores, "pattern");

                        //get the users previous grid size
                        if (mHighScores.get("pattern").get(0) > 44 &&
                                mHighScores.get("pattern").get(0) <= 66) {
                            previousLevel = 2;
                        } else if (mHighScores.get("pattern").get(0) > 66) {
                            previousLevel = 3;
                        }
                    }

                    newLevel = previousLevel;

                    //find a level that is best for the user
                    if (mHighScorePercentageChange < -20 && previousLevel != 1) {
                        //user has been struggling, make the game easier for them
                        newLevel = previousLevel - 1;
                    } else if (mHighScorePercentageChange > 30 && previousLevel != 3) {
                        //user has improved so make the game harder
                        newLevel = previousLevel + 1;
                    }
                }


                //load the level that is best fit for the user
                Intent intent;
                switch (newLevel) {
                    case 2:
                        intent = new Intent(StartMemorizingPatternActivity.this,
                                PatternMemorizing6ButtonsActivity.class);
                        break;
                    case 3:
                        intent = new Intent(StartMemorizingPatternActivity.this,
                                PatternMemorizing9ButtonsActivity.class);
                        break;
                    default:
                        intent = new Intent(StartMemorizingPatternActivity.this,
                                PatternMemorizing4ButtonsActivity.class);
                }

                StartMemorizingPatternActivity.this.startActivity(intent);
            }
        });
    }


    private void setBtnWatchPatternVidOnClick() {
        btnWatchPatternGameVid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vidPatternExample.setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.VISIBLE);

                btnBackToMemoryGames.setVisibility(View.INVISIBLE);
                btnWatchPatternGameVid.setVisibility(View.INVISIBLE);
                btnStartPatternGame.setVisibility(View.INVISIBLE);
                lblPatternGameTitle.setVisibility(View.INVISIBLE);
                lblPatternGameDesc.setVisibility(View.INVISIBLE);

                MediaController mediaController =
                        new MediaController(StartMemorizingPatternActivity.this);
                mediaController.setAnchorView(vidPatternExample);
                vidPatternExample.setMediaController(mediaController);
                vidPatternExample.setVideoURI(Uri.parse("android.resource://" +
                        getPackageName() + "/" +
                        R.raw.pairs_example));
                vidPatternExample.start();

                vidPatternExample.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.setLooping(true);
                    }
                });
            }
        });
    }


    private void setBtnCancelOnClick() {
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vidPatternExample.setVisibility(View.INVISIBLE);
                btnCancel.setVisibility(View.INVISIBLE);

                btnBackToMemoryGames.setVisibility(View.VISIBLE);
                btnWatchPatternGameVid.setVisibility(View.VISIBLE);
                btnStartPatternGame.setVisibility(View.VISIBLE);
                lblPatternGameTitle.setVisibility(View.VISIBLE);
                lblPatternGameDesc.setVisibility(View.VISIBLE);

                vidPatternExample.stopPlayback();
            }
        });
    }
}