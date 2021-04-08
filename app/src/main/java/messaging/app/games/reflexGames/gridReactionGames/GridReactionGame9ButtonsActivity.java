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

public class GridReactionGame9ButtonsActivity extends AppCompatActivity {

    ImageButton btnGrid1Of9;
    ImageButton btnGrid2Of9;
    ImageButton btnGrid3Of9;
    ImageButton btnGrid4Of9;
    ImageButton btnGrid5Of9;
    ImageButton btnGrid6Of9;
    ImageButton btnGrid7Of9;
    ImageButton btnGrid8Of9;
    ImageButton btnGrid9Of9;

    int mRoundNum = 0;
    long mStartTime, mEndTime, mReactionTime;
    Drawable mDefaultColour;
    String mTypeOfLevel = "images";
    Random mRand = new Random();

    List<Long> mPastScores = new ArrayList();
    List<Drawable> mPossibleBackgrounds = new ArrayList<>();
    List<Drawable> mPossibleImages = new ArrayList<>();
    List<ImageButton> mPossibleButtons = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_9_grid_reaction_game);

        btnGrid1Of9 = findViewById(R.id.btnGrid1Of9);
        btnGrid2Of9 = findViewById(R.id.btnGrid2Of9);
        btnGrid3Of9 = findViewById(R.id.btnGrid3Of9);
        btnGrid4Of9 = findViewById(R.id.btnGrid4Of9);
        btnGrid5Of9 = findViewById(R.id.btnGrid5Of9);
        btnGrid6Of9 = findViewById(R.id.btnGrid6Of9);
        btnGrid7Of9 = findViewById(R.id.btnGrid7Of9);
        btnGrid8Of9 = findViewById(R.id.btnGrid8Of9);
        btnGrid9Of9 = findViewById(R.id.btnGrid9Of9);

        fillPossibleBackgrounds(mTypeOfLevel);

        mDefaultColour = getDrawable(R.drawable.btn_rectangle_grey_gradient);

        mPossibleButtons.add(btnGrid1Of9);
        mPossibleButtons.add(btnGrid2Of9);
        mPossibleButtons.add(btnGrid3Of9);
        mPossibleButtons.add(btnGrid4Of9);
        mPossibleButtons.add(btnGrid5Of9);
        mPossibleButtons.add(btnGrid6Of9);
        mPossibleButtons.add(btnGrid7Of9);
        mPossibleButtons.add(btnGrid8Of9);
        mPossibleButtons.add(btnGrid9Of9);


        setButtons();
        newRound();
    }


    private void fillPossibleBackgrounds(String typeOfBackground) {
        if (typeOfBackground.equals("colours")) {
            mPossibleBackgrounds.add(getDrawable(R.drawable.btn_rectangle_blue_gradient));
            mPossibleBackgrounds.add(getDrawable(R.drawable.btn_rectangle_green_gradient));
            mPossibleBackgrounds.add(getDrawable(R.drawable.btn_rectangle_orange_gradient));
            mPossibleBackgrounds.add(getDrawable(R.drawable.btn_rectangle_purple_gradient));
            mPossibleBackgrounds.add(getDrawable(R.drawable.btn_rectangle_red_gradient));
            return;
        }


        int randomImageIndex = mRand.nextInt((2 - 1) - 1 + 1) + 1;
        Log.d("test", "randomImageIndex: " + randomImageIndex);

        switch (randomImageIndex) {
            case 0:
                mPossibleImages.add(getDrawable(R.drawable.friends_shadow_icon));
                mPossibleImages.add(getDrawable(R.drawable.clock_shadow_icon));
                mPossibleImages.add(getDrawable(R.drawable.messages_shadow_icon));
                mPossibleImages.add(getDrawable(R.drawable.closed_envelope_shadow_icon));
                mPossibleImages.add(getDrawable(R.drawable.opened_envelope_shadow_icon));
                break;

            case 1:
                mPossibleImages.add(getDrawable(R.drawable.add_icon));
                mPossibleImages.add(getDrawable(R.drawable.bulb_off_icon));
                mPossibleImages.add(getDrawable(R.drawable.camera_icon));
                mPossibleImages.add(getDrawable(R.drawable.video_icon));
                mPossibleImages.add(getDrawable(R.drawable.cogs_shadow_icon));
                break;
        }


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
                    btnGrid1Of9.setImageDrawable(null);
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
        int randomButtonNum = mRand.nextInt(9 - 1 + 1) + 1;
        int delayInMills = mRand.nextInt(3500 - 1500 + 1) + 1500;


        //start a handler to run a randomly delayed runnable
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Drawable buttonColour = mDefaultColour;
                Drawable buttonImage = null;
                if (mTypeOfLevel.equals("colours")) {
                    int randomColourIndex = mRand.nextInt((mPossibleBackgrounds.size() - 1) - 1 + 1) + 1;
                    buttonColour = mPossibleBackgrounds.get(randomColourIndex);
                } else {
                    int randomImageIndex = mRand.nextInt((mPossibleImages.size() - 1) - 1 + 1) + 1;
                    buttonImage = mPossibleImages.get(randomImageIndex);
                }

                //display and enable the random button. Also start timer
                switch (randomButtonNum) {
                    case 1:
                        btnGrid1Of9.setEnabled(true);
                        btnGrid1Of9.setBackground(buttonColour);
                        btnGrid1Of9.setImageDrawable(buttonImage);
                        mStartTime = System.currentTimeMillis();
                        break;

                    case 2:
                        btnGrid2Of9.setEnabled(true);
                        btnGrid2Of9.setBackground(buttonColour);
                        btnGrid2Of9.setImageDrawable(buttonImage);
                        mStartTime = System.currentTimeMillis();
                        break;

                    case 3:
                        btnGrid3Of9.setEnabled(true);
                        btnGrid3Of9.setBackground(buttonColour);
                        btnGrid3Of9.setImageDrawable(buttonImage);
                        mStartTime = System.currentTimeMillis();
                        break;

                    case 4:
                        btnGrid4Of9.setEnabled(true);
                        btnGrid4Of9.setBackground(buttonColour);
                        btnGrid4Of9.setImageDrawable(buttonImage);
                        mStartTime = System.currentTimeMillis();
                        break;

                    case 5:
                        btnGrid5Of9.setEnabled(true);
                        btnGrid5Of9.setBackground(buttonColour);
                        btnGrid5Of9.setImageDrawable(buttonImage);
                        mStartTime = System.currentTimeMillis();
                        break;

                    case 6:
                        btnGrid6Of9.setEnabled(true);
                        btnGrid6Of9.setBackground(buttonColour);
                        btnGrid6Of9.setImageDrawable(buttonImage);
                        mStartTime = System.currentTimeMillis();
                        break;

                    case 7:
                        btnGrid7Of9.setEnabled(true);
                        btnGrid7Of9.setBackground(buttonColour);
                        btnGrid7Of9.setImageDrawable(buttonImage);
                        mStartTime = System.currentTimeMillis();
                        break;

                    case 8:
                        btnGrid8Of9.setEnabled(true);
                        btnGrid8Of9.setBackground(buttonColour);
                        btnGrid8Of9.setImageDrawable(buttonImage);
                        mStartTime = System.currentTimeMillis();
                        break;

                    case 9:
                        btnGrid9Of9.setEnabled(true);
                        btnGrid9Of9.setBackground(buttonColour);
                        btnGrid9Of9.setImageDrawable(buttonImage);
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
        Intent intent = new Intent(GridReactionGame9ButtonsActivity.this,
                StartGridReactionGameActivity.class);
        intent.putExtra("reactionTime", getAverage(mPastScores));
        GridReactionGame9ButtonsActivity.this.startActivity(intent);
    }
}