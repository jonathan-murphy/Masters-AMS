package com.example.jonny.projectapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Jonny on 16/06/2016.
 */
public class SleepAnalysisScreen extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_analysis_screen);

        Button confirmButton = (Button) findViewById(R.id.confirmRatingButton);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SleepAnalysisScreen.this, HomeScreen.class);
                startActivity(intent);
            }
        });

    }

}
