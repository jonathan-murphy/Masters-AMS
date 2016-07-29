package com.example.jonny.projectapp;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Jonny on 19/06/2016.
 */
public class HistoryFragment extends Fragment {

    double samples = 0;
    int flags = 0;
    int wellnessScore = 0;
    int  i = 0;
    int sum = 0;
    int currentVal = 0;
    double mean = 0;
    double stdDev = 0;
    boolean flag = false;

    String[] varNames = new String[] {"appetite", "mood", "illness", "motivation", "nutrition", "soreness", "stress", "taps", "grip", "FT", "CT", "weight", "calories", "protein", "fat", "carbs"};
    Integer[] importance = new Integer[] {2,6,1,3,4,0,11,12,13,8,9,10,5,7,14,15}; // list most to least important ie 2 = illness. 6 = stress
    Double[][] varMeanStdDev  = new Double[varNames.length][2]; // 2d array to store mean and standard deviation values
    Boolean[][] varFlags = new Boolean[varNames.length][2]; // 2d array to store worse and better flags for values
    String[][] varRcmndtn = new String[varNames.length][2]; // 2d array to store recommendations for different flags
    Integer[][] userData = new Integer[varNames.length][28]; // creating 2d array to store all user data from json

    String JSON_string;
    JSONArray json_array = new JSONArray();
    View view;

    public HistoryFragment() {
        // Required empty public constructor
    }

    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRecommendation();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_history_screen, container, false);
        getJSON(view);
        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState){
    }

    public void calcScore(String result) {
        try {
            JSONObject jsonRootObject = new JSONObject(result);
            JSONArray jsonArray = jsonRootObject.optJSONArray("user_data");

            for (i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                // Creating arraylist of each variable
                if (jsonObject.has("appetite")) {
                    userData[0][i] = Integer.parseInt(jsonObject.optString("appetite").toString());
                }
                if (jsonObject.has("mood")) {
                    userData[1][i] = Integer.parseInt(jsonObject.optString("mood").toString());
                }
                if (jsonObject.has("illness")) {
                    userData[2][i] = Integer.parseInt(jsonObject.optString("illness").toString());
                }
                if (jsonObject.has("motivation")) {
                    userData[3][i] = Integer.parseInt(jsonObject.optString("motivation").toString());
                }
                if (jsonObject.has("nutrition")) {
                    userData[4][i] = Integer.parseInt(jsonObject.optString("nutrition").toString());
                }
                if (jsonObject.has("soreness")) {
                    userData[5][i] = Integer.parseInt(jsonObject.optString("soreness").toString());
                }
                if (jsonObject.has("stress")) {
                    userData[6][i] = Integer.parseInt(jsonObject.optString("stress").toString());
                }
                if (jsonObject.has("taps") && jsonObject.optString("taps") != null) {
                    userData[7][i] = Integer.parseInt(jsonObject.optString("taps").toString());
                }
//                if (jsonObject.has("grip") && jsonObject.optString("grip").toString() != null) {
//                    userData[8][i] = Integer.parseInt(jsonObject.optString("grip").toString());
//                }
                if (jsonObject.has("FT") && jsonObject.optString("FT") != null) {
                    userData[9][i] = Integer.parseInt(jsonObject.optString("FT").toString());
                }
                if (jsonObject.has("CT") && jsonObject.optString("CT") != null) {
                    userData[10][i] = Integer.parseInt(jsonObject.optString("CT").toString());
                }
                if (jsonObject.has("weight")) {
                    userData[11][i] = Integer.parseInt(jsonObject.optString("weight").toString());
                }
                if (jsonObject.has("calories")) {
                    userData[12][i] = Integer.parseInt(jsonObject.optString("calories").toString());
                }
                if (jsonObject.has("protein")) {
                    userData[13][i] = Integer.parseInt(jsonObject.optString("protein").toString());
                }
                if (jsonObject.has("fat")) {
                    userData[14][i] = Integer.parseInt(jsonObject.optString("fat").toString());
                }
                if (jsonObject.has("carbs")) {
                    userData[15][i] = Integer.parseInt(jsonObject.optString("carbs").toString());
                }
            }

            for (i = 0; i < varNames.length; i++) {
                varMeanStdDev[i][0]  = calcMean(i);
                varMeanStdDev[i][1]  = calcStdDev(varMeanStdDev[i][0],i);
                varFlags[i][0]  = checkWorse(varMeanStdDev[i][0],varMeanStdDev[i][1],i);
                varFlags[i][1]  = checkBetter(varMeanStdDev[i][0],varMeanStdDev[i][1],i);
            }

            // GIVE NOTICE OF TWO MOST IMPORTANT ISSUES
            for (i = 0; i < varNames.length; i++) {
                if (varFlags[importance[i]][0] == true) {
                    //print statement
                    Log.i("importance", String.valueOf(importance[i]));
                    Log.i("worse", String.valueOf(varRcmndtn[importance[i]][0]));
                    flags = flags + 1;
                }
                else if (varFlags[importance[i]][1] == true) {
                    //print statement
                    Log.i("importance", String.valueOf(importance[i]));
                    Log.i("better", String.valueOf(varRcmndtn[importance[i]][1]));
                    flags = flags + 1;
                }
                if (flags > 1) {
                    break;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // CALCULATING MEAN OF ARRAY
    public double calcMean(int i) {
        mean = 0; // resetting mean value
        samples = 0; // resetting samples value
        for (int j = 0; j < 28; j++) {
            if (userData[i][j] != null) {
                mean = mean + userData[i][j];
                samples = samples + 1.0; // counting non null values in array row
            }
        }
        return mean / samples;
    }

    // CALCULATING POPULATION STANDARD DEVIATION OF ARRAY
    double calcStdDev(double mean, int i)
    {
        double temp = 0;
        samples = 0; // resetting samples value
        for (int j = 0; j < 28; j++) {
            if (userData[i][j] != null) {
                temp = temp +  (mean-userData[i][j])*(mean-userData[i][j]);
                samples = samples + 1.0; // counting non null values in array row
            }
        }
        //return stdDev = Math.sqrt(temp/values.size()); // returns population standard deviaton
        return stdDev = Math.sqrt(temp/(samples-1)); // returns standard deviation
    }

    // CHECKING WHETHER OR NOT A VARIABLE IS LOW
    boolean checkWorse(double mean, double stdDev, int i) {
        // comparing last value of arraylist to normal values of that arraylist
        flag = false;
        int samples = 0; // resetting samples value
        for (int j = 0; j < 28; j++) {
            if (userData[i][j] != null) {
                samples = samples + 1; // counting non null values in array row
            }
        }
        if (samples > 0) {
            if (userData[i][samples - 1] != null) {
                currentVal = userData[i][samples - 1];
                if (currentVal < mean - stdDev) {
                    flag = true;
                }
            }
        }
        return flag;
    }

    // CHECKING WHETHER OR NOT A VARIABLE IS LOW
    boolean checkBetter(double mean, double stdDev, int i) {
        // comparing last value of arraylist to normal values of that arraylist
        flag = false;
        int samples = 0; // resetting samples value
        for (int j = 0; j < 28; j++) {
            if (userData[i][j] != null) {
                samples = samples + 1; // counting non null values in array row
            }
        }
        if (samples > 0) {
            if (userData[i][samples - 1] != null) {
                currentVal = userData[i][samples - 1];
                if (currentVal > mean + stdDev) {
                    flag = true;
                    varRcmndtn[0][0] = "ads";
                }
            }
        }
        return flag;
    }

    public void showRecommendation() {

    }

    public void setRecommendation() {
        Resources res = getResources();

        varRcmndtn[0][0] = String.format(res.getString(R.string.appetitie_worse));
        varRcmndtn[0][1] = String.format(res.getString(R.string.appetitie_better));
        varRcmndtn[1][0] = String.format(res.getString(R.string.mood_worse));
        varRcmndtn[1][1] = String.format(res.getString(R.string.mood_better));
        varRcmndtn[2][0] = String.format(res.getString(R.string.illness_worse));
        varRcmndtn[2][1] = String.format(res.getString(R.string.illness_better));
        varRcmndtn[3][0] = String.format(res.getString(R.string.motivation_worse));
        varRcmndtn[3][1] = String.format(res.getString(R.string.motivation_better));
        varRcmndtn[4][0] = String.format(res.getString(R.string.nutrition_worse));
        varRcmndtn[4][1] = String.format(res.getString(R.string.nutrition_better));
        varRcmndtn[5][0] = String.format(res.getString(R.string.soreness_worse));
        varRcmndtn[5][1] = String.format(res.getString(R.string.soreness_better));
        varRcmndtn[6][0] = String.format(res.getString(R.string.stress_worse));
        varRcmndtn[6][1] = String.format(res.getString(R.string.stress_better));
        varRcmndtn[7][0] = String.format(res.getString(R.string.taps_worse));
        varRcmndtn[7][1] = String.format(res.getString(R.string.taps_better));
        varRcmndtn[8][0] = String.format(res.getString(R.string.grip_worse));
        varRcmndtn[8][1] = String.format(res.getString(R.string.grip_better));
        varRcmndtn[9][0] = String.format(res.getString(R.string.ft_worse));
        varRcmndtn[9][1] = String.format(res.getString(R.string.ft_better));
        varRcmndtn[10][0] = String.format(res.getString(R.string.ct_worse));
        varRcmndtn[10][1] = String.format(res.getString(R.string.ct_better));
        varRcmndtn[11][0] = String.format(res.getString(R.string.weight_worse));
        varRcmndtn[11][1] = String.format(res.getString(R.string.weight_better));
        varRcmndtn[12][0] = String.format(res.getString(R.string.calories_worse));
        varRcmndtn[12][1] = String.format(res.getString(R.string.calories_better));
        varRcmndtn[13][0] = String.format(res.getString(R.string.protein_worse));
        varRcmndtn[13][1] = String.format(res.getString(R.string.protein_better));
        varRcmndtn[14][0] = String.format(res.getString(R.string.carbs_worse));
        varRcmndtn[14][1] = String.format(res.getString(R.string.carbs_better));
        varRcmndtn[15][0] = String.format(res.getString(R.string.fat_worse));
        varRcmndtn[15][1] = String.format(res.getString(R.string.fat_better));
    }


    public void getJSON(View view)
    {
        Log.i("calling", "background");
        new BackgroundTask().execute();
    }

    class BackgroundTask extends AsyncTask<Void,Void,String> {
        String json_url = "http://ec2-52-91-226-96.compute-1.amazonaws.com/JSONDownload.php";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(json_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();

                while ((JSON_string = bufferedReader.readLine()) != null) {
                    stringBuilder.append(JSON_string + "\n");
                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                return stringBuilder.toString().trim();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i("post", "execute");
            // DO work with data here !!!!!
            TextView textView = (TextView) view.findViewById(R.id.textView13);
            textView.setText(result);
            calcScore(result);
        }
    }
}

