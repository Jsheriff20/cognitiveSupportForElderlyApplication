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

public class GridReactionGame4ButtonsActivity extends AppCompatActivity {

    ImageButton btnGrid1Of4;
    ImageButton btnGrid2Of4;
    ImageButton btnGrid3Of4;
    ImageButton btnGrid4Of4;

    int roundNum = 0;
    List<Long> pastScores = new ArrayList();
    long startTime, endTime, reactionTime;

    Drawable defaultColour;
    List<Drawable> possibleBackgroundColours = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_4_grid_reaction_game);

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


        btnGrid1Of4.setEnabled(false);
        btnGrid2Of4.setEnabled(false);
        btnGrid3Of4.setEnabled(false);
        btnGrid4Of4.setEnabled(false);


        setButtonClicks();
        newRound();
    }

    private void setButtonClicks() {

        //each button, stops the time and records it and resets the game for the next round
        //if there is no more rounds to go then stop and save the game

        btnGrid1Of4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endTime = System.currentTimeMillis();
                reactionTime = endTime - startTime;
                pastScores.add(reactionTime);
                btnGrid1Of4.setBackground(defaultColour);
                btnGrid1Of4.setEnabled(false);

                if (roundNum <= 4) {
                    newRound();
                }
                else {
                    completeGame();
                }
            }
        });


        btnGrid2Of4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endTime = System.currentTimeMillis();
                reactionTime = endTime - startTime;
                pastScores.add(reactionTime);
                btnGrid2Of4.setBackground(defaultColour);
                btnGrid2Of4.setEnabled(false);

                if (roundNum <= 4) {
                    newRound();
                }
                else {
                    completeGame();
                }
            }
        });


        btnGrid3Of4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endTime = System.currentTimeMillis();
                reactionTime = endTime - startTime;
                pastScores.add(reactionTime);
                btnGrid3Of4.setBackground(defaultColour);
                btnGrid3Of4.setEnabled(false);

                if (roundNum <= 4) {
                    newRound();
                }
                else {
                    completeGame();
                }
            }
        });


        btnGrid4Of4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endTime = System.currentTimeMillis();
                reactionTime = endTime - startTime;
                pastScores.add(reactionTime);
                btnGrid4Of4.setBackground(defaultColour);
                btnGrid4Of4.setEnabled(false);

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
        Random rand = new Random();
        int randomButtonNum = rand.nextInt(4 - 1 + 1) + 1;

        int delayInMills = rand.nextInt(3500 - 1500 + 1) + 1500;

        int randomDrawableIndex = rand.nextInt((possibleBackgroundColours.size() - 1) - 1 + 1) + 1;
        Drawable buttonColour = possibleBackgroundColours.get(randomDrawableIndex);


        //start a handler to run a randomly delayed runnable
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //display and enable the random button. Also start timer
                switch (randomButtonNum) {
                    case 1:
                        btnGrid1Of4.setEnabled(true);
                        btnGrid1Of4.setBackground(buttonColour);
                        startTime = System.currentTimeMillis();
                        break;

                    case 2:
                        btnGrid2Of4.setEnabled(true);
                        btnGrid2Of4.setBackground(buttonColour);
                        startTime = System.currentTimeMillis();
                        break;

                    case 3:
                        btnGrid3Of4.setEnabled(true);
                        btnGrid3Of4.setBackground(buttonColour);
                        startTime = System.currentTimeMillis();
                        break;

                    case 4:
                        btnGrid4Of4.setEnabled(true);
                        btnGrid4Of4.setBackground(buttonColour);
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
        Intent intent = new Intent(GridReactionGame4ButtonsActivity.this, StartGridReactionGameActivity.class);
        GridReactionGame4ButtonsActivity.this.startActivity(intent);
    }
}