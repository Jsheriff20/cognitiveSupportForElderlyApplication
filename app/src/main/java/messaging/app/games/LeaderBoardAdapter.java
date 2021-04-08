package messaging.app.games;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.StrictMode;
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
import java.util.List;

import messaging.app.MediaManagement;
import messaging.app.R;

public class LeaderBoardAdapter extends RecyclerView.Adapter<LeaderBoardAdapter.ViewHolder> {

    Context mContext;
    List<AccountsHighScores> mAccountsHighScores;
    MediaManagement mMediaManagement;

    private File mImageFolder;
    private String mImageFilePath;
    private String mGamesType;

    public LeaderBoardAdapter(List<AccountsHighScores> accountsHighScores,
                              String gamesType, Context context) {
        this.mContext = context;
        this.mGamesType = gamesType;
        this.mAccountsHighScores = accountsHighScores;
        mMediaManagement = new MediaManagement(context);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.high_score_row,
                parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        AccountsHighScores accountsHighScore = mAccountsHighScores.get(position);
        holder.lblHighScoreFriendsName.setText(accountsHighScore.getmFullName());
        if (mGamesType.equals("memory")) {
            String patternHighScore;
            String pairsHighScore;

            if (accountsHighScore.getmPatternHighScore() == 0) {
                patternHighScore = "Not played";
            } else {
                patternHighScore = String.valueOf(accountsHighScore.getmPatternHighScore());
            }

            if (accountsHighScore.getmPairsHighScore() == 0) {
                pairsHighScore = "Not played";
            } else {
                pairsHighScore = String.valueOf(accountsHighScore.getmPairsHighScore());
            }


            holder.lblFriendsHighScore.setText(
                    "Pairs: " + pairsHighScore +
                            "\nPattern: " + patternHighScore);
        } else {

            String buttonHighScore;
            String stoopTestHighScore;
            String gridReactionHighScore;

            if (accountsHighScore.getmButtonChangeHighScore() == 0) {
                buttonHighScore = "Not played";
            } else {
                buttonHighScore = accountsHighScore.getmButtonChangeHighScore() + "ms";
            }

            if (accountsHighScore.getmGridReactionHighScore() == 0) {
                gridReactionHighScore = "Not played";
            } else {
                gridReactionHighScore = accountsHighScore.getmGridReactionHighScore() + "ms";
            }

            if (accountsHighScore.getmStoopTestHighScore() == 0) {
                stoopTestHighScore = "Not played";
            } else {
                stoopTestHighScore = accountsHighScore.getmStoopTestHighScore() + "ms";
            }

            holder.lblFriendsHighScore.setText(
                    "Single: " + buttonHighScore +
                            "\nGrid: " + gridReactionHighScore +
                            "\nWords: " + stoopTestHighScore);
        }


        if (!accountsHighScore.getmProfileImageURL().equals(null) &&
                !accountsHighScore.getmProfileImageURL().equals("")) {
            //create directories for files
            File[] mediaFolders = mMediaManagement.createMediaFolders();
            mImageFolder = mediaFolders[1];

            try {

                mImageFilePath = mMediaManagement.createImageFileName(mImageFolder).getAbsolutePath();
                try (BufferedInputStream inputStream = new BufferedInputStream(
                        new URL(accountsHighScore.getmProfileImageURL()).openStream());
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
                int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL);
                Bitmap myBitmap = BitmapFactory.decodeFile(new File(mImageFilePath).getAbsolutePath());

                Bitmap adjustedBitmapImage = mMediaManagement
                        .adjustBitmapImage(exifOrientation, myBitmap);

                holder.imgHighScoreProfileImage.setImageBitmap(adjustedBitmapImage);

                mMediaManagement.deleteMediaFile(mImageFilePath, mContext);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public int getItemCount() {
        return mAccountsHighScores.size();
    }


    //friend row layout
    public class ViewHolder extends RecyclerView.ViewHolder {
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
