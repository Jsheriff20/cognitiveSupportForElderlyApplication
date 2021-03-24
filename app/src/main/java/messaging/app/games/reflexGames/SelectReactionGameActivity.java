package messaging.app.games.reflexGames;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import messaging.app.R;
import messaging.app.games.SelectGameActivity;
import messaging.app.games.reflexGames.gridReactionGames.StartGridReactionGameActivity;

public class SelectReactionGameActivity extends AppCompatActivity {

    LinearLayoutCompat llayColourChange;
    LinearLayoutCompat llayGrid;
    LinearLayoutCompat llayStoop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_reflex_game);

        llayColourChange = findViewById(R.id.llayColourChange);
        llayGrid = findViewById(R.id.llayGrid);
        llayStoop = findViewById(R.id.llayStoop);
        setLlayColourChange();
        setLlayGrid();
        setLlayStoop();
    }


    private void setLlayColourChange(){
        llayColourChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectReactionGameActivity.this, ButtonChangeColourActivity.class);
                SelectReactionGameActivity.this.startActivity(intent);
            }
        });
    }


    private void setLlayGrid(){
        llayGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectReactionGameActivity.this, StartGridReactionGameActivity.class);
                SelectReactionGameActivity.this.startActivity(intent);
            }
        });
    }

    private void setLlayStoop(){
        llayStoop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectReactionGameActivity.this, StoopTestActivity.class);
                SelectReactionGameActivity.this.startActivity(intent);
            }
        });
    }
}