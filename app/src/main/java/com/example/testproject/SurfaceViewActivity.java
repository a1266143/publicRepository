package com.example.testproject;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.example.testproject.customview.AnimationView;

public class SurfaceViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surface_view);

//        AnimationView animationView = findViewById(R.id.animationview);
//        animationView.setZOrderOnTop(true);
//        animationView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        /*FrameLayout container = findViewById(R.id.container);
        Button button = new Button(this);
        button.setText("Button");
        button.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ((FrameLayout.LayoutParams)button.getLayoutParams()).gravity=Gravity.CENTER;
        container.addView(button,button.getLayoutParams());*/

    }
}
