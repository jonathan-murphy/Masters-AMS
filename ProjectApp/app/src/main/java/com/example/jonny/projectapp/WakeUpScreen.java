package com.example.jonny.projectapp;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WakeUpScreen extends AppCompatActivity{

    Ringtone ringtone;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wake_up_screen);

        Date currentTime = new Date();
        SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
        String TimeToStr = format.format(currentTime);

        TextView currentTimeView = (TextView)findViewById(R.id.currentTime);
        currentTimeView.setText(TimeToStr);

        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        final Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), alarmUri);
        ringtone.play();

        Button wakingUpButton = (Button) findViewById(R.id.wakeUpButton);

        wakingUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ringtone.stop();
                Intent intent = new Intent(WakeUpScreen.this, SleepAnalysisScreen.class);
                startActivity(intent);
            }
        });
    }

}
