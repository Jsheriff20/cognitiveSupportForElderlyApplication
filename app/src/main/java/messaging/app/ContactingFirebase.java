package messaging.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

    public ContactingFirebase(Context context) {
        this.context = context;
    }

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    public class contactingFirebaseDatabase {
        DatabaseReference myRef = database.getReference("userDetails");
    }


    public void createUserWithEmailAndPassword(String email, String password) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("Test", "onComplete: Create user with email and pass");

                        if (!task.isSuccessful()) {
                            Toast.makeText(context, "Fail to register", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(context, LoginActivity.class);
                            context.startActivity(intent);
                        }
                    }
                });
    }
}


