package messaging.app.register;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import messaging.app.CheckInputsValidity;
import messaging.app.ContactingFirebase;
import messaging.app.Formatting;
import messaging.app.login.LoginActivity;
import messaging.app.R;


//TODO:
//when user uploads or captures a photo the "Skip" button needs to change to "Register"
public class RegisterUsernameActivity extends AppCompatActivity {

    EditText txtUsername;
    Button btnBackToRegisterPassword;
    Button btnLoadPersonalInfo;
    Button btnLoadLogin;

    String mEmail;
    String mPassword;
    ContactingFirebase contactingFirebase = new ContactingFirebase(this);
    CheckInputsValidity checkInputsValidity = new CheckInputsValidity(this);
    Formatting formatting = new Formatting();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_username);

        txtUsername = findViewById(R.id.txtUsername);
        btnBackToRegisterPassword = findViewById(R.id.btnBackToRegisterPassword);
        btnLoadPersonalInfo = findViewById(R.id.btnLoadPersonalInfoRegister);
        btnLoadLogin = findViewById(R.id.btnLoadLogin);

        mEmail = getIntent().getStringExtra("email");
        mPassword = getIntent().getStringExtra("password");

        if(getIntent().getStringExtra("username") != null){
            txtUsername.setText(getIntent().getStringExtra("username"));
        }


        setBtnBackToRegisterPasswordOnClick();
        setBtnLoadPersonInfoOnClick();
        setBtnLoadLoginOnClick();
    }


    private void setBtnBackToRegisterPasswordOnClick(){
        btnBackToRegisterPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterUsernameActivity.this, RegisterPasswordActivity.class);
                intent.putExtra("email", mEmail);
                RegisterUsernameActivity.this.startActivity(intent);
                return;
            }
        });
    }


    private void setBtnLoadPersonInfoOnClick(){
        btnLoadPersonalInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = formatting.removeEndingSpaceFromString(txtUsername.getText().toString());
                if(checkInputsValidity.isUsernameValid(username)) {

                    contactingFirebase.doesUsernameExist(username, new ContactingFirebase.OnCheckIfUsernameExistsListener() {
                        @Override
                        public void onSuccess(boolean exists) {
                            if(exists){
                                Toast.makeText(RegisterUsernameActivity.this, "Username is taken", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Intent intent = new Intent(RegisterUsernameActivity.this, RegisterPersonalInfoActivity.class);
                                intent.putExtra("password", mPassword);
                                intent.putExtra("email", mEmail);
                                intent.putExtra("username", username);
                                RegisterUsernameActivity.this.startActivity(intent);
                            }
                        }
                    });
                }
            }
        });
    }


    private void setBtnLoadLoginOnClick(){
        btnLoadLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterUsernameActivity.this, LoginActivity.class);
                RegisterUsernameActivity.this.startActivity(intent);
            }
        });
    }
}