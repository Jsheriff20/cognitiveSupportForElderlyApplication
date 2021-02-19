package messaging.app.messages.friendsList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;

import messaging.app.ContactingFirebase;
import messaging.app.R;

public class AddFriendsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<HashMap<String, FriendRequestHelper>> mSentFriendRequests;
    List<HashMap<String, String>> mReceivedFriendRequests;
    Context context;
    ContactingFirebase contactingFirebase = new ContactingFirebase(context);;


    public AddFriendsAdapter(List<HashMap<String, FriendRequestHelper>> mSentFriendRequests, List<HashMap<String, String>> mReceivedFriendRequests, Context context) {
        this.mSentFriendRequests = mSentFriendRequests;
        this.mReceivedFriendRequests = mReceivedFriendRequests;
        this.context = context;

    }


    public class SentFriendRequestsViewHolder extends RecyclerView.ViewHolder {
        int position;
        TextView lblSentFriendRequestUsername;
        TextView lblSentFriendRequestRelationship;
        ImageButton btnCancelFriendRequest;
        String UUID;


        public SentFriendRequestsViewHolder(@NonNull View itemView) {
            super(itemView);

            lblSentFriendRequestRelationship = itemView.findViewById(R.id.lblSentFriendRequestRelationship);
            lblSentFriendRequestUsername = itemView.findViewById(R.id.lblReceivedFriendRequestUsername);
            btnCancelFriendRequest = itemView.findViewById(R.id.btnCancelFriendRequest);
        }
    }


    public class ReceivedFriendRequestsViewHolder extends RecyclerView.ViewHolder {
        public String receivedUsername;
        int position;
        TextView lblReceivedFriendRequestUsername;
        EditText txtReceivedFriendRequestRelationship;
        ImageButton btnRejectFriendRequest;
        ImageButton btnConfirmFriendRequest;
        String UUID;


        public ReceivedFriendRequestsViewHolder(@NonNull View itemView) {
            super(itemView);
            lblReceivedFriendRequestUsername = itemView.findViewById(R.id.lblReceivedFriendRequestUsername);
            txtReceivedFriendRequestRelationship = itemView.findViewById(R.id.txtReceivedFriendRequestRelationship);
            btnRejectFriendRequest = itemView.findViewById(R.id.btnRejectFriendRequest);
            btnConfirmFriendRequest = itemView.findViewById(R.id.btnConfirmFriendRequest);
        }
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType){
            case 0:
                View sentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sent_friend_request, parent, false);
                return new SentFriendRequestsViewHolder(sentView);
            case 1:
                View receivedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.received_friend_request, parent, false);
                return new ReceivedFriendRequestsViewHolder(receivedView);
            default:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sent_friend_request, parent, false);
                return new SentFriendRequestsViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()){
            case 0:

                final SentFriendRequestsViewHolder sentFriendRequestsViewHolder = (SentFriendRequestsViewHolder) holder;
                sentFriendRequestsViewHolder.UUID = (String) mSentFriendRequests.get(position).keySet().toArray()[position];
                sentFriendRequestsViewHolder.position = position;
                FriendRequestHelper friendRequestHelper = mSentFriendRequests.get(position).get(sentFriendRequestsViewHolder.UUID);
                String username = friendRequestHelper.getUsername();
                String relationship = friendRequestHelper.getRelationship();

                //display data
                sentFriendRequestsViewHolder.lblSentFriendRequestUsername.setText(username);
                sentFriendRequestsViewHolder.lblSentFriendRequestRelationship.setText(relationship);

                sentFriendRequestsViewHolder.btnCancelFriendRequest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //cancel the friend requests
                        contactingFirebase.cancelSentFriendRequest(sentFriendRequestsViewHolder.UUID);

                        //hide the friend request
                        sentFriendRequestsViewHolder.itemView.setVisibility(View.GONE);
                        ViewGroup.LayoutParams params = sentFriendRequestsViewHolder.itemView.getLayoutParams();
                        params.height = 0;
                        params.width = 0;
                        sentFriendRequestsViewHolder.itemView.setLayoutParams(params);

                        //remove from list
                        mSentFriendRequests.remove(sentFriendRequestsViewHolder.position);
                    }
                });
                break;
            case 1:
                final ReceivedFriendRequestsViewHolder receivedFriendRequestsViewHolder = (ReceivedFriendRequestsViewHolder) holder;
                receivedFriendRequestsViewHolder.position = position - (mSentFriendRequests.size());
                Log.d("Test", "receivedFriendRequestsViewHolder.position: " + receivedFriendRequestsViewHolder.position);
                Log.d("Test", "position: " + position);
                Log.d("Test", "mSentFriendRequests.size(): " + mSentFriendRequests.size());

                receivedFriendRequestsViewHolder.UUID = (String) mReceivedFriendRequests.get(receivedFriendRequestsViewHolder.position).keySet().toArray()[0];
                Log.d("Test", "receivedFriendRequestsViewHolder.UUID: " + receivedFriendRequestsViewHolder.UUID);
                Log.d("Test", "mReceivedFriendRequests.get(receivedFriendRequestsViewHolder.position).keySet(): " + mReceivedFriendRequests.get(receivedFriendRequestsViewHolder.position).keySet());
                receivedFriendRequestsViewHolder.receivedUsername = (String) mReceivedFriendRequests.get(receivedFriendRequestsViewHolder.position).get(receivedFriendRequestsViewHolder.UUID);
                receivedFriendRequestsViewHolder.lblReceivedFriendRequestUsername.setText(receivedFriendRequestsViewHolder.receivedUsername);

                receivedFriendRequestsViewHolder.btnConfirmFriendRequest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //accept friend request
                        contactingFirebase.acceptFriendRequest(receivedFriendRequestsViewHolder.UUID, receivedFriendRequestsViewHolder.receivedUsername);

                        //hide the friend request
                        receivedFriendRequestsViewHolder.itemView.setVisibility(View.GONE);
                        ViewGroup.LayoutParams params = receivedFriendRequestsViewHolder.itemView.getLayoutParams();
                        params.height = 0;
                        params.width = 0;
                        receivedFriendRequestsViewHolder.itemView.setLayoutParams(params);

                        //remove from list
                        mReceivedFriendRequests.remove(receivedFriendRequestsViewHolder.position);
                    }
                });

                receivedFriendRequestsViewHolder.btnRejectFriendRequest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //remove the friend request
                        contactingFirebase.cancelReceivedFriendRequest(receivedFriendRequestsViewHolder.UUID);

                        //hide the friend request
                        receivedFriendRequestsViewHolder.itemView.setVisibility(View.GONE);
                        ViewGroup.LayoutParams params = receivedFriendRequestsViewHolder.itemView.getLayoutParams();
                        params.height = 0;
                        params.width = 0;
                        receivedFriendRequestsViewHolder.itemView.setLayoutParams(params);

                        //remove from list
                        mReceivedFriendRequests.remove(receivedFriendRequestsViewHolder.position);
                    }
                });

        }
    }


    @Override
    public int getItemCount() {
        return (mSentFriendRequests.size() + mReceivedFriendRequests.size());
    }


    public int getItemViewType(int position){
        Log.d("Test", "position: " + position);
        if(position < mSentFriendRequests.size()){
            Log.d("Test", "return: " + 0);
            return 0;
        }
        Log.d("Test", "return: " + 1);
        return 1;
    }

}
