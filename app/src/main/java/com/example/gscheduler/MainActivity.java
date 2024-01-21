package com.example.gscheduler;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button news,views;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        news=(Button)findViewById(R.id.newsched);
        views=(Button) findViewById(R.id.viewsched);
        // Check if the permission is already granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null && !notificationManager.isNotificationPolicyAccessGranted()) {
                // Permission is not granted, so request it from the user
                Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                startActivity(intent);
            } else {

            }
        } else {

        }

        news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,NewSchedule.class));
            }
        });

        views.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,schedulesView.class));
            }
        });
    }
}