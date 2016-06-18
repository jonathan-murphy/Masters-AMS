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
import java.util.Vector;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class OpenCvActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2{
    private CameraBridgeViewBase mOpenCvCameraView;
    private static final String TAG = "CameraViewFragment";
    CameraBridgeViewBase.CvCameraViewFrame outputFrame;
    //Vector<Rect> boundRect;
    //MatOfPoint contours;
    //List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

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

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat inputImage = inputFrame.rgba();
        org.opencv.core.Size blurAmount = new Size(5,5);
        Mat outputImage = new Mat();
        Mat blurredImage = new Mat();
        Mat rangeImage = new Mat();
        Mat cannyOut = new Mat();
        Mat erodedImage = new Mat();
        Mat dilatedImage = new Mat();
        Scalar lower = new Scalar(0,100,100);
        Scalar upper = new Scalar(30,255,255);
        Scalar color = new Scalar(255, 255, 0);
        Imgproc.cvtColor(inputImage, outputImage , Imgproc.COLOR_RGB2HSV_FULL);
        Imgproc.GaussianBlur(outputImage, blurredImage, blurAmount, 2);
        Core.inRange(blurredImage ,lower , upper, rangeImage);
        Imgproc.dilate(rangeImage , dilatedImage, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(9,9)));
        Imgproc.erode(dilatedImage , erodedImage, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(9,9)));
        Imgproc.Canny(erodedImage, cannyOut, 10, 100);
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

        String text = String.valueOf(maxArea);
        double fontScale = 2;
        int thickness = 2;
        Point position = new Point(100, 500);

        // center the text
        //Point textOrg((inputImage.cols - textSize.width)/2, (img.rows + textSize.height)/2);
        //Point textOrg = new Point(150, 2000);
        MatOfPoint2f approxCurve = new MatOfPoint2f();
        MatOfPoint2f contour2f = new MatOfPoint2f( contours.get(maxAreaId).toArray() );
        double approxDistance = Imgproc.arcLength(contour2f, true)*0.02;
        Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);
        MatOfPoint points = new MatOfPoint( approxCurve.toArray() ); // converting back to mat point
        Rect rect = Imgproc.boundingRect(points); // getting bounding rectangle
        Imgproc.rectangle(inputImage, rect.br(), rect.tl(), color);
        Imgproc.putText(inputImage, text, position, 3, 1, new Scalar(255, 255, 255, 255), 2);
        //Imgproc.putText()
        Imgproc.drawContours(inputImage, contours, maxAreaId, color, -1);
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
