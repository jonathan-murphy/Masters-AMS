package com.example.jonny.projectapp;

import android.bluetooth.BluetoothGatt;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WakeUpScreen extends AppCompatActivity{

    Ringtone ringtone;
    String DateToStr;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wake_up_screen);

        Date currentTime = new Date();
        SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
        String TimeToStr = format.format(currentTime);

        TextView currentTimeView = (TextView)findViewById(R.id.currentTime);
        currentTimeView.setText(TimeToStr);

        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        final Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), alarmUri);
        ringtone.play();

        Button wakingUpButton = (Button) findViewById(R.id.wakeUpButton);

        wakingUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ringtone.stop();
                Date curDate = new Date();
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yy hh:mm:ss a");
                DateToStr = format.format(curDate);
                save();
                Intent intent = new Intent(WakeUpScreen.this, SleepAnalysisScreen.class);
                startActivity(intent);
            }
        });
    }

    public void save() {
//        File exportDir = new File(Environment.getExternalStorageDirectory(), "SleepActigraphy");
//        if (!exportDir.exists()) {
//            exportDir.mkdirs();
//        }
//
//        File file = new File(exportDir, "Actigraphy" + DateToStr + ".csv");
//        try {
//            file.createNewFile();
//            CSVWriter writer = new CSVWriter(new FileWriter(file));
//            writer.writeAll(mylist);
//            writer.close();
//        } catch (IOException e) {
//            System.out.println(e.getMessage());
//        }
//        TransferObserver observer = transferUtility.upload("initiraltestbucket", file.getName(),
//                file);
//        transferObserverListener(observer);
//        Toast.makeText(this, file.getName(), Toast.LENGTH_SHORT).show();
    }

    public void transferObserverListener(TransferObserver transferObserver){

        transferObserver.setTransferListener(new TransferListener(){

            @Override
            public void onStateChanged(int id, TransferState state) {
                Log.e("statechange", state+"");
                //Toast.makeText(getActivity().getApplicationContext(), "State Changed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                int percentage = (int) (bytesCurrent/bytesTotal * 100);
                Log.e("percentage",percentage +"");
                //Toast.makeText(getActivity().getApplicationContext(), "Progress Changed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(int id, Exception ex) {
                Log.e("error","error");
            }

        });
    }

}
