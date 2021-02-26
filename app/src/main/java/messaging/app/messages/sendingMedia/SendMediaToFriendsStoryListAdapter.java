package messaging.app.messages.sendingMedia;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

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

    public interface onFriendsStorySelectRecyclerViewClickedUUIDListener{
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
        Picasso.with(context).load(currentListItem.getProfileImageUrl()).rotate(holder.profileImageRotation).into(holder.imgFriendsImage);


        holder.friendRowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //manage selected friends and the background colour
                if(mSelectedFriends.contains(holder.UUID)){
                    mSelectedFriends.remove(mSelectedFriends.indexOf(holder.UUID));
                    holder.friendRowLayout.setBackgroundColor(Color.rgb(255,255,255));

                }else{
                    mSelectedFriends.add(holder.UUID);
                    holder.friendRowLayout.setBackgroundColor(Color.rgb(230,187,173));

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
    public class ViewHolder extends RecyclerView.ViewHolder{
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
        }

    }

}
