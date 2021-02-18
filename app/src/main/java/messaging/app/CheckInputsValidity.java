package messaging.app;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.regex.Pattern;

public class CheckInputsValidity {
    Context context;

    public CheckInputsValidity(Context context) {
        this.context = context;
    }

    public boolean isEmailValid(String email){
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);

        if(!pat.matcher(email).matches() || email == null || email.length() < 2){
            Toast.makeText(context, "Email is invalid", Toast.LENGTH_SHORT).show();
            return false;
        }
        else{
            return true;
        }
    }

    public boolean isPasswordValid(String password) {
        String overallPattern = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}";
        String missingFromPassword = "Your password is missing: ";

        Log.d("Test", "isPasswordValid: " + password);
        if (password.length() < 8) {
            Toast.makeText(context, "Your password must be at least 8 characters long", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!password.matches("(?=\\S+$).{8,}")) {
            Toast.makeText(context, "Your password cannot contain any whitespace", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if (!password.matches("(?=.*[0-9]).{8,}")) {
                missingFromPassword += "a digit, ";
            }
            if (!password.matches("(?=.*[a-z]).{8,}")) {
                missingFromPassword += "a lower case character, ";
            }
            if (!password.matches("(?=.*[A-Z]).{8,}")) {
                missingFromPassword += "a upper case character, ";
            }
            if (!missingFromPassword.equals("Your password is missing: ")) {
                Toast.makeText(context, missingFromPassword, Toast.LENGTH_SHORT).show();
            }
            Log.d("Test", "isPasswordValid: " + password.matches(overallPattern));
            return password.matches(overallPattern);
        }
    }


    public boolean isNameValid(String name){
        if(name.length() < 2){
            Toast.makeText(context, "Name is too short", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(name.length() > 20){
            Toast.makeText(context, "Name is too long", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (!name.matches("(?=\\S+$).{2,}")) {
            Toast.makeText(context, "Your name cannot contain any whitespace", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (name.matches("(?=.*[0-9]).{2,}")) {
            Toast.makeText(context, "Your name cannot contain any digits", Toast.LENGTH_SHORT).show();
            return false;
        }
        else{
            return true;
        }
    }


    public boolean isUsernameValid(String username){
        if(username.length() < 2){
            Toast.makeText(context, "Username is too short", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(username.length() > 20){
            Toast.makeText(context, "Username is too long", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (!username.matches("(?=\\S+$).{2,}")) {
            Toast.makeText(context, "Your name cannot contain any whitespace", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (username.matches("(?=.*[@#$%^&+=]).{2,}")) {
            Toast.makeText(context, "Your name cannot contain any special symbols", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
