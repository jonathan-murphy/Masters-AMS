package com.example.jonny.projectapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Jonny on 19/06/2016.
 */
public class SleepScoreFragment extends Fragment {

    public SleepScoreFragment() {
        // Required empty public constructor
    }

    public static SleepScoreFragment newInstance() {
        SleepScoreFragment fragment = new SleepScoreFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sleep_score_screen, container, false);

        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState){
    }

}

