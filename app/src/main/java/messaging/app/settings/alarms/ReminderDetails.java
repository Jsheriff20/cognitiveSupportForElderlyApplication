package messaging.app.settings.alarms;

public class ReminderDetails {
    String medicationName;
    String frequency;
    String time;
    String reminderID;
    int intentID;

    public ReminderDetails() {
    }

    public ReminderDetails(String medicationName, String frequency, String time, String reminderID, int intentID) {
        this.medicationName = medicationName;
        this.frequency = frequency;
        this.time = time;
        this.reminderID = reminderID;
        this.intentID = intentID;
    }

    public String getReminderID() {
        return reminderID;
    }

    public void setReminderID(String reminderID) {
        this.reminderID = reminderID;
    }

    public int getIntentID() {
        return intentID;
    }

    public void setIntentID(int intentID) {
        this.intentID = intentID;
    }

    public String getMedicationName() {
        return medicationName;
    }

    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
