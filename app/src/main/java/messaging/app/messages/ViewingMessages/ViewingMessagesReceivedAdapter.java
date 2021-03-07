package messaging.app.messages.ViewingMessages;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import messaging.app.ContactingFirebase;
import messaging.app.MediaManagement;
import messaging.app.R;
import messaging.app.messages.capturingMedia.CaptureActivity;

public class ViewingMessagesReceivedAdapter extends RecyclerView.Adapter {
    List<HashMap<String, String>> mReceivedMediaDetails;
    int mNumberOfStories;
    Context context;
    MediaManagement mediaManagement = new MediaManagement();
    ContactingFirebase contactingFirebase;


    public ViewingMessagesReceivedAdapter(List<HashMap<String, String>> receivedMediaDetails, int numberOfStories, Context context) {
        this.mReceivedMediaDetails = receivedMediaDetails;
        this.mNumberOfStories = numberOfStories;
        this.context = context;
        contactingFirebase = new ContactingFirebase(context);
    }


    public class StoryViewHolder extends RecyclerView.ViewHolder {
        TextView lblStory;
        TextView lblStoryStatus;
        ImageView imgStoryStatus;
        Context itemViewContext;


        public StoryViewHolder(@NonNull View itemView) {
            super(itemView);

            itemViewContext = itemView.getContext();
            lblStory = itemView.findViewById(R.id.lblStory);
            lblStoryStatus = itemView.findViewById(R.id.lblStoryStatus);
            imgStoryStatus = itemView.findViewById(R.id.imgStoryStatus);
        }
    }


    public class FriendsMessagesViewHolder extends RecyclerView.ViewHolder {
        int position;
        String UUID;
        TextView lblViewMessageFriendsName;
        ImageView imgFriendsProfileImage;
        ImageButton btnMessageAction;
        Context itemViewContext;


        public FriendsMessagesViewHolder(@NonNull View itemView) {
            super(itemView);

            itemViewContext = itemView.getContext();
            lblViewMessageFriendsName = itemView.findViewById(R.id.lblViewMessageFriendsName);
            imgFriendsProfileImage = itemView.findViewById(R.id.imgFriendsProfileImage);
            btnMessageAction = itemView.findViewById(R.id.btnMessageAction);
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                View storyView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_story_row, parent, false);
                return new ViewingMessagesReceivedAdapter.StoryViewHolder(storyView);
            case 1:
                View friendsMessagesView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_friends_message_row, parent, false);
                return new ViewingMessagesReceivedAdapter.FriendsMessagesViewHolder(friendsMessagesView);
            default:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_friends_message_row, parent, false);
                return new ViewingMessagesReceivedAdapter.FriendsMessagesViewHolder(view);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 0:
                final ViewingMessagesReceivedAdapter.StoryViewHolder storyViewHolder = (ViewingMessagesReceivedAdapter.StoryViewHolder) holder;
                if(mNumberOfStories > 0) {
                    storyViewHolder.lblStoryStatus.setText(mNumberOfStories + " received");
                    storyViewHolder.imgStoryStatus.setImageResource(R.drawable.closed_envelope_shadow_icon);
                    storyViewHolder.imgStoryStatus.setBackgroundResource(R.drawable.btn_rectangle_red_gradient);
                    storyViewHolder.imgStoryStatus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            contactingFirebase.getStoryForUUID(new ContactingFirebase.OnGetStoryForUUIDListener() {
                                @Override
                                public void onSuccess(ArrayList<MessageData> storyMessagesDataList) {
                                    Intent intent;

                                    //check if there is a text message to display
                                    if(storyMessagesDataList.get(0).getTextMessage().equals(null) || storyMessagesDataList.get(0).getTextMessage().equals("")) {
                                        intent = new Intent(context, ViewMediaMessage.class);
                                    }
                                    else{
                                        intent = new Intent(context, ViewTextMessage.class);
                                    }


                                    intent.putExtra("messagesList", storyMessagesDataList);
                                    intent.putExtra("numOfMessages", (storyMessagesDataList.size()));
                                    intent.putExtra("viewingType", "story");
                                    storyViewHolder.itemViewContext.startActivity(intent);
                                }
                            });
                        }
                    });
                }
                else{
                    storyViewHolder.lblStoryStatus.setText("No new stories");
                    storyViewHolder.imgStoryStatus.setImageResource(R.drawable.opened_envelope_shadow_icon);
                    storyViewHolder.imgStoryStatus.setBackgroundResource(R.drawable.btn_rectangle_green_gradient);
                }
                break;
            case 1:
                final ViewingMessagesReceivedAdapter.FriendsMessagesViewHolder friendsMessagesViewHolder = (ViewingMessagesReceivedAdapter.FriendsMessagesViewHolder) holder;
                friendsMessagesViewHolder.position = position -1;
                Map<String, String> currentKVPair = mReceivedMediaDetails.get(friendsMessagesViewHolder.position);

                friendsMessagesViewHolder.UUID = currentKVPair.get("UUID");
                friendsMessagesViewHolder.lblViewMessageFriendsName.setText(currentKVPair.get("fullName"));

                if(currentKVPair.get("profileImageUrl") != null) {
                    int profileImageRotation = mediaManagement.exifToDegrees(Integer.parseInt(currentKVPair.get("profileImageRotation")));
                    Picasso.with(context).load(currentKVPair.get("profileImageUrl"))
                            .rotate(profileImageRotation)
                            .into(friendsMessagesViewHolder.imgFriendsProfileImage);
                }
                if(Integer.parseInt(currentKVPair.get("unopenedMessage")) == 1){
                    friendsMessagesViewHolder.btnMessageAction.setImageResource(R.drawable.closed_envelope_shadow_icon);
                    friendsMessagesViewHolder.btnMessageAction.setBackgroundResource(R.drawable.btn_rectangle_red_gradient);

                    friendsMessagesViewHolder.btnMessageAction.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            //on success get message data using the messageData class
                            contactingFirebase.getMessagesFromUUID(friendsMessagesViewHolder.UUID, new ContactingFirebase.OnGetMessagesFromListener() {
                                @Override
                                public void onSuccess(ArrayList<MessageData> messageDataList) {
                                    Intent intent;

                                    //check if there is a text message to display
                                    if(messageDataList.get(0).getTextMessage().equals(null) || messageDataList.get(0).getTextMessage().equals("")) {
                                        intent = new Intent(context, ViewMediaMessage.class);
                                    }
                                    else{
                                        intent = new Intent(context, ViewTextMessage.class);
                                    }


                                    intent.putExtra("messagesList", messageDataList);
                                    intent.putExtra("friendsUUID", friendsMessagesViewHolder.UUID);
                                    intent.putExtra("numOfMessages", (messageDataList.size()));
                                    intent.putExtra("viewingType", "directMessages");
                                    friendsMessagesViewHolder.itemViewContext.startActivity(intent);
                                }
                            });
                        }
                    });
                }
                else{
                    friendsMessagesViewHolder.btnMessageAction.setImageResource(R.drawable.send_icon);
                    friendsMessagesViewHolder.btnMessageAction.setBackgroundResource(R.drawable.btn_rectangle_orange_gradient);


                    friendsMessagesViewHolder.btnMessageAction.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, CaptureActivity.class);
                            intent.putExtra("replyingTo", friendsMessagesViewHolder.UUID);
                            friendsMessagesViewHolder.itemViewContext.startActivity(intent);
                        }
                    });
                }

                break;
        }
    }


    @Override
    public int getItemCount() {
        //+1 is for the story
        return (mReceivedMediaDetails.size() + 1);
    }


    public int getItemViewType(int position) {
        //if its the first position, display the story
        if (position == 0) {
            return 0;
        }
        return 1;
    }
}
