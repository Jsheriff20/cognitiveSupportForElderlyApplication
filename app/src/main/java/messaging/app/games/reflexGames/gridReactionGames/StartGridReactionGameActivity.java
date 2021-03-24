package messaging.app.games.reflexGames.gridReactionGames;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import messaging.app.R;
import messaging.app.games.reflexGames.ButtonChangeColourActivity;
import messaging.app.games.reflexGames.SelectReactionGameActivity;

public class StartGridReactionGameActivity extends AppCompatActivity {


    Button btnStartGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_grid_reaction_game);

        btnStartGame = findViewById(R.id.btnStartGridReactionGame);

        setStartGameOnClick();
    }


    private void setStartGameOnClick(){
        btnStartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartGridReactionGameActivity.this, GridReactionGame6ButtonsActivity.class);
                StartGridReactionGameActivity.this.startActivity(intent);
            }
        });
    }
}