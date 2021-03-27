package messaging.app.games.memoryGames.memorizingPatternGame;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import messaging.app.R;

public class PatternMemorizing6ButtonsActivity extends AppCompatActivity {


    ImageButton btnGrid1Of6;
    ImageButton btnGrid2Of6;
    ImageButton btnGrid3Of6;
    ImageButton btnGrid4Of6;
    ImageButton btnGrid5Of6;
    ImageButton btnGrid6Of6;
    TextView lblCurrentStatus;

    int currentButtonNum = 0;
    int currentRound;
    int currentLevel = 3;
    int numberOfRounds = currentLevel + 2;

    List<ImageButton> buttonPatternOrder = new ArrayList();
    ImageButton[] possibleButtons;

    Drawable defaultColour;
    List<Drawable> possibleBackgroundColours = new ArrayList<>();
    List<Drawable> displayedColoursOrder = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern_memorizing6_buttons);

        btnGrid1Of6 = findViewById(R.id.btnGrid1Of6);
        btnGrid2Of6 = findViewById(R.id.btnGrid2Of6);
        btnGrid3Of6 = findViewById(R.id.btnGrid3Of6);
        btnGrid4Of6 = findViewById(R.id.btnGrid4Of6);
        btnGrid5Of6 = findViewById(R.id.btnGrid5Of6);
        btnGrid6Of6 = findViewById(R.id.btnGrid6Of6);
        lblCurrentStatus = findViewById(R.id.lblCurrentStatus);

        lblCurrentStatus.setVisibility(View.INVISIBLE);

        possibleBackgroundColours.add(getDrawable(R.drawable.btn_rectangle_blue_gradient));
        possibleBackgroundColours.add(getDrawable(R.drawable.btn_rectangle_green_gradient));
        possibleBackgroundColours.add(getDrawable(R.drawable.btn_rectangle_orange_gradient));
        possibleBackgroundColours.add(getDrawable(R.drawable.btn_rectangle_purple_gradient));
        possibleBackgroundColours.add(getDrawable(R.drawable.btn_rectangle_red_gradient));
        defaultColour = getDrawable(R.drawable.btn_rectangle_grey_gradient);

        possibleButtons = new ImageButton[]{btnGrid1Of6, btnGrid2Of6, btnGrid3Of6, btnGrid4Of6, btnGrid5Of6, btnGrid6Of6};

        startLevel();
        setButtonClicks();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void setButtonClicks() {
        for (ImageButton button : possibleButtons) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    button.setBackground(defaultColour);

                    if (buttonPatternOrder.get(currentRound) == button) {
                        Log.d("test", "Correct");
                        //correct

                        //display the button to the user, and change to default color after 1 seconds
                        toggleButton(button, (0), (500), displayedColoursOrder.get(currentRound));

                        currentRound++;
                        if (currentRound == numberOfRounds) {
                            currentLevel++;
                            lblCurrentStatus.setText("Complete");
                            lblCurrentStatus.setVisibility(View.VISIBLE);
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
                        }
                    } else {
                        Log.d("test", "Wrong");

                        Intent intent = new Intent(PatternMemorizing6ButtonsActivity.this, StartMemorizingPatternActivity.class);
                        intent.putExtra("level", "2x2");
                        intent.putExtra("highScore", currentLevel);
                        PatternMemorizing6ButtonsActivity.this.startActivity(intent);
                    }
                }
            });
        }
    }


    private void startLevel() {
        buttonPatternOrder = new ArrayList();
        displayedColoursOrder = new ArrayList();
        currentRound = 0;
        currentButtonNum = 0;

        btnGrid1Of6.setEnabled(false);
        btnGrid2Of6.setEnabled(false);
        btnGrid3Of6.setEnabled(false);
        btnGrid4Of6.setEnabled(false);
        btnGrid5Of6.setEnabled(false);
        btnGrid6Of6.setEnabled(false);

        //get a random button for each round;
        for (int i = 0; i < numberOfRounds; i++) {


            //get a random position in the button list
            Random rand = new Random();
            int randomButtonNum = rand.nextInt(possibleButtons.length - 1);

            //add the random button  to the pattern order
            ImageButton button = possibleButtons[randomButtonNum];
            buttonPatternOrder.add(button);

            //get a random position in the colours list
            int randomColourNum = rand.nextInt(possibleBackgroundColours.size());

            //get a random colour and save it in the list of displayed colours
            Drawable currentBackgroundColour = possibleBackgroundColours.get(randomColourNum);
            displayedColoursOrder.add(currentBackgroundColour);


            //display the button to the user, and change to default color after 0.8 seconds
            toggleButton(button, (i * 1300), (i * 1300 + 800), currentBackgroundColour);
        }

        btnGrid1Of6.setEnabled(true);
        btnGrid2Of6.setEnabled(true);
        btnGrid3Of6.setEnabled(true);
        btnGrid4Of6.setEnabled(true);
        btnGrid5Of6.setEnabled(true);
        btnGrid6Of6.setEnabled(true);
    }


    private void toggleButton(ImageButton button, int colourCountDownTimer, int defaultColourCountDownTimer, Drawable colour) {

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
                button.setBackground(defaultColour);
                currentButtonNum++;

                if(currentButtonNum >= numberOfRounds){
                    currentButtonNum = -2000;

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

                    btnGrid2Of6.setEnabled(true);
                    btnGrid3Of6.setEnabled(true);
                    btnGrid4Of6.setEnabled(true);
                    btnGrid5Of6.setEnabled(true);
                    btnGrid6Of6.setEnabled(true);
                }
            }
        }.start();

    }
}