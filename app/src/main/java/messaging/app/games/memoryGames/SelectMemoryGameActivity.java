package messaging.app.games.memoryGames;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import messaging.app.R;
import messaging.app.games.LeaderBoardActivity;
import messaging.app.games.SelectGameActivity;
import messaging.app.games.memoryGames.memorizingPatternGame.StartMemorizingPatternActivity;
import messaging.app.games.memoryGames.pairGame.StartPairsGameActivity;
import messaging.app.games.reflexGames.SelectReactionGameActivity;

public class SelectMemoryGameActivity extends AppCompatActivity {

    LinearLayoutCompat llayPairGame;
    LinearLayoutCompat llayPatternGame;
    LinearLayoutCompat llayLeaderBoard;
    ImageButton btnBackToSelectGames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_memory_game);

        llayPatternGame = findViewById(R.id.llayPattern);
        llayPairGame = findViewById(R.id.llayPair);
        btnBackToSelectGames = findViewById(R.id.btnBackToSelectGames);
        llayLeaderBoard = findViewById(R.id.llayMemoryLeaderBoard);

        setLlayPairGames();
        setLlayPatternGames();
        setLlayLeaderBoardOnClick();
        setBtnBackToSelectGamesOnClick();
    }

    private void setLlayPairGames() {
        llayPairGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectMemoryGameActivity.this,
                        StartPairsGameActivity.class);
                SelectMemoryGameActivity.this.startActivity(intent);
            }
        });
    }

    private void setLlayPatternGames() {
        llayPatternGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectMemoryGameActivity.this,
                        StartMemorizingPatternActivity.class);
                SelectMemoryGameActivity.this.startActivity(intent);
            }
        });
    }

    private void setBtnBackToSelectGamesOnClick() {
        btnBackToSelectGames.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectMemoryGameActivity.this,
                        SelectGameActivity.class);
                SelectMemoryGameActivity.this.startActivity(intent);
            }
        });
    }

    private void setLlayLeaderBoardOnClick() {
        llayLeaderBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectMemoryGameActivity.this,
                        LeaderBoardActivity.class);
                intent.putExtra("gameType", "memory");
                SelectMemoryGameActivity.this.startActivity(intent);
            }
        });
    }
}