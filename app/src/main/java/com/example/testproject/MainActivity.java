package com.example.testproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.testproject.architecture_components.data_binding.activity.DataBindingActivity;
import com.example.testproject.scroller.ScrollerActivity;
import com.example.testproject.sina.SinaActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //蜘蛛网图
    public void cakeView(View view){
        startActivity(newIntent(CakeActivity.class));
    }

    //新浪页
    public void sina(View view){
        startActivity(newIntent(SinaActivity.class));
    }

    public void databinding(View view){
        startActivity(newIntent(DataBindingActivity.class));
    }

    //Scroller
    public void scroller(View view){
        startActivity(newIntent(ScrollerActivity.class));
    }

    private Intent newIntent(Class dstActivity){
        return new Intent(this,dstActivity);
    }
}
