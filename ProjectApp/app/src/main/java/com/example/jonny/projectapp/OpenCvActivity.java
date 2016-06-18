package com.example.jonny.projectapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

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
    int timerSync = 0;

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
    String text;
    double fontScale = 2;
    int thickness = 2;
    Point position = new Point(100, 100);

    org.opencv.core.Size blurAmount = new Size(5,5);
    Scalar lowerHue = new Scalar(0,100,100);
    Scalar upperHue = new Scalar(30,255,255);
    Scalar color = new Scalar(255, 255, 0);
    TextView status;

    //Vector<Rect> boundRect;
    //MatOfPoint contours;
    //List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.opencvlayout);

        status = (TextView)findViewById(R.id.imageStatus);
        status.setText("CALIBRATING");

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.OpenCvView);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        startTime = System.currentTimeMillis();

        initializeTimerTask();
        timer.schedule(timerTask, 1000, 1000);

    }

    public void initializeTimerTask() {

        timerTask = new TimerTask() {
            public void run() {
                if (timerSync < 1){
                    //status.setText("CALIBRATING");
                }
                else if (timerSync == 1){
                    //status.setText("PROCESSING");
                }
                else if (timerSync == 2){
                }
                else {
                    timer.cancel(); //turn off timer
                    finish();
                }
                timerSync = timerSync + 1;
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

        //text = String.valueOf(contours.size());

        if (contours.size() > 0){
            MatOfPoint2f approxCurve = new MatOfPoint2f();
            MatOfPoint2f contour2f = new MatOfPoint2f(contours.get(maxAreaId).toArray());
            double approxDistance = Imgproc.arcLength(contour2f, true) * 0.02;
            Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);
            MatOfPoint points = new MatOfPoint(approxCurve.toArray()); // converting back to mat point
            Rect rect = Imgproc.boundingRect(points); // getting bounding rectangle
            Imgproc.rectangle(inputImage, rect.br(), rect.tl(), color);
            //Imgproc.putText(inputImage, text, position, 3, 1, new Scalar(255, 255, 255, 255), 2);
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
