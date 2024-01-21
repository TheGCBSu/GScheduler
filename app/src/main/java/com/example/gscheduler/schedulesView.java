package com.example.gscheduler;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class schedulesView extends AppCompatActivity {

    Intent intent;
    ListView list;
    Button search,datepick,resb;
    private DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Saved Schedules");
        setContentView(R.layout.activity_schedules_view);
        list=(ListView) findViewById(R.id.scheduleList);
        search=(Button)findViewById(R.id.searchb);
        datepick=(Button)findViewById(R.id.datepicker2);
        resb=(Button)findViewById(R.id.resetb);
        initdatepicker();
        dbhandler db=new dbhandler(this);
        ArrayList<HashMap<String,String>> schedlist=db.getSchedules();
        ListAdapter adapt = new SimpleAdapter(this,schedlist,R.layout.listview_item,
                new String[]{"id","schedtitle","scheddate","schedtime","schedinfo"},
                new int[]{R.id.hiddenID,R.id.schedname,R.id.scheddate,R.id.schedtime,R.id.hiddennotes});
        list.setAdapter(adapt);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String id,title,date,time,notes;
                id=((TextView) view.findViewById(R.id.hiddenID)).getText().toString();
                title=((TextView) view.findViewById(R.id.schedname)).getText().toString();
                date=((TextView) view.findViewById(R.id.scheddate)).getText().toString();
                time=((TextView) view.findViewById(R.id.schedtime)).getText().toString();
                notes=((TextView) view.findViewById(R.id.hiddennotes)).getText().toString();
                intent=new Intent(schedulesView.this,schedDetails.class);
                Bundle bundle = new Bundle();
                bundle.putString("id",id);
                bundle.putString("title",title);
                bundle.putString("date",date);
                bundle.putString("time",time);
                bundle.putString("notes",notes);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        resb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbhandler db=new dbhandler(schedulesView.this);
                ArrayList<HashMap<String,String>> schedlist=db.getSchedules();
                ListAdapter adapt = new SimpleAdapter(schedulesView.this,schedlist,R.layout.listview_item,
                        new String[]{"id","schedtitle","scheddate","schedtime","schedinfo"},
                        new int[]{R.id.hiddenID,R.id.schedname,R.id.scheddate,R.id.schedtime,R.id.hiddennotes});
                list.setAdapter(adapt);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbhandler db = new dbhandler(schedulesView.this);
                String findate="";
                if (datepick.getText().toString().equals("Date")) {
                    findate=getdatenow();
                } else {
                    findate=datepick.getText().toString();
                }ArrayList<HashMap<String, String>> schedlist = db.searchByDate(findate);
                ListAdapter adapt = new SimpleAdapter(schedulesView.this, schedlist, R.layout.listview_item,
                        new String[]{"id", "schedtitle", "scheddate", "schedtime", "schedinfo"},
                        new int[]{R.id.hiddenID, R.id.schedname, R.id.scheddate, R.id.schedtime, R.id.hiddennotes});
                list.setAdapter(adapt);
            }
        });
    }
    private String getdatenow(){
        Calendar cal = Calendar.getInstance();
        int year=cal.get(Calendar.YEAR);
        int month=cal.get(Calendar.MONTH);
        month=month+1;
        int day=cal.get(Calendar.DAY_OF_MONTH);
        return (month+"-"+day+"-"+year);
    }
    private void initdatepicker(){
        DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month=month+1;
                String date=(month+"-"+day+"-"+year);
                datepick.setText(date);
            }
        };
        Calendar cal = Calendar.getInstance();
        int year=cal.get(Calendar.YEAR);
        int month=cal.get(Calendar.MONTH);
        int day=cal.get(Calendar.DAY_OF_MONTH);

        int dia= AlertDialog.THEME_HOLO_LIGHT;
        datePickerDialog=new DatePickerDialog(this,dia, dateSetListener, year, month, day);
    }
    public void opendatePicker2(View view){
        datePickerDialog.show();
    }


}