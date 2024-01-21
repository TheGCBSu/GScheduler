package com.example.gscheduler;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class schedDetails extends AppCompatActivity {

    Intent intent;
    TextView title,date,time,id,notes;
    Button del;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Schedule Details");
        setContentView(R.layout.sched_details);
        title=(TextView) findViewById(R.id.dname);
        date=(TextView) findViewById(R.id.ddate);
        time=(TextView) findViewById(R.id.dtime);
        notes=(TextView) findViewById(R.id.dnotes);
        id=(TextView) findViewById(R.id.dhiddenid);
        del=(Button) findViewById(R.id.dbutton);

        Intent intent = getIntent();
        title.setText(intent.getExtras().getString("title"));
        date.setText(intent.getExtras().getString("date"));
        time.setText(intent.getExtras().getString("time"));
        id.setText(intent.getExtras().getString("id"));
        notes.setText(intent.getExtras().getString("notes"));

        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbhandler db = new dbhandler(schedDetails.this);
                db.deleteSchedule(Integer.parseInt(id.getText().toString()));
                dbhandler.cancelAlarm(schedDetails.this,Integer.parseInt(id.getText().toString()));
                Intent nintent=new Intent(schedDetails.this,MainActivity.class);
                startActivity(nintent);
            }
        });
    }
}
