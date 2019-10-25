package com.example.testproject.customview.waggleview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.testproject.R;

public class WaggleActivity extends AppCompatActivity {

    private WaggleView mWaggleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waggle);
        mWaggleView = findViewById(R.id.waggleView);
        mWaggleView.startWaggleLoop();
    }
}
