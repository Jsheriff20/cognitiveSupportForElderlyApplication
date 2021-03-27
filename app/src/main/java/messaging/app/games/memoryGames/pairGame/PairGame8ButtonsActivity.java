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

public class PairGame8ButtonsActivity extends AppCompatActivity {

    ImageButton btnGrid1Of8, btnGrid2Of8, btnGrid3Of8, btnGrid4Of8, btnGrid5Of8, btnGrid6Of8, btnGrid7Of8, btnGrid8Of8;
    Random rand = new Random();

    Drawable defaultImage;
    int delayTime = 2500;
    boolean secondClick = false;
    HashMap<ImageButton, Drawable> clickedButtonDetails = new HashMap<ImageButton, Drawable>();
    int pairsFound = 0;
    int numberCorrect = 0;

    int streak = 0;


    List<Drawable> images = new ArrayList<>();
    List<ImageButton> imageButtonsList = new ArrayList<>();
    HashMap<ImageButton, Drawable> buttonAndImageMap = new HashMap<ImageButton, Drawable>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pair_game8_buttons);

        btnGrid1Of8 = findViewById(R.id.btnGrid1Of8);
        btnGrid2Of8 = findViewById(R.id.btnGrid2Of8);
        btnGrid3Of8 = findViewById(R.id.btnGrid3Of8);
        btnGrid4Of8 = findViewById(R.id.btnGrid4Of8);
        btnGrid5Of8 = findViewById(R.id.btnGrid5Of8);
        btnGrid6Of8 = findViewById(R.id.btnGrid6Of8);
        btnGrid7Of8 = findViewById(R.id.btnGrid7Of8);
        btnGrid8Of8 = findViewById(R.id.btnGrid8Of8);

        images.addAll(Arrays.asList(
                getDrawable(R.drawable.ant), getDrawable(R.drawable.basketball), getDrawable(R.drawable.carrot),
                getDrawable(R.drawable.cat), getDrawable(R.drawable.cow), getDrawable(R.drawable.dog),
                getDrawable(R.drawable.football), getDrawable(R.drawable.horse), getDrawable(R.drawable.monkey),
                getDrawable(R.drawable.pie), getDrawable(R.drawable.soup)
        ));
        defaultImage = getDrawable(R.drawable.default_pair_image);

        imageButtonsList.addAll(Arrays.asList(
                btnGrid1Of8, btnGrid2Of8, btnGrid3Of8, btnGrid4Of8, btnGrid5Of8, btnGrid6Of8, btnGrid7Of8, btnGrid8Of8
        ));


        for(ImageButton button : imageButtonsList){
            button.setEnabled(false);
        }

        assignButtons();
        startGame();

    }

    private void assignButtons() {
        for(ImageButton button : imageButtonsList){
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(secondClick){
                        //get details of first button
                        Set<ImageButton> buttons = clickedButtonDetails.keySet();
                        ImageButton firstButton = buttons.iterator().next();
                        Drawable firstImage = clickedButtonDetails.get(firstButton);

                        if(buttonAndImageMap.get(button).equals(firstImage)){
                            button.setEnabled(false);
                            firstButton.setEnabled(false);
                            button.setVisibility(View.INVISIBLE);
                            firstButton.setVisibility(View.INVISIBLE);

                            pairsFound++;
                            numberCorrect++;
                            if(numberCorrect == (imageButtonsList.size() / 2)){
                                //game finished
                                Log.d("test", "game finished: ");

                                streak++;

                                //restart the game
                                //have to repeat code to allow button for loops to have the correct target variable
                                images = new ArrayList<>();
                                images.addAll(Arrays.asList(
                                        getDrawable(R.drawable.ant), getDrawable(R.drawable.basketball), getDrawable(R.drawable.carrot),
                                        getDrawable(R.drawable.cat), getDrawable(R.drawable.cow), getDrawable(R.drawable.dog),
                                        getDrawable(R.drawable.football), getDrawable(R.drawable.horse), getDrawable(R.drawable.monkey),
                                        getDrawable(R.drawable.pie), getDrawable(R.drawable.soup)
                                ));
                                imageButtonsList = new ArrayList<>();
                                imageButtonsList.addAll(Arrays.asList(
                                        btnGrid1Of8, btnGrid2Of8, btnGrid3Of8, btnGrid4Of8, btnGrid5Of8, btnGrid6Of8, btnGrid7Of8, btnGrid8Of8
                                ));



                                for(ImageButton button : imageButtonsList){
                                    Log.d("test", "VISIBLE: ");
                                    button.setEnabled(false);
                                    button.setVisibility(View.VISIBLE);
                                }

                                startGame();

                            }
                        }
                        else{
                            Intent intent = new Intent(PairGame8ButtonsActivity.this, StartPairsGameActivity.class);
                            intent.putExtra("numberOfPairs", "4");
                            intent.putExtra("streak", streak);
                            intent.putExtra("numberOfPairsFound", pairsFound);
                            PairGame8ButtonsActivity.this.startActivity(intent);
                        }
                        secondClick = false;
                        clickedButtonDetails = new HashMap<ImageButton, Drawable>();
                        return;
                    }

                    Drawable selectedImage = buttonAndImageMap.get(button);
                    clickedButtonDetails.put(button, selectedImage);
                    secondClick = true;
                }
            });
        }
    }

    private void startGame() {

        numberCorrect = 0;

        //assign 2 buttons an the same image
        int numberOfPairsNeeded = imageButtonsList.size()/2;
        for(int i = 0; i < numberOfPairsNeeded; i++){
            int randomImageIndex = rand.nextInt(images.size());

            for(int j = 0; j < 2; j++){
                //get a random button index
                int randomButtonIndex = rand.nextInt(imageButtonsList.size());

                //Assign the buttonID an image from the image list
                //remove the button from the button list
                ImageButton button = imageButtonsList.get(randomButtonIndex);
                Log.d("test", "imageButtonsList " + imageButtonsList.get(randomButtonIndex));
                imageButtonsList.remove(randomButtonIndex);
                buttonAndImageMap.put(button, images.get(randomImageIndex));
            }

            images.remove(randomImageIndex);
        }

        //display the buttons to the user
        imageButtonsList = Arrays.asList(
                btnGrid1Of8, btnGrid2Of8, btnGrid3Of8, btnGrid4Of8, btnGrid5Of8, btnGrid6Of8, btnGrid7Of8, btnGrid8Of8
        );

        for(ImageButton button : imageButtonsList){
            Drawable image = buttonAndImageMap.get(button);
            button.setBackground(image);
        }


        new CountDownTimer(delayTime, 50) {
            @Override
            public void onTick(long arg0) {
            }

            @Override
            public void onFinish() {
                for(ImageButton button : imageButtonsList){
                    button.setBackground(defaultImage);
                    button.setEnabled(true);
                }
            }
        }.start();
    }
}