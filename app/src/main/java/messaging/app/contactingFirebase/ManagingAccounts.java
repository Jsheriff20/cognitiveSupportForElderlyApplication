package messaging.app.contactingFirebase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.ExifInterface;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import messaging.app.MediaManagement;
import messaging.app.SelectAreaOfApplicationActivity;
import messaging.app.UserHelperClass;
import messaging.app.login.LoginActivity;

public class ManagingAccounts {

    Context context;
    FirebaseAuth mAuth;
    FirebaseUser mCurrentUser;
    FirebaseDatabase mDatabase;
    FirebaseStorage mStorage;

    public ManagingAccounts(Context context) {
        this.context = context;
        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
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
                                            }

                                            MediaManagement mediaManagement = new MediaManagement();
                                            mediaManagement.deleteMediaFile(profileImage.toString(), context);

                                            return;
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




    public interface OnAddNewUserDataListener {
        void onSuccess(boolean success);
    }

    private void addNewUsersData(final String firstName, final String surname, final Uri profileImageUri, final String UUID, final String username, final OnAddNewUserDataListener listener) {

        String fileName = UUID + "_profileImage.jpg";

        mStorage = FirebaseStorage.getInstance();
        StorageReference storageRef = mStorage.getReference("images").child(fileName);


        //upload profile image to storage
        storageRef.putFile(profileImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
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

                            DatabaseReference databaseRef = mDatabase.getReference("userDetails");
                            databaseRef.child(UUID).setValue(userHelperClass);

                            databaseRef = mDatabase.getReference("usernames");
                            databaseRef.child(username).setValue(UUID);


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


    public void logoutUser() {
        mAuth.getInstance().signOut();
        //TODO:
        //stop notification service
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


    boolean returnValue;
    public boolean loginUser(String email, String password) {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {
                            Toast.makeText(context, "Password or email is incorrect", Toast.LENGTH_SHORT).show();
                            returnValue = false;
                        } else {
                            context.startActivity(new Intent(context, SelectAreaOfApplicationActivity.class));
                            returnValue = true;
                        }
                    }
                });
        return returnValue;

    }


}
