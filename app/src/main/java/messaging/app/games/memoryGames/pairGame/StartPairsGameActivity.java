package messaging.app.games.memoryGames.pairGame;

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

public class StartPairsGameActivity extends AppCompatActivity {

    ImageButton btnBackToMemoryGames;
    ImageButton btnCancel;
    Button btnWatchPairGameVid;
    Button btnStartPairGame;
    TextView lblPairGameTitle;
    TextView lblPairGameDesc;
    VideoView vidPairsExample;

    HashMap<String, List<Long>> mHighScores = new HashMap<>();
    double mHighScorePercentageChange = 0;

    ManagingGames mManagingGames = new ManagingGames(this);
    QueryingDatabase mQueryingDatabase = new QueryingDatabase(null);
    Formatting mFormatting = new Formatting();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_pairs);

        btnBackToMemoryGames = findViewById(R.id.btnBackToMemoryGames);
        btnWatchPairGameVid = findViewById(R.id.btnWatchPairGameVid);
        btnStartPairGame = findViewById(R.id.btnStartPairGame);
        btnCancel = findViewById(R.id.btnCancel);
        lblPairGameTitle = findViewById(R.id.lblPairGameTitle);
        lblPairGameDesc = findViewById(R.id.lblPairGameDesc);
        vidPairsExample = findViewById(R.id.vidPairsExample);

        vidPairsExample.setVisibility(View.INVISIBLE);
        btnCancel.setVisibility(View.INVISIBLE);

        mQueryingDatabase.getHighScores(new QueryingDatabase.OnGetHighScoresListener() {
            @Override
            public void onSuccess(HashMap<String, List<Long>> highScores) {
                mHighScores = highScores;
            }
        });


        if (mHighScores.containsKey("pairs")) {
            if (mHighScores.get("pairs").size() >= 5) {
                mHighScorePercentageChange = mFormatting
                        .getPercentageChangeOfHighScores(mHighScores, "pairs");
            }
        }


        if (getIntent().getStringExtra("numberOfPairs") != null) {

            //display to user
            String numberOfPairs = getIntent().getStringExtra("numberOfPairs");
            int streak = getIntent().getIntExtra("streak", 0);
            int numberOfPairsFound = getIntent().getIntExtra("numberOfPairsFound", 0);

            int userScore = 0;
            float maxScoreForLevel = 0f;
            float bonusPointsForLevel = 0f;
            float pointsPerPair = 0f; //this is how many squares were in the pattern

            if (numberOfPairs.equals("6")) {
                maxScoreForLevel = (float) (6f / 16f) * 100f;
                //4 rounds is the target;
                pointsPerPair = maxScoreForLevel / (6f * 4f);

            } else if (numberOfPairs.equals("8")) {
                maxScoreForLevel = (float) (8f / 16f) * 100f;
                bonusPointsForLevel = 10f;
                //4 rounds is the target;
                pointsPerPair = (maxScoreForLevel - bonusPointsForLevel) / (8f * 4f);
            } else if (numberOfPairs.equals("12")) {
                maxScoreForLevel = (float) (12f / 16f) * 100f;
                bonusPointsForLevel = 30f;
                //4 rounds is the target;
                pointsPerPair = (maxScoreForLevel - bonusPointsForLevel) / (8f * 4f);
            } else if (numberOfPairs.equals("16")) {
                //no max score for final level
                bonusPointsForLevel = 50f;
                //3 rounds is the target;
                pointsPerPair = (maxScoreForLevel - bonusPointsForLevel) / (8f * 3f);
            }

            userScore = Math.round(numberOfPairsFound * pointsPerPair);

            lblPairGameTitle.setText("You scores:");
            lblPairGameDesc.setText("You found " + numberOfPairsFound + " pairs and completed " +
                    streak + " round(s), with " + numberOfPairs + " pairs per round. You scored: " +
                    userScore);

            //store score in database
            //if high score set as their high score
            mManagingGames.storeGameResult("pairs", userScore);

        }

        setBtnBackToMemoryGamesOnClick();
        setBtnStartPairGameOnClick();
        setBtnWatchPairsVidOnClick();
        setBtnCancelOnClick();
    }


    private void setBtnBackToMemoryGamesOnClick() {
        btnBackToMemoryGames.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartPairsGameActivity.this, SelectMemoryGameActivity.class);
                StartPairsGameActivity.this.startActivity(intent);
            }
        });
    }


    private void setBtnStartPairGameOnClick() {
        btnStartPairGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int previousLevel = 1;
                int newLevel = previousLevel;

                if (mHighScores.containsKey("pairs")) {
                    if (mHighScores.get("pairs").size() >= 5) {
                        //get the current users progression over the past 5 games
                        mHighScorePercentageChange = mFormatting
                                .getPercentageChangeOfHighScores(mHighScores, "pairs");

                        //get the users previous grid size
                        if (mHighScores.get("pairs").get(0) > 37 &&
                                mHighScores.get("pairs").get(0) <= 50) {
                            previousLevel = 2;
                        } else if (mHighScores.get("pairs").get(0) > 50 &&
                                mHighScores.get("pairs").get(0) <= 75) {
                            previousLevel = 3;
                        } else if (mHighScores.get("pairs").get(0) > 75) {
                            previousLevel = 4;
                        }
                    }

                    newLevel = previousLevel;

                    //find a level that is best for the user
                    if (mHighScorePercentageChange < -20 && previousLevel != 1) {
                        //user has been struggling, make the game easier for them
                        newLevel = previousLevel - 1;
                    } else if (mHighScorePercentageChange > 30 && previousLevel != 4) {
                        //user has improved so make the game harder
                        newLevel = previousLevel + 1;
                    }
                }

                //load the level that is best fit for the user
                Intent intent;
                switch (newLevel) {
                    case 2:
                        intent = new Intent(StartPairsGameActivity.this,
                                PairGame8ButtonsActivity.class);
                        break;
                    case 3:
                        intent = new Intent(StartPairsGameActivity.this,
                                PairGame12ButtonsActivity.class);
                        break;
                    case 4:
                        intent = new Intent(StartPairsGameActivity.this,
                                PairGame16ButtonsActivity.class);
                        break;
                    default:
                        intent = new Intent(StartPairsGameActivity.this,
                                PairGame6ButtonsActivity.class);
                }

                StartPairsGameActivity.this.startActivity(intent);
            }
        });
    }


    private void setBtnWatchPairsVidOnClick() {
        btnWatchPairGameVid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vidPairsExample.setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.VISIBLE);

                btnBackToMemoryGames.setVisibility(View.INVISIBLE);
                btnWatchPairGameVid.setVisibility(View.INVISIBLE);
                btnStartPairGame.setVisibility(View.INVISIBLE);
                lblPairGameTitle.setVisibility(View.INVISIBLE);
                lblPairGameDesc.setVisibility(View.INVISIBLE);

                MediaController mediaController = new MediaController(StartPairsGameActivity.this);
                mediaController.setAnchorView(vidPairsExample);
                vidPairsExample.setMediaController(mediaController);
                vidPairsExample.setVideoURI(Uri.parse("android.resource://" + getPackageName() +
                        "/" + R.raw.pairs_example));
                vidPairsExample.start();

                vidPairsExample.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
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
                vidPairsExample.setVisibility(View.INVISIBLE);
                btnCancel.setVisibility(View.INVISIBLE);

                btnBackToMemoryGames.setVisibility(View.VISIBLE);
                btnWatchPairGameVid.setVisibility(View.VISIBLE);
                btnStartPairGame.setVisibility(View.VISIBLE);
                lblPairGameTitle.setVisibility(View.VISIBLE);
                lblPairGameDesc.setVisibility(View.VISIBLE);

                vidPairsExample.stopPlayback();
            }
        });
    }


}