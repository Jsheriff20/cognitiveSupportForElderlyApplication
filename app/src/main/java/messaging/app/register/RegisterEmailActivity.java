package messaging.app.register;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import messaging.app.CheckInputsValidity;
import messaging.app.ManagingActivityPreview;
import messaging.app.contactingFirebase.QueryingDatabase;
import messaging.app.login.LoginActivity;
import messaging.app.R;

public class RegisterEmailActivity extends AppCompatActivity {

    EditText txtEmail;
    Button btnLoadPasswordRegister;
    Button btnLoadLogin;

    CheckInputsValidity mCheckInputsValidity = new CheckInputsValidity(this);
    ManagingActivityPreview mManagingActivityPreview = new ManagingActivityPreview();
    QueryingDatabase mQueryingDatabase = new QueryingDatabase(null);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_email);


        txtEmail = findViewById(R.id.txtEmail);
        btnLoadPasswordRegister = findViewById(R.id.btnLoadPasswordRegister);
        btnLoadLogin = findViewById(R.id.btnLoadLogin);

        //if user has selected "Back" then this information will be displayed
        if (getIntent().getStringExtra("email") != null) {
            String email = getIntent().getStringExtra("email");
            txtEmail.setText(email);
        } else {
            txtEmail.setText("");
        }

        setBtnLoadPasswordRegisterOnClick();
        setBtnLoadLoginOnClick();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RegisterEmailActivity.this, LoginActivity.class);
        RegisterEmailActivity.this.startActivity(intent);
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            mManagingActivityPreview.hideSystemUI(getWindow().getDecorView());
        }
    }


    private void setBtnLoadPasswordRegisterOnClick() {
        btnLoadPasswordRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = txtEmail.getText().toString().trim();
                if (mCheckInputsValidity.isEmailValid(email)) {
                    mQueryingDatabase.isEmailAvailable(email, new QueryingDatabase.OnEmailCheckListener() {
                        @Override
                        public void onSuccess(boolean isRegistered) {

                            if (isRegistered) {
                                Toast.makeText(RegisterEmailActivity.this,
                                        "Email already linked with another account",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Intent intent = new Intent(RegisterEmailActivity.this,
                                        RegisterPasswordActivity.class);
                                intent.putExtra("email", email);
                                RegisterEmailActivity.this.startActivity(intent);
                                return;
                            }
                        }
                    });
                }
            }
        });
    }


    private void setBtnLoadLoginOnClick() {
        btnLoadLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterEmailActivity.this, LoginActivity.class);
                RegisterEmailActivity.this.startActivity(intent);
            }
        });
    }
}