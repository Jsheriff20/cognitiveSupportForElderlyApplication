package messaging.app.contactingFirebase;

import android.content.Context;
import android.media.ExifInterface;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.UUID;

import messaging.app.Formatting;
import messaging.app.MediaManagement;
import messaging.app.MessageReceivedServiceNotification;

public class ManagingMessages {

    Context context;
    FirebaseDatabase mDatabase;
    FirebaseStorage mStorage;
    Formatting formatting = new Formatting();
    QueryingDatabase queryingDatabase = new QueryingDatabase();

    public ManagingMessages(Context context) {
        this.context = context;
        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
    }

    public void sendMessages(final ArrayList<String> directMessagesUUID, final ArrayList<String> storyMessagesUUID, final String pathToMedia, String fileType, final String message, final int deviceOrientationMode) throws IOException {

        final String profanityCheckedMessage = formatting.removeProfanity(message);

        final String fileExtension;
        if (fileType.equals("Image")) {
            fileExtension = ".jpg";
        } else {
            fileExtension = ".mp4";
        }

        final Uri sendingMediaUri = Uri.fromFile(new File(pathToMedia));

        //get image rotation
        int rotation = 0;
        try {
            ExifInterface exif = null;
            exif = new ExifInterface(sendingMediaUri.getPath());
            rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        } catch (IOException e) {
            e.printStackTrace();
        }

        final int numberOfMessagesSent = storyMessagesUUID.size() + directMessagesUUID.size();
        final int finalRotation = rotation;
        queryingDatabase.getUUIDsFullNameAndProfileImage(queryingDatabase.getCurrentUsersUUID(), new QueryingDatabase.OnGetUUIDsFullNameAndProfileImageListener() {
            @Override
            public void onSuccess(final String sendersFullName, String profileImageURL, String profileImageRotation) {
                if (!directMessagesUUID.isEmpty()) {
                    sendMessage(directMessagesUUID, sendersFullName, profileImageURL, profileImageRotation, sendingMediaUri, finalRotation, deviceOrientationMode, fileExtension, profanityCheckedMessage, "directlyToFriend", new OnSendMessageListener() {
                        @Override
                        public void onSuccess(String fileName) {
                            logNewMediaMessage(numberOfMessagesSent, fileName);

                        }
                    });
                }

                if (!storyMessagesUUID.isEmpty()) {
                    sendMessage(storyMessagesUUID, sendersFullName, profileImageURL, profileImageRotation, sendingMediaUri, finalRotation, deviceOrientationMode, fileExtension, profanityCheckedMessage, "toStory", new OnSendMessageListener() {
                        @Override
                        public void onSuccess(String fileName) {
                            //story messages do not get logged as they work off of a time not views

                        }
                    });
                }

                MediaManagement mediaManagement = new MediaManagement();
                mediaManagement.deleteMediaFile(pathToMedia, context);
            }
        });
    }


    private void logNewMediaMessage(int numberOfMessagesSent, String fileName) {

        String editedFileName = fileName.replace(".", "");

        //add number of links to the media has been sent
        DatabaseReference databaseRef = mDatabase.getReference();
        databaseRef.child("sentMediaLog/" + editedFileName + "/sent").setValue(numberOfMessagesSent);

        increaseNumOfSentMessagesFromUUID(numberOfMessagesSent);
    }


    public void logMediaMessageViewed(final String fileUrl) {

        //get the file name from the url
        //remove the dot
        final String fileName = formatting.getFileNameFromUrl(fileUrl).replace(".", "");


        final DatabaseReference databaseRef = mDatabase.getReference("sentMediaLog/" + fileName);
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int currentValue = 0;
                int sentValue = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    switch (ds.getKey()) {
                        case "viewed":
                            long longCurrentValue = (long) ds.getValue();
                            currentValue = (int) longCurrentValue;
                            break;
                        case "sent":
                            long longSentValue = (long) ds.getValue();
                            sentValue = (int) longSentValue;
                            break;
                    }

                }
                int newValue = currentValue + 1;

                //if all of the media messages have been viewed, then delete.
                //else update the new value
                if (sentValue <= newValue) {
                    //remove from file storage
                    deleteSentMediaFromStorage(fileName);
                    //remove from database
                    databaseRef.setValue(null);
                } else {
                    databaseRef.child("viewed").setValue(newValue);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void increaseNumOfSentMessagesFromUUID(int numberOfMessagesSent) {

        final DatabaseReference databaseRef = mDatabase.getReference("userDetails").child(queryingDatabase.getCurrentUsersUUID());
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int currentValue = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds.getKey().equals("numberOfSentMessages")) {
                        long longCurrentValue = (long) ds.getValue();
                        currentValue = (int) longCurrentValue;
                    }

                }
                int newValue = currentValue + numberOfMessagesSent;

                databaseRef.child("numberOfSentMessages").setValue(newValue);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void deleteSentMediaFromStorage(String fileName) {
        //delete from firebase storage
        FirebaseStorage storage = FirebaseStorage.getInstance();

        //add the . between the filename and the extension
        int dotPosition = (fileName.length() - 3);
        String editedFileName = fileName.substring(0, dotPosition) + "." + fileName.substring(dotPosition);


        StorageReference storageReference = storage.getReference("sentMedia").child(editedFileName);
        storageReference.delete();
    }


    public interface OnSendMessageListener {
        void onSuccess(String fileName);

    }

    private void sendMessage(final ArrayList<String> listOfUUIDs, final String sendersFullName,
                             final String profileImageURL, final String profileImageRotation,
                             final Uri sendingMediaUri, final int rotation,
                             final int deviceOrientationMode, final String fileExtension,
                             final String message, final String sendingTo,
                             final OnSendMessageListener listener) {
        Date now = new Date();
        long ut3 = now.getTime() / 1000L;
        final String timestamp = Long.toString(ut3);
        String randomUUID = String.valueOf(UUID.randomUUID());
        final String fileName = randomUUID + timestamp + fileExtension;

        mStorage = FirebaseStorage.getInstance();
        StorageReference storageRef = mStorage.getReference("sentMedia").child(fileName);

        //upload image to storage
        storageRef.putFile(sendingMediaUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {

                    //get image's download url
                    task.getResult().getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String mediaUrl = uri.toString();
                            DatabaseReference databaseRef = mDatabase.getReference();

                            for (String friendsUUID : listOfUUIDs) {
                                if (sendingTo == "directlyToFriend") {
                                    databaseRef.child("messages/" + friendsUUID + "/" + queryingDatabase.getCurrentUsersUUID() + "/fullName").setValue(sendersFullName);
                                    databaseRef.child("messages/" + friendsUUID + "/" + queryingDatabase.getCurrentUsersUUID() + "/unopened").setValue(true);
                                    databaseRef.child("messages/" + friendsUUID + "/" + queryingDatabase.getCurrentUsersUUID() + "/lastSentMessageTime").setValue(timestamp);
                                    databaseRef.child("messages/" + friendsUUID + "/" + queryingDatabase.getCurrentUsersUUID() + "/profileImageUrl").setValue(profileImageURL);
                                    databaseRef.child("messages/" + friendsUUID + "/" + queryingDatabase.getCurrentUsersUUID() + "/profileImageRotation").setValue(profileImageRotation);
                                    databaseRef.child("messages/" + friendsUUID + "/" + queryingDatabase.getCurrentUsersUUID() + "/" + timestamp + "/fileExtension").setValue(fileExtension);
                                    databaseRef.child("messages/" + friendsUUID + "/" + queryingDatabase.getCurrentUsersUUID() + "/" + timestamp + "/unopened").setValue(true);
                                    databaseRef.child("messages/" + friendsUUID + "/" + queryingDatabase.getCurrentUsersUUID() + "/" + timestamp + "/mediaMessageUrl").setValue(mediaUrl);
                                    databaseRef.child("messages/" + friendsUUID + "/" + queryingDatabase.getCurrentUsersUUID() + "/" + timestamp + "/mediaMessageRotation").setValue(rotation);
                                    databaseRef.child("messages/" + friendsUUID + "/" + queryingDatabase.getCurrentUsersUUID() + "/" + timestamp + "/deviceOrientationMode").setValue(deviceOrientationMode);
                                    databaseRef.child("messages/" + friendsUUID + "/" + queryingDatabase.getCurrentUsersUUID() + "/" + timestamp + "/textMessage").setValue(message);
                                } else {
                                    databaseRef.child("stories/" + friendsUUID + "/" + timestamp + "/fileExtension").setValue(fileExtension);
                                    databaseRef.child("stories/" + friendsUUID + "/" + timestamp + "/fullName").setValue(sendersFullName);
                                    databaseRef.child("stories/" + friendsUUID + "/" + timestamp + "/mediaMessageUrl").setValue(mediaUrl);
                                    databaseRef.child("stories/" + friendsUUID + "/" + timestamp + "/mediaMessageRotation").setValue(rotation);
                                    databaseRef.child("stories/" + friendsUUID + "/" + timestamp + "/deviceOrientationMode").setValue(deviceOrientationMode);
                                    databaseRef.child("stories/" + friendsUUID + "/" + timestamp + "/textMessage").setValue(message);
                                    databaseRef.child("stories/" + friendsUUID + "/" + timestamp + "/unopened").setValue(true);
                                }
                            }

                            listener.onSuccess(fileName);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            listener.onSuccess(null);
                        }
                    });

                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onSuccess(null);

                    }
                });

    }

    public void deleteStoryMessage(String timestamp) {

        DatabaseReference databaseRef = mDatabase.getReference("stories");
        Query getStoryMessage = databaseRef.child(queryingDatabase.getCurrentUsersUUID() + "/" + timestamp);

        getStoryMessage.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds.getKey().equals("mediaMessageUrl")) {
                        String url = (String) ds.getValue();
                        final String fileName = formatting.getFileNameFromUrl(url).replace(".", "");
                        deleteSentMediaFromStorage(fileName);
                    }
                }
                snapshot.getRef().removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void deleteMessage(final String timestamp, final String friendsUUID) {

        final DatabaseReference databaseRef = mDatabase.getReference("messages");
        Query getStoryMessage = databaseRef.child(queryingDatabase.getCurrentUsersUUID() + "/" + friendsUUID);

        getStoryMessage.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean existingMessage = true;
                String lastSentMessageTime = null;

                for (DataSnapshot ds : snapshot.getChildren()) {
                    //if its the message, delete it
                    if (ds.getKey().equals(timestamp)) {
                        ds.getRef().removeValue();
                    }
                    if (ds.getKey().equals("lastSentMessageTime")) {
                        lastSentMessageTime = (String) ds.getValue();
                    }
                }

                //check if the viewing message was the last sent message (therefore the last message)
                if (lastSentMessageTime.equals(timestamp)) {
                    //if there is an existing message found;
                    existingMessage = false;
                }

                //if the last message was opened then set unopened messages to false
                if (!existingMessage) {
                    databaseRef.child(queryingDatabase.getCurrentUsersUUID() + "/" + friendsUUID + "/unopened").setValue(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
