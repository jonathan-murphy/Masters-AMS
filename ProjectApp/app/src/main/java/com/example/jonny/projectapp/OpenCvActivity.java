package com.example.jonny.projectapp;

import android.annotation.SuppressLint;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

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

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class OpenCvActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2{
    private CameraBridgeViewBase mOpenCvCameraView;
    private static final String TAG = "CameraViewFragment";
    CameraBridgeViewBase.CvCameraViewFrame outputFrame;
    //MatOfPoint contours;
    List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.opencvlayout);
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.OpenCvView);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

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
//        Imgproc.cvtColor(src, dst, Imgproc.COLOR_RGB2HSV, 3); //3 is HSV Channel
//        Imgproc.cvtColor(dst, src, Imgproc.COLOR_HSV2RGBA, 4); // 4 is RGBA Channel
        //CameraBridgeViewBase.CvCameraViewFrame outputFrame;

        Mat inputImage = inputFrame.rgba();
        org.opencv.core.Size blurAmount = new Size(15,15);
        int contourIdx = 0;
        Mat outputImage = new Mat();
        Mat blurredImage = new Mat();
        Mat rangeImage = new Mat();
        Scalar lower = new Scalar(0,100,100);
        Scalar upper = new Scalar(30,255,255);
        Scalar color = new Scalar(255, 255, 0);
        Imgproc.cvtColor(inputImage, outputImage , Imgproc.COLOR_RGB2HSV_FULL);
        Imgproc.GaussianBlur(outputImage, blurredImage, blurAmount, 2);
        Core.inRange(blurredImage ,lower , upper, rangeImage);
        Imgproc.Canny(rangeImage, rangeImage, 10, 100);
        Imgproc.findContours(rangeImage, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        //Imgproc.drawContours(rangeImage, contours, contourIdx, color, -1);

        MatOfPoint2f approxCurve = new MatOfPoint2f();

//        //For each contour found
//        for (int i=0; i<contours.size(); i++)
//        {
//            //Convert contours(i) from MatOfPoint to MatOfPoint2f
//            MatOfPoint2f contour2f = new MatOfPoint2f( contours.get(i).toArray() );
//            //Processing on mMOP2f1 which is in type MatOfPoint2f
//            double approxDistance = Imgproc.arcLength(contour2f, true)*0.02;
//            Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);
//
//            //Convert back to MatOfPoint
//            MatOfPoint points = new MatOfPoint( approxCurve.toArray() );
//
//            // Get bounding rect of contour
//            Rect rect = Imgproc.boundingRect(points);
//
//            // draw enclosing rectangle (all same color, but you could use variable i to make them unique)
//            Imgproc.rectangle(rangeImage, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height),color);
//        }

            //Convert contours(i) from MatOfPoint to MatOfPoint2f
            MatOfPoint2f contour2f = new MatOfPoint2f( contours.get(0).toArray() );
            //Processing on mMOP2f1 which is in type MatOfPoint2f
            double approxDistance = Imgproc.arcLength(contour2f, true)*0.02;
            Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);
            //Convert back to MatOfPoint
            MatOfPoint points = new MatOfPoint( approxCurve.toArray() );
            // Get bounding rect of contour
            Rect rect = Imgproc.boundingRect(points);
            // draw enclosing rectangle (all same color, but you could use variable i to make them unique)
            Imgproc.rectangle(rangeImage, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height),color);


        Imgproc.cvtColor(outputImage, inputImage, Imgproc.COLOR_RGB2RGBA);

        //return inputFrame.rgba();
        return rangeImage;
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
