package messaging.app.games.memoryGames.pairGame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;
import java.util.Random;

import messaging.app.R;
import messaging.app.contactingFirebase.ManagingGames;
import messaging.app.games.memoryGames.SelectMemoryGameActivity;
import messaging.app.games.memoryGames.memorizingPatternGame.StartMemorizingPatternActivity;
import messaging.app.games.reflexGames.gridReactionGames.GridReactionGame4ButtonsActivity;

public class StartPairsGameActivity extends AppCompatActivity {

    ImageButton btnBackToMemoryGames;
    Button btnWatchPairGameVid;
    Button btnStartPairGame;
    TextView lblPairGameTitle;
    TextView lblPairGameDesc;

    ManagingGames managingGames = new ManagingGames(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_pairs);

        btnBackToMemoryGames = findViewById(R.id.btnBackToMemoryGames);
        btnWatchPairGameVid = findViewById(R.id.btnWatchPairGameVid);
        btnStartPairGame = findViewById(R.id.btnStartPairGame);
        lblPairGameTitle = findViewById(R.id.lblPairGameTitle);
        lblPairGameDesc = findViewById(R.id.lblPairGameDesc);


        if(getIntent().getStringExtra("numberOfPairs")  != null){

            //display to user
            String numberOfPairs = getIntent().getStringExtra("numberOfPairs");
            int streak = getIntent().getIntExtra("streak", 0);
            int numberOfPairsFound = getIntent().getIntExtra("numberOfPairsFound", 0);

            int userScore = 0;
            float maxScoreForLevel = 0f;
            float bonusPointsForLevel = 0f;
            float pointsPerPair= 0f; //this is how many squares were in the pattern

            if(numberOfPairs.equals("6")){
                maxScoreForLevel = (float) (6f / 16f) * 100f;
                //4 rounds is the target;
                pointsPerPair = maxScoreForLevel / (6f*4f);

            }
            else if(numberOfPairs.equals("8")){
                maxScoreForLevel = (float) (8f / 16f) * 100f;
                bonusPointsForLevel = 10f;
                //4 rounds is the target;
                pointsPerPair = (maxScoreForLevel - bonusPointsForLevel) / (8f*4f);
            }
            else if(numberOfPairs.equals("12")){
                maxScoreForLevel = (float) (12f / 16f) * 100f;
                bonusPointsForLevel = 30f;
                //4 rounds is the target;
                pointsPerPair = (maxScoreForLevel - bonusPointsForLevel) / (8f*4f);
            }
            else if(numberOfPairs.equals("16")){
                //no max score for final level
                bonusPointsForLevel = 50f;
                //3 rounds is the target;
                pointsPerPair = (maxScoreForLevel - bonusPointsForLevel) / (8f*3f);
            }

            userScore = Math.round(numberOfPairsFound * pointsPerPair);

            lblPairGameTitle.setText("You scores:");
            lblPairGameDesc.setText("You found " + numberOfPairsFound +" pairs and completed " + streak + " round(s), with " + numberOfPairs + " pairs per round. You scored: " + userScore);

            //store score in database
            //if high score set as their high score
            managingGames.storeGameResult("pairs", userScore);

        }

        setBtnBackToMemoryGamesOnClick();
        setBtnStartPairGameOnClick();
    }



    private void setBtnBackToMemoryGamesOnClick(){
        btnBackToMemoryGames.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartPairsGameActivity.this, SelectMemoryGameActivity.class);
                StartPairsGameActivity.this.startActivity(intent);
            }
        });
    }



    private void setBtnStartPairGameOnClick(){
        btnStartPairGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartPairsGameActivity.this, PairGame6ButtonsActivity.class);
                StartPairsGameActivity.this.startActivity(intent);
            }
        });
    }
}