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

    int roundNum = 0;
    List<Long> pastScores = new ArrayList();
    long startTime, endTime, reactionTime;

    Drawable defaultColour;
    List<Drawable> possibleBackgrounds = new ArrayList<>();
    List<Drawable> possibleImages = new ArrayList<>();
    Random rand = new Random();

    String typeOfLevel = "images";

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

        fillPossibleBackgrounds(typeOfLevel);

        defaultColour = getDrawable(R.drawable.btn_rectangle_grey_gradient);



        btnGrid1Of9.setEnabled(false);
        btnGrid2Of9.setEnabled(false);
        btnGrid3Of9.setEnabled(false);
        btnGrid4Of9.setEnabled(false);
        btnGrid5Of9.setEnabled(false);
        btnGrid6Of9.setEnabled(false);
        btnGrid7Of9.setEnabled(false);
        btnGrid8Of9.setEnabled(false);
        btnGrid9Of9.setEnabled(false);


        setButtonClicks();
        newRound();
    }



    private void fillPossibleBackgrounds(String typeOfBackground) {

        if(typeOfBackground.equals("colours")){
            possibleBackgrounds.add(getDrawable(R.drawable.btn_rectangle_blue_gradient));
            possibleBackgrounds.add(getDrawable(R.drawable.btn_rectangle_green_gradient));
            possibleBackgrounds.add(getDrawable(R.drawable.btn_rectangle_orange_gradient));
            possibleBackgrounds.add(getDrawable(R.drawable.btn_rectangle_purple_gradient));
            possibleBackgrounds.add(getDrawable(R.drawable.btn_rectangle_red_gradient));
            return;
        }


        int randomImageIndex = rand.nextInt((2 - 1) - 1 + 1) + 1;
        Log.d("test", "randomImageIndex: " + randomImageIndex);

        switch (randomImageIndex){
            case 0:
                possibleImages.add(getDrawable(R.drawable.friends_shadow_icon));
                possibleImages.add(getDrawable(R.drawable.clock_shadow_icon));
                possibleImages.add(getDrawable(R.drawable.messages_shadow_icon));
                possibleImages.add(getDrawable(R.drawable.closed_envelope_shadow_icon));
                possibleImages.add(getDrawable(R.drawable.opened_envelope_shadow_icon));
                break;

            case 1:
                possibleImages.add(getDrawable(R.drawable.add_icon));
                possibleImages.add(getDrawable(R.drawable.bulb_off_icon));
                possibleImages.add(getDrawable(R.drawable.camera_icon));
                possibleImages.add(getDrawable(R.drawable.video_icon));
                possibleImages.add(getDrawable(R.drawable.cogs_shadow_icon));
                break;
        }


    }


    private void setButtonClicks() {

        //each button, stops the time and records it and resets the game for the next round
        //if there is no more rounds to go then stop and save the game

        btnGrid1Of9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endTime = System.currentTimeMillis();
                reactionTime = endTime - startTime;
                pastScores.add(reactionTime);

                btnGrid1Of9.setBackground(defaultColour);
                btnGrid1Of9.setImageDrawable(null);
                btnGrid1Of9.setEnabled(false);

                if (roundNum <= 4) {
                    newRound();
                }
                else {
                    completeGame();
                }
            }
        });


        btnGrid2Of9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endTime = System.currentTimeMillis();
                reactionTime = endTime - startTime;
                pastScores.add(reactionTime);

                btnGrid2Of9.setBackground(defaultColour);
                btnGrid2Of9.setImageDrawable(null);
                btnGrid2Of9.setEnabled(false);

                if (roundNum <= 4) {
                    newRound();
                }
                else {
                    completeGame();
                }
            }
        });


        btnGrid3Of9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endTime = System.currentTimeMillis();
                reactionTime = endTime - startTime;
                pastScores.add(reactionTime);


                btnGrid3Of9.setBackground(defaultColour);
                btnGrid3Of9.setImageDrawable(null);
                btnGrid3Of9.setEnabled(false);

                if (roundNum <= 4) {
                    newRound();
                }
                else {
                    completeGame();
                }
            }
        });


        btnGrid4Of9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endTime = System.currentTimeMillis();
                reactionTime = endTime - startTime;
                pastScores.add(reactionTime);

                btnGrid4Of9.setBackground(defaultColour);
                btnGrid4Of9.setImageDrawable(null);
                btnGrid4Of9.setEnabled(false);

                if (roundNum <= 4) {
                    newRound();
                }
                else {
                    completeGame();
                }
            }
        });


        btnGrid5Of9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endTime = System.currentTimeMillis();
                reactionTime = endTime - startTime;
                pastScores.add(reactionTime);

                btnGrid5Of9.setBackground(defaultColour);
                btnGrid5Of9.setImageDrawable(null);
                btnGrid5Of9.setEnabled(false);

                if (roundNum <= 4) {
                    newRound();
                }
                else {
                    completeGame();
                }
            }
        });


        btnGrid6Of9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endTime = System.currentTimeMillis();
                reactionTime = endTime - startTime;
                pastScores.add(reactionTime);

                btnGrid6Of9.setBackground(defaultColour);
                btnGrid6Of9.setImageDrawable(null);
                btnGrid6Of9.setEnabled(false);

                if (roundNum <= 4) {
                    newRound();
                }
                else {
                    completeGame();
                }
            }
        });


        btnGrid7Of9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endTime = System.currentTimeMillis();
                reactionTime = endTime - startTime;
                pastScores.add(reactionTime);

                btnGrid7Of9.setBackground(defaultColour);
                btnGrid7Of9.setImageDrawable(null);
                btnGrid7Of9.setEnabled(false);

                if (roundNum <= 4) {
                    newRound();
                }
                else {
                    completeGame();
                }
            }
        });


        btnGrid8Of9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endTime = System.currentTimeMillis();
                reactionTime = endTime - startTime;
                pastScores.add(reactionTime);

                btnGrid8Of9.setBackground(defaultColour);
                btnGrid8Of9.setImageDrawable(null);
                btnGrid8Of9.setEnabled(false);

                if (roundNum <= 4) {
                    newRound();
                }
                else {
                    completeGame();
                }
            }
        });


        btnGrid9Of9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endTime = System.currentTimeMillis();
                reactionTime = endTime - startTime;
                pastScores.add(reactionTime);

                btnGrid9Of9.setBackground(defaultColour);
                btnGrid9Of9.setImageDrawable(null);
                btnGrid9Of9.setEnabled(false);

                if (roundNum <= 4) {
                    newRound();
                }
                else {
                    completeGame();
                }
            }
        });
    }


    private void newRound() {
        if (roundNum == 5) {
            roundNum = 0;
        }
        roundNum += 1;

        //get random fields to make the game random, random time delay, random button to highlight, random colour
        int randomButtonNum = rand.nextInt(9 - 1 + 1) + 1;
        int delayInMills = rand.nextInt(3500 - 1500 + 1) + 1500;




        //start a handler to run a randomly delayed runnable
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Drawable buttonColour = defaultColour;
                Drawable buttonImage = null;
                if(typeOfLevel.equals("colours")) {
                    int randomColourIndex = rand.nextInt((possibleBackgrounds.size() - 1) - 1 + 1) + 1;
                    buttonColour = possibleBackgrounds.get(randomColourIndex);
                }
                else {
                    int randomImageIndex = rand.nextInt((possibleImages.size() - 1) - 1 + 1) + 1;
                    buttonImage = possibleImages.get(randomImageIndex);
                }

                //display and enable the random button. Also start timer
                switch (randomButtonNum) {
                    case 1:
                        btnGrid1Of9.setEnabled(true);
                        btnGrid1Of9.setBackground(buttonColour);
                        btnGrid1Of9.setImageDrawable(buttonImage);
                        startTime = System.currentTimeMillis();
                        break;

                    case 2:
                        btnGrid2Of9.setEnabled(true);
                        btnGrid2Of9.setBackground(buttonColour);
                        btnGrid2Of9.setImageDrawable(buttonImage);
                        startTime = System.currentTimeMillis();
                        break;

                    case 3:
                        btnGrid3Of9.setEnabled(true);
                        btnGrid3Of9.setBackground(buttonColour);
                        btnGrid3Of9.setImageDrawable(buttonImage);
                        startTime = System.currentTimeMillis();
                        break;

                    case 4:
                        btnGrid4Of9.setEnabled(true);
                        btnGrid4Of9.setBackground(buttonColour);
                        btnGrid4Of9.setImageDrawable(buttonImage);
                        startTime = System.currentTimeMillis();
                        break;

                    case 5:
                        btnGrid5Of9.setEnabled(true);
                        btnGrid5Of9.setBackground(buttonColour);
                        btnGrid5Of9.setImageDrawable(buttonImage);
                        startTime = System.currentTimeMillis();
                        break;

                    case 6:
                        btnGrid6Of9.setEnabled(true);
                        btnGrid6Of9.setBackground(buttonColour);
                        btnGrid6Of9.setImageDrawable(buttonImage);
                        startTime = System.currentTimeMillis();
                        break;

                    case 7:
                        btnGrid7Of9.setEnabled(true);
                        btnGrid7Of9.setBackground(buttonColour);
                        btnGrid7Of9.setImageDrawable(buttonImage);
                        startTime = System.currentTimeMillis();
                        break;

                    case 8:
                        btnGrid8Of9.setEnabled(true);
                        btnGrid8Of9.setBackground(buttonColour);
                        btnGrid8Of9.setImageDrawable(buttonImage);
                        startTime = System.currentTimeMillis();
                        break;

                    case 9:
                        btnGrid9Of9.setEnabled(true);
                        btnGrid9Of9.setBackground(buttonColour);
                        btnGrid9Of9.setImageDrawable(buttonImage);
                        startTime = System.currentTimeMillis();
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


    private void completeGame(){

        Log.d("Test", "Average: " + getAverage(pastScores));
        Intent intent = new Intent(GridReactionGame9ButtonsActivity.this, StartGridReactionGameActivity.class);
        intent.putExtra("reactionTime", getAverage(pastScores));
        GridReactionGame9ButtonsActivity.this.startActivity(intent);
    }
}