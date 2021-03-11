package messaging.app.contactingFirebase;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
import java.util.Map;

import messaging.app.AccountDetails;
import messaging.app.messages.friendsList.FriendRequestHelper;

public class ManagingFriends {
    Context context;
    FirebaseDatabase mDatabase;
    FirebaseStorage mStorage;
    FirebaseAuth mAuth;

    QueryingDatabase queryingDatabase = new QueryingDatabase();

    public ManagingFriends(Context context) {
        this.context = context;
        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
    }



    public void cancelSentFriendRequest(String friendsUUID) {
        DatabaseReference databaseRef = mDatabase.getReference("userDetails");
        Query getSentFriendRequest = databaseRef.child(queryingDatabase.getCurrentUsersUUID() + "/sentFriendRequests/" + friendsUUID);
        final Query getReceivedFriendRequest = databaseRef.child(friendsUUID + "/friendRequests/" + queryingDatabase.getCurrentUsersUUID());

        getSentFriendRequest.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ds.getRef().removeValue();
                }
                getReceivedFriendRequest.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        snapshot.getRef().removeValue();
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


    public void cancelReceivedFriendRequest(String friendsUUID) {
        DatabaseReference databaseRef = mDatabase.getReference("userDetails");
        Query getSentFriendRequest = databaseRef.child(friendsUUID + "/sentFriendRequests/" + queryingDatabase.getCurrentUsersUUID());
        final Query getReceivedFriendRequest = databaseRef.child(queryingDatabase.getCurrentUsersUUID() + "/friendRequests/" + friendsUUID);

        getSentFriendRequest.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ds.getRef().removeValue();
                }
                getReceivedFriendRequest.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        snapshot.getRef().removeValue();
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


    public void blockFriend(String friendsUUID, String friendsUsername) {

        //remove the friend
        removeFriend(friendsUUID);

        //block the friend
        DatabaseReference databaseRef = mDatabase.getReference("userDetails");
        databaseRef.child(queryingDatabase.getCurrentUsersUUID() + "/blocked/" + friendsUUID).setValue(friendsUsername);
        databaseRef.child(friendsUUID + "/blockedBy/" + queryingDatabase.getCurrentUsersUUID()).setValue(queryingDatabase.getCurrentUsersUsername());

        Toast.makeText(context, "User has been blocked", Toast.LENGTH_SHORT).show();
    }



    public void removeFriend(String friendsUUID) {

        DatabaseReference databaseRef = mDatabase.getReference("userDetails");
        Query removeFriendFromCurrentUsersFriends = databaseRef.child(queryingDatabase.getCurrentUsersUUID() + "/friends/" + friendsUUID);
        final Query removeCurrentUserFromFriendsFriends = databaseRef.child(friendsUUID + "/friends/" + queryingDatabase.getCurrentUsersUUID());

        removeFriendFromCurrentUsersFriends.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ds.getRef().removeValue();
                }
                removeCurrentUserFromFriendsFriends.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        snapshot.getRef().removeValue();
                        Toast.makeText(context, "Friend Removed", Toast.LENGTH_SHORT).show();
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


    public interface OnUpdateFriendRelationshipListener {
        void onSuccess(boolean success);
    }

    public void updateFriendRelationship(String friendsUUID, String updatedRelationship, final OnUpdateFriendRelationshipListener listener) {

        try {
            DatabaseReference databaseRef = mDatabase.getReference("userDetails");
            databaseRef.child(queryingDatabase.getCurrentUsersUUID() + "/friends/" + friendsUUID + "/relationship").setValue(updatedRelationship);
            listener.onSuccess(true);
        } catch (Exception exception) {
            listener.onSuccess(false);
        }
    }


    public void acceptFriendRequest(final String friendsUUID, final String friendsRelationship) {

        //cancel the friend requests
        cancelReceivedFriendRequest(friendsUUID);

        //get details of accounts
        queryingDatabase.getAccountDetails(queryingDatabase.getCurrentUsersUUID(), new QueryingDatabase.OnGetAccountDetailsListener() {
            @Override
            public void onSuccess(final AccountDetails currentUserAccountDetails) {

                queryingDatabase.getAccountDetails(friendsUUID, new QueryingDatabase.OnGetAccountDetailsListener() {
                    @Override
                    public void onSuccess(AccountDetails accountDetails) {
                        AccountDetails friendsDetails = accountDetails;
                        friendsDetails.setRelationship(friendsRelationship);

                        //add each other as a friend on each account
                        DatabaseReference databaseRef = mDatabase.getReference("userDetails");
                        databaseRef.child(friendsUUID + "/friends/" + queryingDatabase.getCurrentUsersUUID()).setValue(currentUserAccountDetails);
                        databaseRef.child(queryingDatabase.getCurrentUsersUUID() + "/friends/" + friendsUUID).setValue(friendsDetails);
                    }
                });
            }
        });

    }


    public interface OnCheckReceivedFriendRequestFromListener {
        void onSuccess(boolean result);
    }

    public void checkReceivedFriendRequestFrom(final String friendRequestFromUUID, final OnCheckReceivedFriendRequestFromListener listener) {

        mDatabase.getReference("userDetails").child(queryingDatabase.getCurrentUsersUUID() + "/friendRequests").orderByValue().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds.getKey().equals(friendRequestFromUUID)) {
                        listener.onSuccess(true);
                    } else {
                        listener.onSuccess(false);
                    }
                }
                listener.onSuccess(false);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    public interface OnCheckIfFriendRequestSentListener {
        void onSuccess(boolean requestSentSuccessfully);
    }

    public void sendFriendRequest(final String friendsUsername, final String relationship, final OnCheckIfFriendRequestSentListener listener) {

        queryingDatabase.getUsernamesUUID(friendsUsername, new QueryingDatabase.OnGetUUIDListener() {
            @Override
            public void onSuccess(final String friendsUUID) {
                final String usersUUID = mAuth.getCurrentUser().getUid();

                try {
                    //build friend request
                    Map<String, String> usernameMap = new HashMap<String, String>();
                    Map<String, String> relationshipMap = new HashMap<String, String>();

                    usernameMap.put("username", friendsUsername);
                    relationshipMap.put("relationship", relationship);

                    FriendRequestHelper friendRequest = new FriendRequestHelper(friendsUsername, relationship);

                    //add friend request to database
                    DatabaseReference databaseRef = mDatabase.getReference("userDetails");
                    databaseRef.child(usersUUID + "/sentFriendRequests/" + friendsUUID).setValue(friendRequest);
                    databaseRef.child(friendsUUID + "/friendRequests/" + usersUUID).setValue(queryingDatabase.getCurrentUsersUsername());


                    listener.onSuccess(true);
                } catch (Exception exception) {

                    listener.onSuccess(false);
                }

            }
        });
    }


    public void addFriend(final String friendsUsername, final String relationship) {

        mAuth = FirebaseAuth.getInstance();

        //check user is not adding themselves
        if (friendsUsername.equals(queryingDatabase.getCurrentUsersUsername())) {
            Toast.makeText(context, "You can not add yourself", Toast.LENGTH_SHORT).show();
            return;
        } else {

            //check username exists
            queryingDatabase.doesUsernameExist(friendsUsername, new QueryingDatabase.OnCheckIfUsernameExistsListener(){
                @Override
                public void onSuccess(boolean exists) {
                    if (exists) {

                        //check to see if user is already friends with the user
                        queryingDatabase.isUserAlreadyFriendsWith(friendsUsername, new QueryingDatabase.OnCheckIfUserIsAlreadyFriendsWithListener() {
                            @Override
                            public void onSuccess(boolean alreadyFriends) {

                                if (alreadyFriends) {
                                    Toast.makeText(context, "You and " + friendsUsername + " are already friends", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                //check adding user is not blocked by the recipient user
                                queryingDatabase.isUserBlockedBy(friendsUsername, new QueryingDatabase.OnCheckIfUserIsBlockedByListener() {
                                    @Override
                                    public void onSuccess(boolean blocked) {

                                        if (blocked) {
                                            Toast.makeText(context, "You have been blocked by this user", Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        //check if friend has already sent a friend request to the user. If so auto accept
                                        queryingDatabase.getUsernamesUUID(friendsUsername, new QueryingDatabase.OnGetUUIDListener() {
                                            @Override
                                            public void onSuccess(final String friendsUUID) {
                                                checkReceivedFriendRequestFrom(friendsUUID, new OnCheckReceivedFriendRequestFromListener() {
                                                    @Override
                                                    public void onSuccess(boolean haveExistingFriendRequest) {

                                                        if (haveExistingFriendRequest) {
                                                            Toast.makeText(context, "Friend request to " + friendsUsername + " was automatically accepted", Toast.LENGTH_SHORT).show();
                                                            acceptFriendRequest(friendsUUID, friendsUsername);
                                                            return;
                                                        }
                                                        //if all is good add the user as a friend
                                                        sendFriendRequest(friendsUsername, relationship, new OnCheckIfFriendRequestSentListener() {
                                                            @Override
                                                            public void onSuccess(boolean requestSentSuccessfully) {
                                                                if (requestSentSuccessfully) {
                                                                    Toast.makeText(context, "Friend request to " + friendsUsername + " was sent successfully", Toast.LENGTH_SHORT).show();
                                                                } else {
                                                                    Toast.makeText(context, "Friend request to " + friendsUsername + " failed", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    } else {
                        Toast.makeText(context, "Username cannot be found", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

}
