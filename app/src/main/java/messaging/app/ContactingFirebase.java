package messaging.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.google.firebase.database.ValueEventListener;

public class ContactingFirebase {


    Context context;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseAuth auth;

    public ContactingFirebase(Context context) {
        this.context = context;
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
    }


    public void createUserWithEmailAndPassword(String email, String password, final String firstName, final String surname, final Bitmap profileImage, final String username) {

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
                            AddNewUsersData(firstName, surname, profileImage, UUID, username);
                            Intent intent = new Intent(context, LoginActivity.class);
                            context.startActivity(intent);
                        }
                    }
                });
    }


    public interface OnEmailCheckListener{
        void onSuccess(boolean isRegistered);
    }

    public void isEmailAvailable(final String email,final OnEmailCheckListener listener){
        auth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>()
        {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                boolean check = !task.getResult().getSignInMethods().isEmpty();

                listener.onSuccess(check);
            }
        });

    }


    private void AddNewUsersData(String firstName, String surname, Bitmap profileImage, String UUID, String username) {
        //create new user in database using UUID already created
        UserHelperClass userHelperClass = new UserHelperClass(firstName, surname, profileImage, username);

        reference = database.getReference("userDetails");
        reference.child(UUID).setValue(userHelperClass);

        reference = database.getReference("usernames");
        reference.child(username).setValue(UUID);

    }


    boolean usernameExists = false;
    public boolean isUsernameTaken(final String username){


        database.getReference("usernames").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    if(username.toLowerCase().equals(ds.getKey().toLowerCase())){
                        usernameExists = true;
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return usernameExists;
    }


    public void LoginUser(String email, String password){
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
}


