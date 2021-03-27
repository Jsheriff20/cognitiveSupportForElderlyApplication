package messaging.app.games.reflexGames.stroopTest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import messaging.app.R;
import messaging.app.games.reflexGames.SelectReactionGameActivity;
import messaging.app.games.reflexGames.gridReactionGames.StartGridReactionGameActivity;

public class StartStroopTestActivity extends AppCompatActivity {


    ImageButton btnBackToReactionGame;
    Button btnWatchStoopTestVid;
    Button btnStartStoopTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_stroop_test);

        btnBackToReactionGame = findViewById(R.id.btnBackToReactionGame);
        btnWatchStoopTestVid = findViewById(R.id.btnWatchStoopTestVid);
        btnStartStoopTest = findViewById(R.id.btnStartStoopTest);

        setBtnBackToReactionGamesOnClick();
        setBtnStartStoopTestOnClick();
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