package messaging.app.messages.friendsList;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.view.View;
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
    EditText txtRelationship;
    ImageButton btnSearchFriend;

    CheckInputsValidity checkInputsValidity = new CheckInputsValidity(this);
    ContactingFirebase contactingFirebase = new ContactingFirebase(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        txtAddingUsername = findViewById(R.id.txtAddingUsername);
        btnSearchFriend = findViewById(R.id.btnSearchFriend);
        txtRelationship = findViewById(R.id.txtRelationship);

        setBtnSearchFriendOnClick();
    }

    private void setBtnSearchFriendOnClick(){
        btnSearchFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = txtAddingUsername.getText().toString();
                String relationship = txtRelationship.getText().toString();
                if(checkInputsValidity.isUsernameValid(username) && checkInputsValidity.isRelationshipValid(relationship)){
                    contactingFirebase.addFriend(username, relationship);
                }
            }
        });
    }

}