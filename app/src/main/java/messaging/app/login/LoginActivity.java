package messaging.app.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.text.Normalizer;

import messaging.app.ContactingFirebase;
import messaging.app.Formatting;
import messaging.app.R;
import messaging.app.register.RegisterEmailActivity;

public class LoginActivity extends AppCompatActivity {

    EditText txtEmail;
    EditText txtPassword;
    Button btnLogin;
    Button btnLoadRegister;

    ContactingFirebase contactingFirebase = new ContactingFirebase(this);
    Formatting formatting = new Formatting();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtEmail = findViewById(R.id.txtLoginEmail);
        txtPassword = findViewById(R.id.txtLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnLoadRegister = findViewById(R.id.btnLoadRegister);

        setBtnLoadRegisterOnClick();
        setBtnLoginOnClick();
    }


    private void setBtnLoadRegisterOnClick(){
        btnLoadRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterEmailActivity.class);
                LoginActivity.this.startActivity(intent);
                return;
            }
        });
    }


    private void setBtnLoginOnClick(){
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactingFirebase.loginUser(formatting.removeEndingSpaceFromString(txtEmail.getText().toString()), txtPassword.getText().toString());
            }
        });
    }
}