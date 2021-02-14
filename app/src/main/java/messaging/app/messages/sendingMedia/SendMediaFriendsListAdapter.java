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

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import messaging.app.FriendsDetails;
import messaging.app.R;

class SendMediaFriendsListAdapter extends RecyclerView.Adapter<SendMediaFriendsListAdapter.ViewHolder> {

    Context context;
    private List<FriendsDetails> mFriendsDetailsList;
    public List mSelectedFriends = new ArrayList();


    public SendMediaFriendsListAdapter(List<FriendsDetails> friendsDetailsList, Context context) {
        mFriendsDetailsList = friendsDetailsList;
        this.context = context;
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

        FriendsDetails currentListItem = mFriendsDetailsList.get(position);
        //provide the details of each view element for each friend row
        holder.txtName.setText(currentListItem.getName());
        holder.txtDesc.setText(currentListItem.getDescription());
        holder.id = currentListItem.getId();
        Glide.with(context).load(currentListItem.getImageURL()).into(holder.imgFriendsImage);


        holder.friendRowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //manage selected friends and the background colour
                if(mSelectedFriends.contains(holder.id)){
                    mSelectedFriends.remove(mSelectedFriends.indexOf(holder.id));
                    holder.friendRowLayout.setBackgroundColor(Color.rgb(255,255,255));

                }else{
                    mSelectedFriends.add(holder.id);
                    holder.friendRowLayout.setBackgroundColor(Color.rgb(173,216,230));

                }
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
        TextView txtName;
        TextView txtDesc;
        ConstraintLayout friendRowLayout;
        int id;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgFriendsImage = itemView.findViewById(R.id.imgFriendsImage);
            txtName = itemView.findViewById(R.id.txtName);
            txtDesc = itemView.findViewById(R.id.txtDesc);
            friendRowLayout = itemView.findViewById(R.id.friendRowLayout);
        }

    }

}
