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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Jonny on 19/06/2016.
 */
public class HistoryFragment extends Fragment {

    int wellnessScore = 0;
    int  i = 0;
    int sum = 0;

    ArrayList<Integer> appetiteList = new ArrayList<>();


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
                //String name = jsonObject.optString("appetite").toString();
                //appetiteList.add(jsonObject.optString("appetite").toInteger());
                //sum = jsonObject.getInt("appetite");
                if (jsonObject.optString("appetite") != null) {
                    sum = Integer.parseInt(jsonObject.optString("appetite").toString());
                }
            }

            Log.i("json", String.valueOf(sum));

        } catch (JSONException e) {
            e.printStackTrace();
        }
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

