package com.example.testproject.longpic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import com.example.testproject.R;

public class LongPicActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bolang);

        AppCompatImageView testImageView = findViewById(R.id.testImageview);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.bolang,options);
        Log.e("xiaojun","获取到的图片宽:"+bitmap.getWidth()+",获取到的图片高度:"+bitmap.getHeight());
        testImageView.setImageBitmap(bitmap);
    }
}
