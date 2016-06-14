package com.example.jonny.jumpTest;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.example.jonny.csvtest.R;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static AmazonS3Client sS3Client;
    private static CognitoCachingCredentialsProvider sCredProvider;
    private static TransferUtility sTransferUtility;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ToneGenerator mToneGenerator;

    String DateToStr;

    int concStart = 0;
    float gMin = 100;
    float gMax = -100;
    long maxTime = 0;
    long minTime = 0;
    float difference;
    boolean getStart = true;
    long startTime;
    float ecThreshold = 0.3f;
    float prevGForce = 0f;
    float gForce, gForceRaw = 0f;
    float ALPHA = 0.15f;
    float rawX, filterX, rawY, filterY, rawZ, filterZ;
    int sampleRate = 50; // Accelerometer read speed
    boolean save = false;
    static int samples = 0;
    static int recordTime = 3; // enter time to record in seconds
    static int beepDelay = 3000; // enter time in milliseconds
    ArrayList<String[]> mylist = new ArrayList<String[]>();
    long time = 0;
    int i = 0;
    int timerSync = 0;

    private Timer timer  = new Timer();;
    private TimerTask timerTask ;
    //private TransferUtility transferUtility;

    AmazonS3 s3;
    TransferUtility transferUtility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        credentialsProvider();
        setTransferUtility();

        samples = recordTime * sampleRate; // calculating neccesary number of samples to record

        mToneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer=mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            //Toast.makeText(getApplicationContext(),"Accelerometer Found",Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(getApplicationContext(),"No Accelerometer Found",Toast.LENGTH_LONG).show();
        }

        final Button startButton = (Button) findViewById(R.id.start);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                initializeTimerTask();
                timer.schedule(timerTask, beepDelay, 1000);
            }
        });

        final Button saveButton = (Button) findViewById(R.id.saveCSV);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                save();
            }
        });
    }

    public void credentialsProvider(){

        // Initialize the Amazon Cognito credentials provider
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "us-east-1:c161571a-167b-4647-bd16-263937eec637", // Identity Pool ID
                Regions.US_EAST_1 // Region
        );

        setAmazonS3Client(credentialsProvider);
    }

    public void setAmazonS3Client(CognitoCachingCredentialsProvider credentialsProvider){
        s3 = new AmazonS3Client(credentialsProvider);
        s3.setRegion(Region.getRegion(Regions.US_EAST_1));
    }

    public void setTransferUtility(){
        transferUtility = new TransferUtility(s3, getApplicationContext());
    }


    public void initializeTimerTask() {

        timerTask = new TimerTask() {
            public void run() {
                if (timerSync < 1){
                    mToneGenerator.startTone(ToneGenerator.TONE_PROP_PROMPT);
                }
                else if (timerSync == 1){
                    mToneGenerator.startTone(ToneGenerator.TONE_PROP_PROMPT);
                    save = true;
                }
                else if (timerSync == 2){
                    mToneGenerator.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT);
                }
                else {
                    timer.cancel(); //turn off timer
                }
                timerSync = timerSync + 1;
            }
        };
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        rawX = event.values[0];
        rawY = event.values[1];
        rawZ = event.values[2];
        time = System.currentTimeMillis();

        filterX = filterX + ALPHA * (rawX - filterX);
        filterY = filterY + ALPHA * (rawY - filterY);
        filterZ = filterZ + ALPHA * (rawZ - filterZ);

        gForce = Math.abs(filterX)+Math.abs(filterY)+Math.abs(filterZ);
        gForceRaw = Math.abs(rawX)+Math.abs(rawY)+Math.abs(rawZ);

        String alphaVal = "Alpha = " + ALPHA;

        if (i == 0) {
            Date curDate = new Date();
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yy hh:mm:ss a");
            DateToStr = format.format(curDate);
            mylist.add(new String[]{DateToStr});
            mylist.add(new String[]{"Time","X","Y","Z"});
            i = i+1;
        }

        if (i < samples + 1 && save == true) {
            findPoints();
            mylist.add(new String[] {Long.toString(time),Float.toString(rawX),Float.toString(rawY),Float.toString(rawZ)});
            i = i+1;
        }

        prevGForce = gForce;
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    public void findPoints() {
        difference = Math.abs(gForce -  prevGForce);
        startTime = 0;
        minTime = 0;
        maxTime = 0;
        concStart = 0;

        if (difference > ecThreshold && getStart == true)
        {
            Toast.makeText(getApplicationContext(),"Eccentric Phase",Toast.LENGTH_SHORT).show();
            startTime = 10;
            getStart = false;
        }

        if (gForce > gMax){
            gMax = gForce;
            maxTime = 10;
        }

        if (gForce < gMin){
            gMin = gForce;
            minTime = 10;
        }
    }

    public void save() {
        File exportDir = new File(Environment.getExternalStorageDirectory(), "JumpTestData");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }

        File file = new File(exportDir, "jumpTest" + DateToStr + ".csv");
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