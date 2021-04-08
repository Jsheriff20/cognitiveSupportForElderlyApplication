package messaging.app.messages.friendsList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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

import messaging.app.AccountDetails;
import messaging.app.MediaManagement;
import messaging.app.R;

public class ViewFriendsListAdapter extends RecyclerView.Adapter<ViewFriendsListAdapter.ViewHolder> {

    Context mContext;
    private List mFriendsDetailsList;
    private File mImageFolder;
    private String mImageFilePath;

    MediaManagement mMediaManagement;


    public ViewFriendsListAdapter(List friendsDetailsList, Context context) {
        mFriendsDetailsList = friendsDetailsList;
        this.mContext = context;
        mMediaManagement = new MediaManagement(context);
    }


    @NonNull
    @Override
    public ViewFriendsListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_list_row, parent, false);
        ViewFriendsListAdapter.ViewHolder viewHolder = new ViewFriendsListAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final AccountDetails friendsDetails = (AccountDetails) mFriendsDetailsList.get(position);
        //provide the details of each view element for each friend row
        holder.lblName.setText(friendsDetails.getmFirstName() + " " + friendsDetails.getmSurname());
        holder.lblRelationship.setText(friendsDetails.getmRelationship());
        holder.UUID = friendsDetails.getmUUID();
        holder.username = friendsDetails.getmUsername();
        holder.profileImageRotation = mMediaManagement.exifToDegrees(friendsDetails.getmProfileImageRotation());
        holder.profileImageUrl = friendsDetails.getmProfileImageUrl();


        if (holder.profileImageUrl != null && !holder.profileImageUrl.equals("")) {
            //create directories for files
            File[] mediaFolders = mMediaManagement.createMediaFolders();
            mImageFolder = mediaFolders[1];

            try {
                mImageFilePath = mMediaManagement.createImageFileName(mImageFolder).getAbsolutePath();
                try (BufferedInputStream inputStream =
                             new BufferedInputStream(new URL(holder.profileImageUrl).openStream());
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
                int exifOrientation =
                        exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                Bitmap myBitmap = BitmapFactory.decodeFile(new File(mImageFilePath).getAbsolutePath());

                Bitmap adjustedBitmapImage = mMediaManagement.adjustBitmapImage(exifOrientation, myBitmap);

                holder.imgProfileImage.setImageBitmap(adjustedBitmapImage);


                mMediaManagement.deleteMediaFile(mImageFilePath, mContext);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        holder.btnEditFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, EditFriendActivity.class);
                intent.putExtra("name", holder.lblName.getText().toString());
                intent.putExtra("relationship", holder.lblRelationship.getText().toString());
                intent.putExtra("UUID", holder.UUID);
                intent.putExtra("username", holder.username);
                intent.putExtra("profileImageUrl", holder.profileImageUrl);
                intent.putExtra("profileImageRotation", holder.profileImageRotation);

                if (((Activity) mContext).getIntent().getStringExtra("adminUUID") != null) {
                    intent.putExtra("adminUUID",
                            ((Activity) mContext).getIntent().getStringExtra("adminUUID"));
                }
                holder.itemViewContext.startActivity(intent);
                return;
            }
        });

    }

    @Override
    public int getItemCount() {
        return mFriendsDetailsList.size();
    }


    //friend row layout
    public class ViewHolder extends RecyclerView.ViewHolder {
        String profileImageUrl;
        ImageView imgProfileImage;
        int profileImageRotation = 0;
        TextView lblName;
        TextView lblRelationship;
        ConstraintLayout friendRowLayout;
        String UUID;
        String username;
        ImageButton btnEditFriend;
        Context itemViewContext;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            itemViewContext = itemView.getContext();

            imgProfileImage = itemView.findViewById(R.id.imgFriendsListProfileImage);
            lblName = itemView.findViewById(R.id.lblFriendsName);
            lblRelationship = itemView.findViewById(R.id.lblFriendsRelationship);
            friendRowLayout = itemView.findViewById(R.id.friendsListRow);
            btnEditFriend = itemView.findViewById(R.id.btnEditFriend);

            int SDK_INT = android.os.Build.VERSION.SDK_INT;
            if (SDK_INT > 8) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);

            }

        }

    }

}
