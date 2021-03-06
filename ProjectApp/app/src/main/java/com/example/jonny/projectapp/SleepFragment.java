package com.example.jonny.projectapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

/**
 * Created by Jonny on 16/06/2016.
 */
public class SleepFragment extends Fragment {

    long sleepStartTime;
    private BluetoothLeService mBluetoothLeService;

    public SleepFragment() {
        // Required empty public constructor
    }

    public static SleepFragment newInstance() {
        SleepFragment fragment = new SleepFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_sleep_screen, container, false);

        ImageButton goToSleepButton = (ImageButton) view.findViewById(R.id.sleepButton);
        goToSleepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long sleepStartTime = System.currentTimeMillis(); // get the time user says they are going to sleep
                Intent intent = new Intent(getActivity(), SleepingScreen.class);
                startActivity(intent);

            }
        });
        return view;
    }
}
