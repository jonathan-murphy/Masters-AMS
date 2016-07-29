package com.example.jonny.projectapp;

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
    int wellnessScore = 0;
    int  i = 0;
    int sum = 0;
    int currentVal = 0;
    double mean = 0;
    double stdDev = 0;
    boolean flag = false;

    double mAppetite = 0;
    double sdAppetite = 0;
    double mMood = 0;
    double sdMood = 0;
    double mIllness = 0;
    double sdIllness = 0;
    double mMotivation = 0;
    double sdMotivation = 0;
    double mNutrition = 0;
    double sdNutrition = 0;
    double mSoreness = 0;
    double sdSoreness = 0;
    double mStress = 0;
    double sdStress = 0;
    double mGrip = 0;
    double sdGrip = 0;
    double mTaps = 0;
    double sdTaps = 0;
    double mFT = 0;
    double sdFT = 0;
    double mCT = 0;
    double sdCT = 0;
    double mWeight = 0;
    double sdWeight = 0;
    double mCalories = 0;
    double sdCalories = 0;
    double mProtein = 0;
    double sdProtein = 0;
    double mFat = 0;
    double sdFat = 0;
    double mCarbs = 0;
    double sdCarbs = 0;

    boolean appetiteFLAG = false;
    boolean moodFLAG = false;
    boolean illnessFLAG = false;
    boolean motivationFLAG = false;
    boolean nutritionFLAG = false;
    boolean sorenessFLAG = false;
    boolean stressFLAG = false;
    boolean testFLAG = false;
    boolean weightFLAG = false;
    boolean caloriesFLAG = false;

    String[] varNames = new String[] {"appetite", "mood", "illness", "motivation", "nutrition", "soreness", "stress", "taps", "grip", "FT", "CT", "weight", "calories", "protein", "fat", "carbs"};
    Double[][] varMeanStdDev  = new Double[varNames.length][2]; // 2d array to store mean and standard deviation values
    Boolean[][] varFlags = new Boolean[varNames.length][2]; // 2d array to store worse and better flags for values
    String[][] varRcmndtn = new String[varNames.length][2]; // 2d array to store recommendations for different flags
    Integer[][] userData = new Integer[varNames.length][28]; // creating 2d array to store all user data from json

//    ArrayList<Integer> appetiteList = new ArrayList<>();
//    ArrayList<Integer> moodList = new ArrayList<>();
//    ArrayList<Integer> illnessList = new ArrayList<>();
//    ArrayList<Integer> motivationList = new ArrayList<>();
//    ArrayList<Integer> nutritionList = new ArrayList<>();
//    ArrayList<Integer> sorenessList = new ArrayList<>();
//    ArrayList<Integer> stressList = new ArrayList<>();
//    ArrayList<Integer> gripList = new ArrayList<>();
//    ArrayList<Integer> tapsList = new ArrayList<>();
//    ArrayList<Integer> FTList = new ArrayList<>();
//    ArrayList<Integer> CTList = new ArrayList<>();
//    ArrayList<Integer> weightList = new ArrayList<>();
//    ArrayList<Integer> caloriesList = new ArrayList<>();
//    ArrayList<Integer> proteinList = new ArrayList<>();
//    ArrayList<Integer> fatList = new ArrayList<>();
//    ArrayList<Integer> carbsList = new ArrayList<>();

    ArrayList<ArrayList<Integer>> allUserData = new ArrayList<ArrayList<Integer>>();

    ArrayList<Integer> appetite = new ArrayList<>();
    ArrayList<Integer> mood = new ArrayList<>();
    ArrayList<Integer> illness = new ArrayList<>();
    ArrayList<Integer> motivation = new ArrayList<>();
    ArrayList<Integer> nutrition = new ArrayList<>();
    ArrayList<Integer> soreness = new ArrayList<>();
    ArrayList<Integer> stress = new ArrayList<>();
    ArrayList<Integer> grip = new ArrayList<>();
    ArrayList<Integer> taps = new ArrayList<>();
    ArrayList<Integer> FT = new ArrayList<>();
    ArrayList<Integer> CT = new ArrayList<>();
    ArrayList<Integer> weight = new ArrayList<>();
    ArrayList<Integer> calories = new ArrayList<>();
    ArrayList<Integer> protein = new ArrayList<>();
    ArrayList<Integer> fat = new ArrayList<>();
    ArrayList<Integer> carbs = new ArrayList<>();

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
//                if (jsonObject.has("appetite")) {
//                    appetite.add(Integer.parseInt(jsonObject.optString("appetite").toString()));
//                }
//                if (jsonObject.has("mood")) {
//                    mood.add(Integer.parseInt(jsonObject.optString("mood").toString()));
//                }
//                if (jsonObject.has("illness")) {
//                    illness.add(Integer.parseInt(jsonObject.optString("illness").toString()));
//                }
//                if (jsonObject.has("motivation")) {
//                    motivation.add(Integer.parseInt(jsonObject.optString("motivation").toString()));
//                }
//                if (jsonObject.has("nutrition")) {
//                    nutrition.add(Integer.parseInt(jsonObject.optString("nutrition").toString()));
//                }
//                if (jsonObject.has("soreness")) {
//                    soreness.add(Integer.parseInt(jsonObject.optString("soreness").toString()));
//                }
//                if (jsonObject.has("stress")) {
//                    stress.add(Integer.parseInt(jsonObject.optString("stress").toString()));
//                }
//                if (jsonObject.has("taps") && jsonObject.optString("taps") != null) {
//                    taps.add(Integer.parseInt(jsonObject.optString("taps").toString()));
//                }
////                if (jsonObject.has("grip") && jsonObject.optString("grip").toString() != null) {
////                    gripList.add(Integer.parseInt(jsonObject.optString("grip").toString()));
////                }
//                if (jsonObject.has("FT") && jsonObject.optString("FT") != null) {
//                    FT.add(Integer.parseInt(jsonObject.optString("FT").toString()));
//                }
//                if (jsonObject.has("CT") && jsonObject.optString("CT") != null) {
//                    CT.add(Integer.parseInt(jsonObject.optString("CT").toString()));
//                }
//                if (jsonObject.has("weight")) {
//                    weight.add(Integer.parseInt(jsonObject.optString("weight").toString()));
//                }
//                if (jsonObject.has("calories")) {
//                    calories.add(Integer.parseInt(jsonObject.optString("calories").toString()));
//                }
//                if (jsonObject.has("protein")) {
//                    protein.add(Integer.parseInt(jsonObject.optString("protein").toString()));
//                }
//                if (jsonObject.has("fat")) {
//                    fat.add(Integer.parseInt(jsonObject.optString("fat").toString()));
//                }
//                if (jsonObject.has("carbs")) {
//                    carbs.add(Integer.parseInt(jsonObject.optString("carbs").toString()));
//                }
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

//            Log.i("json", String.valueOf(appetiteList));
//            Log.i("json", String.valueOf(moodList));
//            Log.i("json", String.valueOf(illnessList));
//            Log.i("json", String.valueOf(motivationList));
//            Log.i("json", String.valueOf(nutritionList));
//            Log.i("json", String.valueOf(sorenessList));
//            Log.i("json", String.valueOf(stressList));
//            Log.i("json", String.valueOf(tapsList));
//            Log.i("json", String.valueOf(gripList));
//            Log.i("json", String.valueOf(FTList));
//            Log.i("json", String.valueOf(CTList));
//            Log.i("json", String.valueOf(weightList));
//            Log.i("json", String.valueOf(caloriesList));
//            Log.i("json", String.valueOf(proteinList));
//            Log.i("json", String.valueOf(fatList));
//            Log.i("json", String.valueOf(carbsList));

//            String a = varNames[i];
//
            for (i = 0; i < varNames.length; i++) {
                varMeanStdDev[i][0]  = calcMean(i);
                varMeanStdDev[i][1]  = calcStdDev(varMeanStdDev[i][0],i);
                varFlags[i][0]  = checkWorse(varMeanStdDev[i][0],varMeanStdDev[i][1],i);
                varFlags[i][1]  = checkBetter(varMeanStdDev[i][0],varMeanStdDev[i][1],i);
                Log.i("worse", String.valueOf(varFlags[i][0]));
                Log.i("better", String.valueOf(varFlags[i][1]));
            }

            //double mean  = calcMean(0);

            //Log.i("mean", String.valueOf(mean));

//            mAppetite = calcMean(appetiteList);
//            sdAppetite = calcStdDev(mAppetite, appetiteList);
//            appetiteFLAG = checkWorse(mAppetite, sdAppetite, appetiteList);
//            boolean appetiteFLAG2 = checkBetter(mAppetite, sdAppetite, appetiteList);

//            Log.i("mean", String.valueOf(mAppetite));
//            Log.i("stdDev", String.valueOf(sdAppetite));
//            Log.i("worse", String.valueOf(appetiteFLAG));
//            Log.i("better", String.valueOf(appetiteFLAG2));

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
                }
            }
        }
        return flag;
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

