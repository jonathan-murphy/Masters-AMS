package com.example.jonny.projectapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SleepingScreen extends AppCompatActivity{

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleeping_screen);

        Date currentTime = new Date();
        SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
        String TimeToStr = format.format(currentTime);

        TextView currentTimeView = (TextView)findViewById(R.id.currentTime);
        currentTimeView.setText(TimeToStr);

    }

}
