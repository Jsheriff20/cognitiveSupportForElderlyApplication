package messaging.app;


import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;

public class NotifyMessageReceivedService extends Service {
    ContactingFirebase contactingFirebase = new ContactingFirebase(this);
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        contactingFirebase.listenForReceivedMessage(getApplicationContext());
        contactingFirebase.listenForReceivedStoryMessage(getApplicationContext());


        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
