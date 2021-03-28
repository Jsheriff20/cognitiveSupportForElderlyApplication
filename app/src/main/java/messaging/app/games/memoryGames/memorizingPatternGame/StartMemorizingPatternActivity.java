package messaging.app.games.memoryGames.memorizingPatternGame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import messaging.app.R;
import messaging.app.contactingFirebase.ManagingGames;
import messaging.app.games.memoryGames.SelectMemoryGameActivity;
import messaging.app.games.reflexGames.SelectReactionGameActivity;
import messaging.app.games.reflexGames.stroopTest.StartStroopTestActivity;

public class StartMemorizingPatternActivity extends AppCompatActivity {


    ImageButton btnBackToMemoryGames;
    Button btnWatchPatternGameVid;
    Button btnStartPatternGame;
    TextView lblPatternGameTitle;
    TextView lblPatternGameDesc;

    ManagingGames managingGames = new ManagingGames(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_memorizing_pattern);

        btnBackToMemoryGames = findViewById(R.id.btnBackToMemoryGames);
        btnWatchPatternGameVid = findViewById(R.id.btnWatchPatternGameVid);
        btnStartPatternGame = findViewById(R.id.btnStartPatternGame);
        lblPatternGameTitle = findViewById(R.id.lblPatternGameTitle);
        lblPatternGameDesc = findViewById(R.id.lblPatternGameDesc);

        if(getIntent().getStringExtra("level")  != null){
            //display scores to user
            String level = getIntent().getStringExtra("level");
            int highScore = getIntent().getIntExtra("highScore", 0);

            int userScore = 0;
            float maxScoreForLevel = 0f;
            float bonusPointsForLevel = 0f;
            float pointsPerHighestSquare = 0f; //this is how many squares were in the pattern


            if(level.equals("2x2")){
                maxScoreForLevel = (float) (4f / 9f) * 100f;
                //8 is the target number for this level
                pointsPerHighestSquare = maxScoreForLevel / 8f;

            }
            else if(level.equals("2x3")){
                maxScoreForLevel = (float) (6f / 9f) * 100f;
                bonusPointsForLevel = 20f;
                //6 is the target number for this level
                pointsPerHighestSquare = (maxScoreForLevel - bonusPointsForLevel) / 6f;
            }
            else if(level.equals("3x3")){
                //no max score for final level
                bonusPointsForLevel = 40f;
                pointsPerHighestSquare = (float) (100f - bonusPointsForLevel) / 5f;
            }

            userScore = Math.round(highScore * pointsPerHighestSquare);


            lblPatternGameTitle.setText("You scored:");
            lblPatternGameDesc.setText(userScore + " on level " + level);

            //store score in database
            //if high score set as their high score
            managingGames.storeGameResult("pattern", userScore);
        }

        setBtnBackToMemoryGamesOnClick();
        setBtnStartPatternGameOnClick();
    }



    private void setBtnBackToMemoryGamesOnClick(){
        btnBackToMemoryGames.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartMemorizingPatternActivity.this, SelectMemoryGameActivity.class);
                StartMemorizingPatternActivity.this.startActivity(intent);
            }
        });
    }



    private void setBtnStartPatternGameOnClick(){
        btnStartPatternGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartMemorizingPatternActivity.this, PatternMemorizing4ButtonsActivity.class);
                StartMemorizingPatternActivity.this.startActivity(intent);
            }
        });
    }
}