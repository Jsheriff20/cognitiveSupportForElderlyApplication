package messaging.app.games.reflexGames;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import messaging.app.R;
import messaging.app.contactingFirebase.ManagingGames;

public class ButtonChangeColourActivity extends AppCompatActivity {

    Button btnStartButtonChangeColour;
    ImageButton btnCancel;
    Button btnColourChangingButton;
    ImageButton btnBackToReactionGames;
    Button btnWatchColourChangeGameVid;
    TextView lblColourChangeDesc;
    TextView lblColourChangeTitle;
    VideoView vidColourChangeExample;

    boolean mGreenColourActive = false;
    int mRoundNum = 0;
    long mStartTime, mEndTime, mReactionTime;

    List<Long> mPastScores = new ArrayList();
    ManagingGames mManagingGames = new ManagingGames(this);

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
        btnCancel = findViewById(R.id.btnCancel);
        vidColourChangeExample = findViewById(R.id.vidColourChangeExample);

        btnColourChangingButton.setVisibility(View.INVISIBLE);
        vidColourChangeExample.setVisibility(View.INVISIBLE);
        btnCancel.setVisibility(View.INVISIBLE);

        setBtnColourChangingButton();
        setBtnStartButtonChangeColour();
        setBtnBackToReactionGames();
        setBtnWatchColourChangeVidOnClick();
        setBtnCancelOnClick();
    }


    private void setBtnColourChangingButton() {
        btnColourChangingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if user pressed too early (trying to predict they will be penalised)
                if (!mGreenColourActive) {
                    Toast.makeText(ButtonChangeColourActivity.this,
                            "Pressed too early! Penalised 2 seconds",
                            Toast.LENGTH_SHORT).show();
                    mPastScores.add((long) 2000);
                } else {
                    mEndTime = System.currentTimeMillis();
                    mReactionTime = mEndTime - mStartTime;

                    btnColourChangingButton
                            .setBackgroundResource(R.drawable.btn_rectangle_red_gradient);
                    btnColourChangingButton.setVisibility(View.INVISIBLE);
                    btnColourChangingButton.setText("");

                    mGreenColourActive = false;
                    mPastScores.add(mReactionTime);

                    if (mRoundNum < 4) {
                        startNewRound();
                        mRoundNum++;
                    } else {
                        btnStartButtonChangeColour.setVisibility(View.VISIBLE);
                        btnBackToReactionGames.setVisibility(View.VISIBLE);
                        lblColourChangeTitle.setVisibility(View.VISIBLE);
                        lblColourChangeDesc.setVisibility(View.VISIBLE);

                        lblColourChangeTitle.setText("Your results:");
                        lblColourChangeDesc.setText("Your average reaction speed was " +
                                getAverage(mPastScores) + " ms");

                        //store score in database
                        //if high score set as their high score
                        mManagingGames.storeGameResult("buttonChange",
                                getAverage(mPastScores));

                    }
                }
            }
        });
    }


    private void setBtnStartButtonChangeColour() {
        btnStartButtonChangeColour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRoundNum = 0;
                mPastScores = new ArrayList();
                startNewRound();
            }
        });
    }


    private void setBtnBackToReactionGames() {
        btnBackToReactionGames.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ButtonChangeColourActivity.this,
                        SelectReactionGameActivity.class);
                ButtonChangeColourActivity.this.startActivity(intent);
            }
        });
    }

    private void startNewRound() {
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
                mStartTime = System.currentTimeMillis();
                mGreenColourActive = true;
                btnColourChangingButton.setBackgroundResource(R.drawable.btn_rectangle_blue_gradient);
                btnColourChangingButton.setText("PRESS");
            }
        }, delayInMills);
    }


    private long getAverage(List<Long> pastScores) {
        long total = 0;
        for (Long score : pastScores) {
            Log.d("test", "score: " + score);
            total += score;
        }

        return (total / 5);
    }


    private void setBtnWatchColourChangeVidOnClick() {
        btnWatchColourChangeGameVid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vidColourChangeExample.setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.VISIBLE);


                btnStartButtonChangeColour.setVisibility(View.INVISIBLE);
                btnBackToReactionGames.setVisibility(View.INVISIBLE);
                btnWatchColourChangeGameVid.setVisibility(View.INVISIBLE);
                lblColourChangeDesc.setVisibility(View.INVISIBLE);
                lblColourChangeTitle.setVisibility(View.INVISIBLE);

                MediaController mediaController = new MediaController(ButtonChangeColourActivity.this);
                mediaController.setAnchorView(vidColourChangeExample);
                vidColourChangeExample.setMediaController(mediaController);
                vidColourChangeExample.setVideoURI(Uri.parse("android.resource://" +
                        getPackageName() + "/" +
                        R.raw.button_change_example));
                vidColourChangeExample.start();

                vidColourChangeExample.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.setLooping(true);
                    }
                });
            }
        });
    }


    private void setBtnCancelOnClick() {
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vidColourChangeExample.setVisibility(View.INVISIBLE);
                btnCancel.setVisibility(View.INVISIBLE);

                btnStartButtonChangeColour.setVisibility(View.VISIBLE);
                btnBackToReactionGames.setVisibility(View.VISIBLE);
                btnWatchColourChangeGameVid.setVisibility(View.VISIBLE);
                lblColourChangeDesc.setVisibility(View.VISIBLE);
                lblColourChangeTitle.setVisibility(View.VISIBLE);

                vidColourChangeExample.stopPlayback();
            }
        });
    }


}