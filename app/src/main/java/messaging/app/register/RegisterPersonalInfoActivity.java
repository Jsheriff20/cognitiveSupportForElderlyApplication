package messaging.app.register;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import messaging.app.CheckInputsValidity;
import messaging.app.Formatting;
import messaging.app.login.LoginActivity;
import messaging.app.R;

public class RegisterPersonalInfoActivity extends AppCompatActivity {

    EditText txtFirstName;
    EditText txtSurname;
    Button btnLoadProfileImageRegister;
    Button btnLoadLogin;
    Button btnBackToRegisterUsername;
    CheckInputsValidity checkInputsValidity = new CheckInputsValidity(this);
    Formatting formatting = new Formatting();

    String mEmail;
    String mPassword;
    String mUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_personal_info);

        txtFirstName = findViewById(R.id.txtFirstName);
        txtSurname = findViewById(R.id.txtSurname);
        btnLoadProfileImageRegister = (Button) findViewById(R.id.btnLoadProfileImageRegister);
        btnLoadLogin = findViewById(R.id.btnLoadLogin);
        btnBackToRegisterUsername = findViewById(R.id.btnBackToRegisterUsername);

        //data received from previous activity
        mEmail = getIntent().getStringExtra("email");
        mPassword = getIntent().getStringExtra("password");
        mUsername = getIntent().getStringExtra("username");


        //if user has selected "Back" then this information will be displayed
        if(getIntent().getStringExtra("firstName") != null){
            txtFirstName.setText(getIntent().getStringExtra("firstName"));
        }
        if(getIntent().getStringExtra("surname") != null){
            txtSurname.setText(getIntent().getStringExtra("surname"));
        }

        setBtnLoadProfileImageRegisterOnClick();
        setBtnBackToRegisterUsernameOnClick();
        setBtnLoadLoginOnClick();
    }

    @Override
    public void onBackPressed() {

        btnBackToRegisterUsername.callOnClick();
    }


    private void setBtnBackToRegisterUsernameOnClick(){
        btnBackToRegisterUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterPersonalInfoActivity.this, RegisterUsernameActivity.class);
                intent.putExtra("email", mEmail);
                intent.putExtra("username", mUsername);
                RegisterPersonalInfoActivity.this.startActivity(intent);
                return;
            }
        });
    }


    private void setBtnLoadProfileImageRegisterOnClick(){
        btnLoadProfileImageRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = formatting.removeEndingSpaceFromString(txtFirstName.getText().toString());
                String surname = formatting.removeEndingSpaceFromString(txtSurname.getText().toString());
                if(checkInputsValidity.isNameValid(firstName + surname)){
                    Intent intent = new Intent(RegisterPersonalInfoActivity.this, RegisterProfileImageActivity.class);
                    intent.putExtra("password", mPassword);
                    intent.putExtra("email", mEmail);
                    intent.putExtra("username", mUsername);
                    intent.putExtra("firstName", firstName);
                    intent.putExtra("surname", surname);
                    RegisterPersonalInfoActivity.this.startActivity(intent);
                    return;
                }
            }
        });
    }

    private void setBtnLoadLoginOnClick(){
        btnLoadLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterPersonalInfoActivity.this, LoginActivity.class);
                RegisterPersonalInfoActivity.this.startActivity(intent);
            }
        });
    }
}