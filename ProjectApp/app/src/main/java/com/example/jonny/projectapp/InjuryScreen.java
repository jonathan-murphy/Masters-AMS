package com.example.jonny.projectapp;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

public class InjuryScreen extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_injury_screen);

        final LinearLayout ll =  (LinearLayout)findViewById(R.id.linearLayout2);
        final TextView tv = new TextView(this);
        final TextView tv2 = new TextView(this);
        tv.setText("NEW!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        tv2.setText("NEWER!!!");
//        ll.addView(tv);

        final Button popupButton = (Button) findViewById(R.id.button);

        popupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(InjuryScreen.this, popupButton);


                final PopupMenu popupCore = new PopupMenu(InjuryScreen.this, popupButton);
                popup.getMenuInflater().inflate(R.menu.menu_injury_screen, popup.getMenu());


                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.item_core:
                                Toast.makeText(InjuryScreen.this, "Core selected", Toast.LENGTH_SHORT).show();
                                popupCore.getMenuInflater().inflate(R.menu.menu_injury_core, popupCore.getMenu());
                                popupCore.show();
                                return true;
                            case R.id.item_lowerBody:
                                Toast.makeText(InjuryScreen.this, "Lower body selected", Toast.LENGTH_SHORT).show();
                                ll.addView(tv);
                                return true;
                            case R.id.item_upperBody:
                                Toast.makeText(InjuryScreen.this, "Upper body selected", Toast.LENGTH_SHORT).show();
                                ll.addView(tv2);
                                return true;
                        }
                        return true;
                    }
                });
                popup.show();//showing popup menu
            }
        });//closing the setOnClickListener method
    }
}
