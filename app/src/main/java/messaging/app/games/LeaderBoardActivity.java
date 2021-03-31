package messaging.app.games;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import java.util.HashMap;
import java.util.List;

import messaging.app.R;
import messaging.app.contactingFirebase.QueryingDatabase;
import messaging.app.messages.viewingMessages.ViewingMessagesReceivedAdapter;

public class LeaderBoardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    ImageButton btnBackToSelectGamesActivity;
    ImageButton btnRefreshLeaderBoard;

    private LeaderBoardAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    QueryingDatabase queryingDatabase = new QueryingDatabase();
    String gameType = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);

        recyclerView = findViewById(R.id.lstLeaderboard);
        btnBackToSelectGamesActivity = findViewById(R.id.btnBackToSelectGames);
        btnRefreshLeaderBoard = findViewById(R.id.btnRefreshLeaderBoard);

        gameType = getIntent().getStringExtra("gameType");

        displayHighScores();
        setBtnBackToSelectGamesActivity();
        setBtnRefreshLeaderBoard();

    }

    private void setBtnRefreshLeaderBoard() {
        //restart activity
        btnRefreshLeaderBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(getIntent());
            }
        });
    }

        private void setBtnBackToSelectGamesActivity () {
            btnBackToSelectGamesActivity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(LeaderBoardActivity.this, SelectGameActivity.class);
                    LeaderBoardActivity.this.startActivity(intent);
                }
            });
        }

        private void displayHighScores () {

            queryingDatabase.getLeaderBoardData(new QueryingDatabase.OnGetLeaderBoardDataListener() {
                @Override
                public void onSuccess(List<AccountsHighScores> accountsHighScores) {

                    //display to user
                    mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    mAdapter = new LeaderBoardAdapter(accountsHighScores, gameType, getApplicationContext());

                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setAdapter(mAdapter);
                }
            });

        }
    }