package messaging.app.settings;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import messaging.app.MediaManagement;
import messaging.app.R;
import messaging.app.contactingFirebase.ManagingFriends;
import messaging.app.games.AccountsHighScores;

public class AdminFriendsAdapter extends RecyclerView.Adapter<AdminFriendsAdapter.ViewHolder> {

    Context context;
    ManagingFriends managingFriends;
    HashMap<String, String> mAdminFriends;
    List keys;

    public AdminFriendsAdapter(HashMap<String, String> mAdminFriends, Context context) {
        this.context = context;
        this.mAdminFriends = mAdminFriends;
        managingFriends = new ManagingFriends(context);
        this.keys = new ArrayList(mAdminFriends.keySet());
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.current_admin_friends, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        String UUID = (String) keys.get(position);
        String name = mAdminFriends.get(UUID);

        holder.lblAdminFriendName.setText(name);

        holder.btnRemoveAdminFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdminFriends.remove(UUID);
                managingFriends.removeFriendAsAdmin(UUID);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mAdminFriends.size());
            }
        });
    }


    @Override
    public int getItemCount() {
        return mAdminFriends.size();
    }


    //friend row layout
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView lblAdminFriendName;
        ImageButton btnRemoveAdminFriend;
        ConstraintLayout adminFriendRowLayout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            lblAdminFriendName = itemView.findViewById(R.id.lblAdminFriendName);
            btnRemoveAdminFriend = itemView.findViewById(R.id.btnRemoveAdminFriend);
            adminFriendRowLayout = itemView.findViewById(R.id.adminFriendRowLayout);

        }

    }

}
