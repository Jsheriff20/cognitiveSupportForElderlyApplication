package messaging.app.games;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import java.util.List;

import messaging.app.R;
import messaging.app.contactingFirebase.QueryingDatabase;

public class LeaderBoardActivity extends AppCompatActivity {

    ImageButton btnBackToSelectGamesActivity;
    ImageButton btnRefreshLeaderBoard;

    LeaderBoardAdapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    String mGameType = "";
    private RecyclerView mRecyclerView;

    QueryingDatabase mQueryingDatabase = new QueryingDatabase(null);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);

        mRecyclerView = findViewById(R.id.lstLeaderboard);
        btnBackToSelectGamesActivity = findViewById(R.id.btnBackToSelectGames);
        btnRefreshLeaderBoard = findViewById(R.id.btnRefreshLeaderBoard);

        mGameType = getIntent().getStringExtra("gameType");

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

    private void setBtnBackToSelectGamesActivity() {
        btnBackToSelectGamesActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LeaderBoardActivity.this,
                        SelectGameActivity.class);
                LeaderBoardActivity.this.startActivity(intent);
            }
        });
    }

    private void displayHighScores() {

        mQueryingDatabase.getLeaderBoardData(new QueryingDatabase.OnGetLeaderBoardDataListener() {
            @Override
            public void onSuccess(List<AccountsHighScores> accountsHighScores) {

                //display to user
                mLayoutManager = new LinearLayoutManager(LeaderBoardActivity.this);
                mAdapter = new LeaderBoardAdapter(accountsHighScores, mGameType,
                        LeaderBoardActivity.this);

                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setAdapter(mAdapter);
            }
        });

    }
}