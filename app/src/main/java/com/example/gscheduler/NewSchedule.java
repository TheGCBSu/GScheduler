package com.example.gscheduler;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class NewSchedule extends AppCompatActivity {
    Button dpick,tpick,saveb;
    int hour,min;
    private DatePickerDialog datePickerDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Save New Schedule");
        setContentView(R.layout.newschedule);
        initdatepicker();
        dpick=(Button) findViewById(R.id.datepicker);
        dpick.setText(getdatenow());
        tpick=(Button) findViewById(R.id.timepicker);
        gettimenow();
        saveb=(Button)findViewById(R.id.dbutton);

        saveb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String time,date,title,details;
                time=tpick.getText().toString();
                date=dpick.getText().toString();
                String datesplit[]=date.split(" ");
                datesplit[0]=numMonth(datesplit[0]);
                date=datesplit[0]+"-"+datesplit[1]+"-"+datesplit[2];
                title=((TextView)findViewById(R.id.inName)).getText().toString();
                details=((TextView)findViewById(R.id.noteset)).getText().toString();
                if(!title.equals("")){
                    if(afterDate(date,time)){
                        dbhandler db = new dbhandler(NewSchedule.this);
                        int scheduleId = db.savenewsched(title, date, details, time); // Save and get the unique ID
                        Toast.makeText(NewSchedule.this, "Saved Successful!", Toast.LENGTH_SHORT).show();

                        long alarmTime = calculateAlarmTime(date, time);
                        String scheduleName = title;
                        String scheduleNotes = details;
                        dbhandler.scheduleAlarm(NewSchedule.this,scheduleId, scheduleName, scheduleNotes, alarmTime);

                        ((TextView) findViewById(R.id.inName)).setText("");
                        ((TextView) findViewById(R.id.noteset)).setText("");
                    }else{
                        Toast.makeText(NewSchedule.this,"Cannot Schedule for the Past",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(NewSchedule.this,"Need a name for schedule!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private long calculateAlarmTime(String date, String time) {
        try {
            String dateTimeString = date + " " + time;
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm", Locale.getDefault());
            Date selectedDateTime = sdf.parse(dateTimeString);
            long alarmTime = selectedDateTime.getTime();

            return alarmTime;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private boolean afterDate(String date1,String time1) {
        try {
            String string=date1+" "+time1;
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy kk:mm");
            Date current = new Date();
            Date compare = sdf.parse(string);
            if(compare.after(current)) {
                return true;
            }
            else{
                return false;
            }
        } catch(Exception e) {
            return false;
        }
    }


    private void gettimenow(){
        Calendar cal = Calendar.getInstance();
        hour=cal.get(Calendar.HOUR_OF_DAY);
        min=cal.get(Calendar.MINUTE);
        tpick.setText(hour+":"+min);
    }
    private String getdatenow(){
        Calendar cal = Calendar.getInstance();
        int year=cal.get(Calendar.YEAR);
        int month=cal.get(Calendar.MONTH);
        month=month+1;
        int day=cal.get(Calendar.DAY_OF_MONTH);
        return makestringdate(day,month,year);
    }
    private void initdatepicker(){
        DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month=month+1;
                String date=makestringdate(day,month,year);
                dpick.setText(date);
            }
        };
        Calendar cal = Calendar.getInstance();
        int year=cal.get(Calendar.YEAR);
        int month=cal.get(Calendar.MONTH);
        int day=cal.get(Calendar.DAY_OF_MONTH);

        int dia= AlertDialog.THEME_HOLO_LIGHT;
        datePickerDialog=new DatePickerDialog(this,dia, dateSetListener, year, month, day);
    }

    private String makestringdate(int day, int month, int year){
        return GetMonth(month)+" "+day+" "+year;
    }

    private String GetMonth(int month){
        String smonth="";
        switch (month){
            case 1: smonth="January"; break;
            case 2: smonth="February"; break;
            case 3: smonth="March"; break;
            case 4: smonth="April"; break;
            case 5: smonth="May"; break;
            case 6: smonth="June"; break;
            case 7: smonth="July"; break;
            case 8: smonth="August"; break;
            case 9: smonth="September"; break;
            case 10: smonth="October"; break;
            case 11: smonth="November"; break;
            case 12: smonth="December"; break;
        }
        return smonth;
    }

    private String numMonth(String month){
        String num="";
        switch(month){
            case "January": num="1"; break;
            case "February": num="2"; break;
            case "March": num="3"; break;
            case "April": num="4"; break;
            case "May": num="5"; break;
            case "June": num="6"; break;
            case "July": num="7"; break;
            case "August": num="8"; break;
            case "September": num="9"; break;
            case "October": num="10"; break;
            case "November": num="11"; break;
            case "December": num="12"; break;
        }
        return num;
    }

    public void opendatePicker(View view){
        datePickerDialog.show();
    }
    public void openTimePicker(View view){
        TimePickerDialog.OnTimeSetListener onTimeSetListener=new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int shour, int smin) {
                hour=shour;
                min=smin;
                tpick.setText(String.format(Locale.getDefault(),"%02d:%02d",hour,min));
            }
        };
        int style = AlertDialog.THEME_HOLO_DARK;
        TimePickerDialog timepd=new TimePickerDialog(this,style,onTimeSetListener,hour,min,true);

        timepd.setTitle("Select Time");
        timepd.show();
    }
}
