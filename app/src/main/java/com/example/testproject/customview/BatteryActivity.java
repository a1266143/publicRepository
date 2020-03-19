package com.example.testproject.customview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;

import com.example.testproject.R;
import com.example.testproject.customview.batteryview.BatteryView;

public class BatteryActivity extends AppCompatActivity {

    private BatteryView batteryView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery);
        batteryView = findViewById(R.id.battery);
        batteryView.setCapacity(80,true);

        SeekBar seekBar = findViewById(R.id.seekbar);
        seekBar.setMax(100);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                batteryView.setCapacity(progress,false);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void charge(View view){
        batteryView.setCapacity(0,true);
    }
}
