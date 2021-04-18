package messaging.app.games.memoryGames.pairGame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import messaging.app.R;

public class PairGame6ButtonsActivity extends AppCompatActivity {

    ImageButton btnGrid1Of6, btnGrid2Of6, btnGrid3Of6, btnGrid4Of6, btnGrid5Of6, btnGrid6Of6;
    Random mRand = new Random();

    Drawable mDefaultImage;
    int mDelayTime = 5000;
    boolean mSecondClick = false;
    int mPairsFound = 0;
    int mNumberCorrect = 0;
    int mStreak = 0;


    List<Drawable> mImages = new ArrayList<>();
    List<ImageButton> mImageButtonsList = new ArrayList<>();
    HashMap<ImageButton, Drawable> mClickedButtonDetails = new HashMap<ImageButton, Drawable>();
    HashMap<ImageButton, Drawable> mButtonAndImageMap = new HashMap<ImageButton, Drawable>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pair_game6_buttons);

        btnGrid1Of6 = findViewById(R.id.btnGrid1Of6);
        btnGrid2Of6 = findViewById(R.id.btnGrid2Of6);
        btnGrid3Of6 = findViewById(R.id.btnGrid3Of6);
        btnGrid4Of6 = findViewById(R.id.btnGrid4Of6);
        btnGrid5Of6 = findViewById(R.id.btnGrid5Of6);
        btnGrid6Of6 = findViewById(R.id.btnGrid6Of6);

        mImages.addAll(Arrays.asList(
                getDrawable(R.drawable.ant),
                getDrawable(R.drawable.basketball),
                getDrawable(R.drawable.carrot),
                getDrawable(R.drawable.cat),
                getDrawable(R.drawable.cow),
                getDrawable(R.drawable.dog),
                getDrawable(R.drawable.football),
                getDrawable(R.drawable.horse),
                getDrawable(R.drawable.monkey),
                getDrawable(R.drawable.pie),
                getDrawable(R.drawable.soup)
        ));
        mDefaultImage = getDrawable(R.drawable.default_pair_image);

        mImageButtonsList.addAll(Arrays.asList(
                btnGrid1Of6, btnGrid2Of6, btnGrid3Of6, btnGrid4Of6, btnGrid5Of6, btnGrid6Of6
        ));


        for (ImageButton button : mImageButtonsList) {
            button.setEnabled(false);
        }

        assignButtons();
        startGame();

    }

    private void assignButtons() {
        for (ImageButton button : mImageButtonsList) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (mSecondClick) {
                        //get details of first button
                        Set<ImageButton> buttons = mClickedButtonDetails.keySet();
                        ImageButton firstButton = buttons.iterator().next();
                        Drawable firstImage = mClickedButtonDetails.get(firstButton);

                        //prevent user from double clicking the same button
                        if (firstButton.equals(button)) {
                            return;
                        }

                        if (mButtonAndImageMap.get(button).equals(firstImage)) {
                            button.setEnabled(false);
                            firstButton.setEnabled(false);
                            button.setVisibility(View.INVISIBLE);
                            firstButton.setVisibility(View.INVISIBLE);

                            mPairsFound++;
                            mNumberCorrect++;
                            if (mNumberCorrect == (mImageButtonsList.size() / 2)) {
                                //game finished

                                mStreak++;

                                if (mStreak < 4) {
                                    //restart the game
                                    //have to repeat code to allow button for loops to have the correct target variable
                                    mImages = new ArrayList<>();
                                    mImages.addAll(Arrays.asList(
                                            getDrawable(R.drawable.ant),
                                            getDrawable(R.drawable.basketball),
                                            getDrawable(R.drawable.carrot),
                                            getDrawable(R.drawable.cat),
                                            getDrawable(R.drawable.cow),
                                            getDrawable(R.drawable.dog),
                                            getDrawable(R.drawable.football),
                                            getDrawable(R.drawable.horse),
                                            getDrawable(R.drawable.monkey),
                                            getDrawable(R.drawable.pie),
                                            getDrawable(R.drawable.soup)
                                    ));
                                    mImageButtonsList = new ArrayList<>();
                                    mImageButtonsList.addAll(Arrays.asList(
                                            btnGrid1Of6, btnGrid2Of6, btnGrid3Of6, btnGrid4Of6,
                                            btnGrid5Of6, btnGrid6Of6
                                    ));


                                    for (ImageButton button : mImageButtonsList) {
                                        button.setEnabled(false);
                                        button.setVisibility(View.VISIBLE);
                                    }

                                    startGame();
                                } else {
                                    //hit the target number of rounds
                                    Intent intent = new Intent(PairGame6ButtonsActivity.this,
                                            StartPairsGameActivity.class);
                                    intent.putExtra("numberOfPairs", "6");
                                    intent.putExtra("streak", mStreak);
                                    intent.putExtra("numberOfPairsFound", mPairsFound);
                                    PairGame6ButtonsActivity.this.startActivity(intent);
                                }

                            }
                        } else {
                            Intent intent = new Intent(PairGame6ButtonsActivity.this,
                                    StartPairsGameActivity.class);
                            intent.putExtra("numberOfPairs", "6");
                            intent.putExtra("streak", mStreak);
                            intent.putExtra("numberOfPairsFound", mPairsFound);
                            PairGame6ButtonsActivity.this.startActivity(intent);
                        }
                        mSecondClick = false;
                        mClickedButtonDetails = new HashMap<ImageButton, Drawable>();
                        return;
                    }

                    Drawable selectedImage = mButtonAndImageMap.get(button);
                    mClickedButtonDetails.put(button, selectedImage);
                    mSecondClick = true;
                }
            });
        }
    }

    private void startGame() {

        mNumberCorrect = 0;

        //assign 2 buttons an the same image
        int numberOfPairsNeeded = mImageButtonsList.size() / 2;
        for (int i = 0; i < numberOfPairsNeeded; i++) {
            int randomImageIndex = mRand.nextInt(mImages.size());

            for (int j = 0; j < 2; j++) {
                //get a random button index
                int randomButtonIndex = mRand.nextInt(mImageButtonsList.size());

                //Assign the buttonID an image from the image list
                //remove the button from the button list
                ImageButton button = mImageButtonsList.get(randomButtonIndex);
                mImageButtonsList.remove(randomButtonIndex);
                mButtonAndImageMap.put(button, mImages.get(randomImageIndex));
            }

            mImages.remove(randomImageIndex);
        }

        //display the buttons to the user
        mImageButtonsList = Arrays.asList(
                btnGrid1Of6, btnGrid2Of6, btnGrid3Of6, btnGrid4Of6, btnGrid5Of6, btnGrid6Of6
        );

        for (ImageButton button : mImageButtonsList) {
            Drawable image = mButtonAndImageMap.get(button);
            button.setBackground(image);
        }


        new CountDownTimer(mDelayTime, 50) {
            @Override
            public void onTick(long arg0) {
            }

            @Override
            public void onFinish() {
                for (ImageButton button : mImageButtonsList) {
                    button.setBackground(mDefaultImage);
                    button.setEnabled(true);
                }
            }
        }.start();
    }
}