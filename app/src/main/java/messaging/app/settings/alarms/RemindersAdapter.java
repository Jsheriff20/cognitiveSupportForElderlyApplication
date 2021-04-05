package messaging.app.settings.alarms;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ExifInterface;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import messaging.app.AccountDetails;
import messaging.app.MediaManagement;
import messaging.app.R;
import messaging.app.contactingFirebase.ManagingReminders;
import messaging.app.contactingFirebase.QueryingDatabase;

public class RemindersAdapter extends RecyclerView.Adapter<RemindersAdapter.ViewHolder> {

    Context context;
    private List<ReminderDetails> reminderDetailsList;
    ManagingReminders managingReminders = new ManagingReminders();

    public RemindersAdapter(List<ReminderDetails> reminderDetailsList, Context context) {
        this.reminderDetailsList = reminderDetailsList;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.current_reminder_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        ReminderDetails reminderDetails = reminderDetailsList.get(position);
        holder.lblMedicationName.setText(reminderDetails.getMedicationName());
        holder.lblFrequency.setText(reminderDetails.getFrequency());
        holder.lblReminderTime.setText(reminderDetails.getTime());

        holder.reminderID = reminderDetails.getReminderID();
        holder.intentID = reminderDetails.getIntentID();

        holder.btnRemoveReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                managingReminders.removeReminder(holder.reminderID);
                reminderDetailsList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, reminderDetailsList.size());


                AlarmManager manager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
                Intent alarmIntent = new Intent(context, AlarmReceiver.class);

                //overwriting the alarm
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, holder.intentID, alarmIntent, 0);

                //cancelling the new set alarm alarm
                manager.cancel(pendingIntent);

            }
        });


    }


    @Override
    public int getItemCount() {
        return reminderDetailsList.size();
    }


    //friend row layout
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView lblMedicationName;
        TextView lblReminderTime;
        TextView lblFrequency;
        ImageButton btnRemoveReminder;
        ConstraintLayout reminderRowLayout;

        String reminderID;
        int intentID;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            lblMedicationName = itemView.findViewById(R.id.lblMedicationName);
            lblReminderTime = itemView.findViewById(R.id.lblReminderTime);
            lblFrequency = itemView.findViewById(R.id.lblFrequency);
            btnRemoveReminder = itemView.findViewById(R.id.btnRemoveReminder);
            reminderRowLayout = itemView.findViewById(R.id.reminderRowLayout);


            int SDK_INT = android.os.Build.VERSION.SDK_INT;
            if (SDK_INT > 8) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);

            }
        }

    }

}
