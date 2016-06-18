package com.example.jonny.projectapp;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Calendar;

/**
 * Created by Jonny on 18/06/2016.
 */

public class AlarmAddScreen extends Activity {

    AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private TimePicker alarmTimePicker;
    private static AlarmAddScreen inst;
    private TextView alarmTextView;

    public static AlarmAddScreen instance() {
        return inst;
    }

    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm_screen);
        alarmTimePicker = (TimePicker) findViewById(R.id.alarmTime);
        //alarmTextView = (TextView) findViewById(R.id.alarmText);
        ToggleButton alarmToggle = (ToggleButton) findViewById(R.id.alarmToggleButton);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        alarmToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onToggleClicked(view);
            }
        });
    }

    public void onToggleClicked(View view) {
        if (((ToggleButton) view).isChecked()) {
            Log.d("MyActivity", "Alarm On");
            Calendar calendar = Calendar.getInstance();
            if (Build.VERSION.SDK_INT >= 23 ) {
                calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getHour());
                calendar.set(Calendar.MINUTE, alarmTimePicker.getMinute());
            }
            else {
                calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getCurrentHour());
                calendar.set(Calendar.MINUTE, alarmTimePicker.getCurrentMinute());
            }
            Intent myIntent = new Intent(AlarmAddScreen.this, AlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(AlarmAddScreen.this, 0, myIntent, 0);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            Toast.makeText(this, "ALARM ON", Toast.LENGTH_SHORT).show();
        } else {
            alarmManager.cancel(pendingIntent);
            Log.d("MyActivity", "Alarm Off");
            Toast.makeText(this, "ALARM OFF", Toast.LENGTH_SHORT).show();
        }
    }

//    public void setAlarmText(String alarmText) {
//        alarmTextView.setText(alarmText);
//    }
}
//    public void setAlarmText(String alarmText) {
//        alarmTextView.setText(alarmText);
//    }
