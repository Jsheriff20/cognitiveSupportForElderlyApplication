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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ContactingFirebase {


    Context context;
    FirebaseDatabase database;
    DatabaseReference reference;

    public ContactingFirebase(Context context) {
        this.context = context;
        database = FirebaseDatabase.getInstance();

    }


    public void createUserWithEmailAndPassword(String email, String password, final String firstName, final String surname, final Bitmap profileImage) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
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
                            AddNewUsersData(firstName, surname, profileImage, UUID);
                            Intent intent = new Intent(context, LoginActivity.class);
                            context.startActivity(intent);
                        }
                    }
                });
    }

    private void AddNewUsersData(String firstName, String surname, Bitmap profileImage, String UUID) {
        //create new user in database using UUID already created
        UserHelperClass userHelperClass = new UserHelperClass(firstName, surname, profileImage);

        reference = database.getReference("userDetails");
        reference.child(UUID).setValue(userHelperClass);

        //store info in global variables to be used across application
    }
}


