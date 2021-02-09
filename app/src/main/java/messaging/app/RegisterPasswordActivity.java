package messaging.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Pattern;

public class RegisterPasswordActivity extends AppCompatActivity {

    EditText txtPassword;
    EditText txtConfirmPassword;
    Button btnLoadPersonalInfoRegister;
    Button btnLoadLogin;
    CheckInputsValidity checkInputsValidity = new CheckInputsValidity(this);

    //data received from previous activity
    String mEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_password);

        txtPassword = findViewById(R.id.txtPassword);
        txtConfirmPassword = findViewById(R.id.txtConfirmPassword);
        btnLoadPersonalInfoRegister = findViewById(R.id.btnLoadPersonalInfoRegister);
        btnLoadLogin = findViewById(R.id.btnLoadLogin);

        setBtnLoadPersonInfoOnClick();
        setBtnLoadLoginOnClick();
        mEmail = getIntent().getStringExtra("email");
    }


    private void setBtnLoadPersonInfoOnClick(){
        btnLoadPersonalInfoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkRegisterPasswordIsValid()) {
                    Intent intent = new Intent(RegisterPasswordActivity.this, RegisterPersonalInfoActivity.class);
                    intent.putExtra("password", txtPassword.getText().toString());
                    intent.putExtra("email", mEmail);
                    RegisterPasswordActivity.this.startActivity(intent);
                    return;
                }
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