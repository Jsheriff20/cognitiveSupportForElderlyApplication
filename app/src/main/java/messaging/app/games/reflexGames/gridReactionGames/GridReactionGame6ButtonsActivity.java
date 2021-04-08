package messaging.app.games.reflexGames.gridReactionGames;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import messaging.app.R;

public class GridReactionGame6ButtonsActivity extends AppCompatActivity {

    ImageButton btnGrid1Of6;
    ImageButton btnGrid2Of6;
    ImageButton btnGrid3Of6;
    ImageButton btnGrid4Of6;
    ImageButton btnGrid5Of6;
    ImageButton btnGrid6Of6;

    int mRoundNum = 0;
    long mStartTime, mEndTime, mReactionTime;
    Drawable mDefaultColour;

    List<Long> mPastScores = new ArrayList();
    List<Drawable> mPossibleBackgroundColours = new ArrayList<>();
    List<ImageButton> mPossibleButtons = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_6_grid_reaction_game);

        btnGrid1Of6 = findViewById(R.id.btnGrid1Of6);
        btnGrid2Of6 = findViewById(R.id.btnGrid2Of6);
        btnGrid3Of6 = findViewById(R.id.btnGrid3Of6);
        btnGrid4Of6 = findViewById(R.id.btnGrid4Of6);
        btnGrid5Of6 = findViewById(R.id.btnGrid5Of6);
        btnGrid6Of6 = findViewById(R.id.btnGrid6Of6);

        mPossibleBackgroundColours.add(getDrawable(R.drawable.btn_rectangle_blue_gradient));
        mPossibleBackgroundColours.add(getDrawable(R.drawable.btn_rectangle_green_gradient));
        mPossibleBackgroundColours.add(getDrawable(R.drawable.btn_rectangle_orange_gradient));
        mPossibleBackgroundColours.add(getDrawable(R.drawable.btn_rectangle_purple_gradient));
        mPossibleBackgroundColours.add(getDrawable(R.drawable.btn_rectangle_red_gradient));
        mDefaultColour = getDrawable(R.drawable.btn_rectangle_grey_gradient);

        mPossibleButtons.add(btnGrid1Of6);
        mPossibleButtons.add(btnGrid2Of6);
        mPossibleButtons.add(btnGrid3Of6);
        mPossibleButtons.add(btnGrid4Of6);
        mPossibleButtons.add(btnGrid5Of6);
        mPossibleButtons.add(btnGrid6Of6);

        setButtons();
        newRound();

    }


    private void setButtons() {

        //each button, stops the time and records it and resets the game for the next round
        //if there is no more rounds to go then stop and save the game

        for (ImageButton button : mPossibleButtons) {
            button.setEnabled(false);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mEndTime = System.currentTimeMillis();
                    mReactionTime = mEndTime - mStartTime;
                    mPastScores.add(mReactionTime);
                    button.setBackground(mDefaultColour);
                    button.setEnabled(false);

                    if (mRoundNum <= 4) {
                        newRound();
                    } else {
                        completeGame();
                    }
                }
            });
        }
    }


    private void newRound() {
        if (mRoundNum == 5) {
            mRoundNum = 0;
        }
        mRoundNum += 1;

        //get random fields to make the game random, random time delay,
        // random button to highlight, random colour
        Random rand = new Random();
        int randomButtonNum = rand.nextInt(6 - 1 + 1) + 1;

        int delayInMills = rand.nextInt(3500 - 1500 + 1) + 1500;

        int randomDrawableIndex = rand.nextInt((mPossibleBackgroundColours.size() - 1) - 1 + 1) + 1;
        Drawable buttonColour = mPossibleBackgroundColours.get(randomDrawableIndex);


        //start a handler to run a randomly delayed runnable
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //display and enable the random button. Also start timer
                switch (randomButtonNum) {
                    case 1:
                        btnGrid1Of6.setEnabled(true);
                        btnGrid1Of6.setBackground(buttonColour);
                        mStartTime = System.currentTimeMillis();
                        break;

                    case 2:
                        btnGrid2Of6.setEnabled(true);
                        btnGrid2Of6.setBackground(buttonColour);
                        mStartTime = System.currentTimeMillis();
                        break;

                    case 3:
                        btnGrid3Of6.setEnabled(true);
                        btnGrid3Of6.setBackground(buttonColour);
                        mStartTime = System.currentTimeMillis();
                        break;

                    case 4:
                        btnGrid4Of6.setEnabled(true);
                        btnGrid4Of6.setBackground(buttonColour);
                        mStartTime = System.currentTimeMillis();
                        break;

                    case 5:
                        btnGrid5Of6.setEnabled(true);
                        btnGrid5Of6.setBackground(buttonColour);
                        mStartTime = System.currentTimeMillis();
                        break;

                    case 6:
                        btnGrid6Of6.setEnabled(true);
                        btnGrid6Of6.setBackground(buttonColour);
                        mStartTime = System.currentTimeMillis();
                        break;
                }
            }
        }, delayInMills);

    }


    private long getAverage(List<Long> pastScores) {
        long total = 0;
        for (Long score : pastScores) {
            total += score;
        }

        return (total / 5);
    }


    private void completeGame() {

        Log.d("Test", "Average: " + getAverage(mPastScores));
        Intent intent = new Intent(GridReactionGame6ButtonsActivity.this,
                StartGridReactionGameActivity.class);
        intent.putExtra("reactionTime", getAverage(mPastScores));
        GridReactionGame6ButtonsActivity.this.startActivity(intent);
    }
}