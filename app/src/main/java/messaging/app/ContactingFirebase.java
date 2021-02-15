package messaging.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import messaging.app.login.LoginActivity;
import messaging.app.register.RegisterEmailActivity;
import messaging.app.register.RegisterPasswordActivity;

public class ContactingFirebase {


    String TAG = "Test";

    Context context;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseAuth auth;
    Formatting formatting = new Formatting();

    public ContactingFirebase(Context context) {
        this.context = context;
        database = FirebaseDatabase.getInstance();
    }


    public void createUserWithEmailAndPassword(String email, String password, final String firstName, final String surname, final Bitmap profileImage, final String username) {

        auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {
                            Toast.makeText(context, "Fail to register", Toast.LENGTH_SHORT).show();
                        } else {
                            //get UUID of the user account created
                            String UUID = (String) task.getResult().getUser().getUid();

                            //add usersData to the database
                            addNewUsersData(firstName, surname, profileImage, UUID, username);
                            Intent intent = new Intent(context, LoginActivity.class);
                            context.startActivity(intent);
                        }
                    }
                });
    }


    public interface OnEmailCheckListener{
        void onSuccess(boolean isRegistered);
    }
    public void isEmailAvailable(final String email, final OnEmailCheckListener listener){

        auth = FirebaseAuth.getInstance();
        auth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>()
        {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                boolean check = !task.getResult().getSignInMethods().isEmpty();

                listener.onSuccess(check);
            }
        });

    }


    private void addNewUsersData(String firstName, String surname, Bitmap profileImage, String UUID, String username) {
        //create new user in database using UUID already created
        UserHelperClass userHelperClass = new UserHelperClass(firstName, surname, profileImage, username);

        reference = database.getReference("userDetails");
        reference.child(UUID).setValue(userHelperClass);

        reference = database.getReference("usernames");
        reference.child(username).setValue(UUID);

    }


    public interface OnCheckIfUsernameExistsListener{
        void onSuccess(boolean exists);
    }
    public void doesUsernameExist(final String username, final OnCheckIfUsernameExistsListener listener) {

        database.getReference("usernames").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot ds : snapshot.getChildren()){
                    if(username.toLowerCase().equals(ds.getKey().toLowerCase())){
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


    public void loginUser(String email, String password){

        auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {
                            Toast.makeText(context, "Password or email is incorrect", Toast.LENGTH_SHORT).show();

                        } else {
                            context.startActivity(new Intent(context, SelectAreaOfApplicationActivity.class));

                            //TODO:
                            //store info in global variables to be used across application
                        }
                    }
                });
    }


    public void resetPassword(String email) {
        auth = FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(email)
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


    public void addFriend(final String friendsUsername){
        auth = FirebaseAuth.getInstance();
        String userAddingsUUID = auth.getCurrentUser().getUid();

        //check user is not adding themselves
        getUUIDsUsername(userAddingsUUID,new OnGetUsernameListener(){
            @Override
            public void onSuccess(String username) {

                if(friendsUsername.equals(username)){
                    Toast.makeText(context, "You can not add yourself", Toast.LENGTH_SHORT).show();
                    return;
                }
                else{

                    //check username exists
                    doesUsernameExist(friendsUsername, new OnCheckIfUsernameExistsListener() {
                        @Override
                        public void onSuccess(boolean exists) {

                            if(exists){

                                //check adding user is not blocked by the recipient user
                                isUserBlockedBy(friendsUsername, new OnCheckIfUserIsBlockedByListener() {
                                    @Override
                                    public void onSuccess(boolean blocked) {

                                        if(blocked){
                                            Toast.makeText(context, "You have been blocked by this user", Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            //if all is good add the user as a friend
                                            sendFriendRequest(friendsUsername, new OnCheckIfFriendRequestSentListener() {
                                                @Override
                                                public void onSuccess(boolean requestSentSuccessfully) {
                                                    if(requestSentSuccessfully){
                                                        Toast.makeText(context, "Friend request to " + friendsUsername + " was sent successfully", Toast.LENGTH_SHORT).show();
                                                    }
                                                    else{
                                                        Toast.makeText(context, "Friend request to " + friendsUsername + " failed", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                            else{
                                Toast.makeText(context, "Username cannot be found", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }


    public interface OnGetUsernameListener{
        void onSuccess(String username);
    }
    public void getUUIDsUsername(final String UUID, final OnGetUsernameListener listener) {

        database.getReference("userDetails").child(UUID + "/username").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Log.d("Test", "snapshot.getValue(): " + snapshot.getValue());
                if(!snapshot.getValue().toString().equals(null)) {
                    listener.onSuccess(formatting.removeEndingSpaceFromString((String) snapshot.getValue()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onSuccess(null);
            }
        });
    }


    public interface OnGetUUIDListener{
        void onSuccess(String UUID);
    }
    public void getUsernamesUUID(final String username, final OnGetUUIDListener listener) {

        database.getReference("usernames").child(username).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                listener.onSuccess(formatting.removeEndingSpaceFromString((String) snapshot.getValue()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    public interface OnCheckIfUserIsBlockedByListener{
        void onSuccess(boolean blocked);
    }
    public void isUserBlockedBy(final String blockedByUsername, final OnCheckIfUserIsBlockedByListener listener) {

        getUsernamesUUID(blockedByUsername, new OnGetUUIDListener() {
            @Override
            public void onSuccess(String blockedByUUID) {
                String usersUUID = auth.getCurrentUser().getUid();

                database.getReference("userDetails").child(usersUUID + "/blockedBy/" + blockedByUUID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.getValue() == null){
                            listener.onSuccess(false);
                        }
                        else{
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



    public interface OnCheckIfFriendRequestSentListener{
        void onSuccess(boolean requestSentSuccessfully);
    }
    public void sendFriendRequest(final String friendsUsername, final OnCheckIfFriendRequestSentListener listener) {

        getUsernamesUUID(friendsUsername, new OnGetUUIDListener() {
            @Override
            public void onSuccess(String friendsUUID) {
                String usersUUID = auth.getCurrentUser().getUid();


                //TODO:
                //check if friend request has been received by friend already. If so automatically accept the friend request.

                try {
                    reference = database.getReference("userDetails");
                    reference.child(usersUUID + "/sentFriendRequests/" + friendsUUID).setValue(true);

                    reference = database.getReference("userDetails");
                    reference.child(friendsUUID + "/friendRequests/" + usersUUID).setValue(true);

                    listener.onSuccess(true);
                }
                catch (Exception exception){

                    listener.onSuccess(false);
                }

            }
        });
    }
}


