package messaging.app.games.reflexGames;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import messaging.app.R;
import messaging.app.games.memoryGames.SelectMemoryGameActivity;
import messaging.app.games.memoryGames.memorizingPatternGame.StartMemorizingPatternActivity;

public class ButtonChangeColourActivity extends AppCompatActivity {

    Button btnStartButtonChangeColour;
    Button btnColourChangingButton;
    ImageButton btnBackToReactionGames;
    Button btnWatchColourChangeGameVid;
    TextView lblColourChangeDesc;
    TextView lblColourChangeTitle;

    boolean greenColourActive = false;

    int roundNum = 0;
    List <Long> pastScores = new ArrayList();
    long startTime, endTime, reactionTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button_change_colour);

        btnStartButtonChangeColour = findViewById(R.id.btnStartButtonChangeColourGame);
        btnColourChangingButton = findViewById(R.id.btnColourChangingButton);
        btnBackToReactionGames = findViewById(R.id.btnBackToReactionGames);
        btnWatchColourChangeGameVid = findViewById(R.id.btnWatchColourChangeGameVid);
        lblColourChangeDesc = findViewById(R.id.lblColourChangeDesc);
        lblColourChangeTitle = findViewById(R.id.lblColourChangeTitle);

        btnColourChangingButton.setVisibility(View.INVISIBLE);


        setBtnColourChangingButton();
        setBtnStartButtonChangeColour();
        setBtnBackToReactionGames();
    }


    private void setBtnColourChangingButton(){
        btnColourChangingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if user pressed too early (trying to predict they will be penalised)
                if(!greenColourActive){
                    Toast.makeText(ButtonChangeColourActivity.this, "Pressed too early! Penalised 2 seconds", Toast.LENGTH_SHORT).show();
                    pastScores.add((long) 2000);
                }
                else {
                    endTime = System.currentTimeMillis();
                    reactionTime = endTime - startTime;

                    btnColourChangingButton.setBackgroundResource(R.drawable.btn_rectangle_red_gradient);
                    btnColourChangingButton.setVisibility(View.INVISIBLE);
                    btnColourChangingButton.setText("");

                    greenColourActive = false;
                    pastScores.add(reactionTime);

                    if (roundNum < 4) {
                        startNewRound();
                        roundNum++;
                    } else {
                        btnStartButtonChangeColour.setVisibility(View.VISIBLE);
                        btnBackToReactionGames.setVisibility(View.VISIBLE);
                        lblColourChangeTitle.setVisibility(View.VISIBLE);
                        lblColourChangeDesc.setVisibility(View.VISIBLE);

                        lblColourChangeTitle.setText("Your results:");
                        lblColourChangeDesc.setText("Your average reaction speed was " + getAverage(pastScores) +" ms");

                    }
                }
            }
        });
    }


    private void setBtnStartButtonChangeColour(){
        btnStartButtonChangeColour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                roundNum = 0;
                pastScores = new ArrayList();
                startNewRound();
            }
        });
    }


    private void setBtnBackToReactionGames(){
        btnBackToReactionGames.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ButtonChangeColourActivity.this, SelectReactionGameActivity.class);
                ButtonChangeColourActivity.this.startActivity(intent);
            }
        });
    }

    private void startNewRound(){
        Random rand = new Random();
        int delayInMills = rand.nextInt(3500 - 1500 + 1) + 1500;

        btnColourChangingButton.setVisibility(View.VISIBLE);
        btnColourChangingButton.setBackgroundResource(R.drawable.btn_rectangle_red_gradient);

        btnStartButtonChangeColour.setVisibility(View.INVISIBLE);
        btnBackToReactionGames.setVisibility(View.INVISIBLE);
        lblColourChangeDesc.setVisibility(View.INVISIBLE);
        lblColourChangeTitle.setVisibility(View.INVISIBLE);
        btnWatchColourChangeGameVid.setVisibility(View.INVISIBLE);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startTime = System.currentTimeMillis();
                greenColourActive = true;
                btnColourChangingButton.setBackgroundResource(R.drawable.btn_rectangle_blue_gradient);
                btnColourChangingButton.setText("PRESS");
            }
        }, delayInMills);
    }


    private long getAverage(List <Long> pastScores) {
        long total = 0;
        for(Long score : pastScores){
            Log.d("test", "score: " + score);
            total += score;
        }

        return (total / 5);
    }
}