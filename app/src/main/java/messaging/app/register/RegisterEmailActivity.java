package messaging.app.register;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import messaging.app.CheckInputsValidity;
import messaging.app.ContactingFirebase;
import messaging.app.login.LoginActivity;
import messaging.app.R;

public class RegisterEmailActivity extends AppCompatActivity {

    EditText txtEmail;
    Button btnLoadPasswordRegister;
    Button btnLoadLogin;
    CheckInputsValidity checkInputsValidity = new CheckInputsValidity(this);
    ContactingFirebase contactingFirebase = new ContactingFirebase(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_email);



        txtEmail = findViewById(R.id.txtEmail);
        btnLoadPasswordRegister = findViewById(R.id.btnLoadPasswordRegister);
        btnLoadLogin = findViewById(R.id.btnLoadLogin);

        //if user has selected "Back" then this information will be displayed
        if(getIntent().getStringExtra("email") != null ){
            String email = getIntent().getStringExtra("email");
            txtEmail.setText(email);
        }

        setBtnLoadPasswordRegisterOnClick();
        setBtnLoadLoginOnClick();
    }

    private void setBtnLoadPasswordRegisterOnClick(){
        btnLoadPasswordRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkInputsValidity.isEmailValid(txtEmail.getText().toString())) {
                    contactingFirebase.isEmailAvailable(txtEmail.getText().toString(),new ContactingFirebase.OnEmailCheckListener(){
                        @Override
                        public void onSuccess(boolean isRegistered){

                            if(isRegistered){
                                Toast.makeText(RegisterEmailActivity.this, "Email already linked with another account", Toast.LENGTH_SHORT).show();
                            } else {
                                Intent intent = new Intent(RegisterEmailActivity.this, RegisterPasswordActivity.class);
                                intent.putExtra("email", txtEmail.getText().toString());
                                RegisterEmailActivity.this.startActivity(intent);
                                return;
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
                Intent intent = new Intent(RegisterEmailActivity.this, LoginActivity.class);
                RegisterEmailActivity.this.startActivity(intent);
            }
        });
    }
}