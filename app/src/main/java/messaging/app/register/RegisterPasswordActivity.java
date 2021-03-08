package messaging.app.register;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import messaging.app.CheckInputsValidity;
import messaging.app.login.LoginActivity;
import messaging.app.R;

public class RegisterPasswordActivity extends AppCompatActivity {

    EditText txtPassword;
    EditText txtConfirmPassword;
    Button btnLoadRegisterUsername;
    Button btnLoadLogin;
    Button btnBackToRegisterEmail;
    CheckInputsValidity checkInputsValidity = new CheckInputsValidity(this);

    //data received from previous activity
    String mEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_password);

        txtPassword = findViewById(R.id.txtPassword);
        txtConfirmPassword = findViewById(R.id.txtConfirmPassword);
        btnLoadRegisterUsername = findViewById(R.id.btnLoadRegisterUsername);
        btnLoadLogin = findViewById(R.id.btnLoadLogin);
        btnBackToRegisterEmail = findViewById(R.id.btnBackToRegisterEmail);


        setBtnLoadPersonInfoOnClick();
        setBtnBackToRegisterEmailOnClick();
        setBtnLoadLoginOnClick();

        mEmail = getIntent().getStringExtra("email");
    }



    @Override
    public void onBackPressed() {

        btnBackToRegisterEmail.callOnClick();
    }


    private void setBtnLoadPersonInfoOnClick(){
        btnLoadRegisterUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkRegisterPasswordIsValid()) {
                    Intent intent = new Intent(RegisterPasswordActivity.this, RegisterUsernameActivity.class);
                    intent.putExtra("password", txtPassword.getText().toString());
                    intent.putExtra("email", mEmail);
                    RegisterPasswordActivity.this.startActivity(intent);
                    return;
                }
            }
        });
    }


    private void setBtnBackToRegisterEmailOnClick(){
        btnBackToRegisterEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterPasswordActivity.this, RegisterEmailActivity.class);
                intent.putExtra("email", mEmail);
                RegisterPasswordActivity.this.startActivity(intent);
                return;
            }
        });
    }


    private boolean checkRegisterPasswordIsValid(){
        if(txtPassword.getText().toString().equals(txtConfirmPassword.getText().toString())){
            if(checkInputsValidity.isPasswordValid(txtPassword.getText().toString())){
                return true;
            }
            return false;
        }
        Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
        return false;
    }


    private void setBtnLoadLoginOnClick(){
        btnLoadLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterPasswordActivity.this, LoginActivity.class);
                RegisterPasswordActivity.this.startActivity(intent);
            }
        });
    }
}