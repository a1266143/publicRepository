package com.example.testproject.sina;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;

import com.example.testproject.R;

import java.io.IOException;

import retrofit2.Retrofit;

/**
 * mofang xinlang yemian
 * created by xiaojun at 2019/10/17 9:43
 */
public class SinaActivity extends AppCompatActivity {

    private Retrofit mRetrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sina);
        BaiduService();
    }

    public void BaiduService(){
        String url = "http://audio04.dmhmusic.com/71_53_T10046221354_128_4_1_0_sdk-cpm/cn/0207/M00/66/33/ChR47FsrnnaAaaArAEBKMzL4rQM751.mp3?xcode=e079ee4a29e6285d55ac5e8673fe60bfd1f4a8f";
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(url);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("xiaojun","网络问题");
        }
        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("xiaojun","prepare failed");
        }
        mediaPlayer.start();

    }

}
