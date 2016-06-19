package com.example.jonny.projectapp;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class WellnessScoreFragment extends Fragment {

    String DateToStr;
    List<String[]> assessmentVals;
    int wellness = 0;
    int appetite = 0;
    int fatigue = 0;
    int illness = 0;
    int mood = 0;
    int motivation = 0;
    int nutrition = 0;
    int recovery = 0;
    int sleep = 0;
    int soreness = 0;
    int stress = 0;
    String [] nextLine;

    public WellnessScoreFragment() {
        // Required empty public constructor
    }

    public static WellnessScoreFragment newInstance() {
        WellnessScoreFragment fragment = new WellnessScoreFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Date todaysDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yy");
        DateToStr = format.format(todaysDate);

        File exportDir = new File(Environment.getExternalStorageDirectory(), "ProjectAppData");
        if (!exportDir.exists()) {
            Toast.makeText(getActivity(), "Cannot find directory", Toast.LENGTH_SHORT).show();
        }

        File file = new File(exportDir, "assessment" + DateToStr + ".csv");
        if(file.exists()) {
            try {
                CSVReader reader = new CSVReader(new FileReader(file));
                assessmentVals = reader.readAll();
                calculateWellness();
            }
            catch (IOException e) {
                Toast.makeText(getActivity(), "File read error", Toast.LENGTH_LONG).show();
                System.out.println(e.getMessage());
            }
        }
        else {
            Toast.makeText(getActivity(), "File not found", Toast.LENGTH_SHORT).show();
        }
    }

    public void calculateWellness () {
        appetite = Integer.valueOf(assessmentVals.get(0)[0]);
        fatigue = Integer.valueOf(assessmentVals.get(1)[0]);
        illness = Integer.valueOf(assessmentVals.get(2)[0]);
        mood = Integer.valueOf(assessmentVals.get(3)[0]);
        motivation = Integer.valueOf(assessmentVals.get(4)[0]);
        nutrition = Integer.valueOf(assessmentVals.get(5)[0]);
        recovery = Integer.valueOf(assessmentVals.get(6)[0]);
        sleep = Integer.valueOf(assessmentVals.get(7)[0]);
        soreness = Integer.valueOf(assessmentVals.get(8)[0]);
        stress = Integer.valueOf(assessmentVals.get(9)[0]);

        wellness = appetite + fatigue + illness + mood + motivation + nutrition + recovery + sleep + soreness + stress;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_wellness_score_screen, container, false);

        TextView wellnessScore = (TextView)view.findViewById(R.id.wellnessScore);
        String score = String.valueOf(wellness);
        wellnessScore.setText(score);

        TextView wellnessAction = (TextView)view.findViewById(R.id.wellnessAction);
        if (wellness > 80) {
            wellnessAction.setText("YOU ARE WELL RECOVERED TRAIN HARD");
            wellnessScore.setTextColor(Color.rgb(0,200,0));
        }
        else if (wellness > 70) {
            wellnessAction.setText("YOU ARE MODERATELY RECOVERED TRAIN AS NORMAL");
        }
        else if (wellness > 60) {
            wellnessAction.setText("YOU ARE UNDERRECOVERED TAKE IT LIGHT TODAY");
        }
        else {
            wellnessAction.setText("YOU AREN'T RECOVERED DONT TRAIN HARD");
            wellnessScore.setTextColor(Color.rgb(200,0,0));
        }

        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState){
    }

}
