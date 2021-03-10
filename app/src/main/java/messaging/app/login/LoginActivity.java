 package messaging.app.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.Normalizer;

import messaging.app.CheckInputsValidity;
import messaging.app.ContactingFirebase;
import messaging.app.Formatting;
import messaging.app.NotifyMessageReceivedService;
import messaging.app.R;
import messaging.app.SelectAreaOfApplicationActivity;
import messaging.app.register.RegisterEmailActivity;
import messaging.app.register.RegisterProfileImageActivity;

public class LoginActivity extends AppCompatActivity {

    EditText txtEmail;
    EditText txtPassword;
    Button btnLogin;
    Button btnLoadRegister;
    TextView lblResetPassword;

    ContactingFirebase contactingFirebase = new ContactingFirebase(this);
    Formatting formatting = new Formatting();
    CheckInputsValidity checkInputsValidity = new CheckInputsValidity(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtEmail = findViewById(R.id.txtLoginEmail);
        txtPassword = findViewById(R.id.txtLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnLoadRegister = findViewById(R.id.btnLoadRegister);
        lblResetPassword = findViewById(R.id.lblResetPassword);

        setBtnLoadRegisterOnClick();
        setBtnLoginOnClick();
        setLblResetPasswordOnClick();
    }


    @Override
    public void onBackPressed() {
        //do nothing
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            LoginActivity.this.startActivity(new Intent(LoginActivity.this, SelectAreaOfApplicationActivity.class));
        }
    }

    private void setBtnLoadRegisterOnClick(){
        btnLoadRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterEmailActivity.class);
                LoginActivity.this.startActivity(intent);
            }
        });
    }


    public void setLblResetPasswordOnClick() {
        lblResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                LoginActivity.this.startActivity(intent);
            }
        });
    }


    private void setBtnLoginOnClick(){
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = txtEmail.getText().toString().trim();
                if(!checkInputsValidity.isEmailValid(email)){
                    return;
                }
                else if(txtPassword.getText().toString().length() < 1){
                    Toast.makeText(LoginActivity.this, "Please enter a password", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    boolean loginSuccess = contactingFirebase.loginUser(email, txtPassword.getText().toString());

                }
            }
        });
    }
}