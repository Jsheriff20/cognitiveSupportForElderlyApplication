package messaging.app.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import messaging.app.R;
import messaging.app.contactingFirebase.ManagingAccounts;

public class ResetPasswordActivity extends AppCompatActivity {

    EditText txtEmail;
    Button btnResetPassword;
    Button btnLoadLogin;

    ManagingAccounts managingAccounts = new ManagingAccounts(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        txtEmail = findViewById(R.id.txtResetPasswordEmail);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        btnLoadLogin = findViewById(R.id.btnLoadLogin);

        setBtnLoadLoginOnClick();
        setBtnResetPasswordOnClick();
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
        ResetPasswordActivity.this.startActivity(intent);
    }


    private void setBtnLoadLoginOnClick(){
        btnLoadLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                ResetPasswordActivity.this.startActivity(intent);
            }
        });
    }

    private void setBtnResetPasswordOnClick(){
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set user email to reset password and load login if successful;
                managingAccounts.resetPassword(txtEmail.getText().toString());
            }
        });
    }
}