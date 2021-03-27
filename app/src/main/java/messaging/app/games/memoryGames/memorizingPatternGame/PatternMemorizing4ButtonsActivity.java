package messaging.app.games.memoryGames.memorizingPatternGame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import messaging.app.R;
import messaging.app.games.memoryGames.SelectMemoryGameActivity;

public class PatternMemorizing4ButtonsActivity extends AppCompatActivity {

    ImageButton btnGrid1Of4;
    ImageButton btnGrid2Of4;
    ImageButton btnGrid3Of4;
    ImageButton btnGrid4Of4;

    int currentRound;
    int currentLevel = 3;
    List<Integer> buttonPatternOrder = new ArrayList();
    Integer[] possibleButtonIDs;

    Drawable defaultColour;
    List<Drawable> possibleBackgroundColours = new ArrayList<>();
    List<Drawable> displayedColoursOrder = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern_memorizing4_buttons);

        btnGrid1Of4 = findViewById(R.id.btnGrid1Of4);
        btnGrid2Of4 = findViewById(R.id.btnGrid2Of4);
        btnGrid3Of4 = findViewById(R.id.btnGrid3Of4);
        btnGrid4Of4 = findViewById(R.id.btnGrid4Of4);

        possibleBackgroundColours.add(getDrawable(R.drawable.btn_rectangle_blue_gradient));
        possibleBackgroundColours.add(getDrawable(R.drawable.btn_rectangle_green_gradient));
        possibleBackgroundColours.add(getDrawable(R.drawable.btn_rectangle_orange_gradient));
        possibleBackgroundColours.add(getDrawable(R.drawable.btn_rectangle_purple_gradient));
        possibleBackgroundColours.add(getDrawable(R.drawable.btn_rectangle_red_gradient));
        defaultColour = getDrawable(R.drawable.btn_rectangle_grey_gradient);

        possibleButtonIDs = new Integer[]{btnGrid1Of4.getId(), btnGrid2Of4.getId(), btnGrid3Of4.getId(), btnGrid4Of4.getId()};

        startLevel();
        setButtonClicks();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void setButtonClicks() {
        btnGrid1Of4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnGrid1Of4.setBackground(defaultColour);

                int currentID = btnGrid1Of4.getId();

                if (buttonPatternOrder.get(currentRound) == currentID) {
                    Log.d("test", "Correct");
                    //correct

                    //display the button to the user, and change to default color after 1 seconds
                    toggleButton(btnGrid1Of4.getId(), (0), (500), displayedColoursOrder.get(currentRound));

                    currentRound++;
                    if (currentRound == currentLevel + 2) {
                        currentLevel++;
                        startLevel();
                    }
                } else {
                    Log.d("test", "Wrong");

                    Intent intent = new Intent(PatternMemorizing4ButtonsActivity.this, StartMemorizingPatternActivity.class);
                    intent.putExtra("level", "2x2");
                    intent.putExtra("highScore", currentLevel);
                    PatternMemorizing4ButtonsActivity.this.startActivity(intent);
                }
            }
        });



        btnGrid2Of4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnGrid2Of4.setBackground(defaultColour);

                int currentID = btnGrid2Of4.getId();

                if (buttonPatternOrder.get(currentRound) == currentID) {
                    Log.d("test", "Correct");
                    //correct

                    //display the button to the user, and change to default color after 1 seconds
                    toggleButton(btnGrid2Of4.getId(), (0), (500), displayedColoursOrder.get(currentRound));

                    currentRound++;
                    if (currentRound == currentLevel + 2) {
                        currentLevel++;
                        startLevel();
                    }
                } else {
                    Log.d("test", "Wrong");

                    Intent intent = new Intent(PatternMemorizing4ButtonsActivity.this, StartMemorizingPatternActivity.class);
                    intent.putExtra("level", "2x2");
                    intent.putExtra("highScore", currentLevel);
                    PatternMemorizing4ButtonsActivity.this.startActivity(intent);
                }
            }
        });



        btnGrid3Of4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnGrid3Of4.setBackground(defaultColour);

                int currentID = btnGrid3Of4.getId();

                if (buttonPatternOrder.get(currentRound) == currentID) {
                    Log.d("test", "Correct");
                    //correct

                    //display the button to the user, and change to default color after 1 seconds
                    toggleButton(btnGrid3Of4.getId(), (0), (500), displayedColoursOrder.get(currentRound));

                    currentRound++;
                    if (currentRound == currentLevel + 2) {
                        currentLevel++;
                        startLevel();
                    }
                } else {
                    Log.d("test", "Wrong");

                    Intent intent = new Intent(PatternMemorizing4ButtonsActivity.this, StartMemorizingPatternActivity.class);
                    intent.putExtra("level", "2x2");
                    intent.putExtra("highScore", currentLevel);
                    PatternMemorizing4ButtonsActivity.this.startActivity(intent);
                }
            }
        });



        btnGrid4Of4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnGrid4Of4.setBackground(defaultColour);

                int currentID = btnGrid4Of4.getId();

                if (buttonPatternOrder.get(currentRound) == currentID) {
                    Log.d("test", "Correct");
                    //correct

                    //display the button to the user, and change to default color after 1 seconds
                    toggleButton(btnGrid4Of4.getId(), (0), (500), displayedColoursOrder.get(currentRound));

                    currentRound++;
                    if (currentRound == currentLevel + 2) {
                        currentLevel++;
                        startLevel();
                    }
                } else {
                    Log.d("test", "Wrong");

                    Intent intent = new Intent(PatternMemorizing4ButtonsActivity.this, StartMemorizingPatternActivity.class);
                    intent.putExtra("level", "2x2");
                    intent.putExtra("highScore", currentLevel);
                    PatternMemorizing4ButtonsActivity.this.startActivity(intent);
                }
            }
        });
    }


    private void startLevel() {
        int numberOfRounds = currentLevel + 2;
        buttonPatternOrder = new ArrayList();
        currentRound = 0;

        //get a random button for each round;
        for (int i = 0; i < numberOfRounds; i++) {


            //get a random position in the buttonID list
            Random rand = new Random();
            int randomButtonNum = rand.nextInt(possibleButtonIDs.length - 1);

            //add the random button ID to the pattern order
            int buttonID = possibleButtonIDs[randomButtonNum];
            buttonPatternOrder.add(buttonID);

            //get a random position in the colours list
            int randomColourNum = rand.nextInt(possibleBackgroundColours.size());

            //get a random colour and save it in the list of displayed colours
            Drawable currentBackgroundColour = possibleBackgroundColours.get(randomColourNum);
            displayedColoursOrder.add(currentBackgroundColour);


            //display the button to the user, and change to default color after 0.8 seconds
              toggleButton(buttonID, (i * 1300), (i * 1300 + 800), currentBackgroundColour);
        }

    }


    private void toggleButton(int buttonID, int colourCountDownTimer, int defaultColourCountDownTimer, Drawable colour){

        if(buttonID == possibleButtonIDs[0]){
            new CountDownTimer(colourCountDownTimer, 50) {
                @Override
                public void onTick(long arg0) {
                }

                @Override
                public void onFinish() {
                    btnGrid1Of4.setBackground(colour);
                }
            }.start();

            new CountDownTimer(defaultColourCountDownTimer, 50) {
                @Override
                public void onTick(long arg0) {
                }

                @Override
                public void onFinish() {
                    btnGrid1Of4.setBackground(defaultColour);
                }
            }.start();


        }
        else if(buttonID == possibleButtonIDs[1]){
            new CountDownTimer(colourCountDownTimer, 50) {
                @Override
                public void onTick(long arg0) {
                }

                @Override
                public void onFinish() {
                    btnGrid2Of4.setBackground(colour);
                }
            }.start();

            new CountDownTimer(defaultColourCountDownTimer, 50) {
                @Override
                public void onTick(long arg0) {
                }

                @Override
                public void onFinish() {
                    btnGrid2Of4.setBackground(defaultColour);
                }
            }.start();

        }
        else if(buttonID == possibleButtonIDs[2]){
            new CountDownTimer(colourCountDownTimer, 50) {
                @Override
                public void onTick(long arg0) {
                }

                @Override
                public void onFinish() {
                    btnGrid3Of4.setBackground(colour);
                }
            }.start();

            new CountDownTimer(defaultColourCountDownTimer, 50) {
                @Override
                public void onTick(long arg0) {
                }

                @Override
                public void onFinish() {
                    btnGrid3Of4.setBackground(defaultColour);
                }
            }.start();

        }
        else if(buttonID == possibleButtonIDs[3]){
            new CountDownTimer(colourCountDownTimer, 50) {
                @Override
                public void onTick(long arg0) {
                }

                @Override
                public void onFinish() {
                    btnGrid4Of4.setBackground(colour);
                }
            }.start();

            new CountDownTimer(defaultColourCountDownTimer, 50) {
                @Override
                public void onTick(long arg0) {
                }

                @Override
                public void onFinish() {
                    btnGrid4Of4.setBackground(defaultColour);
                }
            }.start();
        }

    }
}