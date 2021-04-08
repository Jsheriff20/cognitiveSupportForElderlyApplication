package messaging.app.settings.alarms;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import messaging.app.R;

public class AlarmRingtoneService extends Service {

    private static final String NOTIFICATION_CHANNEL_ID = "AlarmRingtoneService";
    MediaPlayer mMediaPlayer;
    AudioManager mAudioManager;
    Vibrator mVibrator;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        super.onStartCommand(intent, flags, startId);

        //set audio volume to max
        mAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

        //vibrate the phone instantly for 2 seconds and then stop for 1 second and repeat
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 2000, 1000};
        mVibrator.vibrate(pattern, -1);

        //only play the sound once to notify user
        mMediaPlayer = MediaPlayer.create(this, R.raw.default_alarm_clock_sound);
        mMediaPlayer.start();

        //create notification
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                "Medication Reminder", NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,
                NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("Time to take your " + intent.getStringExtra("medication"))
                .setPriority(NotificationManager.IMPORTANCE_MAX)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
