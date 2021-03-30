package messaging.app.contactingFirebase;

import android.accounts.Account;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import messaging.app.AccountDetails;
import messaging.app.MessageReceivedServiceNotification;
import messaging.app.games.AccountsHighScores;
import messaging.app.messages.viewingMessages.MessageData;
import messaging.app.messages.friendsList.FriendRequestHelper;

public class QueryingDatabase {

    FirebaseDatabase mDatabase;
    FirebaseAuth mAuth;

    public QueryingDatabase() {
        mDatabase = FirebaseDatabase.getInstance();
    }

    public interface OnCheckIfUsernameExistsListener {
        void onSuccess(boolean exists);
    }

    public void doesUsernameExist(final String username, final OnCheckIfUsernameExistsListener listener) {

        mDatabase.getReference("usernames").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    listener.onSuccess(true);
                    return;
                }
                listener.onSuccess(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    public interface OnCheckIfUserIsBlockedByListener {
        void onSuccess(boolean blocked);
    }

    public void isUserBlockedBy(final String blockedByUsername, final OnCheckIfUserIsBlockedByListener listener) {

        getUsernamesUUID(blockedByUsername, new OnGetUUIDListener() {
            @Override
            public void onSuccess(String blockedByUUID) {

                mAuth = FirebaseAuth.getInstance();
                String usersUUID = mAuth.getCurrentUser().getUid();

                mDatabase.getReference("userDetails").child(usersUUID + "/blockedBy/" + blockedByUUID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.getValue() == null) {
                            listener.onSuccess(false);
                        } else {
                            listener.onSuccess(true);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });
    }


    public interface OnCheckIfUserIsAlreadyFriendsWithListener {
        void onSuccess(boolean alreadyFriends);
    }

    public void isUserAlreadyFriendsWith(final String friendsWithUsername, final OnCheckIfUserIsAlreadyFriendsWithListener listener) {

        getUsernamesUUID(friendsWithUsername, new OnGetUUIDListener() {
            @Override
            public void onSuccess(String friendsWithUUID) {

                mDatabase.getReference("userDetails").child(getCurrentUsersUUID() + "/friends/" + friendsWithUUID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.getValue() == null) {
                            listener.onSuccess(false);
                        } else {
                            listener.onSuccess(true);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });
    }

    public boolean isStoryMessageOld(String timestamp) {
        Date now = new Date();
        long currentUnixTime = now.getTime() / 1000L;


        long messageSentUnixTime = Long.parseLong(timestamp);
        long unixTimeAgo = currentUnixTime - messageSentUnixTime;
        if (unixTimeAgo < 259200) {
            return false;
        }
        return true;
    }


    public interface OnEmailCheckListener {
        void onSuccess(boolean isRegistered);
    }

    public void isEmailAvailable(final String email, final OnEmailCheckListener listener) {

        mAuth = FirebaseAuth.getInstance();
        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                boolean check = !task.getResult().getSignInMethods().isEmpty();

                listener.onSuccess(check);
            }
        });

    }


    int previousNumberOfMessage = 0;
    boolean initiationOfMessageListener = true;

    public void listenForReceivedMessage(final Context context) {

        DatabaseReference databaseRef = mDatabase.getReference("messages/" + getCurrentUsersUUID());
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //check that there is a message
                if (snapshot.getValue() == null) {
                    return;
                }

                int numberOfMessages = getNumberOfMessages((Map<String, Object>) snapshot.getValue());

                //check that a new message has been added and that it is not the first loop
                if (previousNumberOfMessage < numberOfMessages && !initiationOfMessageListener) {
                    //send notification
                    MessageReceivedServiceNotification messageReceivedServiceNotification =
                            new MessageReceivedServiceNotification(context);
                    messageReceivedServiceNotification.sendNotification("New message received");
                }
                initiationOfMessageListener = false;
                previousNumberOfMessage = numberOfMessages;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    int previousNumberOfStoryMessages = 0;
    boolean initiationOfStoryMessageListener = true;

    public void listenForReceivedStoryMessage(final Context context) {

        DatabaseReference databaseRef = mDatabase.getReference("stories/" + getCurrentUsersUUID());
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int numberOfStoryMessages = (int) snapshot.getChildrenCount();

                String sendersName = "New message";
//                int loopNum = 0;
//                for (DataSnapshot ds : snapshot.getChildren()) {
//                    loopNum++;
//                    if (loopNum == numberOfStoryMessages) {
//                        for (DataSnapshot subDS : ds.getChildren()) {
//                            {
//                                if (subDS.getKey().equals("fullName")) {
//                                    sendersName = (String) subDS.getValue();
//                                    break;
//
//                                }
//                            }
//                        }
//                        break;
//                    }
//                }
//

                //check that a new message has been added and that it is not the first loop
                if (previousNumberOfStoryMessages < numberOfStoryMessages && !initiationOfStoryMessageListener) {
                    //send notification
                    MessageReceivedServiceNotification messageReceivedServiceNotification =
                            new MessageReceivedServiceNotification(context);
                    messageReceivedServiceNotification.sendNotification(sendersName + " added to your story");
                }
                initiationOfStoryMessageListener = false;
                previousNumberOfStoryMessages = numberOfStoryMessages;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public String getCurrentUsersUUID() {
        mAuth = FirebaseAuth.getInstance();
        return mAuth.getCurrentUser().getUid();
    }


    public String getCurrentUsersUsername() {
        mAuth = FirebaseAuth.getInstance();
        return mAuth.getCurrentUser().getDisplayName();
    }


    public interface OnGetFriendsDetailsListener {
        void onSuccess(List friendsDetails);
    }


    public void getFriendsDetails(final OnGetFriendsDetailsListener listener) {

        mDatabase.getReference("userDetails").child(getCurrentUsersUUID() + "/friends").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //get friends
                List friendsDetailsList = new ArrayList<AccountDetails>();
                for (DataSnapshot ds : snapshot.getChildren()) {

                    AccountDetails friendsDetails = getAccountDetailsFromSnapshot(ds);
                    friendsDetails.setUUID(ds.getKey());
                    friendsDetailsList.add(friendsDetails);
                }
                listener.onSuccess(friendsDetailsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    public AccountDetails getAccountDetailsFromSnapshot(DataSnapshot snapshot) {
        AccountDetails accountDetails = new AccountDetails();

        for (DataSnapshot ds : snapshot.getChildren()) {
            switch (ds.getKey()) {
                case "profileImageUrl":
                    accountDetails.setProfileImageUrl((String) ds.getValue());
                    break;
                case "profileImageRotation":
                    long rotation = (long) ds.getValue();
                    accountDetails.setProfileImageRotation((int) rotation);
                    break;
                case "firstName":
                    accountDetails.setFirstName((String) ds.getValue());
                    break;
                case "surname":
                    accountDetails.setSurname((String) ds.getValue());
                    break;
                case "relationship":
                    accountDetails.setRelationship((String) ds.getValue());
                    break;
                case "username":
                    accountDetails.setUsername((String) ds.getValue());
                    break;
                default:
            }
        }
        return accountDetails;
    }


    public int getNumberOfMessages(Map<String, Object> users) {

        int numberOfMessages = 0;
        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()) {

            //Get user map
            Map singleUser = (Map) entry.getValue();
            numberOfMessages = +singleUser.size();
        }

        return numberOfMessages;
    }


    public interface OnGetAccountDetailsListener {
        void onSuccess(AccountDetails accountDetails);
    }

    public void getAccountDetails(final String UUID, final OnGetAccountDetailsListener listener) {

        mDatabase.getReference("userDetails").child(UUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listener.onSuccess(getAccountDetailsFromSnapshot(snapshot));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    private List receivedFriendRequests = new ArrayList<HashMap<String, String>>();

    public interface OnGetReceivedFriendRequestsListener {
        void onSuccess(List friendRequests);
    }

    public void getReceivedFriendRequests(final OnGetReceivedFriendRequestsListener listener) {
        String UUID = getCurrentUsersUUID();

        mDatabase.getReference("userDetails").child(UUID + "/friendRequests").orderByValue().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                HashMap UUIDMap = new HashMap<String, String>();


                for (DataSnapshot ds : snapshot.getChildren()) {
                    //build friend request
                    UUIDMap.put(ds.getKey(), ds.getValue());

                    receivedFriendRequests.add(UUIDMap);
                }

                listener.onSuccess(receivedFriendRequests);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    private List sentFriendRequests = new ArrayList<HashMap<String, FriendRequestHelper>>();

    public interface OnGetSentFriendRequestsListener {
        void onSuccess(List friendRequests);
    }

    public void getSentFriendRequests(final OnGetSentFriendRequestsListener listener) {
        String UUID = getCurrentUsersUUID();

        mDatabase.getReference("userDetails").child(UUID + "/sentFriendRequests").orderByChild("/username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                HashMap UUIDMap = new HashMap<String, FriendRequestHelper>();
                FriendRequestHelper friendRequest;


                for (DataSnapshot ds : snapshot.getChildren()) {
                    //build friend request
                    friendRequest = ds.getValue(FriendRequestHelper.class);
                    UUIDMap.put(ds.getKey(), friendRequest);

                    sentFriendRequests.add(UUIDMap);
                }


                listener.onSuccess(sentFriendRequests);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    public interface OnGetMessagesFromListener {
        void onSuccess(ArrayList<MessageData> messageDataList);
    }

    public void getMessagesFromUUID(final String friendsUUID, final OnGetMessagesFromListener listener) {

        mDatabase.getReference("messages").child(getCurrentUsersUUID() + "/" + friendsUUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<MessageData> messageDataList = new ArrayList<MessageData>();

                MessageData messageData;
                String fullName = null;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    messageData = new MessageData();
                    int dsSize = (int) ds.getChildrenCount();

                    if (ds.getKey().equals("fullName")) {
                        fullName = (String) ds.getValue();
                    }


                    for (DataSnapshot subDS : ds.getChildren()) {

                        //get the data of the message
                        switch (subDS.getKey()) {
                            case "fileExtension":
                                messageData.setFileExtension((String) subDS.getValue());
                                break;
                            case "mediaMessageRotation":
                                long rotation = (long) subDS.getValue();
                                messageData.setMediaMessageRotation((int) rotation);
                                break;
                            case "mediaMessageUrl":
                                messageData.setMediaMessageUrl((String) subDS.getValue());
                                break;
                            case "textMessage":
                                messageData.setTextMessage((String) subDS.getValue());
                                break;
                            case "deviceOrientationMode":
                                long deviceOrientationMode = (long) subDS.getValue();
                                messageData.setDeviceOrientationMode((int) deviceOrientationMode);
                                break;
                            case "unopened":
                                messageData.setUnopened((boolean) subDS.getValue());
                                break;
                        }
                    }
                    //if the count is more than one then it must be a message
                    if (dsSize > 0) {
                        messageData.setFullName(fullName);
                        messageData.setTimeStamp(ds.getKey());
                        messageDataList.add(messageData);
                    }
                }

                for (MessageData data : messageDataList) {
                    data.setFullName(fullName);
                }
                listener.onSuccess(messageDataList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    public interface OnGetStoryForUUIDListener {
        void onSuccess(ArrayList<MessageData> storyMessagesDataList);
    }

    public void getStoryForUUID(final OnGetStoryForUUIDListener listener) {

        mDatabase.getReference("stories").child(getCurrentUsersUUID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<MessageData> messageDataList = new ArrayList<MessageData>();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    MessageData messageData = new MessageData();

                    for (DataSnapshot subDS : ds.getChildren()) {
                        //get the data of the message
                        switch (subDS.getKey()) {
                            case "fileExtension":
                                messageData.setFileExtension((String) subDS.getValue());
                                break;
                            case "mediaMessageRotation":
                                long rotation = (long) subDS.getValue();
                                messageData.setMediaMessageRotation((int) rotation);
                                break;
                            case "mediaMessageUrl":
                                messageData.setMediaMessageUrl((String) subDS.getValue());
                                break;
                            case "textMessage":
                                messageData.setTextMessage((String) subDS.getValue());
                                break;
                            case "unopened":
                                messageData.setUnopened((boolean) subDS.getValue());
                                break;
                            case "fullName":
                                messageData.setFullName((String) subDS.getValue());
                                break;
                        }
                    }

                    messageData.setTimeStamp(ds.getKey());
                    messageDataList.add(messageData);
                }

                listener.onSuccess(messageDataList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    public interface OnGetUsernameListener {
        void onSuccess(String username);
    }

    public void getUUIDsUsername(final String UUID, final OnGetUsernameListener listener) {

        mDatabase.getReference("userDetails").child(UUID + "/username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!snapshot.getValue().toString().equals(null)) {
                    listener.onSuccess(((String) snapshot.getValue()).trim());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onSuccess(null);
            }
        });
    }


    public interface OnGetUUIDsFullNameAndProfileImageListener {
        void onSuccess(String fullName, String profileImageURL, String profileImageRotation);
    }

    public void getUUIDsFullNameAndProfileImage(final String UUID, final OnGetUUIDsFullNameAndProfileImageListener listener) {

        mDatabase.getReference("userDetails").child(UUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String firstName = null;
                String surname = null;
                String profileImageUrl = null;
                String profileImageRotation = null;

                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds.getKey().equals("firstName")) {
                        firstName = ds.getValue().toString();
                    } else if (ds.getKey().equals("surname")) {
                        surname = ds.getValue().toString();
                    } else if (ds.getKey().equals("profileImageUrl")) {
                        profileImageUrl = ds.getValue().toString();
                    } else if (ds.getKey().equals("profileImageRotation")) {
                        profileImageRotation = ds.getValue().toString();
                    }
                }

                listener.onSuccess(firstName + " " + surname, profileImageUrl, profileImageRotation);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onSuccess(null, null, null);
            }
        });
    }


    public interface OnGetUUIDListener {
        void onSuccess(String UUID);
    }

    public void getUsernamesUUID(final String username, final OnGetUUIDListener listener) {

        mDatabase.getReference("usernames").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    listener.onSuccess((snapshot.getValue().toString().trim()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public interface OnGetExistingReceivedMediaDetailsListener {
        void onSuccess(List<HashMap<String, String>> receivedMediaDetails, int numberOfStories);
    }

    List<HashMap<String, String>> existingReceivedMediaDetails = new ArrayList<HashMap<String, String>>();
    int numberOfUnopenedStories = 0;

    public void getExistingReceivedMediaDetails(final Context context, final OnGetExistingReceivedMediaDetailsListener listener) {

        String UUID = getCurrentUsersUUID();
        //get story details
        DatabaseReference databaseRef = mDatabase.getReference();
        final Query getNumberOfStories = databaseRef.child("stories/" + UUID);
        final Query getUnopenedFriendsMessages = databaseRef.child("messages/" + UUID);
        getNumberOfStories.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String timestamp = ds.getKey();

                    //check if the story message is old, if it is delete it. If not add it to the count
                    if (isStoryMessageOld(timestamp)) {

                        ManagingMessages managingMessages = new ManagingMessages(context);
                        managingMessages.deleteStoryMessage(timestamp);
                    } else {
                        for (DataSnapshot subDS : ds.getChildren()) {
                            if (subDS.getKey().equals("unopened") && subDS.getValue().equals(true)) {
                                numberOfUnopenedStories++;
                            }
                        }
                    }
                }


                //get info about friends messages
                getUnopenedFriendsMessages.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {

                            HashMap friendsMessagesHashMap = new HashMap();
                            //add the UUID of the sender
                            friendsMessagesHashMap.put("UUID", ds.getKey());

                            //get the other data
                            for (DataSnapshot subDS : ds.getChildren()) {


                                if (subDS.getKey().equals("unopened")) {
                                    boolean unopenedMessage = (boolean) subDS.getValue();
                                    String strUnopenedMessage = unopenedMessage ? "1" : "0";
                                    friendsMessagesHashMap.put("unopenedMessage", strUnopenedMessage);
                                } else if (subDS.getKey().equals("fullName")) {
                                    friendsMessagesHashMap.put("fullName", subDS.getValue());
                                } else if (subDS.getKey().equals("profileImageUrl")) {
                                    friendsMessagesHashMap.put("profileImageUrl", subDS.getValue());
                                } else if (subDS.getKey().equals("profileImageRotation")) {
                                    friendsMessagesHashMap.put("profileImageRotation", subDS.getValue().toString());
                                } else if (subDS.getKey().equals("lastSentMessageTime")) {
                                    friendsMessagesHashMap.put("lastMessageTimeStamp", subDS.getValue().toString());
                                }

                            }
                            //after all the data is received, add the map to the list
                            existingReceivedMediaDetails.add(friendsMessagesHashMap);

                        }

                        listener.onSuccess(existingReceivedMediaDetails, numberOfUnopenedStories);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    public interface OnGetFriendsGamingDataListener {
        void onSuccess(AccountsHighScores friendsGamingData);
    }


    public void getFriendsGamingData(final String friendsUUID, HashMap<String, String> friendsMap, final OnGetFriendsGamingDataListener listener) {
        DatabaseReference databaseRef = mDatabase.getReference("userDetails");

        Query query = databaseRef.child(friendsUUID + "/highScore");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                AccountsHighScores accountsHighScore = new AccountsHighScores();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    //get friends high scores
                    accountsHighScore.setUsersUUID(friendsUUID);
                    switch (ds.getKey()) {
                        case "buttonChange":
                            accountsHighScore.setButtonChangeHighScore((Long) ds.getValue());
                            break;
                        case "gridReaction":
                            accountsHighScore.setGridReactionHighScore((Long) ds.getValue());
                            break;
                        case "pairs":
                            accountsHighScore.setPairsHighScore((Long) ds.getValue());
                            break;
                        case "pattern":
                            accountsHighScore.setPatternHighScore((Long) ds.getValue());
                            break;
                        case "stroopTest":
                            accountsHighScore.setStoopTestHighScore((Long) ds.getValue());
                            break;
                    }
                }

                String fullName = friendsMap.get("firstName") + friendsMap.get("surname");
                String profileImageRotation = friendsMap.get("profileImageRotation");
                String profileImageUrl = friendsMap.get("profileImageUrl");

                accountsHighScore.setFullName(fullName);
                accountsHighScore.setProfileImageRotation(profileImageRotation);
                accountsHighScore.setProfileImageURL(profileImageUrl);

                listener.onSuccess(accountsHighScore);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public interface OnGetLeaderBoardDataListener {
        void onSuccess(List<AccountsHighScores> accountsHighScores);

    }

    public void getLeaderBoardData(final OnGetLeaderBoardDataListener listener) {

        mAuth = FirebaseAuth.getInstance();
        String usersUUID = mAuth.getCurrentUser().getUid();

        DatabaseReference databaseRef = mDatabase.getReference("userDetails");
        Query getFriends = databaseRef.child(usersUUID);

        getFriends.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int numberOfFriends = 0;
                HashMap<String, HashMap<String, String>> friendsDetails = new HashMap<>();

                AccountsHighScores currentAccountHighScore = new AccountsHighScores();

                //get UUIDs for each friend
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds.getKey().equals("friends")) {
                        for (DataSnapshot subDS : ds.getChildren()) {
                            numberOfFriends++;
                            HashMap<String, String> friendsDetail = new HashMap<String, String>();

                            boolean profileImageFound = false;
                            //get friends data
                            for(DataSnapshot friendsData : subDS.getChildren()){
                                switch (friendsData.getKey()){
                                    case "firstName":
                                        friendsDetail.put("firstName", (String) friendsData.getValue());
                                        break;
                                    case "surname":
                                        friendsDetail.put("surname", (String) friendsData.getValue());
                                        break;
                                    case "profileImageUrl":
                                        profileImageFound = true;
                                        friendsDetail.put("profileImageUrl", (String) friendsData.getValue());
                                        break;
                                    case "profileImageRotation":
                                        friendsDetail.put("profileImageRotation", Long.toString((Long) friendsData.getValue()));
                                        break;
                                }
                            }

                            if(!profileImageFound){
                                friendsDetail.put("profileImageUrl", "");
                            }

                            //store friends data with a key of the users UUId
                            friendsDetails.put(subDS.getKey(), friendsDetail);
                        }
                    } else if (ds.getKey().equals("highScores")) {
                        for (DataSnapshot subDS : ds.getChildren()) {
                            //get the current users high scores
                            currentAccountHighScore.setUsersUUID(usersUUID);
                            switch (subDS.getKey()) {
                                case "buttonChange":
                                    currentAccountHighScore.setButtonChangeHighScore((Long) subDS.getValue());
                                    break;
                                case "gridReaction":
                                    currentAccountHighScore.setGridReactionHighScore((Long) subDS.getValue());
                                    break;
                                case "pairs":
                                    currentAccountHighScore.setPairsHighScore((Long) subDS.getValue());
                                    break;
                                case "pattern":
                                    currentAccountHighScore.setPatternHighScore((Long) subDS.getValue());
                                    break;
                                case "stroopTest":
                                    currentAccountHighScore.setStoopTestHighScore((Long) subDS.getValue());
                                    break;
                            }
                        }

                        currentAccountHighScore.setFullName("My Scores");

                    }
                    else if (ds.getKey().equals("profileImageUrl")) {
                        currentAccountHighScore.setProfileImageURL((String) ds.getValue());
                    }
                }


                List<AccountsHighScores> accountsHighScores = new ArrayList<>();
                accountsHighScores.add(currentAccountHighScore);


                int finalNumberOfFriends = numberOfFriends;
                //get each friends high score
                for (HashMap<String, String> friendsMap : friendsDetails.values()) {

                    Set friendsUUIDSet = friendsMap.keySet();
                    String friendsUUID = (String) friendsUUIDSet.iterator().next();

                    //get friends
                    getFriendsGamingData(friendsUUID, friendsMap, new OnGetFriendsGamingDataListener() {
                        @Override
                        public void onSuccess(AccountsHighScores friendsGamingData) {
                            accountsHighScores.add(friendsGamingData);

                            //if all data has been found and stored (friends + current users)
                            if(accountsHighScores.size() == (finalNumberOfFriends + 1)){
                                listener.onSuccess(accountsHighScores);
                            }
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
