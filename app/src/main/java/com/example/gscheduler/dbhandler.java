package com.example.gscheduler;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import com.example.gscheduler.AlarmReceiver;

import java.util.ArrayList;
import java.util.HashMap;

public class dbhandler extends SQLiteOpenHelper {
    private static final int DB_Version = 1;
    private static final String DB_Name = "gscheduler";
    private static final String Table_Name = "tblsched";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "schedtitle";
    private static final String KEY_DATE = "scheddate";
    private static final String KEY_TIME = "schedtime";
    private static final String KEY_INFO = "schedinfo";

    public dbhandler(Context context) {
        super(context, DB_Name, null, DB_Version);
    }

    // Table creation
    @Override
    public void onCreate(SQLiteDatabase db) {
        String Create_Table = "CREATE TABLE " + Table_Name + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NAME + " TEXT,"
                + KEY_INFO + " TEXT,"
                + KEY_DATE + " TEXT,"
                + KEY_TIME + " TEXT" + ")";
        db.execSQL(Create_Table);
    }

    // Deletes old table if it exists
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Table_Name);
        onCreate(db);
    }

    // Save new record
    public int savenewsched(String title, String date, String info, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues newvalues = new ContentValues();
        newvalues.put(KEY_NAME, title);
        newvalues.put(KEY_DATE, date);
        newvalues.put(KEY_TIME, time);
        newvalues.put(KEY_INFO, info);
        long rowID = db.insert(Table_Name, null, newvalues);
        int scheduleId = (int) rowID;
        db.close();
        return scheduleId;
    }

    // Load data
    @SuppressLint("Range")
    public ArrayList<HashMap<String, String>> getSchedules() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> schedList = new ArrayList<>();
        String query = "SELECT id,schedtitle,scheddate,schedtime,schedinfo FROM " + Table_Name;
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            HashMap<String, String> sched = new HashMap<>();
            sched.put("id", cursor.getString(cursor.getColumnIndex(KEY_ID)));
            sched.put("schedtitle", cursor.getString(cursor.getColumnIndex(KEY_NAME)));
            sched.put("scheddate", cursor.getString(cursor.getColumnIndex(KEY_DATE)));
            sched.put("schedtime", cursor.getString(cursor.getColumnIndex(KEY_TIME)));
            sched.put("schedinfo", cursor.getString(cursor.getColumnIndex(KEY_INFO)));
            schedList.add(sched);
        }
        return schedList;
    }
    //search
    @SuppressLint("Range")
    public ArrayList<HashMap<String, String>> searchByDate(String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> schedList = new ArrayList<>();
        String query = "SELECT id, schedtitle, scheddate, schedtime FROM " + Table_Name + " WHERE " + KEY_DATE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{date});

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> sched = new HashMap<>();
                sched.put("id", cursor.getString(cursor.getColumnIndex(KEY_ID)));
                sched.put("schedtitle", cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                sched.put("scheddate", cursor.getString(cursor.getColumnIndex(KEY_DATE)));
                sched.put("schedtime", cursor.getString(cursor.getColumnIndex(KEY_TIME)));
                schedList.add(sched);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return schedList;
    }
    //delete data
    public void deleteSchedule(int scheduleId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Table_Name, KEY_ID + "=?", new String[]{String.valueOf(scheduleId)});
        db.close();
    }

    // Method to schedule an alarm
    public static void scheduleAlarm(Context context, int scheduleId, String scheduleName, String scheduleNotes, long alarmTime) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("schedule_id", scheduleId);
        intent.putExtra("schedule_name", scheduleName);
        intent.putExtra("schedule_notes", scheduleNotes);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getBroadcast(context, scheduleId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
        }
    }

    // Method to cancel an alarm
    public static void cancelAlarm(Context context, int scheduleId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, scheduleId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }

        // Remove the corresponding notification
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancel(scheduleId);
        }
    }
}
