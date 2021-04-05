package messaging.app.settings.alarms;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import messaging.app.Formatting;
import messaging.app.R;
import messaging.app.contactingFirebase.ManagingReminders;

public class CreateNewReminder extends AppCompatActivity {

    Button btnCreateReminder;
    ImageButton btnCancelSettingReminder;
    EditText txtMedication;
    Spinner spnFrequency;
    android.widget.TimePicker tpkTime;

    String mFrequency;
    String mTime;
    int mIntentID = 0;

    AlarmManager alarmManager;

    ManagingReminders managingReminders = new ManagingReminders();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_reminder);

        btnCreateReminder = findViewById(R.id.btnCreateReminder);
        btnCancelSettingReminder = findViewById(R.id.btnCancelSettingReminder);
        txtMedication = findViewById(R.id.txtMedication);
        tpkTime = findViewById(R.id.tpkTime);
        spnFrequency = findViewById(R.id.spnFrequency);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        setBtnCreateReminderOnClick();
        setBtnCancelSettingReminderOnClick();
        setupFrequencySpinner();
        tpkTime.setIs24HourView(true);
    }


    private void setupFrequencySpinner() {
        List<String> frequencyOptions = new ArrayList<>(
                Arrays.asList(
                        "Daily", "Monday",
                        "Tuesday", "Wednesday", "Thursday",
                        "Friday", "Saturday", "Sunday"
                ));

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(CreateNewReminder.this, R.layout.spinner_item, frequencyOptions);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spnFrequency.setAdapter(arrayAdapter);
        spnFrequency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position,
                                       long id) {
                mFrequency = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    //a spinner option alternative for the user instead of the time picker
//    private void setupTimeSpinner(){
//        List<String> timeOptions = new ArrayList<>(
//                Arrays. asList(
//                        "00:00", "00:15", "00:30", "00:45", "01:00", "01:15", "01:30", "01:45",
//                        "02:00", "02:15", "02:30", "02:45", "03:00", "03:15", "03:30", "03:45",
//                        "04:00", "04:15", "04:30", "04:45", "05:00", "05:15", "05:30", "05:45",
//                        "06:00", "06:15", "06:30", "06:45", "07:00", "07:15", "07:30", "07:45",
//                        "08:00", "08:15", "08:30", "08:45", "09:00", "09:15", "09:30", "09:45",
//                        "10:00", "10:15", "10:30", "10:45", "11:00", "11:15", "11:30", "11:45",
//                        "12:00", "12:15", "12:30", "12:45", "13:00", "13:15", "13:30", "13:45",
//                        "14:00", "14:15", "14:30", "14:45", "15:00", "15:15", "15:30", "15:45",
//                        "16:00", "16:15", "16:30", "16:45", "17:00", "17:15", "17:30", "17:45",
//                        "18:00", "18:15", "18:30", "18:45", "19:00", "19:15", "19:30", "19:45",
//                        "20:00", "20:15", "20:30", "20:45", "21:00", "21:15", "21:30", "21:45",
//                        "22:00", "22:15", "22:30", "22:45", "23:00", "23:15", "23:30", "23:45",
//                        "00:00"
//                ));
//
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(CreateNewReminder.this, R.layout.spinner_item, timeOptions);
//        arrayAdapter.setDropDownViewResource(R.layout.spinner_item);
//        spnTime.setAdapter(arrayAdapter);
//        spnTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position,
//                                       long id) {
//                mTime = parent.getItemAtPosition(position).toString();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
//    }


    private void setBtnCancelSettingReminderOnClick() {
        btnCancelSettingReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateNewReminder.this, ManageRemindersActivity.class);
                CreateNewReminder.this.startActivity(intent);
            }
        });
    }

    private void setBtnCreateReminderOnClick() {
        btnCreateReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String medicationName = txtMedication.getText().toString();

                int hour, minute;
                hour = tpkTime.getHour();
                minute = tpkTime.getMinute();

                String strHour = Integer.toString(hour);
                String strMin = Integer.toString(minute);

                if(hour < 10){
                    strHour = "0" + Integer.toString(hour);
                }
                if(minute < 10){
                    strMin = "0" + Integer.toString(minute);
                }

                mTime = strHour + ":" + strMin;

                if (medicationName.length() < 1) {
                    Toast.makeText(CreateNewReminder.this, "Please enter a name for your medication", Toast.LENGTH_SHORT).show();
                } else {
                    //create a reminder service
                    createReminderService(hour, minute, medicationName, mFrequency);
                }
            }
        });
    }

    private void createReminderService(int hour, int minute, String medicationName, String frequency){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        Intent intent = new Intent(CreateNewReminder.this, AlarmReceiver.class);
        intent.putExtra("medication", medicationName);
        intent.putExtra("frequency", frequency);

        //get a unique id for the pending intent broadcast

        managingReminders.getNewIntentID(new ManagingReminders.OnGetNewIntentIDListener() {
            @Override
            public void onSuccess(int newIntentID) {

                if(newIntentID == -1){
                    Toast.makeText(CreateNewReminder.this, "Failed to set reminder", Toast.LENGTH_SHORT).show();
                }
                else{
                    mIntentID = newIntentID;

                    //create pending intent to delay the intent until the reminder time is reached
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(CreateNewReminder.this,
                            newIntentID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    //86,400,000 = 1 day. every day it will repeat and check if it needs to remind the user
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 86400000, pendingIntent);

                    //store details about the reminder
                    ReminderDetails reminderDetails = new ReminderDetails(medicationName,
                            mFrequency, mTime, null, mIntentID);
                    managingReminders.storeNewReminder(reminderDetails);

                    //go to the list of reminders
                    Intent intent = new Intent(CreateNewReminder.this, ManageRemindersActivity.class);
                    CreateNewReminder.this.startActivity(intent);
                }
            }
        });
    }
}