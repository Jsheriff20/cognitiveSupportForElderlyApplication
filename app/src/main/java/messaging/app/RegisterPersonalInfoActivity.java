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
        Log.d("Test", "btnLoadProfileImageRegister " + btnLoadProfileImageRegister);
        Log.d("Test", "findViewById(R.id.btnLoadProfileImageRegister) " + (Button) findViewById(R.id.btnLoadProfileImageRegister));
        btnLoadLogin = findViewById(R.id.btnLoadLogin);

        //data received from previous activity
        mEmail = getIntent().getStringExtra("email");
        mPassword = getIntent().getStringExtra("password");

        setBtnLoadProfileImageRegisterOnClick();
        setBtnLoadLoginOnClick();
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