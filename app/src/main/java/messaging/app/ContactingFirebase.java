package messaging.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import messaging.app.login.LoginActivity;
import messaging.app.messages.friendsList.FriendRequestHelper;

public class ContactingFirebase {


    String TAG = "Test";

    Context context;
    FirebaseDatabase mDatabase;
    DatabaseReference mDatabaseRef;
    FirebaseAuth mAuth;
    FirebaseStorage mStorage;
    StorageReference mStorageRef;
    FirebaseUser mCurrentUser;
    Formatting formatting = new Formatting();


    public ContactingFirebase(Context context) {
        this.context = context;
        mDatabase = FirebaseDatabase.getInstance();
    }


    public void createUserWithEmailAndPassword(String email, String password, final String firstName, final String surname, final Uri profileImage, final int profileImageRotation, final String username) {

        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {
                            Toast.makeText(context, "Fail to register", Toast.LENGTH_SHORT).show();
                        } else {
                            //get UUID of the user account created
                            final String UUID = (String) task.getResult().getUser().getUid();


                            final UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setDisplayName(username).build();
                            mCurrentUser = mAuth.getCurrentUser();
                            mCurrentUser.updateProfile(request).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //add usersData to the database
                                    addNewUsersData(firstName, surname, profileImage, profileImageRotation, UUID, username, new OnAddNewUserDataListener() {
                                        @Override
                                        public void onSuccess(boolean success) {
                                            if (success) {
                                                //load activity
                                                Intent intent = new Intent(context, SelectAreaOfApplicationActivity.class);
                                                context.startActivity(intent);
                                            } else {
                                                mCurrentUser.delete();
                                                Toast.makeText(context, "Registration failed!", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                        }
                                    });

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    mCurrentUser.delete();
                                    Toast.makeText(context, "Registration failed!", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            });

                        }
                    }
                });
    }


    public void cancelSentFriendRequest(String friendsUUID) {
        mDatabaseRef = mDatabase.getReference("userDetails");
        Query getSentFriendRequest = mDatabaseRef.child(getCurrentUsersUUID() + "/sentFriendRequests/" + friendsUUID);
        final Query getReceivedFriendRequest = mDatabaseRef.child(friendsUUID + "/friendRequests/" + getCurrentUsersUUID());

        getSentFriendRequest.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds :snapshot.getChildren()){
                    ds.getRef().removeValue();
                }
                getReceivedFriendRequest.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        snapshot.getRef().removeValue();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "onCancelled: " + error);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: " + error);
            }
        });
    }


    public void cancelReceivedFriendRequest(String friendsUUID) {
        mDatabaseRef = mDatabase.getReference("userDetails");
        Log.d(TAG, "sentF: " + friendsUUID + "/sentFriendRequests/" + getCurrentUsersUUID());
        Log.d(TAG, "fre: " + getCurrentUsersUUID() + "/friendRequests/" + friendsUUID);
        Query getSentFriendRequest = mDatabaseRef.child(friendsUUID + "/sentFriendRequests/" + getCurrentUsersUUID());
        final Query getReceivedFriendRequest = mDatabaseRef.child(getCurrentUsersUUID() + "/friendRequests/" + friendsUUID);

        getSentFriendRequest.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds :snapshot.getChildren()){
                    ds.getRef().removeValue();
                }
                getReceivedFriendRequest.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        snapshot.getRef().removeValue();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "onCancelled: " + error);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: " + error);
            }
        });
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

    public interface OnAddNewUserDataListener {
        void onSuccess(boolean success);
    }

    private void addNewUsersData(final String firstName, final String surname, Uri profileImageUri, final int profileImageRotation, final String UUID, final String username, final OnAddNewUserDataListener listener) {

        String path = "images/" + UUID + "_profileImage.jpg";

        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference().child(path);

        //upload profile image to storage
        mStorageRef.putFile(profileImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                try {
                    //get profile image's download url
                    String profileImageUrl = mStorageRef.getDownloadUrl().toString();

                    //create new user in database using UUID already created
                    UserHelperClass userHelperClass = new UserHelperClass(firstName, surname, profileImageUrl, profileImageRotation, username);

                    mDatabaseRef = mDatabase.getReference("userDetails");
                    mDatabaseRef.child(UUID).setValue(userHelperClass);

                    mDatabaseRef = mDatabase.getReference("usernames");
                    mDatabaseRef.child(username).setValue(UUID);

                    listener.onSuccess(true);
                } catch (Exception e) {
                    listener.onSuccess(false);

                }
            }
        });


    }


    public interface OnCheckIfUsernameExistsListener {
        void onSuccess(boolean exists);
    }

    public void doesUsernameExist(final String username, final OnCheckIfUsernameExistsListener listener) {

        mDatabase.getReference("usernames").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (username.toLowerCase().equals(ds.getKey().toLowerCase())) {
                        listener.onSuccess(true);
                        return;
                    }
                }
                listener.onSuccess(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    public void loginUser(String email, String password) {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {
                            Toast.makeText(context, "Password or email is incorrect", Toast.LENGTH_SHORT).show();

                        } else {
                            context.startActivity(new Intent(context, SelectAreaOfApplicationActivity.class));
                        }
                    }
                });
    }


    public void resetPassword(String email) {
        mAuth = FirebaseAuth.getInstance();
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                            context.startActivity(new Intent(context, LoginActivity.class));

                        } else {
                            Toast.makeText(context, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    public void addFriend(final String friendsUsername, final String relationship) {
        mAuth = FirebaseAuth.getInstance();

        //check user is not adding themselves
        if (friendsUsername.equals(getCurrentUsersUsername())) {
            Toast.makeText(context, "You can not add yourself", Toast.LENGTH_SHORT).show();
            return;
        } else {

            //check username exists
            doesUsernameExist(friendsUsername, new OnCheckIfUsernameExistsListener() {
                @Override
                public void onSuccess(boolean exists) {

                    if (exists) {
                        //check adding user is not blocked by the recipient user
                        isUserBlockedBy(friendsUsername, new OnCheckIfUserIsBlockedByListener() {
                            @Override
                            public void onSuccess(boolean blocked) {

                                if (blocked) {
                                    Toast.makeText(context, "You have been blocked by this user", Toast.LENGTH_SHORT).show();
                                } else {
                                    //check if friend has already sent a friend request to the user. If so auto accept
                                    getUsernamesUUID(friendsUsername, new OnGetUUIDListener() {
                                        @Override
                                        public void onSuccess(final String friendsUUID) {
                                            checkReceivedFriendRequestFrom(friendsUUID, new OnCheckReceivedFriendRequestFromListener() {
                                                @Override
                                                public void onSuccess(boolean haveExistingFriendRequest) {

                                                    if(haveExistingFriendRequest){
                                                        Toast.makeText(context, "Friend request to " + friendsUsername + " was automatically accepted", Toast.LENGTH_SHORT).show();
                                                        acceptFriendRequest(friendsUUID, friendsUsername);
                                                    }
                                                    else{
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
                                                }
                                            });
                                        }
                                    });
                                }
                            }
                        });
                    } else {
                        Toast.makeText(context, "Username cannot be found", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    public interface OnGetUsernameListener {
        void onSuccess(String username);
    }
    public void getUUIDsUsername(final String UUID, final OnGetUsernameListener listener) {

        mDatabase.getReference("userDetails").child(UUID + "/username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!snapshot.getValue().toString().equals(null)) {
                    listener.onSuccess(formatting.removeEndingSpaceFromString((String) snapshot.getValue()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onSuccess(null);
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

                listener.onSuccess(formatting.removeEndingSpaceFromString((String) snapshot.getValue()));
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


    public interface OnCheckIfFriendRequestSentListener {
        void onSuccess(boolean requestSentSuccessfully);
    }
    public void sendFriendRequest(final String friendsUsername, final String relationship, final OnCheckIfFriendRequestSentListener listener) {

        getUsernamesUUID(friendsUsername, new OnGetUUIDListener() {
            @Override
            public void onSuccess(final String friendsUUID) {
                final String usersUUID = mAuth.getCurrentUser().getUid();


                //TODO:
                //check if friend request has been received by friend already. If so automatically accept the friend request.

                try {
                    //build friend request
                    Map<String, String> usernameMap = new HashMap<String, String>();
                    Map<String, String> relationshipMap = new HashMap<String, String>();

                    usernameMap.put("username", friendsUsername);
                    relationshipMap.put("relationship", relationship);

                    FriendRequestHelper friendRequest = new FriendRequestHelper(friendsUsername, relationship);

                    //add friend request to database
                    mDatabaseRef = mDatabase.getReference("userDetails");
                    mDatabaseRef.child(usersUUID + "/sentFriendRequests/" + friendsUUID).setValue(friendRequest);
                    mDatabaseRef.child(friendsUUID + "/friendRequests/" + usersUUID).setValue(getCurrentUsersUsername());


                    listener.onSuccess(true);
                } catch (Exception exception) {

                    listener.onSuccess(false);
                }

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


                for(DataSnapshot ds : snapshot.getChildren()){
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


                for(DataSnapshot ds : snapshot.getChildren()){
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

    public interface OnCheckReceivedFriendRequestFromListener {
        void onSuccess(boolean result);
    }
    public void checkReceivedFriendRequestFrom(final String friendRequestFromUUID, final OnCheckReceivedFriendRequestFromListener listener) {

        mDatabase.getReference("userDetails").child(getCurrentUsersUUID() + "/friendRequests").orderByValue().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot ds : snapshot.getChildren()){
                    if(ds.getKey().equals(friendRequestFromUUID)){
                        listener.onSuccess(true);
                    }
                    else{
                        listener.onSuccess(false);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void acceptFriendRequest(String friendsUUID, String friendsUsername){

        //cancel the friend requests
        cancelReceivedFriendRequest(friendsUUID);

        //add each other as a friend on each account
        mDatabaseRef = mDatabase.getReference("userDetails");
        mDatabaseRef.child(friendsUUID + "/friends/username" + getCurrentUsersUUID()).setValue(getCurrentUsersUsername());
        mDatabaseRef.child(getCurrentUsersUUID() + "/friends/username" + friendsUUID).setValue(friendsUsername);
    }


    public String getCurrentUsersUUID() {
        mAuth = FirebaseAuth.getInstance();
        return mAuth.getCurrentUser().getUid();
    }


    public String getCurrentUsersUsername() {
        mAuth = FirebaseAuth.getInstance();
        return mAuth.getCurrentUser().getDisplayName();
    }


    public void logoutUser() {
        mAuth.getInstance().signOut();
    }
}


