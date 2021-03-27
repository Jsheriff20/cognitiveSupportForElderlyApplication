package messaging.app.games.reflexGames.gridReactionGames;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import messaging.app.R;
import messaging.app.games.reflexGames.ButtonChangeColourActivity;
import messaging.app.games.reflexGames.SelectReactionGameActivity;

public class StartGridReactionGameActivity extends AppCompatActivity {


    ImageButton btnBackToReactionGames;
    Button btnWatchGridReactionGameVid;
    Button btnStartGridReactionGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_grid_reaction_game);

        btnStartGridReactionGame = findViewById(R.id.btnStartGridReactionGame);
        btnWatchGridReactionGameVid = findViewById(R.id.btnWatchGridReactionGameVid);
        btnBackToReactionGames = findViewById(R.id.btnBackToReactionGames);

        setStartGridReactionGameOnClick();
        setBtnBackToReactionGamesOnClick();
    }


    private void setStartGridReactionGameOnClick(){
        btnStartGridReactionGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartGridReactionGameActivity.this, GridReactionGame6ButtonsActivity.class);
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
}