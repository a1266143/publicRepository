package com.example.testproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import java.util.ArrayList;
import java.util.List;

public class CakeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);
        CakeView cakeView = findViewById(R.id.cakeview);
        List<Float> list = new ArrayList<>();
        list.add(0.7f);
        list.add(0.65f);
        list.add(0.55f);
        list.add(0.95f);
        list.add(0.9f);
        cakeView.setDataList(list);
    }

}
