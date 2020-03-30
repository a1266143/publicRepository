package com.example.testproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.testproject.activity.FreezeLiveButtonActivity;
import com.example.testproject.activity.MusicActivity;
import com.example.testproject.activity.PopupWindowActivity;
import com.example.testproject.activity.ProgressBarProActivity;
import com.example.testproject.architecture_components.data_binding.activity.DataBindingActivity;
import com.example.testproject.customview.BatteryActivity;
import com.example.testproject.customview.ruler.RulerActivity;
import com.example.testproject.customview.ruler.RulerActivity2;
import com.example.testproject.longpic.LongPicActivity;
import com.example.testproject.customview.updateview.UpdateViewActivity;
import com.example.testproject.customview.waggleview.WaggleActivity;
import com.example.testproject.recyclerview.RecyclerViewTestActivity;
import com.example.testproject.scroller.ScrollerActivity;
import com.example.testproject.sina.SinaActivity;


public class MainActivity extends AppCompatActivity {

    private Button mBtnLayoutmanager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtnLayoutmanager = findViewById(R.id.layoutmanager);
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

    public void updateView(View view){
        startActivity(newIntent(UpdateViewActivity.class));
    }

    public void waggle(View view){
        startActivity(newIntent(WaggleActivity.class));
    }

    public void audioTrack(View view){
//        AudioTrack audioTrack = new AudioTrack();
//        AudioFormat.ENCODING_
    }

    public void bolang(View view){
        startActivity(newIntent(LongPicActivity.class));
    }

    public void ruler(View view){ startActivity(newIntent(RulerActivity.class));}

    public void ruler2(View view){
        startActivity(newIntent(RulerActivity2.class));
    }

    public void surfaceview(View view){
        startActivity(newIntent(SurfaceViewActivity.class));
    }

    public void music(View view){
        startActivity(newIntent(MusicActivity.class));
    }

    public void progressBarPro(View view){
        startActivity(newIntent(ProgressBarProActivity.class));
    }

    public void freezelive(View view){
        startActivity(newIntent(FreezeLiveButtonActivity.class));
    }

    public void battery(View view){
        startActivity(newIntent(BatteryActivity.class));
    }

    public void layoutmanager(View view){
        startActivity(newIntent(RecyclerViewTestActivity.class));
    }

    public void popupwindow(View view){
        startActivity(newIntent(PopupWindowActivity.class));
    }

    private Intent newIntent(Class dstActivity){
        return new Intent(this,dstActivity);
    }
}
