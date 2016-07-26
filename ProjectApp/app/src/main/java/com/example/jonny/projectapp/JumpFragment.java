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
        import com.example.jonny.projectapp.R;
        import com.opencsv.CSVWriter;

        import java.io.File;
        import java.io.FileWriter;
        import java.io.IOException;
        import java.lang.reflect.Array;
        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Arrays;
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
    Double calibrationVal = 0.0;
    float difference;
    boolean getStart = true;
    int startTime;
    float ecThreshold = 0.3f;
    float prevGForce = 0f;
    float gForce, gForceRaw = 0f;
    float ALPHA = 0.15f;
    float rawX, filterX, rawY, filterY, rawZ, filterZ;
    static int sampleRate = 50; // Accelerometer read speed
    boolean save = false;
    static int recordTime = 4; // enter time to record in seconds
    static int samples = recordTime * sampleRate; // calculating neccesary number of samples to record
    static int beepDelay = 3000; // enter time in milliseconds
    ArrayList<String[]> dataFile = new ArrayList<String[]>();
    Float[] mylist = new Float[samples];
    Double[] filtered = new Double[samples];
    long time = 0;
    int i = 0;
    int timerSync = 0;

    double eccentricThreshold = 10.3;
    double peakThreshold = 0.1;
    double peakRolloff = 0.5;
    int startSamples = 30;
    int concentricTime = 1000;
    int landedTime = 0;
    boolean jumpStarted = false;
    boolean flightStarted = false;
    boolean landed = false;
    boolean touchdown = false;
    int touchdownTime = 0;
    int landedBound = 0;

    TextView cTime;
    TextView fTime;

    int contractionTime;
    int flightTime;

    // filter coefficients //
    float a1 = 1f;
    float a2 = -1.7293f;
    float a3 = 1.4461f;
    float a4 = -0.5713f;
    float a5 = 0.0917f;
    float b1 = 0.0148f;
    float b2 = 0.0593f;
    float b3 = 0.089f;
    float b4 = 0.0593f;
    float b5 = 0.0148f;

    double in1, in2, in3, in4, in5;
    double filt1, filt2, filt3, filt4, filt5;

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

        Arrays.fill(filtered, 0.0);

        transferUtility = awsSetup.getTransferUtility(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_jump_screen, container, false);

        //((AppCompatActivity) getActivity()).getSupportActionBar().show();

        v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

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

//        filterX = filterX + ALPHA * (rawX - filterX);
//        filterY = filterY + ALPHA * (rawY - filterY);
//        filterZ = filterZ + ALPHA * (rawZ - filterZ);
//
//        gForce = Math.abs(filterX)+Math.abs(filterY)+Math.abs(filterZ);
        gForce = Math.abs(rawX)+Math.abs(rawY)+Math.abs(rawZ);

        String alphaVal = "Alpha = " + ALPHA;

        if (i == 0) {
            Date curDate = new Date();
//            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yy hh:mm:ss a");
//            DateToStr = format.format(curDate);
//            mylist.add(new String[]{DateToStr});
//            mylist.add(new String[]{"Time","X","Y","Z"});
            i = i+1;
        }

        if (i < samples && save == true) {
            dataFile.add(new String[] {Long.toString(time),Float.toString(rawX),Float.toString(rawY),Float.toString(rawZ),Float.toString(gForce)});
            mylist[i] = gForce;
            i = i+1;
        }

        if (i == samples && testComplete == false) {
            v.vibrate(250);
            filter();
            analyseJump();
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

    public void analyseJump (){

        for (i = 10; i<=startSamples; i++){
            calibrationVal = filtered[i] + calibrationVal;
            Log.i("calibration", String.valueOf(calibrationVal));
        }

        eccentricThreshold = (calibrationVal / (startSamples-9))* 1.05;
        Double eccentricThreshold2 = (calibrationVal / (startSamples-9))* 0.95;
        Log.i("eccentricThreshold", String.valueOf(eccentricThreshold));

        peakThreshold = eccentricThreshold * 0.01;
        Log.i("peakThreshold", String.valueOf(peakThreshold));
        peakThreshold = 0.1;

        for (i = startSamples; i<samples-10; i++)
        {
            if (jumpStarted == false) {
                if (filtered[i] >= eccentricThreshold || filtered[i] <= eccentricThreshold2) {
                    jumpStarted = true;
                    startTime = i;
                    Toast.makeText(getActivity().getApplicationContext(), "start " + i, Toast.LENGTH_SHORT).show();
                }
            }

            if (filtered[i] > filtered[i+1] + peakThreshold && filtered[i] > filtered[i-1] + peakThreshold && i > concentricTime + 10 && landed == false && filtered[i+10] < filtered[i] * peakRolloff ){
                landedTime = i;
                landed = true;
                Toast.makeText(getActivity().getApplicationContext(), "landed "+i, Toast.LENGTH_SHORT).show();
            }

            if (filtered[i] > filtered[i+1] + peakThreshold && filtered[i] > filtered[i-1] + peakThreshold && flightStarted == false && filtered[i+10] < filtered[i] * peakRolloff ){
                flightStarted = true;
                concentricTime = i;
                Toast.makeText(getActivity().getApplicationContext(), "flight "+i, Toast.LENGTH_SHORT).show();
            }
        }

        contractionTime = (concentricTime - startTime) * (1000/sampleRate);
        flightTime = (landedTime - concentricTime) * (1000/sampleRate);

        for (i = landedTime; i > landedTime-20; i--)
        {
            if (touchdown == false ){
//                touchdown = true;
//                touchdownTime = i;
//                Toast.makeText(getActivity().getApplicationContext(), "touchdown "+i, Toast.LENGTH_SHORT).show();
            }
        }

        cTime = (TextView)getActivity().findViewById(R.id.CT);
        cTime.setText("CT: " + contractionTime + "ms");

        fTime = (TextView)getActivity().findViewById(R.id.FT);
        fTime.setText("FT: " + flightTime + "ms");


        dataFile.add(new String[] {"Start",(Double.toString(startTime))});
        dataFile.add(new String[] {"Land",(Double.toString(landedTime))});
        dataFile.add(new String[] {"Contraction",(Double.toString(concentricTime))});
        dataFile.add(new String[] {"Threshold",(Double.toString(eccentricThreshold))});
    }

    public void filter() {

        for (i = 1; i < samples; i++) {
            in5 = in4;
            in4 = in3;
            in3 = in2;
            in2 = in1;
            in1 = mylist[i];

            filt1 = filtered[i];
            filt5 = filt4;
            filt4 = filt3;
            filt3 = filt2;
            filt2 = filt1;

            //filt1 = a1*in1  + a2*in2 + a3*in3 + a4*in4 + a5*in5 - b1*filt1  - b2*filt2 - b3*filt3 - b4*filt4 - b5*filt5;
            filt1 = b1*in1  + b2*in2 + b3*in3 + b4*in4 + b5*in5 - a2*filt1 - a3*filt2 - a4*filt3 - a5*filt4;
            filtered[i] = filt1;
            dataFile.add(new String[] {(Double.toString(filtered[i]))});
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
            writer.writeAll(dataFile);
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
