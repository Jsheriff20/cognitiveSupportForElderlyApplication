package messaging.app.messages.friendsList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import messaging.app.AccountDetails;
import messaging.app.MediaManagement;
import messaging.app.R;
import messaging.app.register.RegisterPasswordActivity;
import messaging.app.register.RegisterUsernameActivity;

public class ViewFriendsListAdapter extends RecyclerView.Adapter<ViewFriendsListAdapter.ViewHolder> {

    Context context;
    private List mFriendsDetailsList;

    MediaManagement mediaManagement = new MediaManagement();


    public ViewFriendsListAdapter(List friendsDetailsList, Context context) {
        mFriendsDetailsList = friendsDetailsList;
        this.context = context;
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
        holder.lblName.setText(friendsDetails.getFirstName() + " " + friendsDetails.getSurname());
        holder.lblRelationship.setText(friendsDetails.getRelationship());
        holder.UUID = friendsDetails.getUUID();
        holder.username = friendsDetails.getUsername();
        holder.profileImageRotation = mediaManagement.exifToDegrees(friendsDetails.getProfileImageRotation());
        holder.profileImageUrl = friendsDetails.getProfileImageUrl();
        Picasso.with(context).load(holder.profileImageUrl).rotate(holder.profileImageRotation).into(holder.imgProfileImage);

        Log.d("Test", "profileImageRotation: " + holder.profileImageRotation);
        Log.d("Test", "profileImageUrl: " + holder.profileImageUrl);


        //TODO:
        //display profile image
        //setup image button

        holder.btnEditFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditFriendActivity.class);
                intent.putExtra("name", holder.lblName.getText().toString());
                intent.putExtra("relationship", holder.lblRelationship.getText().toString());
                intent.putExtra("UUID", holder.UUID);
                intent.putExtra("username", holder.username);
                intent.putExtra("profileImageUrl", holder.profileImageUrl);
                intent.putExtra("profileImageRotation", holder.profileImageRotation);
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
    public class ViewHolder extends RecyclerView.ViewHolder{
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

        }

    }

}
