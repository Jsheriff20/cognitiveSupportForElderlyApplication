package messaging.app.messages.sendingMedia;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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

import com.squareup.picasso.Picasso;

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

public class SendMediaToFriendsStoryListAdapter extends RecyclerView.Adapter<SendMediaToFriendsStoryListAdapter.ViewHolder> {

    Context context;
    private List<AccountDetails> mFriendsDetailsList;
    public List mSelectedFriends = new ArrayList();
    MediaManagement mediaManagement = new MediaManagement();
    onFriendsStorySelectRecyclerViewClickedUUIDListener listener;
    private File mImageFolder;
    private String mImageFilePath;


    public interface onFriendsStorySelectRecyclerViewClickedUUIDListener {
        //message type will either be story or friend (the direct message)
        void onSelected(String UUID, String messageType);
    }


    public SendMediaToFriendsStoryListAdapter(List<AccountDetails> friendsDetailsList, Context context, onFriendsStorySelectRecyclerViewClickedUUIDListener listener) {
        mFriendsDetailsList = friendsDetailsList;
        this.context = context;
        this.listener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.send_media_friend_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        AccountDetails currentListItem = mFriendsDetailsList.get(position);
        //provide the details of each view element for each friend row
        holder.lblName.setText(currentListItem.getFirstName() + " " + currentListItem.getSurname());
        holder.lblRelationship.setText(currentListItem.getRelationship());
        holder.UUID = currentListItem.getUUID();
        holder.profileImageRotation = mediaManagement.exifToDegrees(currentListItem.getProfileImageRotation());

        if (currentListItem.getProfileImageUrl() != null && !currentListItem.getProfileImageUrl().equals("")) {
            //create directories for files
            File[] mediaFolders = mediaManagement.createMediaFolders();
            mImageFolder = mediaFolders[1];

            try {

                mImageFilePath = mediaManagement.createImageFileName(mImageFolder).getAbsolutePath();
                try (BufferedInputStream inputStream = new BufferedInputStream(new URL(currentListItem.getProfileImageUrl()).openStream());
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

                holder.imgFriendsImage.setImageBitmap(adjustedBitmapImage);

                mediaManagement.deleteMediaFile(mImageFilePath, context);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        holder.friendRowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //manage selected friends and the background colour
                if (mSelectedFriends.contains(holder.UUID)) {
                    mSelectedFriends.remove(mSelectedFriends.indexOf(holder.UUID));
                    holder.friendRowLayout.setBackgroundColor(Color.rgb(255, 255, 255));

                } else {
                    mSelectedFriends.add(holder.UUID);
                    holder.friendRowLayout.setBackgroundColor(Color.rgb(230, 187, 173));

                }
                listener.onSelected(holder.UUID, "story");
            }
        });

    }


    @Override
    public int getItemCount() {
        return mFriendsDetailsList.size();
    }


    //friend row layout
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFriendsImage;
        TextView lblName;
        TextView lblRelationship;
        ConstraintLayout friendRowLayout;
        String UUID;
        int profileImageRotation;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgFriendsImage = itemView.findViewById(R.id.imgFriendsImage);
            lblName = itemView.findViewById(R.id.lblSendMessageName);
            lblRelationship = itemView.findViewById(R.id.lblSendMessageRelationship);
            friendRowLayout = itemView.findViewById(R.id.sendMediaFriendRowLayout);


            int SDK_INT = android.os.Build.VERSION.SDK_INT;
            if (SDK_INT > 8) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);

            }
        }

    }

}
