package messaging.app.contactingFirebase;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;

import messaging.app.settings.alarms.ReminderDetails;

public class ManagingReminders {

    FirebaseDatabase mDatabase;
    FirebaseStorage mStorage;
    QueryingDatabase queryingDatabase = new QueryingDatabase(null);

    public ManagingReminders() {
        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
    }

    public void storeNewReminder(ReminderDetails reminderDetails){

        String usersUUID = queryingDatabase.getCurrentUsersUUID();

        DatabaseReference databaseRef = mDatabase.getReference("reminders");

        Long tsLong = System.currentTimeMillis()/1000;
        String timeStamp = tsLong.toString();
        databaseRef.child(usersUUID + "/" + timeStamp).setValue(reminderDetails);
    }

    public void removeReminder(String ID){

        String usersUUID = queryingDatabase.getCurrentUsersUUID();

        DatabaseReference databaseRef = mDatabase.getReference("reminders/" + usersUUID);
        databaseRef.getRef().removeValue();

    }
}
