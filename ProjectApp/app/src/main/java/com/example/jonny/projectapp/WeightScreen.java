package com.example.jonny.projectapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by johnaton on 15/06/2016.
 */
public class WeightScreen extends Activity {

    TextView tv;

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

        Button btn = (Button) findViewById(R.id.button2);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                wei = weight.getText().toString();
                cal = calories.getText().toString();
                pro = protein.getText().toString();
                fat = fats.getText().toString();
                car = carbs.getText().toString();
                Toast.makeText(getApplicationContext(), pro, Toast.LENGTH_LONG).show();
            }
        });
    }
}
