package com.zhuelise.lab03bsharedpreferences;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    TextView personalBest;
    TextView clickHere;
    TextView displayCPS;
    ConstraintLayout layout;
    int clickCount = 0;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    int timeLength = 0;
    int personalBestCount= 0;
    long startTime;
    long endTime;
    SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        seekBar = (findViewById(R.id.seek_bar));
        layout = findViewById(R.id.constraint_layout);
        preferences = getSharedPreferences("com.zhuelise.lab03.sharedpreferences", Context.MODE_PRIVATE);
        editor = preferences.edit();
        personalBest = findViewById(R.id.personal_best_display);
        clickHere = findViewById(R.id.cps_textview);
        displayCPS = findViewById(R.id.cps_display_textview);
        clickHere.setOnClickListener(this);
        layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                editor.clear();
                setInitialValues();
                editor.apply();
                return false;
            }
        });
        setInitialValues();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                timeLength = seekBar.getProgress() * 1000; //time in milliseconds
                clickCount = 0;
                displayCPS.setText(getString(R.string.click_count, clickCount));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Snackbar snackbar = Snackbar.make(layout, getString(R.string.timer_length, seekBar.getProgress()), Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        if(clickCount==0) {
            startTime = System.currentTimeMillis();
            endTime = startTime + timeLength;
        }
        if((System.currentTimeMillis())<=(endTime)) {
            clickCount++;
            displayCPS.setText((getString(R.string.click_count, clickCount)).toString());
        }
        else {
            startTime = 0;
            endTime = 0;
            if((clickCount>=personalBestCount)){
                editor.putInt("personalBest", personalBestCount);
                editor.apply();
                personalBest.setText((getString(R.string.personal_best, personalBestCount)).toString());
            }
            displayCPS.setText(getString(R.string.cps, Integer.parseInt(String.valueOf(clickCount/(timeLength/1000)))));
            clickCount = 0;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setInitialValues();
    }
    private void setInitialValues(){
        personalBestCount = preferences.getInt("personalBest", 0);
        personalBest.setText((getString(R.string.personal_best, personalBestCount)).toString());
        seekBar.setProgress(preferences.getInt("seekBar", 25));
    }
    @Override
    protected void onPause() {
        super.onPause();
        editor.putInt("personalBest", personalBestCount);
        editor.putInt("seekBar", seekBar.getProgress());
        editor.apply();
    }
}