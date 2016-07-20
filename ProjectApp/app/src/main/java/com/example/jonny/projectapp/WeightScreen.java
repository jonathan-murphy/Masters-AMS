package com.example.jonny.projectapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.HashMap;

public class WeightScreen extends Activity {

    private static String nutrition_url = "http://ec2-52-91-226-96.compute-1.amazonaws.com/NutritionUpdate.php";

    private EditText weight, calories, protein, fats, carbs;
    private String wei, cal, pro, fat, car;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_screen);

        weight = (EditText) findViewById(R.id.weightVal);
        calories = (EditText) findViewById(R.id.caloriesVal);
        protein = (EditText) findViewById(R.id.proteinVal);
        carbs = (EditText) findViewById(R.id.carbsVal);
        fats = (EditText) findViewById(R.id.fatsVal);

        Button saveButton = (Button) findViewById(R.id.saveWeightButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                wei = weight.getText().toString();
                cal = calories.getText().toString();
                pro = protein.getText().toString();
                fat = fats.getText().toString();
                car = carbs.getText().toString();

                nutritionUpdate();

                Intent intent = new Intent(WeightScreen.this, HomeScreen.class);
                startActivity(intent);
            }
        });
    }

    private void nutritionUpdate(){

        class nutritionUpdate extends AsyncTask<Void,Void,String>{

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(WeightScreen.this,"Adding...","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(WeightScreen.this,s,Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                params.put("Weight",wei);
                params.put("Calories",cal);
                params.put("Protein",pro);
                params.put("Fat",fat);
                params.put("Carbs",car);

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest("http://ec2-52-91-226-96.compute-1.amazonaws.com/NutritionUpdate.php", params);
                return res;
            }
        }

        nutritionUpdate nu = new nutritionUpdate();
        nu.execute();
    }

}
