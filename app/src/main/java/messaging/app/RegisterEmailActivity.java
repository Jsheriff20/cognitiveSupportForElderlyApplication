package messaging.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Pattern;

public class RegisterEmailActivity extends AppCompatActivity {

    EditText txtEmail;
    Button btnLoadPasswordRegister;
    Button btnLoadLogin;
    CheckInputsValidity checkInputsValidity = new CheckInputsValidity(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_email);

        txtEmail = findViewById(R.id.txtEmail);
        btnLoadPasswordRegister = findViewById(R.id.btnLoadPasswordRegister);
        btnLoadLogin = findViewById(R.id.btnLoadLogin);

        setBtnLoadPasswordRegisterOnClick();
        setBtnLoadLoginOnClick();
    }

    private void setBtnLoadPasswordRegisterOnClick(){
        btnLoadPasswordRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkInputsValidity.isEmailValid(txtEmail.getText().toString())) {
                    Intent intent = new Intent(RegisterEmailActivity.this, RegisterPasswordActivity.class);
                    intent.putExtra("email", txtEmail.getText().toString());
                    RegisterEmailActivity.this.startActivity(intent);
                    return;
                }
            }
        });
    }


    private void setBtnLoadLoginOnClick(){
        btnLoadLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterEmailActivity.this, LoginActivity.class);
                RegisterEmailActivity.this.startActivity(intent);
            }
        });
    }
}