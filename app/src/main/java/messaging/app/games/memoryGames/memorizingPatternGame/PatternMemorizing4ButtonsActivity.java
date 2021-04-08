package messaging.app.games.memoryGames.memorizingPatternGame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import messaging.app.R;

public class PatternMemorizing4ButtonsActivity extends AppCompatActivity {

    ImageButton btnGrid1Of4;
    ImageButton btnGrid2Of4;
    ImageButton btnGrid3Of4;
    ImageButton btnGrid4Of4;
    TextView lblCurrentStatus;

    int mCurrentButtonNum = 0;
    int mCurrentRound;
    int mCurrentLevel = 1;
    int mNumberOfRounds;

    List<ImageButton> mButtonPatternOrder = new ArrayList();
    List<Drawable> mPossibleBackgroundColours = new ArrayList<>();
    List<Drawable> mDisplayedColoursOrder = new ArrayList();
    ImageButton[] mPossibleButtons;

    Drawable mDefaultColour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern_memorizing4_buttons);

        btnGrid1Of4 = findViewById(R.id.btnGrid1Of4);
        btnGrid2Of4 = findViewById(R.id.btnGrid2Of4);
        btnGrid3Of4 = findViewById(R.id.btnGrid3Of4);
        btnGrid4Of4 = findViewById(R.id.btnGrid4Of4);
        lblCurrentStatus = findViewById(R.id.lblCurrentStatus);

        lblCurrentStatus.setVisibility(View.INVISIBLE);

        mPossibleBackgroundColours.add(getDrawable(R.drawable.btn_rectangle_blue_gradient));
        mPossibleBackgroundColours.add(getDrawable(R.drawable.btn_rectangle_green_gradient));
        mPossibleBackgroundColours.add(getDrawable(R.drawable.btn_rectangle_orange_gradient));
        mPossibleBackgroundColours.add(getDrawable(R.drawable.btn_rectangle_purple_gradient));
        mPossibleBackgroundColours.add(getDrawable(R.drawable.btn_rectangle_red_gradient));
        mDefaultColour = getDrawable(R.drawable.btn_rectangle_grey_gradient);

        mPossibleButtons = new ImageButton[]{btnGrid1Of4, btnGrid2Of4, btnGrid3Of4, btnGrid4Of4};

        btnGrid1Of4.setEnabled(false);
        btnGrid2Of4.setEnabled(false);
        btnGrid3Of4.setEnabled(false);
        btnGrid4Of4.setEnabled(false);

        startLevel();
        setButtonClicks();
    }


    @Override
    protected void onStart() {
        super.onStart();
    }


    private void setButtonClicks() {
        for (ImageButton button : mPossibleButtons) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    button.setBackground(mDefaultColour);

                    if (mButtonPatternOrder.get(mCurrentRound) == button) {
                        Log.d("test", "Correct");
                        //correct

                        //display the button to the user, and change to default color after 1 seconds
                        toggleButton(button, (0), (500), mDisplayedColoursOrder.get(mCurrentRound));

                        mCurrentRound++;
                        if (mCurrentRound == mNumberOfRounds) {
                            mCurrentLevel++;
                            if (mCurrentLevel < 8) {
                                lblCurrentStatus.setText("Complete");
                                lblCurrentStatus.setVisibility(View.VISIBLE);

                                btnGrid1Of4.setEnabled(false);
                                btnGrid2Of4.setEnabled(false);
                                btnGrid3Of4.setEnabled(false);
                                btnGrid4Of4.setEnabled(false);

                                new CountDownTimer(1500, 50) {
                                    @Override
                                    public void onTick(long arg0) {
                                    }

                                    @Override
                                    public void onFinish() {
                                        lblCurrentStatus.setVisibility(View.INVISIBLE);
                                        startLevel();
                                    }
                                }.start();
                            } else {
                                //user has hit the target number
                                Intent intent = new Intent(PatternMemorizing4ButtonsActivity.this,
                                        StartMemorizingPatternActivity.class);
                                intent.putExtra("level", "2x2");
                                intent.putExtra("highScore", mCurrentLevel);
                                PatternMemorizing4ButtonsActivity.this.startActivity(intent);
                            }
                        }
                    } else {
                        Log.d("test", "Wrong");

                        Intent intent = new Intent(PatternMemorizing4ButtonsActivity.this,
                                StartMemorizingPatternActivity.class);
                        intent.putExtra("level", "2x2");
                        intent.putExtra("highScore", mCurrentLevel);
                        PatternMemorizing4ButtonsActivity.this.startActivity(intent);
                    }
                }
            });
        }
    }


    private void startLevel() {
        mButtonPatternOrder = new ArrayList();
        mDisplayedColoursOrder = new ArrayList();
        mCurrentRound = 0;
        mCurrentButtonNum = 0;
        mNumberOfRounds = mCurrentLevel + 2;

        //get a random button for each round;
        for (int i = 0; i < mNumberOfRounds; i++) {


            //get a random position in the button list
            Random rand = new Random();
            int randomButtonNum = rand.nextInt(mPossibleButtons.length - 1);

            //add the random button  to the pattern order
            ImageButton button = mPossibleButtons[randomButtonNum];
            mButtonPatternOrder.add(button);

            //get a random position in the colours list
            int randomColourNum = rand.nextInt(mPossibleBackgroundColours.size());

            //get a random colour and save it in the list of displayed colours
            Drawable currentBackgroundColour = mPossibleBackgroundColours.get(randomColourNum);
            mDisplayedColoursOrder.add(currentBackgroundColour);
            Log.d("Test", "displayedColoursOrder" + mDisplayedColoursOrder);

            //display the button to the user, and change to default color after 0.8 seconds
            toggleButton(button, (i * 1300), (i * 1300 + 800), currentBackgroundColour);
        }
    }

    private void toggleButton(ImageButton button, int colourCountDownTimer,
                              int defaultColourCountDownTimer, Drawable colour) {

        new CountDownTimer(colourCountDownTimer, 50) {
            @Override
            public void onTick(long arg0) {
            }

            @Override
            public void onFinish() {
                button.setBackground(colour);
            }
        }.start();

        new CountDownTimer(defaultColourCountDownTimer, 50) {
            @Override
            public void onTick(long arg0) {
            }

            @Override
            public void onFinish() {
                button.setBackground(mDefaultColour);
                mCurrentButtonNum++;

                if (mCurrentButtonNum >= mNumberOfRounds) {
                    mCurrentButtonNum = -2000;

                    lblCurrentStatus.setText("GO");
                    lblCurrentStatus.setVisibility(View.VISIBLE);
                    new CountDownTimer(1200, 50) {
                        @Override
                        public void onTick(long arg0) {
                        }

                        @Override
                        public void onFinish() {
                            lblCurrentStatus.setVisibility(View.INVISIBLE);
                        }
                    }.start();

                    btnGrid1Of4.setEnabled(true);
                    btnGrid2Of4.setEnabled(true);
                    btnGrid3Of4.setEnabled(true);
                    btnGrid4Of4.setEnabled(true);
                }
            }
        }.start();

    }
}