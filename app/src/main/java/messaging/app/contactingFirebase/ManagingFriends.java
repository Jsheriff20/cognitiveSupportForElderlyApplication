package messaging.app.contactingFirebase;

import android.content.Context;
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
    Context mContext;
    FirebaseDatabase mDatabase;
    FirebaseStorage mStorage;
    FirebaseAuth mAuth;

    QueryingDatabase mQueryingDatabase;

    public ManagingFriends(Context context, String friendsUUID) {
        this.mContext = context;
        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mQueryingDatabase = new QueryingDatabase(friendsUUID);
    }


    public void cancelSentFriendRequest(String friendsUUID) {
        DatabaseReference databaseRef = mDatabase.getReference("userDetails");
        Query getSentFriendRequest = databaseRef.child(mQueryingDatabase.getCurrentUsersUUID() +
                "/sentFriendRequests/" + friendsUUID);
        final Query getReceivedFriendRequest = databaseRef.child(friendsUUID + "/friendRequests/" +
                mQueryingDatabase.getCurrentUsersUUID());

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
        Query getSentFriendRequest = databaseRef.child(friendsUUID + "/sentFriendRequests/" +
                mQueryingDatabase.getCurrentUsersUUID());
        final Query getReceivedFriendRequest = databaseRef.child(
                mQueryingDatabase.getCurrentUsersUUID() + "/friendRequests/" + friendsUUID);

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
        databaseRef.child(mQueryingDatabase.getCurrentUsersUUID() + "/blocked/" + friendsUUID)
                .setValue(friendsUsername);
        mQueryingDatabase.getCurrentUsersUsername(
                new QueryingDatabase.OnGetCurrentUsersUsernameListener() {
                    @Override
                    public void onSuccess(String username) {

                        databaseRef.child(friendsUUID + "/blockedBy/" +
                                mQueryingDatabase.getCurrentUsersUUID()).setValue(username);

                        Toast.makeText(mContext, "User has been blocked", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void removeFriend(String friendsUUID) {

        DatabaseReference databaseRef = mDatabase.getReference("userDetails");
        Query removeFriendFromCurrentUsersFriends = databaseRef.child(
                mQueryingDatabase.getCurrentUsersUUID() + "/friends/" + friendsUUID);
        final Query removeCurrentUserFromFriendsFriends = databaseRef.child(friendsUUID +
                "/friends/" + mQueryingDatabase.getCurrentUsersUUID());

        removeFriendFromCurrentUsersFriends
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ds.getRef().removeValue();
                        }
                        removeCurrentUserFromFriendsFriends
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        snapshot.getRef().removeValue();
                                        Toast.makeText(mContext, "Friend Removed", Toast.LENGTH_SHORT).show();
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

    public void updateFriendRelationship(String friendsUUID, String updatedRelationship,
                                         final OnUpdateFriendRelationshipListener listener) {

        try {
            DatabaseReference databaseRef = mDatabase.getReference("userDetails");
            databaseRef.child(mQueryingDatabase.getCurrentUsersUUID() + "/friends/" + friendsUUID +
                    "/relationship").setValue(updatedRelationship);
            listener.onSuccess(true);
        } catch (Exception exception) {
            listener.onSuccess(false);
        }
    }


    public void acceptFriendRequest(final String friendsUUID, final String friendsRelationship) {

        //cancel the friend requests
        cancelReceivedFriendRequest(friendsUUID);

        //get details of accounts
        mQueryingDatabase.getAccountDetails(mQueryingDatabase.getCurrentUsersUUID(),
                new QueryingDatabase.OnGetAccountDetailsListener() {
                    @Override
                    public void onSuccess(final AccountDetails currentUserAccountDetails) {

                        mQueryingDatabase.getAccountDetails(friendsUUID,
                                new QueryingDatabase.OnGetAccountDetailsListener() {
                                    @Override
                                    public void onSuccess(AccountDetails accountDetails) {
                                        AccountDetails friendsDetails = accountDetails;
                                        friendsDetails.setRelationship(friendsRelationship);

                                        //add each other as a friend on each account
                                        DatabaseReference databaseRef = mDatabase.getReference("userDetails");
                                        databaseRef.child(friendsUUID + "/friends/" +
                                                mQueryingDatabase.getCurrentUsersUUID())
                                                .setValue(currentUserAccountDetails);
                                        databaseRef.child(mQueryingDatabase.getCurrentUsersUUID() + "/friends/" +
                                                friendsUUID).setValue(friendsDetails);
                                    }
                                });
                    }
                });

    }


    public interface OnCheckReceivedFriendRequestFromListener {
        void onSuccess(boolean result);
    }

    public void checkReceivedFriendRequestFrom(final String friendRequestFromUUID,
                                               final OnCheckReceivedFriendRequestFromListener listener) {

        mDatabase.getReference("userDetails").child(mQueryingDatabase.getCurrentUsersUUID() +
                "/friendRequests").orderByValue().addListenerForSingleValueEvent(
                new ValueEventListener() {
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

    public void sendFriendRequest(final String friendsUsername, final String relationship,
                                  final OnCheckIfFriendRequestSentListener listener) {

        mQueryingDatabase.getUsernamesUUID(friendsUsername,
                new QueryingDatabase.OnGetUUIDListener() {
                    @Override
                    public void onSuccess(final String friendsUUID) {
                        final String usersUUID = mQueryingDatabase.getCurrentUsersUUID();

                        try {
                            //build friend request
                            Map<String, String> usernameMap = new HashMap<String, String>();
                            Map<String, String> relationshipMap = new HashMap<String, String>();

                            usernameMap.put("username", friendsUsername);
                            relationshipMap.put("relationship", relationship);

                            FriendRequestHelper friendRequest = new FriendRequestHelper(friendsUsername,
                                    relationship);

                            //add friend request to database
                            DatabaseReference databaseRef = mDatabase.getReference("userDetails");
                            databaseRef.child(usersUUID + "/sentFriendRequests/" + friendsUUID)
                                    .setValue(friendRequest);
                            mQueryingDatabase.getCurrentUsersUsername(
                                    new QueryingDatabase.OnGetCurrentUsersUsernameListener() {
                                        @Override
                                        public void onSuccess(String username) {
                                            databaseRef.child(friendsUUID + "/friendRequests/" + usersUUID)
                                                    .setValue(username);


                                            listener.onSuccess(true);
                                        }
                                    });

                        } catch (Exception exception) {

                            listener.onSuccess(false);
                        }

                    }
                });
    }


    public interface OnAddFriendListener {
        void onSuccess(boolean requestSentSuccessfully);
    }

    public void addFriend(final String friendsUsername, final String relationship,
                          OnAddFriendListener listener) {

        mAuth = FirebaseAuth.getInstance();

        //check user is not adding themselves
        mQueryingDatabase.getCurrentUsersUsername(
                new QueryingDatabase.OnGetCurrentUsersUsernameListener() {
                    @Override
                    public void onSuccess(String username) {
                        if (friendsUsername.equals(username)) {
                            Toast.makeText(mContext, "You can not add yourself",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        } else {

                            //check username exists
                            mQueryingDatabase.doesUsernameExist(friendsUsername,
                                    new QueryingDatabase.OnCheckIfUsernameExistsListener() {
                                        @Override
                                        public void onSuccess(boolean exists) {
                                            if (exists) {

                                                //check to see if user is already friends with the user
                                                mQueryingDatabase.isUserAlreadyFriendsWith(friendsUsername,
                                                        new QueryingDatabase.OnCheckIfUserIsAlreadyFriendsWithListener() {
                                                            @Override
                                                            public void onSuccess(boolean alreadyFriends) {

                                                                if (alreadyFriends) {
                                                                    Toast.makeText(mContext, "You and " +
                                                                                    friendsUsername + " are already friends",
                                                                            Toast.LENGTH_SHORT).show();
                                                                    return;
                                                                }
                                                                //check adding user is not blocked by the recipient user
                                                                mQueryingDatabase.isUserBlockedBy(friendsUsername,
                                                                        new QueryingDatabase.OnCheckIfUserIsBlockedByListener() {
                                                                            @Override
                                                                            public void onSuccess(boolean blocked) {

                                                                                if (blocked) {
                                                                                    Toast.makeText(mContext,
                                                                                            "You have been blocked by this user",
                                                                                            Toast.LENGTH_SHORT).show();
                                                                                    return;
                                                                                }

                                                                                //check if friend has already sent a friend request to the user. If so auto accept
                                                                                mQueryingDatabase.getUsernamesUUID(friendsUsername,
                                                                                        new QueryingDatabase.OnGetUUIDListener() {
                                                                                            @Override
                                                                                            public void onSuccess(final String friendsUUID) {
                                                                                                checkReceivedFriendRequestFrom(friendsUUID,
                                                                                                        new OnCheckReceivedFriendRequestFromListener() {
                                                                                                            @Override
                                                                                                            public void onSuccess(boolean haveExistingFriendRequest) {

                                                                                                                if (haveExistingFriendRequest) {
                                                                                                                    Toast.makeText(mContext,
                                                                                                                            "Friend request to " +
                                                                                                                                    friendsUsername +
                                                                                                                                    " was automatically accepted",
                                                                                                                            Toast.LENGTH_SHORT).show();
                                                                                                                    acceptFriendRequest(friendsUUID,
                                                                                                                            friendsUsername);
                                                                                                                    return;
                                                                                                                }
                                                                                                                //if all is good add the user as a friend
                                                                                                                sendFriendRequest(friendsUsername,
                                                                                                                        relationship,
                                                                                                                        new OnCheckIfFriendRequestSentListener() {
                                                                                                                            @Override
                                                                                                                            public void onSuccess(boolean requestSentSuccessfully) {
                                                                                                                                if (requestSentSuccessfully) {
                                                                                                                                    Toast.makeText(mContext, "Friend request to " +
                                                                                                                                                    friendsUsername + " was sent successfully",
                                                                                                                                            Toast.LENGTH_SHORT).show();
                                                                                                                                    listener.onSuccess(true);
                                                                                                                                } else {
                                                                                                                                    Toast.makeText(mContext, "Friend request to " +
                                                                                                                                                    friendsUsername + " failed",
                                                                                                                                            Toast.LENGTH_SHORT).show();
                                                                                                                                    listener.onSuccess(false);
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
                                                Toast.makeText(mContext, "Username cannot be found",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                });

    }


    public interface OnAdminFriendAddedListener {
        void onSuccess(boolean adminFriendAdded);
    }

    public void addFriendAsAdmin(final String friendsUUID, OnAdminFriendAddedListener listener) {
        String UUID = mQueryingDatabase.getCurrentUsersUUID();

        DatabaseReference databaseRef = mDatabase.getReference("userDetails");
        databaseRef.child(UUID + "/friends/" + friendsUUID + "/admin").setValue(true);
        mQueryingDatabase.getCurrentUsersFullName(
                new QueryingDatabase.OnGetCurrentUsersFullNameListener() {
                    @Override
                    public void onSuccess(String fullName) {
                        databaseRef.child(friendsUUID + "/administering/" + UUID).setValue(fullName);

                        listener.onSuccess(true);

                    }
                });

    }


    public void removeFriendAsAdmin(final String friendsUUID) {
        String UUID = mQueryingDatabase.getCurrentUsersUUID();

        DatabaseReference databaseRef = mDatabase.getReference("userDetails");
        databaseRef.child(UUID + "/friends/" + friendsUUID + "/admin").removeValue();
    }
}
