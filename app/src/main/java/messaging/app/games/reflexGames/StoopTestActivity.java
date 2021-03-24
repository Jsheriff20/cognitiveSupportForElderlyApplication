package messaging.app.games.reflexGames;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import messaging.app.R;

public class StoopTestActivity extends AppCompatActivity {
    ImageButton btn1Of6Option;
    ImageButton btn2Of6Option;
    ImageButton btn3Of6Option;
    ImageButton btn4Of6Option;
    ImageButton btn5Of6Option;
    ImageButton btn6Of6Option;

    List<ImageButton> imageButtonsList;

    TextView lblStoopWord;

    int textColourIndex = -1;
    int textStringIndex = -1;

    int roundNumber = 0;

    Random rand = new Random();
    List<String> wordOptions;
    List<String> wordColourOptions;

    HashMap<String, Drawable> colourOptions;
    long startTime, endTime, reactionTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stoop_test);

        lblStoopWord = findViewById(R.id.lblStoopWord);
        btn1Of6Option = findViewById(R.id.btn1Of6Option);
        btn2Of6Option = findViewById(R.id.btn2Of6Option);
        btn3Of6Option = findViewById(R.id.btn3Of6Option);
        btn4Of6Option = findViewById(R.id.btn4Of6Option);
        btn5Of6Option = findViewById(R.id.btn5Of6Option);
        btn6Of6Option = findViewById(R.id.btn6Of6Option);


        colourOptions = new HashMap<String, Drawable>() {{
            put("Blue", getDrawable(R.drawable.btn_rectangle_blue_gradient));
            put("red", getDrawable(R.drawable.btn_rectangle_red_gradient));
            put("Purple", getDrawable(R.drawable.btn_rectangle_purple_gradient));
            put("Orange", getDrawable(R.drawable.btn_rectangle_orange_gradient));
            put("Green", getDrawable(R.drawable.btn_rectangle_green_gradient));
            put("Brown", getDrawable(R.drawable.btn_rectangle_brown_gradient));
            put("Black", getDrawable(R.drawable.btn_rectangle_black_gradient));
        }};


        wordOptions = new ArrayList<String>(
                Arrays.asList("Blue", "Red", "Purple", "Orange", "Green", "Brown", "White"));

        wordColourOptions  = new ArrayList<String>(
                Arrays.asList("#0000FF", "#FF0000", "#800080", "#FFA500", "#00FF00", "#964B00", "#000000"));

        imageButtonsList = new ArrayList<ImageButton>(
                Arrays.asList(btn1Of6Option, btn2Of6Option, btn3Of6Option, btn4Of6Option, btn5Of6Option, btn6Of6Option));

        newRound();
        startTime = System.currentTimeMillis();
    }

    private void newRound() {
        roundNumber++;
        textStringIndex = rand.nextInt(wordOptions.size());
        lblStoopWord.setText(wordOptions.get(textStringIndex));

        wordColourOptions  = new ArrayList<String>(
                Arrays.asList("#0000FF", "#FF0000", "#800080", "#FFA500", "#00FF00", "#964B00", "#000000"));
        String chosenTextColour = setTextColour();
        setColourOptions(chosenTextColour);
    }


    private void setColourOptions(String chosenTextColour) {
        //mix the button positions
        List<Integer> buttonNumbers = Arrays.asList(0, 1, 2, 3, 4, 5);
        Collections.shuffle(buttonNumbers);
        Collections.shuffle(buttonNumbers);
        Collections.shuffle(buttonNumbers, new Random(System.nanoTime()));

        //mix the possible colours
        Collections.shuffle(wordColourOptions, new Random(System.nanoTime()));

        wordColourOptions.remove(chosenTextColour);



        int correctButtonNum = rand.nextInt(imageButtonsList.size() - 1);
        int count = 0;
        for (ImageButton imageButton : imageButtonsList) {

            //set background colours
            imageButton.setBackgroundColor(Color.parseColor(wordColourOptions.get(count)));

            if(count == correctButtonNum){
                imageButton.setBackgroundColor(Color.parseColor(chosenTextColour));

                //set on click for the correct button
                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //stop timer and store reaction time
                        imageButton.setOnClickListener(null);
                        if(roundNumber <= 10) {
                            newRound();
                        }
                        else{
                            endTime = System.currentTimeMillis();
                            reactionTime = (endTime - startTime) / 10;
                            Log.d("testLog", "reaction time: " + reactionTime);
                        }
                    }
                });
            }

            count++;
        }
    }


    private String setTextColour() {
        do {
            textColourIndex = rand.nextInt(wordOptions.size());
        }
        while (textColourIndex == textStringIndex);
        String colour = wordColourOptions.get(textColourIndex);

        lblStoopWord.setTextColor(Color.parseColor(colour));
        return colour;
    }
}