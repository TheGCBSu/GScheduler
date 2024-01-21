package com.example.gscheduler;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "AlarmChannel";
    private static final int NOTIFICATION_ID = 1;

    private Ringtone ringtone;
    private Handler handler;


    @Override
    public void onReceive(Context context, Intent intent) {
        // Retrieve the schedule details from the intent extras
        int scheduleId = intent.getIntExtra("schedule_id", 0);
        dbhandler dbHandler = new dbhandler(context);
        dbHandler.deleteSchedule(scheduleId);
        String scheduleName = intent.getStringExtra("schedule_name");
        String scheduleNotes = intent.getStringExtra("schedule_notes");

        // Display a notification as a banner
        showNotification(context, scheduleName, scheduleNotes);

        // Play the alarm sound
        playAlarmSound(context);
    }

    private void showNotification(Context context, String title, String message) {

        // Create a notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Alarm Channel", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Alarm Notification Channel");
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);

            // Set the custom sound
            Uri customSoundUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.notif_sound);
            channel.setSound(customSoundUri, null);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        // Show the notification
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void playAlarmSound(Context context) {
        // Create and start the ringtone with the default alarm sound
        Uri alarmSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        ringtone = RingtoneManager.getRingtone(context, alarmSoundUri);
        if (ringtone != null) {
            ringtone.play();

            // Schedule to stop the playback after 5 seconds
            handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopAlarmSound();
                }
            }, 5000); // 5000 milliseconds = 5 seconds
        }
    }

    private void stopAlarmSound() {
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
            ringtone = null;
        }

        // Remove any pending callbacks from the handler
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }
}
