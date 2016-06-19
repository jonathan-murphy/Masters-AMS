package com.example.jonny.projectapp;

import android.app.Activity;
import android.content.Intent;
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

        Button saveButton = (Button) findViewById(R.id.saveWeightButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                wei = weight.getText().toString();
                cal = calories.getText().toString();
                pro = protein.getText().toString();
                fat = fats.getText().toString();
                car = carbs.getText().toString();
                Toast.makeText(getApplicationContext(), "SAVED", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(WeightScreen.this, HomeScreen.class);
                startActivity(intent);
            }
        });
    }
}
