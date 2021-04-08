package messaging.app.settings.alarms;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import messaging.app.R;
import messaging.app.contactingFirebase.ManagingReminders;

public class RemindersAdapter extends RecyclerView.Adapter<RemindersAdapter.ViewHolder> {

    Context mContext;
    private List<ReminderDetails> mReminderDetailsList;
    ManagingReminders mManagingReminders = new ManagingReminders();

    public RemindersAdapter(List<ReminderDetails> reminderDetailsList, Context context) {
        this.mReminderDetailsList = reminderDetailsList;
        this.mContext = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.current_reminder_row,
                parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        ReminderDetails reminderDetails = mReminderDetailsList.get(position);
        holder.lblMedicationName.setText(reminderDetails.getmMedicationName());
        holder.lblFrequency.setText(reminderDetails.getmFrequency());
        holder.lblReminderTime.setText(reminderDetails.getmTime());

        holder.reminderID = reminderDetails.getmReminderID();
        holder.intentID = reminderDetails.getmIntentID();

        holder.btnRemoveReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mManagingReminders.removeReminder(holder.reminderID);
                mReminderDetailsList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mReminderDetailsList.size());


                AlarmManager manager =
                        (AlarmManager) mContext.getSystemService(mContext.ALARM_SERVICE);
                Intent alarmIntent = new Intent(mContext, AlarmReceiver.class);

                //overwriting the alarm
                PendingIntent pendingIntent =
                        PendingIntent.getBroadcast(mContext, holder.intentID, alarmIntent, 0);

                //cancelling the new set alarm alarm
                manager.cancel(pendingIntent);

            }
        });


    }


    @Override
    public int getItemCount() {
        return mReminderDetailsList.size();
    }


    //friend row layout
    public class ViewHolder extends RecyclerView.ViewHolder {
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
