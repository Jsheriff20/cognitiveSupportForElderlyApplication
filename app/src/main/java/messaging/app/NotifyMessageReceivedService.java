package messaging.app;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import messaging.app.contactingFirebase.QueryingDatabase;

public class NotifyMessageReceivedService extends Service {

    QueryingDatabase mQueryingDatabase = new QueryingDatabase(null);

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mQueryingDatabase.listenForReceivedMessage(getApplicationContext());
        mQueryingDatabase.listenForReceivedStoryMessage(getApplicationContext());


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
