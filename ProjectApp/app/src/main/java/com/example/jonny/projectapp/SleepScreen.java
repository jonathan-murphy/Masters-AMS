package com.example.jonny.projectapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by Jonny on 16/06/2016.
 */
public class SleepScreen extends AppCompatActivity{

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_screen);

        ImageButton goToSleepButton = (ImageButton) findViewById(R.id.sleepButton);

        goToSleepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SleepScreen.this, SleepingScreen.class);
                startActivity(intent);
            }
        });

    }

}
