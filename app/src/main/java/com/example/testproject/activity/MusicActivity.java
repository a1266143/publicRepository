package com.example.testproject.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.testproject.R;

public class MusicActivity extends AppCompatActivity {

    private MediaPlayer mPlayer;
    private Visualizer mVisualizer;

    public static int REQUEST_CODE_PERMISSION = 0x01;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        mPlayer = MediaPlayer.create(this,R.raw.shijian);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length>0&&grantResults[0] == PackageManager.PERMISSION_GRANTED){
            play(null);
        }else{
            Toast.makeText(this,"您拒绝了权限",Toast.LENGTH_SHORT).show();
        }
    }

    public void play(View view){
        //如果未获取到权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},MusicActivity.REQUEST_CODE_PERMISSION);
            return;
        }else{
            setupVisualizer();//初始化示波器
            mPlayer.start();
        }
    }

    private void setupVisualizer(){
        mVisualizer = new Visualizer(mPlayer.getAudioSessionId());
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        //设置监听器
        mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
            @Override
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
                Log.e("xiaojun","visualizer,waveform.size()="+waveform.length+",samplingRate="+samplingRate);
                for (int i = 0; i < waveform.length; i++) {
                    Log.e("xiaojun","waveForm="+(waveform[i]&0xFF));
                }
            }

            @Override
            public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
            }
        },Visualizer.getMaxCaptureRate()/2,true,false);
        mVisualizer.setEnabled(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()&&mPlayer!=null){
            mVisualizer.release();
            mPlayer.release();
        }
    }
}
