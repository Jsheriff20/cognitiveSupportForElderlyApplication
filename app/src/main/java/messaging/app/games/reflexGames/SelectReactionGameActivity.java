package messaging.app.games.reflexGames;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import messaging.app.R;
import messaging.app.games.LeaderBoardActivity;
import messaging.app.games.SelectGameActivity;
import messaging.app.games.memoryGames.SelectMemoryGameActivity;
import messaging.app.games.reflexGames.gridReactionGames.StartGridReactionGameActivity;
import messaging.app.games.reflexGames.stroopTest.StartStroopTestActivity;
import messaging.app.games.reflexGames.stroopTest.StroopTestActivity;

public class SelectReactionGameActivity extends AppCompatActivity {

    LinearLayoutCompat llayColourChange;
    LinearLayoutCompat llayGrid;
    LinearLayoutCompat llayStoop;
    LinearLayoutCompat llayLeaderBoard;
    ImageButton btnBackToSelectGames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_reflex_game);

        llayColourChange = findViewById(R.id.llayColourChange);
        llayGrid = findViewById(R.id.llayGrid);
        llayStoop = findViewById(R.id.llayStoop);
        llayLeaderBoard = findViewById(R.id.llayReactionLeaderBoard);
        btnBackToSelectGames = findViewById(R.id.btnBackToSelectGames);

        setLlayColourChange();
        setLlayGrid();
        setLlayStoop();
        setLlayLeaderBoardOnClick();
        setBtnBackToSelectGamesOnClick();
    }


    private void setLlayColourChange() {
        llayColourChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectReactionGameActivity.this,
                        ButtonChangeColourActivity.class);
                SelectReactionGameActivity.this.startActivity(intent);
            }
        });
    }


    private void setLlayGrid() {
        llayGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectReactionGameActivity.this,
                        StartGridReactionGameActivity.class);
                SelectReactionGameActivity.this.startActivity(intent);
            }
        });
    }

    private void setLlayStoop() {
        llayStoop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectReactionGameActivity.this,
                        StartStroopTestActivity.class);
                SelectReactionGameActivity.this.startActivity(intent);
            }
        });
    }


    private void setBtnBackToSelectGamesOnClick() {
        btnBackToSelectGames.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectReactionGameActivity.this,
                        SelectGameActivity.class);
                SelectReactionGameActivity.this.startActivity(intent);
            }
        });
    }


    private void setLlayLeaderBoardOnClick() {
        llayLeaderBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectReactionGameActivity.this,
                        LeaderBoardActivity.class);
                intent.putExtra("gameType", "reaction");
                SelectReactionGameActivity.this.startActivity(intent);
            }
        });
    }
}