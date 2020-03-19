package com.example.testproject.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.testproject.R;
import com.example.testproject.customview.FreezeLiveButton;

public class FreezeLiveButtonActivity extends AppCompatActivity {

    private FreezeLiveButton mFreezeLiveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freeze_live_button);
        mFreezeLiveButton = findViewById(R.id.freezelivebutton);
    }

    public void start(View view){
        mFreezeLiveButton.toggle();
    }

}
