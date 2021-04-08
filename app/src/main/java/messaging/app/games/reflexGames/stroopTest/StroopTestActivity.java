package messaging.app.games.reflexGames.stroopTest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
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

public class StroopTestActivity extends AppCompatActivity {
    ImageButton btn1Of6Option;
    ImageButton btn2Of6Option;
    ImageButton btn3Of6Option;
    ImageButton btn4Of6Option;
    ImageButton btn5Of6Option;
    ImageButton btn6Of6Option;
    TextView lblStoopWord;


    int mTextColourIndex = -1;
    int mTextStringIndex = -1;
    int mRoundNumber = 0;
    int mNumberOfRounds = 10;
    long mStartTime, mEndTime, mReactionTime;
    Random mRand = new Random();


    HashMap<String, Drawable> mColourOptions;
    List<ImageButton> mImageButtonsList;
    List<String> mWordOptions;
    List<String> mWordColourOptions;


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


        mColourOptions = new HashMap<String, Drawable>() {{
            put("Blue", getDrawable(R.drawable.btn_rectangle_blue_gradient));
            put("red", getDrawable(R.drawable.btn_rectangle_red_gradient));
            put("Purple", getDrawable(R.drawable.btn_rectangle_purple_gradient));
            put("Orange", getDrawable(R.drawable.btn_rectangle_orange_gradient));
            put("Green", getDrawable(R.drawable.btn_rectangle_green_gradient));
            put("Brown", getDrawable(R.drawable.btn_rectangle_brown_gradient));
            put("Black", getDrawable(R.drawable.btn_rectangle_black_gradient));
        }};


        mWordOptions = new ArrayList<String>(
                Arrays.asList("Blue", "Red", "Purple", "Orange", "Green", "Brown", "White"));

        mWordColourOptions = new ArrayList<String>(
                Arrays.asList("#0000FF", "#FF0000", "#800080", "#FFA500",
                        "#00FF00", "#964B00", "#000000"));

        mImageButtonsList = new ArrayList<ImageButton>(
                Arrays.asList(btn1Of6Option, btn2Of6Option, btn3Of6Option,
                        btn4Of6Option, btn5Of6Option, btn6Of6Option));

        newRound();
        mStartTime = System.currentTimeMillis();
    }

    private void newRound() {
        mRoundNumber++;
        mTextStringIndex = mRand.nextInt(mWordOptions.size());
        lblStoopWord.setText(mWordOptions.get(mTextStringIndex));

        mWordColourOptions = new ArrayList<String>(
                Arrays.asList("#0000FF", "#FF0000", "#800080", "#FFA500",
                        "#00FF00", "#964B00", "#000000"));
        String chosenTextColour = setTextColour();
        setmColourOptions(chosenTextColour);
    }


    private void setmColourOptions(String chosenTextColour) {
        //mix the button positions
        List<Integer> buttonNumbers = Arrays.asList(0, 1, 2, 3, 4, 5);
        Collections.shuffle(buttonNumbers);
        Collections.shuffle(buttonNumbers);
        Collections.shuffle(buttonNumbers, new Random(System.nanoTime()));

        //mix the possible colours
        Collections.shuffle(mWordColourOptions, new Random(System.nanoTime()));

        mWordColourOptions.remove(chosenTextColour);


        int correctButtonNum = mRand.nextInt(mImageButtonsList.size() - 1);
        int count = 0;
        for (ImageButton imageButton : mImageButtonsList) {

            //set background colours
            imageButton.setBackgroundColor(Color.parseColor(mWordColourOptions.get(count)));

            if (count == correctButtonNum) {
                imageButton.setBackgroundColor(Color.parseColor(chosenTextColour));

                //set on click for the correct button
                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //stop timer and store reaction time
                        imageButton.setOnClickListener(null);
                        if (mRoundNumber <= mNumberOfRounds) {
                            newRound();
                        } else {
                            mEndTime = System.currentTimeMillis();
                            mReactionTime = (mEndTime - mStartTime) / mNumberOfRounds;
                            Log.d("testLog", "reaction time: " + mReactionTime);

                            Intent intent = new Intent(StroopTestActivity.this,
                                    StartStroopTestActivity.class);
                            intent.putExtra("reactionTime", mReactionTime);
                            StroopTestActivity.this.startActivity(intent);
                        }
                    }
                });
            }

            count++;
        }
    }


    private String setTextColour() {
        do {
            mTextColourIndex = mRand.nextInt(mWordOptions.size());
        }
        while (mTextColourIndex == mTextStringIndex);
        String colour = mWordColourOptions.get(mTextColourIndex);

        lblStoopWord.setTextColor(Color.parseColor(colour));
        return colour;
    }
}