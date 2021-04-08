package messaging.app.games;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import messaging.app.R;
import messaging.app.SelectAreaOfApplicationActivity;
import messaging.app.contactingFirebase.QueryingDatabase;
import messaging.app.games.memoryGames.SelectMemoryGameActivity;
import messaging.app.games.reflexGames.SelectReactionGameActivity;

public class SelectGameActivity extends AppCompatActivity {

    LinearLayoutCompat llayReactionGames;
    LinearLayoutCompat llayMemoryGames;
    ImageButton btnBackToOptions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_game);

        llayReactionGames = findViewById(R.id.llayReactionGames);
        llayMemoryGames = findViewById(R.id.llayMemoryGames);
        btnBackToOptions = findViewById(R.id.btnBackToOptions);

        setLlayReactionGames();
        setLlayMemoryGames();
        setBtnBackToOptionsOnClick();
    }

    private void setLlayReactionGames() {
        llayReactionGames.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectGameActivity.this,
                        SelectReactionGameActivity.class);
                SelectGameActivity.this.startActivity(intent);
            }
        });
    }


    private void setLlayMemoryGames() {
        llayMemoryGames.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectGameActivity.this,
                        SelectMemoryGameActivity.class);
                SelectGameActivity.this.startActivity(intent);
            }
        });
    }


    private void setBtnBackToOptionsOnClick() {
        btnBackToOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectGameActivity.this,
                        SelectAreaOfApplicationActivity.class);
                SelectGameActivity.this.startActivity(intent);
            }
        });
    }

}