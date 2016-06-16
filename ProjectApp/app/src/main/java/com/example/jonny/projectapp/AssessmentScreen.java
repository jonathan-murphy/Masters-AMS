package com.example.jonny.projectapp;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AssessmentScreen extends ActionBarActivity {

    AmazonS3 s3;
    TransferUtility transferUtility;

    ArrayList<String[]> mylist = new ArrayList<String[]>();

    String DateToStr;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_screen);

        transferUtility = awsSetup.getTransferUtility(this);

        Date curDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yy hh:mm:ss a");
        DateToStr = format.format(curDate);

        mylist.add(new String[]{DateToStr});
        mylist.add(new String[]{"Factor","Rating"});

        final SeekBar appetite = (SeekBar)findViewById(R.id.seekBar);
        final SeekBar fatigue = (SeekBar)findViewById(R.id.seekBar2);
        final SeekBar illness = (SeekBar)findViewById(R.id.seekBar3);
        final SeekBar mood = (SeekBar)findViewById(R.id.seekBar4);
        final SeekBar motivation = (SeekBar)findViewById(R.id.seekBar5);
        final SeekBar nutrition = (SeekBar)findViewById(R.id.seekBar6);
        final SeekBar recovery = (SeekBar)findViewById(R.id.seekBar7);
        final SeekBar sleep = (SeekBar)findViewById(R.id.seekBar8);
        final SeekBar soreness = (SeekBar)findViewById(R.id.seekBar9);
        final SeekBar stress = (SeekBar)findViewById(R.id.seekBar10);

        button = (Button) findViewById(R.id.save);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String appval = String.valueOf(appetite.getProgress());
                String fatval = String.valueOf(fatigue.getProgress());
                String illval = String.valueOf(illness.getProgress());
                String mooval = String.valueOf(mood.getProgress());
                String motval = String.valueOf(motivation.getProgress());
                String nutval = String.valueOf(nutrition.getProgress());
                String recval = String.valueOf(recovery.getProgress());
                String sleval = String.valueOf(sleep.getProgress());
                String sorval = String.valueOf(soreness.getProgress());
                String strval = String.valueOf(stress.getProgress());

                mylist.add(new String[]{"Appetite",appval});
                mylist.add(new String[]{"Fatigue",fatval});
                mylist.add(new String[]{"Illness",illval});
                mylist.add(new String[]{"Mood",mooval});
                mylist.add(new String[]{"Motivation",motval});
                mylist.add(new String[]{"Nutrition",nutval});
                mylist.add(new String[]{"Recovery",recval});
                mylist.add(new String[]{"Sleep",sleval});
                mylist.add(new String[]{"Soreness",sorval});
                mylist.add(new String[]{"Stress",strval});
                save();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_assessment_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void save() {
        File exportDir = new File(Environment.getExternalStorageDirectory(), "JumpTestData");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }

        File file = new File(exportDir, "assessment" + DateToStr + ".csv");
        try {
            file.createNewFile();
            CSVWriter writer = new CSVWriter(new FileWriter(file));
            writer.writeAll(mylist);
            writer.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        TransferObserver observer = transferUtility.upload("initiraltestbucket", file.getName(),
                file);
        transferObserverListener(observer);
        Toast.makeText(getApplicationContext(), "Upload Complete", Toast.LENGTH_SHORT).show();
    }

    public void transferObserverListener(TransferObserver transferObserver){

        transferObserver.setTransferListener(new TransferListener(){

            @Override
            public void onStateChanged(int id, TransferState state) {
                Log.e("statechange", state+"");
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                int percentage = (int) (bytesCurrent/bytesTotal * 100);
                Log.e("percentage",percentage +"");
            }

            @Override
            public void onError(int id, Exception ex) {
                Log.e("error","error");
            }

        });
    }
}
