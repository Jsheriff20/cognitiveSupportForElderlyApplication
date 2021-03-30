package messaging.app.games;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ExifInterface;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import messaging.app.AccountDetails;
import messaging.app.MediaManagement;
import messaging.app.R;

public class LeaderBoardAdapter extends RecyclerView.Adapter<LeaderBoardAdapter.ViewHolder> {

    Context context;
    List<AccountsHighScores> accountsHighScores;
    MediaManagement mediaManagement;

    private File mImageFolder;
    private String mImageFilePath;
    private String gamesType;

    public LeaderBoardAdapter(List<AccountsHighScores> accountsHighScores, String gamesType, Context context) {
        this.context = context;
        this.gamesType = gamesType;
        this.accountsHighScores = accountsHighScores;
        mediaManagement = new MediaManagement(context);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.high_score_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        AccountsHighScores accountsHighScore = accountsHighScores.get(position);
        holder.lblHighScoreFriendsName.setText(accountsHighScore.getFullName());
        if(gamesType.equals("memory")){
            String patternHighScore;
            String pairsHighScore;

            if(accountsHighScore.getPatternHighScore() == 0){
                patternHighScore = "Not played";
            }
            else{
                patternHighScore = String.valueOf(accountsHighScore.getPatternHighScore());
            }

            if(accountsHighScore.getPairsHighScore() == 0){
                pairsHighScore = "Not played";
            }
            else{
                pairsHighScore = String.valueOf(accountsHighScore.getPairsHighScore());
            }


            holder.lblFriendsHighScore.setText(
                    "Pairs: " + pairsHighScore +
                    "\nPattern: " + patternHighScore);
        }
        else{

            String buttonHighScore;
            String stoopTestHighScore;
            String gridReactionHighScore;

            if(accountsHighScore.getButtonChangeHighScore() == 0){
                buttonHighScore = "Not played";
            }
            else{
                buttonHighScore = accountsHighScore.getButtonChangeHighScore() + "ms";
            }

            if(accountsHighScore.getGridReactionHighScore() == 0){
                gridReactionHighScore = "Not played";
            }
            else{
                gridReactionHighScore = accountsHighScore.getGridReactionHighScore() + "ms";
            }

            if(accountsHighScore.getStoopTestHighScore() == 0){
                stoopTestHighScore = "Not played";
            }
            else{
                stoopTestHighScore = accountsHighScore.getStoopTestHighScore() + "ms";
            }

                holder.lblFriendsHighScore.setText(
                    "Single: " + buttonHighScore +
                    "\nGrid: " + gridReactionHighScore +
                    "\nWords: " + stoopTestHighScore );
        }


        if(!accountsHighScore.getProfileImageURL().equals(null) && !accountsHighScore.getProfileImageURL().equals("") ) {
            //create directories for files
            File[] mediaFolders = mediaManagement.createMediaFolders();
            mImageFolder = mediaFolders[1];

            try {

                mImageFilePath = mediaManagement.createImageFileName(mImageFolder).getAbsolutePath();
                try (BufferedInputStream inputStream = new BufferedInputStream(new URL(accountsHighScore.getProfileImageURL()).openStream());
                     FileOutputStream fileOS = new FileOutputStream(mImageFilePath)) {
                    byte data[] = new byte[1024];
                    int byteContent;
                    while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
                        fileOS.write(data, 0, byteContent);
                    }

                } catch (IOException e) {
                    // handles IO exceptions
                }


                ExifInterface exif = null;
                //display the media in the correct rotation
                exif = new ExifInterface(mImageFilePath);
                int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                Bitmap myBitmap = BitmapFactory.decodeFile(new File(mImageFilePath).getAbsolutePath());

                Bitmap adjustedBitmapImage = mediaManagement.adjustBitmapImage(exifOrientation, myBitmap);

                holder.imgHighScoreProfileImage.setImageBitmap(adjustedBitmapImage);

                mediaManagement.deleteMediaFile(mImageFilePath, context);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public int getItemCount() {
        return accountsHighScores.size();
    }


    //friend row layout
    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imgHighScoreProfileImage;
        TextView lblHighScoreFriendsName;
        TextView lblFriendsHighScore;
        ConstraintLayout highScoreRow;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgHighScoreProfileImage = itemView.findViewById(R.id.imgHighScoreProfileImage);
            lblHighScoreFriendsName = itemView.findViewById(R.id.lblHighScoreFriendsName);
            lblFriendsHighScore = itemView.findViewById(R.id.lblFriendsHighScore);
            highScoreRow = itemView.findViewById(R.id.highScoreRow);


            int SDK_INT = android.os.Build.VERSION.SDK_INT;
            if (SDK_INT > 8) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);

            }
        }

    }

}
