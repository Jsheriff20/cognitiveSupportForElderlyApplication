package messaging.app.messages.friendsList;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

import messaging.app.CheckInputsValidity;
import messaging.app.ContactingFirebase;
import messaging.app.R;

public class AddFriendActivity extends AppCompatActivity {

    EditText txtAddingUsername;
    ImageButton btnSearchFriend;

    CheckInputsValidity checkInputsValidity = new CheckInputsValidity(this);
    ContactingFirebase contactingFirebase = new ContactingFirebase(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        txtAddingUsername = findViewById(R.id.txtAddingUsername);
        btnSearchFriend = findViewById(R.id.btnSearchFriend);
    }

    private void setBtnSearchFriendOnClick(){
        String username = txtAddingUsername.getText().toString();
        if(checkInputsValidity.isUsernameValid(username) ){
            if(contactingFirebase.doesUsernameExist(username)){
                //TODO:
                //Add user
                contactingFirebase.addFriend(username);
            }
            else{
                Toast.makeText(this, "Username could not be found", Toast.LENGTH_SHORT).show();
            }
        }
    }

}