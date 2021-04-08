package messaging.app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MessageReceivedServiceNotification {

    Context mContext;

    public MessageReceivedServiceNotification(Context context) {
        this.mContext = context;
    }

    public void sendNotification(String notificationMessage) {

        //notify user
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel =
                    new NotificationChannel("n", "n",
                            NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = mContext.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, "n")
                .setContentText(notificationMessage)
                .setSmallIcon(R.drawable.messages_shadow_icon)
                .setAutoCancel(true);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(mContext);
        managerCompat.notify(999, builder.build());
    }
}
