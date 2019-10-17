package com.example.testproject.cakeview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroupOverlay;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.testproject.R;

import java.util.ArrayList;
import java.util.List;

public class CakeActivity extends AppCompatActivity {

    private LinearLayout mLlout,mLl1,mLl2,mLl3;
    private TextView mTv1,mTv2,mTv3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);
        CakeView cakeView = findViewById(R.id.cakeview);
        List<Float> list = new ArrayList<>();
        list.add(0.7f);
        list.add(0.65f);
        list.add(0.55f);
        list.add(0.71f);
        list.add(0.9f);
        cakeView.setDataList(list);
    }

}
