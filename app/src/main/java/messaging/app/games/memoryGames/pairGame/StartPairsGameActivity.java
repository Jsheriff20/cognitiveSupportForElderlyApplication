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
import messaging.app.games.memoryGames.SelectMemoryGameActivity;
import messaging.app.games.memoryGames.memorizingPatternGame.StartMemorizingPatternActivity;
import messaging.app.games.reflexGames.gridReactionGames.GridReactionGame4ButtonsActivity;

public class StartPairsGameActivity extends AppCompatActivity {

    ImageButton btnBackToMemoryGames;
    Button btnWatchPairGameVid;
    Button btnStartPairGame;
    TextView lblPairGameTitle;
    TextView lblPairGameDesc;


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
            lblPairGameTitle.setText("You scores:");
            lblPairGameDesc.setText("You found " + numberOfPairsFound +" pairs and completed " + streak + " round(s), with " + numberOfPairs + " pairs per round");

            //store score in database
            //if high score set as their high score
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
                Intent intent = new Intent(StartPairsGameActivity.this, PairGame16ButtonsActivity.class);
                StartPairsGameActivity.this.startActivity(intent);
            }
        });
    }
}