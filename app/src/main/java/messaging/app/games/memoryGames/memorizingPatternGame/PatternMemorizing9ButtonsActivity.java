package messaging.app.games.memoryGames.memorizingPatternGame;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import messaging.app.R;

public class PatternMemorizing9ButtonsActivity extends AppCompatActivity {


    ImageButton btnGrid1Of9;
    ImageButton btnGrid2Of9;
    ImageButton btnGrid3Of9;
    ImageButton btnGrid4Of9;
    ImageButton btnGrid5Of9;
    ImageButton btnGrid6Of9;
    ImageButton btnGrid7Of9;
    ImageButton btnGrid8Of9;
    ImageButton btnGrid9Of9;

    int currentRound;
    int currentLevel = 3;
    List<Integer> levels = new ArrayList();
    List<Integer> buttonPatternOrder = new ArrayList();
    Integer[] possibleButtonIDs;

    Drawable defaultColour;
    List<Drawable> possibleBackgroundColours = new ArrayList<>();
    List<Drawable> displayedColoursOrder = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern_memorizing6_buttons);

        btnGrid1Of9 = findViewById(R.id.btnGrid1Of9);
        btnGrid2Of9 = findViewById(R.id.btnGrid2Of9);
        btnGrid3Of9 = findViewById(R.id.btnGrid3Of9);
        btnGrid4Of9 = findViewById(R.id.btnGrid4Of9);
        btnGrid5Of9 = findViewById(R.id.btnGrid5Of9);
        btnGrid6Of9 = findViewById(R.id.btnGrid6Of9);
        btnGrid7Of9 = findViewById(R.id.btnGrid7Of9);
        btnGrid8Of9 = findViewById(R.id.btnGrid8Of9);
        btnGrid9Of9 = findViewById(R.id.btnGrid9Of9);


        possibleBackgroundColours.add(getDrawable(R.drawable.btn_rectangle_blue_gradient));
        possibleBackgroundColours.add(getDrawable(R.drawable.btn_rectangle_green_gradient));
        possibleBackgroundColours.add(getDrawable(R.drawable.btn_rectangle_orange_gradient));
        possibleBackgroundColours.add(getDrawable(R.drawable.btn_rectangle_purple_gradient));
        possibleBackgroundColours.add(getDrawable(R.drawable.btn_rectangle_red_gradient));
        defaultColour = getDrawable(R.drawable.btn_rectangle_grey_gradient);

        possibleButtonIDs = new Integer[]{
                btnGrid1Of9.getId(), btnGrid2Of9.getId(), btnGrid3Of9.getId(),
                btnGrid4Of9.getId(), btnGrid5Of9.getId(), btnGrid6Of9.getId(),
                btnGrid7Of9.getId(), btnGrid8Of9.getId(), btnGrid9Of9.getId(),
        };


        levels.add(1);
        levels.add(2);
        levels.add(3);
        levels.add(4);
        levels.add(5);
        levels.add(6);
        levels.add(7);
        levels.add(8);
        levels.add(9);


        startLevel();
        setButtonClicks();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void setButtonClicks() {
        btnGrid1Of9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnGrid1Of9.setBackground(defaultColour);

                int currentID = btnGrid1Of9.getId();

                if (buttonPatternOrder.get(currentRound) == currentID) {
                    Log.d("test", "Correct");
                    //correct

                    //display the button to the user, and change to default color after 1 seconds
                    toggleButton(btnGrid1Of9.getId(), (0), (500), displayedColoursOrder.get(currentRound));

                    currentRound++;
                    if (currentRound == levels.get(currentLevel)) {
                        currentLevel++;
                        startLevel();
                    }
                } else {
                    Log.d("test", "Wrong");
                    //wrong
                }
            }
        });



        btnGrid2Of9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnGrid2Of9.setBackground(defaultColour);

                int currentID = btnGrid2Of9.getId();

                if (buttonPatternOrder.get(currentRound) == currentID) {
                    Log.d("test", "Correct");
                    //correct

                    //display the button to the user, and change to default color after 1 seconds
                    toggleButton(btnGrid2Of9.getId(), (0), (500), displayedColoursOrder.get(currentRound));

                    currentRound++;
                    if (currentRound == levels.get(currentLevel)) {
                        currentLevel++;
                        startLevel();
                    }
                } else {
                    Log.d("test", "Wrong");
                    //wrong
                }
            }
        });



        btnGrid3Of9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnGrid3Of9.setBackground(defaultColour);

                int currentID = btnGrid3Of9.getId();

                if (buttonPatternOrder.get(currentRound) == currentID) {
                    Log.d("test", "Correct");
                    //correct

                    //display the button to the user, and change to default color after 1 seconds
                    toggleButton(btnGrid3Of9.getId(), (0), (500), displayedColoursOrder.get(currentRound));

                    currentRound++;
                    if (currentRound == levels.get(currentLevel)) {
                        currentLevel++;
                        startLevel();
                    }
                } else {
                    Log.d("test", "Wrong");
                    //wrong
                }
            }
        });



        btnGrid4Of9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnGrid4Of9.setBackground(defaultColour);

                int currentID = btnGrid4Of9.getId();

                if (buttonPatternOrder.get(currentRound) == currentID) {
                    Log.d("test", "Correct");
                    //correct

                    //display the button to the user, and change to default color after 1 seconds
                    toggleButton(btnGrid4Of9.getId(), (0), (500), displayedColoursOrder.get(currentRound));

                    currentRound++;
                    if (currentRound == levels.get(currentLevel)) {
                        currentLevel++;
                        startLevel();
                    }
                } else {
                    Log.d("test", "Wrong");
                    //wrong
                }
            }
        });



        btnGrid5Of9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnGrid5Of9.setBackground(defaultColour);

                int currentID = btnGrid5Of9.getId();

                if (buttonPatternOrder.get(currentRound) == currentID) {
                    Log.d("test", "Correct");
                    //correct

                    //display the button to the user, and change to default color after 1 seconds
                    toggleButton(btnGrid5Of9.getId(), (0), (500), displayedColoursOrder.get(currentRound));

                    currentRound++;
                    if (currentRound == levels.get(currentLevel)) {
                        currentLevel++;
                        startLevel();
                    }
                } else {
                    Log.d("test", "Wrong");
                    //wrong
                }
            }
        });



        btnGrid6Of9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnGrid6Of9.setBackground(defaultColour);

                int currentID = btnGrid6Of9.getId();

                if (buttonPatternOrder.get(currentRound) == currentID) {
                    Log.d("test", "Correct");
                    //correct

                    //display the button to the user, and change to default color after 1 seconds
                    toggleButton(btnGrid6Of9.getId(), (0), (500), displayedColoursOrder.get(currentRound));

                    currentRound++;
                    if (currentRound == levels.get(currentLevel)) {
                        currentLevel++;
                        startLevel();
                    }
                } else {
                    Log.d("test", "Wrong");
                    //wrong
                }
            }
        });



        btnGrid7Of9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnGrid7Of9.setBackground(defaultColour);

                int currentID = btnGrid7Of9.getId();

                if (buttonPatternOrder.get(currentRound) == currentID) {
                    Log.d("test", "Correct");
                    //correct

                    //display the button to the user, and change to default color after 1 seconds
                    toggleButton(btnGrid7Of9.getId(), (0), (500), displayedColoursOrder.get(currentRound));

                    currentRound++;
                    if (currentRound == levels.get(currentLevel)) {
                        currentLevel++;
                        startLevel();
                    }
                } else {
                    Log.d("test", "Wrong");
                    //wrong
                }
            }
        });



        btnGrid8Of9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnGrid8Of9.setBackground(defaultColour);

                int currentID = btnGrid8Of9.getId();

                if (buttonPatternOrder.get(currentRound) == currentID) {
                    Log.d("test", "Correct");
                    //correct

                    //display the button to the user, and change to default color after 1 seconds
                    toggleButton(btnGrid8Of9.getId(), (0), (500), displayedColoursOrder.get(currentRound));

                    currentRound++;
                    if (currentRound == levels.get(currentLevel)) {
                        currentLevel++;
                        startLevel();
                    }
                } else {
                    Log.d("test", "Wrong");
                    //wrong
                }
            }
        });



        btnGrid9Of9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnGrid9Of9.setBackground(defaultColour);

                int currentID = btnGrid9Of9.getId();

                if (buttonPatternOrder.get(currentRound) == currentID) {
                    Log.d("test", "Correct");
                    //correct

                    //display the button to the user, and change to default color after 1 seconds
                    toggleButton(btnGrid9Of9.getId(), (0), (500), displayedColoursOrder.get(currentRound));

                    currentRound++;
                    if (currentRound == levels.get(currentLevel)) {
                        currentLevel++;
                        startLevel();
                    }
                } else {
                    Log.d("test", "Wrong");
                    //wrong
                }
            }
        });
    }


    private void startLevel() {
        int numberOfRounds = levels.get(currentLevel);
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
                    btnGrid1Of9.setBackground(colour);
                }
            }.start();

            new CountDownTimer(defaultColourCountDownTimer, 50) {
                @Override
                public void onTick(long arg0) {
                }

                @Override
                public void onFinish() {
                    btnGrid1Of9.setBackground(defaultColour);
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
                    btnGrid2Of9.setBackground(colour);
                }
            }.start();

            new CountDownTimer(defaultColourCountDownTimer, 50) {
                @Override
                public void onTick(long arg0) {
                }

                @Override
                public void onFinish() {
                    btnGrid2Of9.setBackground(defaultColour);
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
                    btnGrid3Of9.setBackground(colour);
                }
            }.start();

            new CountDownTimer(defaultColourCountDownTimer, 50) {
                @Override
                public void onTick(long arg0) {
                }

                @Override
                public void onFinish() {
                    btnGrid3Of9.setBackground(defaultColour);
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
                    btnGrid4Of9.setBackground(colour);
                }
            }.start();

            new CountDownTimer(defaultColourCountDownTimer, 50) {
                @Override
                public void onTick(long arg0) {
                }

                @Override
                public void onFinish() {
                    btnGrid4Of9.setBackground(defaultColour);
                }
            }.start();
        }
        else if(buttonID == possibleButtonIDs[4]){
            new CountDownTimer(colourCountDownTimer, 50) {
                @Override
                public void onTick(long arg0) {
                }

                @Override
                public void onFinish() {
                    btnGrid5Of9.setBackground(colour);
                }
            }.start();

            new CountDownTimer(defaultColourCountDownTimer, 50) {
                @Override
                public void onTick(long arg0) {
                }

                @Override
                public void onFinish() {
                    btnGrid5Of9.setBackground(defaultColour);
                }
            }.start();
        }
        else if(buttonID == possibleButtonIDs[5]){
            new CountDownTimer(colourCountDownTimer, 50) {
                @Override
                public void onTick(long arg0) {
                }

                @Override
                public void onFinish() {
                    btnGrid6Of9.setBackground(colour);
                }
            }.start();

            new CountDownTimer(defaultColourCountDownTimer, 50) {
                @Override
                public void onTick(long arg0) {
                }

                @Override
                public void onFinish() {
                    btnGrid6Of9.setBackground(defaultColour);
                }
            }.start();
        }
        else if(buttonID == possibleButtonIDs[6]){
            new CountDownTimer(colourCountDownTimer, 50) {
                @Override
                public void onTick(long arg0) {
                }

                @Override
                public void onFinish() {
                    btnGrid7Of9.setBackground(colour);
                }
            }.start();

            new CountDownTimer(defaultColourCountDownTimer, 50) {
                @Override
                public void onTick(long arg0) {
                }

                @Override
                public void onFinish() {
                    btnGrid7Of9.setBackground(defaultColour);
                }
            }.start();
        }
        else if(buttonID == possibleButtonIDs[7]){
            new CountDownTimer(colourCountDownTimer, 50) {
                @Override
                public void onTick(long arg0) {
                }

                @Override
                public void onFinish() {
                    btnGrid8Of9.setBackground(colour);
                }
            }.start();

            new CountDownTimer(defaultColourCountDownTimer, 50) {
                @Override
                public void onTick(long arg0) {
                }

                @Override
                public void onFinish() {
                    btnGrid8Of9.setBackground(defaultColour);
                }
            }.start();
        }
        else if(buttonID == possibleButtonIDs[8]){
            new CountDownTimer(colourCountDownTimer, 50) {
                @Override
                public void onTick(long arg0) {
                }

                @Override
                public void onFinish() {
                    btnGrid9Of9.setBackground(colour);
                }
            }.start();

            new CountDownTimer(defaultColourCountDownTimer, 50) {
                @Override
                public void onTick(long arg0) {
                }

                @Override
                public void onFinish() {
                    btnGrid9Of9.setBackground(defaultColour);
                }
            }.start();
        }
    }
}
