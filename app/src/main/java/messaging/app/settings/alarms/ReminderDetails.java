package messaging.app.settings.alarms;

public class ReminderDetails {
    String mMedicationName;
    String mFrequency;
    String mTime;
    String mReminderID;
    int mIntentID;

    public ReminderDetails() {
    }

    public ReminderDetails(String medicationName, String frequency, String time, String reminderID,
                           int intentID) {
        this.mMedicationName = medicationName;
        this.mFrequency = frequency;
        this.mTime = time;
        this.mReminderID = reminderID;
        this.mIntentID = intentID;
    }

    public String getmReminderID() {
        return mReminderID;
    }

    public void setmReminderID(String mReminderID) {
        this.mReminderID = mReminderID;
    }

    public int getmIntentID() {
        return mIntentID;
    }

    public void setmIntentID(int mIntentID) {
        this.mIntentID = mIntentID;
    }

    public String getmMedicationName() {
        return mMedicationName;
    }

    public void setmMedicationName(String mMedicationName) {
        this.mMedicationName = mMedicationName;
    }

    public String getmFrequency() {
        return mFrequency;
    }

    public void setmFrequency(String mFrequency) {
        this.mFrequency = mFrequency;
    }

    public String getmTime() {
        return mTime;
    }

    public void setmTime(String mTime) {
        this.mTime = mTime;
    }
}
