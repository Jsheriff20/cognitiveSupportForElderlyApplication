package messaging.app.settings.alarms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent serviceIntent = new Intent(context, AlarmRingtoneService.class);
        serviceIntent.putExtras(intent.getExtras());


        //check if today is the selected day
        if (frequencyMatch(intent.getStringExtra("frequency"))) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent);
            } else {
                context.startService(serviceIntent);
            }
        }
    }


    private boolean frequencyMatch(String frequency) {

        if (frequency.equals("Daily")) {
            return true;
        }

        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        String currentDayOfWeek = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.getTime());


        if (frequency.equals(currentDayOfWeek)) {
            return true;
        } else {
            return false;
        }
    }
}
