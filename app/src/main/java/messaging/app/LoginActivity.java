package messaging.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    EditText txtEmail;
    EditText txtPassword;
    Button btnLogin;
    Button btnLoadRegister;

    ContactingFirebase contactingFirebase = new ContactingFirebase(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
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
                contactingFirebase.LoginUser(txtEmail.getText().toString(), txtPassword.getText().toString());
            }
        });
    }
}