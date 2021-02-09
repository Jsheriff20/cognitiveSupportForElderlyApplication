package messaging.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterPersonalInfoActivity extends AppCompatActivity {

    EditText txtFirstName;
    EditText txtSurname;
    Button btnLoadProfileImageRegister;
    Button btnLoadLogin;
    Button btnBackToRegisterPassword;
    CheckInputsValidity checkInputsValidity = new CheckInputsValidity(this);

    String mEmail;
    String mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_personal_info);

        txtFirstName = findViewById(R.id.txtFirstName);
        txtSurname = findViewById(R.id.txtSurname);
        btnLoadProfileImageRegister = (Button) findViewById(R.id.btnLoadProfileImageRegister);
        btnLoadLogin = findViewById(R.id.btnLoadLogin);
        btnBackToRegisterPassword = findViewById(R.id.btnBackToRegisterPassword);

        //data received from previous activity
        mEmail = getIntent().getStringExtra("email");
        mPassword = getIntent().getStringExtra("password");


        //if user has selected "Back" then this information will be displayed
        if(getIntent().getStringExtra("firstName") != null){
            txtFirstName.setText(getIntent().getStringExtra("firstName"));
        }
        if(getIntent().getStringExtra("surname") != null){
            txtSurname.setText(getIntent().getStringExtra("surname"));
        }

        setBtnLoadProfileImageRegisterOnClick();
        setBtnBackToRegisterPasswordOnClick();
        setBtnLoadLoginOnClick();
    }



    private void setBtnBackToRegisterPasswordOnClick(){
        btnBackToRegisterPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterPersonalInfoActivity.this, RegisterPasswordActivity.class);
                intent.putExtra("email", mEmail);
                RegisterPersonalInfoActivity.this.startActivity(intent);
                return;
            }
        });
    }


    private void setBtnLoadProfileImageRegisterOnClick(){
        btnLoadProfileImageRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkInputsValidity.isNameValid(txtFirstName.getText().toString()+txtSurname.getText().toString())){
                    Intent intent = new Intent(RegisterPersonalInfoActivity.this, RegisterProfileImageActivity.class);
                    intent.putExtra("password", mPassword);
                    intent.putExtra("email", mEmail);
                    intent.putExtra("firstName", txtFirstName.getText().toString());
                    intent.putExtra("surname", txtSurname.getText().toString());
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