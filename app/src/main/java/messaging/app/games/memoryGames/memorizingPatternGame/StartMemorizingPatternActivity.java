package messaging.app.games.memoryGames.memorizingPatternGame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import messaging.app.R;
import messaging.app.games.memoryGames.SelectMemoryGameActivity;
import messaging.app.games.reflexGames.SelectReactionGameActivity;
import messaging.app.games.reflexGames.stroopTest.StartStroopTestActivity;

public class StartMemorizingPatternActivity extends AppCompatActivity {


    ImageButton btnBackToMemoryGames;
    Button btnWatchPatternGameVid;
    Button btnStartPatternGame;
    TextView lblPatternGameTitle;
    TextView lblPatternGameDesc;

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
            lblPatternGameTitle.setText("You scored:");
            lblPatternGameDesc.setText(highScore + " on the " + level + " level");

            //store score in database
            //if high score set as their high score
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