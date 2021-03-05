package messaging.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.ContactsContract;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import messaging.app.login.LoginActivity;
import messaging.app.messages.ViewingMessages.MessageData;
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


    public void createUserWithEmailAndPassword(String email, String password, final String firstName, final String surname, final Uri profileImage, final String username) {

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
                                    addNewUsersData(firstName, surname, profileImage, UUID, username, new OnAddNewUserDataListener() {
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
        Query getSentFriendRequest = mDatabaseRef.child(friendsUUID + "/sentFriendRequests/" + getCurrentUsersUUID());
        final Query getReceivedFriendRequest = mDatabaseRef.child(getCurrentUsersUUID() + "/friendRequests/" + friendsUUID);

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


    public void blockFriend(String friendsUUID, String friendsUsername) {

        //remove the friend
        removeFriend(friendsUUID);

        //block the friend
        mDatabaseRef = mDatabase.getReference("userDetails");
        mDatabaseRef.child(getCurrentUsersUUID() + "/blocked/" + friendsUUID).setValue(friendsUsername);
        mDatabaseRef.child(friendsUUID + "/blockedBy/" + getCurrentUsersUUID()).setValue(getCurrentUsersUsername());

        Toast.makeText(context, "User has been blocked", Toast.LENGTH_SHORT).show();
    }


    String failedToSendTo = "Failed to media message to: ";

    public void sendMessages(final ArrayList<String> directMessagesUUID, final ArrayList<String> storyMessagesUUID, final String pathToMedia, String fileType, final String message) {
        failedToSendTo = "Failed to media message to: ";
        final String fileExtension;
        if (fileType.equals("Image")) {
            fileExtension = ".jpg";
        } else {
            fileExtension = ".mp4";
        }

        final Uri sendingMediaUri = Uri.fromFile(new File(pathToMedia));


        getUUIDsFullNameAndProfileImage(getCurrentUsersUUID(), new OnGetUUIDsFullNameAndProfileImageListener() {
            @Override
            public void onSuccess(final String sendersFullName, String profileImageURL, String profileImageRotation) {
                for (final String UUID : directMessagesUUID) {
                    sendMessage(UUID, sendersFullName, profileImageURL, profileImageRotation, sendingMediaUri, fileExtension, message, "directlyToFriend", new OnSendMessageListener() {
                        @Override
                        public void onSuccess(boolean success) {
                        }
                    });
                }

                for (final String UUID : storyMessagesUUID) {
                    sendMessage(UUID, sendersFullName, profileImageURL, profileImageRotation, sendingMediaUri, fileExtension, message, "toStory", new OnSendMessageListener() {
                        @Override
                        public void onSuccess(boolean success) {
                        }
                    });
                }


                MediaManagement mediaManagement = new MediaManagement();
                mediaManagement.deleteMediaFile(pathToMedia, context);
            }
        });
    }



    public interface OnSendMessageListener {
        void onSuccess(boolean success);
    }

    private void sendMessage(final String friendsUUID, final String sendersFullName, final String profileImageURL, final String profileImageRotation, final Uri sendingMediaUri, final String fileExtension, final String message, final String sendingTo, final OnSendMessageListener listener) {
        Date now = new Date();
        long ut3 = now.getTime() / 1000L;
        final String timestamp = Long.toString(ut3);
        String fileName = getCurrentUsersUUID() + "_messageTo_" + friendsUUID + "_from_" + timestamp + fileExtension;

        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference("sentMedia").child(fileName);

        //upload image to storage
        mStorageRef.putFile(sendingMediaUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {

                    //get image's download url
                    task.getResult().getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String mediaUrl = uri.toString();

                            //get image rotation
                            int rotation = 0;
                            try {
                                ExifInterface exif = null;
                                exif = new ExifInterface(sendingMediaUri.getPath());
                                rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            mDatabaseRef = mDatabase.getReference();
                            if (sendingTo == "directlyToFriend") {
                                mDatabaseRef.child("messages/" + friendsUUID + "/" + getCurrentUsersUUID() + "/fullName").setValue(sendersFullName);
                                mDatabaseRef.child("messages/" + friendsUUID + "/" + getCurrentUsersUUID() + "/unopened").setValue(true);
                                mDatabaseRef.child("messages/" + friendsUUID + "/" + getCurrentUsersUUID() + "/profileImageUrl").setValue(profileImageURL);
                                mDatabaseRef.child("messages/" + friendsUUID + "/" + getCurrentUsersUUID() + "/profileImageRotation").setValue(rotation);
                                mDatabaseRef.child("messages/" + friendsUUID + "/" + getCurrentUsersUUID() + "/" + timestamp + "/fileExtension").setValue(fileExtension);
                                mDatabaseRef.child("messages/" + friendsUUID + "/" + getCurrentUsersUUID() + "/" + timestamp + "/unopened").setValue(true);
                                mDatabaseRef.child("messages/" + friendsUUID + "/" + getCurrentUsersUUID() + "/" + timestamp + "/mediaMessageUrl").setValue(mediaUrl);
                                mDatabaseRef.child("messages/" + friendsUUID + "/" + getCurrentUsersUUID() + "/" + timestamp + "/mediaMessageRotation").setValue(rotation);
                                mDatabaseRef.child("messages/" + friendsUUID + "/" + getCurrentUsersUUID() + "/" + timestamp + "/textMessage").setValue(message);
                            } else {
                                mDatabaseRef.child("stories/" + friendsUUID + "/" + timestamp + "/fileExtension").setValue(fileExtension);
                                mDatabaseRef.child("stories/" + friendsUUID + "/" + timestamp + "/fullName").setValue(sendersFullName);
                                mDatabaseRef.child("stories/" + friendsUUID + "/" + timestamp + "/mediaMessageUrl").setValue(mediaUrl);
                                mDatabaseRef.child("stories/" + friendsUUID + "/" + timestamp + "/mediaMessageRotation").setValue(rotation);
                                mDatabaseRef.child("stories/" + friendsUUID + "/" + timestamp + "/textMessage").setValue(message);
                                mDatabaseRef.child("stories/" + friendsUUID + "/" + timestamp + "/unopened").setValue(true);
                            }

                            listener.onSuccess(true);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            listener.onSuccess(false);
                        }
                    });

                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onSuccess(false);

                    }
                });

    }

    public interface OnGetExistingReceivedMediaDetailsListener {
        void onSuccess(List<HashMap<String, String>> receivedMediaDetails, int numberOfStories);
    }

    List<HashMap<String, String>> existingReceivedMediaDetails = new ArrayList<HashMap<String, String>>() {
    };
    int numberOfUnopenedStories = 0;

    public void getExistingReceivedMediaDetails(final OnGetExistingReceivedMediaDetailsListener listener) {

        String UUID = getCurrentUsersUUID();
        //get story details
        mDatabaseRef = mDatabase.getReference();
        final Query getNumberOfStories = mDatabaseRef.child("stories/" + UUID);
        final Query getUnopenedFriendsMessages = mDatabaseRef.child("messages/" + UUID);
        getNumberOfStories.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String timestamp = ds.getKey();

                    //check if the story message is old, if it is delete it. If not add it to the count
                    if (isStoryMessageOld(timestamp)) {
                        deleteStoryMessage(timestamp);
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
                            boolean firstLoop = true;

                            //add the UUID of the sender
                            friendsMessagesHashMap.put("UUID", ds.getKey());

                            //get the other data
                            for (DataSnapshot subDS : ds.getChildren()) {
                                //if this is the first child, then this is the most recent message sent
                                if (firstLoop) {
                                    friendsMessagesHashMap.put("lastMessageTimeStamp", subDS.getKey());
                                    firstLoop = false;
                                }


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

        //get friends messages
    }

    public interface OnUpdateFriendRelationshipListener {
        void onSuccess(boolean success);
    }

    public void updateFriendRelationship(String friendsUUID, String updatedRelationship, final OnUpdateFriendRelationshipListener listener) {

        try {
            mDatabaseRef = mDatabase.getReference("userDetails");
            mDatabaseRef.child(getCurrentUsersUUID() + "/friends/" + friendsUUID + "/relationship").setValue(updatedRelationship);
            listener.onSuccess(true);
        } catch (Exception exception) {
            listener.onSuccess(false);
        }
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


    public void removeFriend(String friendsUUID) {

        mDatabaseRef = mDatabase.getReference("userDetails");
        Query removeFriendFromCurrentUsersFriends = mDatabaseRef.child(getCurrentUsersUUID() + "/friends/" + friendsUUID);
        final Query removeCurrentUserFromFriendsFriends = mDatabaseRef.child(friendsUUID + "/friends/" + getCurrentUsersUUID());

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

    private void addNewUsersData(final String firstName, final String surname, final Uri profileImageUri, final String UUID, final String username, final OnAddNewUserDataListener listener) {

        String fileName = UUID + "_profileImage.jpg";

        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference("images").child(fileName);


        //upload profile image to storage
        mStorageRef.putFile(profileImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {

                    //get profile image's download url
                    task.getResult().getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String profileImageUrl = uri.toString();
                            //remove the unnecessary data from the url
                            String[] partsOfProfileImageUrl = profileImageUrl.split("\\?");
                            profileImageUrl = partsOfProfileImageUrl[0];

                            //get image rotation
                            int rotation = 0;
                            try {
                                ExifInterface exif = null;
                                exif = new ExifInterface(profileImageUri.getPath());
                                rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            //create new user in database using UUID already created
                            UserHelperClass userHelperClass = new UserHelperClass(username, firstName, surname, profileImageUrl, rotation);

                            mDatabaseRef = mDatabase.getReference("userDetails");
                            mDatabaseRef.child(UUID).setValue(userHelperClass);

                            mDatabaseRef = mDatabase.getReference("usernames");
                            mDatabaseRef.child(username).setValue(UUID);


                            listener.onSuccess(true);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            listener.onSuccess(false);
                        }
                    });

                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onSuccess(false);

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

                        //check to see if user is already friends with the user
                        isUserAlreadyFriendsWith(friendsUsername, new OnCheckIfUserIsAlreadyFriendsWithListener() {
                            @Override
                            public void onSuccess(boolean alreadyFriends) {

                                if (alreadyFriends) {
                                    Toast.makeText(context, "You and " + friendsUsername + " are already friends", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                //check adding user is not blocked by the recipient user
                                isUserBlockedBy(friendsUsername, new OnCheckIfUserIsBlockedByListener() {
                                    @Override
                                    public void onSuccess(boolean blocked) {

                                        if (blocked) {
                                            Toast.makeText(context, "You have been blocked by this user", Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        //check if friend has already sent a friend request to the user. If so auto accept
                                        getUsernamesUUID(friendsUsername, new OnGetUUIDListener() {
                                            @Override
                                            public void onSuccess(final String friendsUUID) {
                                                Log.d(TAG, "addFriend: 0");
                                                checkReceivedFriendRequestFrom(friendsUUID, new OnCheckReceivedFriendRequestFromListener() {
                                                    @Override
                                                    public void onSuccess(boolean haveExistingFriendRequest) {

                                                        Log.d(TAG, "addFriend: 1");
                                                        if (haveExistingFriendRequest) {
                                                            Toast.makeText(context, "Friend request to " + friendsUsername + " was automatically accepted", Toast.LENGTH_SHORT).show();
                                                            acceptFriendRequest(friendsUUID, friendsUsername);
                                                            return;
                                                        }
                                                        //if all is good add the user as a friend
                                                        sendFriendRequest(friendsUsername, relationship, new OnCheckIfFriendRequestSentListener() {
                                                            @Override
                                                            public void onSuccess(boolean requestSentSuccessfully) {
                                                                Log.d(TAG, "addFriend: 2");
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
                    listener.onSuccess(formatting.removeEndingSpaceFromString((String) snapshot.getValue()));
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


    public interface OnCheckIfFriendRequestSentListener {
        void onSuccess(boolean requestSentSuccessfully);
    }

    public void sendFriendRequest(final String friendsUsername, final String relationship, final OnCheckIfFriendRequestSentListener listener) {

        getUsernamesUUID(friendsUsername, new OnGetUUIDListener() {
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

    public interface OnCheckReceivedFriendRequestFromListener {
        void onSuccess(boolean result);
    }

    public void checkReceivedFriendRequestFrom(final String friendRequestFromUUID, final OnCheckReceivedFriendRequestFromListener listener) {

        mDatabase.getReference("userDetails").child(getCurrentUsersUUID() + "/friendRequests").orderByValue().addListenerForSingleValueEvent(new ValueEventListener() {
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

    public void acceptFriendRequest(final String friendsUUID, final String friendsRelationship) {

        //cancel the friend requests
        cancelReceivedFriendRequest(friendsUUID);

        //get details of accounts
        getAccountDetails(getCurrentUsersUUID(), new OnGetAccountDetailsListener() {
            @Override
            public void onSuccess(final AccountDetails currentUserAccountDetails) {

                getAccountDetails(friendsUUID, new OnGetAccountDetailsListener() {
                    @Override
                    public void onSuccess(AccountDetails accountDetails) {
                        AccountDetails friendsDetails = accountDetails;
                        friendsDetails.setRelationship(friendsRelationship);

                        //add each other as a friend on each account
                        mDatabaseRef = mDatabase.getReference("userDetails");
                        mDatabaseRef.child(friendsUUID + "/friends/" + getCurrentUsersUUID()).setValue(currentUserAccountDetails);
                        mDatabaseRef.child(getCurrentUsersUUID() + "/friends/" + friendsUUID).setValue(friendsDetails);
                    }
                });
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
                    Log.e(TAG, "could not find match for value: " + ds.getKey());
            }
        }
        return accountDetails;
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


    private boolean isStoryMessageOld(String timestamp) {
        Date now = new Date();
        long currentUnixTime = now.getTime() / 1000L;


        long messageSentUnixTime = Long.parseLong(timestamp);
        long unixTimeAgo = currentUnixTime - messageSentUnixTime;
        if (unixTimeAgo < 259200) {
            return false;
        }
        return true;
    }


    private void deleteStoryMessage(String timestamp) {

        mDatabaseRef = mDatabase.getReference("stories");
        Query getStoryMessage = mDatabaseRef.child(getCurrentUsersUUID() + "/" + timestamp);

        getStoryMessage.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getRef().removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void deleteMessage(final String timestamp, final String friendsUUID) {

        mDatabaseRef = mDatabase.getReference("messages");
        Query getStoryMessage = mDatabaseRef.child(getCurrentUsersUUID() + "/" + friendsUUID);

        getStoryMessage.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean existingMessage = false;

                for(DataSnapshot ds : snapshot.getChildren()){
                    //if its the message, delete it
                    if(ds.getKey().equals(timestamp)){
                        ds.getRef().removeValue();
                    }
                    else if(!ds.getKey().equals("fullName") && !ds.getKey().equals("profileImageRotation")  &&
                            !ds.getKey().equals("profileImageUrl")  && !ds.getKey().equals("unopened")){

                        //if there is an existing message found, break out of for loop;
                        existingMessage = true;
                        break;
                    }
                }

                //if the last message was opened then set unopened messages to false
                if(!existingMessage){
                    mDatabaseRef.child(getCurrentUsersUUID() + "/" + friendsUUID + "/unopened").setValue(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}


