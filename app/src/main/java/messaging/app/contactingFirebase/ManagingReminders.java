package messaging.app.contactingFirebase;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

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



    public interface OnGetNewIntentIDListener {
        void onSuccess(int newIntentID);

    }

    public void getNewIntentID(OnGetNewIntentIDListener listener) {

        String usersUUID = queryingDatabase.getCurrentUsersUUID();

        DatabaseReference databaseRef = mDatabase.getReference("reminders/" + usersUUID);

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Integer> currentIntentIDs = new ArrayList<>();

                //get all currently used intent IDs
                for(DataSnapshot reminder : snapshot.getChildren()){
                    for(DataSnapshot alarmDetails : reminder.getChildren()){
                        if(alarmDetails.getKey().equals("intentID")){
                            long intentID = (long) alarmDetails.getValue();
                            currentIntentIDs.add((int) intentID);
                        }
                    }
                }

                //find an available intent ID
                for(int i = 0; i <1000 ; i++){
                    if(!currentIntentIDs.contains(i)){
                        listener.onSuccess(i);
                        return;
                    }
                }

                listener.onSuccess(-1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
