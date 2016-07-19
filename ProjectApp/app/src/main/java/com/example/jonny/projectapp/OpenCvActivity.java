package com.example.jonny.projectapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class OpenCvActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2{
    private CameraBridgeViewBase mOpenCvCameraView;
    private static final String TAG = "CameraViewFragment";
    CameraBridgeViewBase.CvCameraViewFrame outputFrame;

    private Timer timer  = new Timer();;
    private TimerTask timerTask ;
    int calibrationCycle = 0;
    int analysisCycle = 0;

    double bandArea = 0;
    double bandAreaMax = 0;
    double bandAreaMin = 100000;
    boolean bandPresent = false;
    boolean calibrated = false;
    boolean stretched = false;
    boolean analysing = false;
    boolean analysed = false;
    double stretchedArea = 0;
    double calibratedArea = 0;
    double analysedArea = 0;
    double threshold1 = 10;
    double threshold2 = 100;
    double prevArea = 0;
    double calibVal = 0;
    double maxVal = 0;
    double increase = 0;
    int frameNo = 1;
    long time = 0;
    long startTime = 0;
    long runTime = 0;
    String userNote = "NOTE";
    String currentStatus = "CALIBRATING";
    String toastMsg = "CALIBRATION FAILED";
    double fontScale = 2;
    int thickness = 2;
    Point position = new Point(100, 100);

    org.opencv.core.Size blurAmount = new Size(5,5);
    Scalar lowerHue = new Scalar(20,100,100);
    Scalar upperHue = new Scalar(40,255,255);
    Scalar color = new Scalar(255, 255, 0);
    TextView status;
    TextView note;
    final Handler statusHandler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.opencvlayout);

        status = (TextView)findViewById(R.id.imageStatus);
        status.setText(currentStatus);
        note = (TextView)findViewById(R.id.userNote);
        note.setText(userNote);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.OpenCvView);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        startTime = System.currentTimeMillis();

        initializeTimerTask();
        timer.schedule(timerTask, 1000, 500);
    }

    final Runnable statusRunnable = new Runnable() {
        public void run() {
            status.setText(currentStatus);
            note.setText(userNote);
        }
    };

    final Runnable errorRunnable = new Runnable() {
        public void run() {
            Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_LONG).show();
        }
    };

    public void initializeTimerTask() {

        timerTask = new TimerTask() {
            public void run() {

                if (bandAreaMax > 500) {
                    userNote = String.valueOf(bandArea);

                    double bandAreaChange = (bandAreaMax / bandAreaMin) - 1;
                    if (bandAreaChange <= 0.1 && calibrated == false) {
                        calibratedArea = ((bandAreaMax - bandAreaMin) / 2) + bandAreaMin; // set calibrated area to middle of max and min values
                        calibrated = true;
                    } else if (bandAreaMax > (calibratedArea * 1.3) && calibrated == true && analysing == false) {
                        stretched = true;
                        analysing = true;
                    } else if (bandAreaChange <= 0.15 && analysing == true) {
                        analysedArea = ((bandAreaMax - bandAreaMin) / 2) + bandAreaMin; // set analysed (i.e. stretched) area to middle of max and min values
                        analysed = true;
                    }

                    if (calibrated == false) {
                        currentStatus = "CALIBRATING";
                        userNote = "HOLD STILL";
                        calibrationCycle++;
                        if (calibrationCycle >= 10) {
                            timer.cancel(); // turn off timer
                            finish(); // close Activity
                            statusHandler.post(errorRunnable);
                        }
                    } else if (stretched == false) {
                        userNote = "PLEASE STRETCH BAND";
                        currentStatus = "STRETCH";
                    } else if (stretched == true && analysing == true && analysed == false) {
                        currentStatus = "ANALYSING";
                        userNote = "HOLD STILL";
                    } else if (analysed == true) {
                        timer.cancel(); // turn off timer
                        finish(); // close Activity
                    }
                }
                else {
                    userNote = "BAND NOT FOUND";
                }

                statusHandler.post(statusRunnable);

                // reset band max & min values
                bandAreaMax = 0;
                bandAreaMin = 100000;

            }
        };
    }



    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
    }

    public void onCameraViewStopped() {
    }

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        runTime = System.currentTimeMillis() - startTime;

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat inputImage = inputFrame.rgba();
        Mat hsvImage = new Mat();
        Mat blurredImage = new Mat();
        Mat rangeImage = new Mat();
        Mat cannyOut = new Mat();
        Mat erodedImage = new Mat();
        Mat dilatedImage = new Mat();

        Imgproc.cvtColor(inputImage, hsvImage , Imgproc.COLOR_RGB2HSV_FULL);
        Imgproc.GaussianBlur(hsvImage, blurredImage, blurAmount, 2);
        Core.inRange(blurredImage ,lowerHue , upperHue, rangeImage);
        Imgproc.dilate(rangeImage , dilatedImage, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(9,9)));
        Imgproc.erode(dilatedImage , erodedImage, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(9,9)));
        Imgproc.Canny(erodedImage, cannyOut, threshold1, threshold2);
        Imgproc.findContours(cannyOut, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

        double maxArea = -1;
        int maxAreaId = -1;

        // finding max contour
        for (int i = 0; i < contours.size(); i++) {
            Mat contour = contours.get(i);
            double contArea = Imgproc.contourArea(contour);
            if (contArea > maxArea) {
                maxArea = contArea;
                maxAreaId = i;
            }
        }

        bandArea = maxArea;
        if (bandArea > bandAreaMax) {
            bandAreaMax = bandArea;
        }
        if (bandArea < bandAreaMin) {
            bandAreaMin = bandArea;
        }

        if (contours.size() > 0){
            MatOfPoint2f approxCurve = new MatOfPoint2f();
            MatOfPoint2f contour2f = new MatOfPoint2f(contours.get(maxAreaId).toArray());
            double approxDistance = Imgproc.arcLength(contour2f, true) * 0.02;
            Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);
            MatOfPoint points = new MatOfPoint(approxCurve.toArray()); // converting back to mat point
            Rect rect = Imgproc.boundingRect(points); // getting bounding rectangle
            Imgproc.rectangle(inputImage, rect.br(), rect.tl(), color);
            Imgproc.drawContours(inputImage, contours, maxAreaId, color, -1);
        }
        else {
            Imgproc.putText(inputImage, "No band found", position, 3, 1, new Scalar(255, 255, 255, 255), 2);
        }

        frameNo++;
        prevArea = maxArea;
        return inputImage;
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
    }
}
