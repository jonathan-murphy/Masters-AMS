package com.example.jonny.projectapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class WellnessScoreFragment extends Fragment {

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_wellness_score_screen, container, false);

        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState){
    }

}
