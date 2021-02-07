package messaging.app;

import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

class friendsListAdapter extends RecyclerView.Adapter<friendsListAdapter.ViewHolder> {

    List<friendsDetails> friendsDetailsList;
    Context context;


    public friendsListAdapter(List<friendsDetails> friendsDetailsList, Context context) {
        this.friendsDetailsList = friendsDetailsList;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        //provide the details of each view element for each friend row
        holder.txtName.setText(friendsDetailsList.get(position).getName());
        holder.txtDesc.setText(friendsDetailsList.get(position).getDescription());
        Glide.with(context).load(friendsDetailsList.get(position).getImageURL()).into(holder.imgFriendsImage);
        holder.friendRowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //save selected friend
                holder.imgBackground.setBackgroundColor(Color.rgb(37, 122, 253));
                Log.d("test", "onClick: change background" );
            }
        });

    }


    @Override
    public int getItemCount() {
        return friendsDetailsList.size();
    }


    //friend row layout
    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imgFriendsImage;
        ImageView imgBackground;
        TextView txtName;
        TextView txtDesc;
        ConstraintLayout friendRowLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgFriendsImage = itemView.findViewById(R.id.imgFriendsImage);
            imgBackground = itemView.findViewById(R.id.imgBackground);
            txtName = itemView.findViewById(R.id.txtName);
            txtDesc = itemView.findViewById(R.id.txtDesc);
            friendRowLayout = itemView.findViewById(R.id.friendRowLayout);
        }
    }
}
