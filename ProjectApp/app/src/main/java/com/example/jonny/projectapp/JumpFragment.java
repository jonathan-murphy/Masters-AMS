package com.example.jonny.projectapp;

        import android.content.Context;
        import android.hardware.Sensor;
        import android.hardware.SensorEvent;
        import android.hardware.SensorEventListener;
        import android.hardware.SensorManager;
        import android.media.AudioManager;
        import android.media.ToneGenerator;
        import android.os.Bundle;
        import android.os.Environment;
        import android.os.Vibrator;
        import android.support.design.widget.Snackbar;
        import android.support.v4.app.Fragment;
        import android.support.v7.app.AppCompatActivity;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
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
        import com.example.jonny.projectapp.R;
        import com.opencsv.CSVWriter;

        import java.io.File;
        import java.io.FileWriter;
        import java.io.IOException;
        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Date;
        import java.util.Timer;
        import java.util.TimerTask;

//public class MainActivity extends AppCompatActivity implements SensorEventListener {
public class JumpFragment extends Fragment implements SensorEventListener{

    private static AmazonS3Client sS3Client;
    private static CognitoCachingCredentialsProvider sCredProvider;
    private static TransferUtility sTransferUtility;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ToneGenerator mToneGenerator;

    String DateToStr;
    View view;

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

    boolean testComplete = false;

    private Timer timer  = new Timer();;
    private TimerTask timerTask ;
    View.OnClickListener mOnClickListener;
    Vibrator v;

    AmazonS3 s3;
    TransferUtility transferUtility;

    public JumpFragment() {
        // Required empty public constructor
    }

    public static JumpFragment newInstance() {
        JumpFragment fragment = new JumpFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        transferUtility = awsSetup.getTransferUtility(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_jump_screen, container, false);

        //((AppCompatActivity) getActivity()).getSupportActionBar().show();

        v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        samples = recordTime * sampleRate; // calculating neccesary number of samples to record

        mToneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            //Toast.makeText(getApplicationContext(),"Accelerometer Found",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity(), "No Accelerometer Found", Toast.LENGTH_LONG).show();
        }

        final Button startButton = (Button)view.findViewById(R.id.start);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Snackbar.make(view, "STARTED", Snackbar.LENGTH_LONG).show();
                startButton.setVisibility(View.GONE);
                mToneGenerator.startTone(ToneGenerator.TONE_PROP_PROMPT);
                initializeTimerTask();
                timer.schedule(timerTask, beepDelay, 1000);
            }
        });
        return view;
    }

    public void initializeTimerTask() {

        timerTask = new TimerTask() {
            public void run() {
                if (timerSync < 1){
                    mToneGenerator.startTone(ToneGenerator.TONE_PROP_PROMPT);
                    v.vibrate(100);
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
            mylist.add(new String[] {Long.toString(time),Float.toString(rawX),Float.toString(rawY),Float.toString(rawZ)});
            i = i+1;
        }

        if (i == samples && testComplete == false) {
            v.vibrate(250);
            Snackbar snack = Snackbar.make(getActivity().findViewById(android.R.id.content), "Test complete", Snackbar.LENGTH_INDEFINITE)
                    .setAction("SAVE", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            save();
                        }
                    });
            snack.show();
            testComplete = true;
        }
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
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
        Toast.makeText(getActivity().getApplicationContext(), file.getName(), Toast.LENGTH_SHORT).show();
    }

    public void transferObserverListener(TransferObserver transferObserver){

        transferObserver.setTransferListener(new TransferListener(){

            @Override
            public void onStateChanged(int id, TransferState state) {
                Log.e("statechange", state+"");
                Toast.makeText(getActivity().getApplicationContext(), "State Changed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                int percentage = (int) (bytesCurrent/bytesTotal * 100);
                Log.e("percentage",percentage +"");
                Toast.makeText(getActivity().getApplicationContext(), "Progress Changed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(int id, Exception ex) {
                Log.e("error","error");
            }

        });
    }
}
