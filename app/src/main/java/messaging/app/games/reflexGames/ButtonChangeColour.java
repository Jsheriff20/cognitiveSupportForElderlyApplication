package messaging.app.games.reflexGames;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import messaging.app.R;

public class ButtonChangeColour extends AppCompatActivity {

    Button btnStartButtonChangeColour;
    Button btnColourChangingButton;

    long startTimer, endTime, reactionTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button_change_colour);

        btnStartButtonChangeColour = findViewById(R.id.btnStartButtonchangeColourGame);
        btnColourChangingButton = findViewById(R.id.btnColourChangingButton);

        btnColourChangingButton.setEnabled(false);
        btnColourChangingButton.setVisibility(View.INVISIBLE);


        setBtnColourChangingButton();
        setBtnStartButtonChangeColour();
    }


    private void setBtnColourChangingButton(){
        btnColourChangingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endTime = System.currentTimeMillis();
                reactionTime = startTimer - endTime;
                btnColourChangingButton.setBackgroundResource(R.drawable.btn_rectangle_red_gradient);

                btnColourChangingButton.setEnabled(false);
                btnColourChangingButton.setVisibility(View.INVISIBLE);
                btnColourChangingButton.setText("");

                btnStartButtonChangeColour.setEnabled(true);
                btnStartButtonChangeColour.setVisibility(View.VISIBLE);

            }
        });
    }

    private void setBtnStartButtonChangeColour(){
        btnStartButtonChangeColour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startTimer = System.currentTimeMillis();
                        btnColourChangingButton.setBackgroundResource(R.drawable.btn_rectangle_green_gradient);

                        btnColourChangingButton.setEnabled(true);
                        btnColourChangingButton.setVisibility(View.VISIBLE);
                        btnColourChangingButton.setText("PRESS");

                        btnStartButtonChangeColour.setEnabled(false);
                        btnStartButtonChangeColour.setVisibility(View.INVISIBLE);
                    }
                }, 1000);
            }
        });

    }
}