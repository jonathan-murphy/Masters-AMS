package com.example.jonny.projectapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.util.HashMap;

/**
 * Created by Jonny on 20/02/2016.
 */
public class GripFragment extends Fragment {

    String grip;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_grip_screen,
                container, false);

        Button gripButton = (Button) rootView.findViewById(R.id.gripButton);

        gripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getActivity(), OpenCvActivity.class);
                //startActivity(intent);
                startActivityForResult(new Intent(getActivity(),OpenCvActivity.class), 1);
            }
        });

        return rootView;
    }

    // Called when the OPENCV activity finishes, calls the testUpdate()
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                grip = data.getStringExtra("grip");
                testUpdate();
                Log.i("calling", "update");
            }
        }
    }

    public static GripFragment newInstance() {
        GripFragment fragment = new GripFragment();
        return fragment;
    }

    public GripFragment() {
        // TODO Auto-generated constructor stub
    }

    private void testUpdate() {

        class testUpdate extends AsyncTask<Void, Void, String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //loading = ProgressDialog.show(getActivity.this,"Adding...","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //loading.dismiss();
                //            Toast.makeText(Jum.this,s,Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String, String> params = new HashMap<>();
                //            params.put("Taps",taps);
                params.put("grip",String.valueOf(grip));
                //params.put("FT", String.valueOf(flightTime));
                //params.put("CT", String.valueOf(contractionTime));

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest("http://ec2-52-91-226-96.compute-1.amazonaws.com/GripUpdate.php", params);
                return res;
            }
        }
        testUpdate tu = new testUpdate();
        tu.execute();
    }
}

//    public static final String TAG = "GripFragment";
//    private CameraBridgeViewBase mOpenCvCameraView;
//
//    public GripFragment() {
//        // Required empty public constructor
//    }
//
//    public static GripFragment newInstance() {
//        GripFragment fragment = new GripFragment();
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
////        Log.i(TAG, "called onCreate");
//        super.onCreate(savedInstanceState);
////        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
////        getActivity().setContentView(R.layout.fragment_grip_screen);
////        mOpenCvCameraView = (CameraBridgeViewBase) getActivity().findViewById(R.id.HelloOpenCvView);
////        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
////        mOpenCvCameraView.setCvCameraViewListener(this);
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//
//        Log.i(TAG, "called onCreate");
////        super.onCreate(savedInstanceState);
//        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        getActivity().setContentView(R.layout.fragment_grip_screen);
//        mOpenCvCameraView = (CameraBridgeViewBase) getActivity().findViewById(R.id.HelloOpenCvView);
//        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
//        mOpenCvCameraView.setCvCameraViewListener(this);
//        return inflater.inflate(R.layout.fragment_grip_screen, container, false);
//    }
//
//    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this.getActivity()) {
//        @Override
//        public void onManagerConnected(int status) {
//            switch (status) {
//                case LoaderCallbackInterface.SUCCESS:
//                {
//                    Log.i(TAG, "OpenCV loaded");
//                    mOpenCvCameraView.enableView();
//                } break;
//                default:
//                {
//                    super.onManagerConnected(status);
//                } break;
//            }
//        }
//    };
//
//    @Override
//    public void onResume()
//    {
//        super.onResume();
//        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this.getActivity(), mLoaderCallback);
//    }
//
//    @Override
//    public void onPause()
//    {
//        super.onPause();
//        if (mOpenCvCameraView != null)
//            mOpenCvCameraView.disableView();
//    }
//
//    public void onDestroy() {
//        super.onDestroy();
//        if (mOpenCvCameraView != null)
//            mOpenCvCameraView.disableView();
//    }
//
//    public void onCameraViewStarted(int width, int height) {
//    }
//
//    public void onCameraViewStopped() {
//    }
//
//    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
//        return inputFrame.rgba();
//    }

