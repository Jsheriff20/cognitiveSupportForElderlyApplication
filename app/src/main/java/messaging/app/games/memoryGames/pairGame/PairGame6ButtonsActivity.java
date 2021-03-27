package messaging.app.games.memoryGames.pairGame;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.media.Image;
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
    Random rand = new Random();

    Drawable defaultImage;
    int delayTime = 2000;
    boolean secondClick = false;
    HashMap<ImageButton, Drawable> clickedButtonDetails = new HashMap<ImageButton, Drawable>();
    int numberCorrect = 0;


    List<Drawable> images = new ArrayList<>();
    List<ImageButton> imageButtonsList = new ArrayList<>();
    HashMap<ImageButton, Drawable> buttonAndImageMap = new HashMap<ImageButton, Drawable>();

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

        images.addAll(Arrays.asList(
                getDrawable(R.drawable.ant), getDrawable(R.drawable.basketball), getDrawable(R.drawable.carrot),
                getDrawable(R.drawable.cat), getDrawable(R.drawable.cow), getDrawable(R.drawable.dog),
                getDrawable(R.drawable.football), getDrawable(R.drawable.horse), getDrawable(R.drawable.monkey),
                getDrawable(R.drawable.pie), getDrawable(R.drawable.soup)
        ));
        defaultImage = getDrawable(R.drawable.default_pair_image);

        imageButtonsList.addAll(Arrays.asList(
                btnGrid1Of6, btnGrid2Of6, btnGrid3Of6, btnGrid4Of6, btnGrid5Of6, btnGrid6Of6
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

                            numberCorrect++;
                            if(numberCorrect == (imageButtonsList.size() / 2)){
                                //game finished
                                Log.d("test", "game finished: ");
                            }
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

        //assign 2 buttons an the same image
        int numberOfPairsNeeded = imageButtonsList.size()/2;
        for(int i = 0; i < numberOfPairsNeeded; i++){
            int randomImageIndex = rand.nextInt(images.size());

            for(int j = 0; j < 2; j++){
                //get a random button index
                int randomButtonIndex = rand.nextInt(imageButtonsList.size());

                //Assign the buttonID an image from the image list
                //remove the button from the button list
                ImageButton button  = imageButtonsList.get(randomButtonIndex);
                imageButtonsList.remove(randomButtonIndex);
                buttonAndImageMap.put(button, images.get(randomImageIndex));
            }

            images.remove(randomImageIndex);
        }

        //display the buttons to the user
        imageButtonsList = Arrays.asList(
                btnGrid1Of6, btnGrid2Of6, btnGrid3Of6, btnGrid4Of6, btnGrid5Of6, btnGrid6Of6
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