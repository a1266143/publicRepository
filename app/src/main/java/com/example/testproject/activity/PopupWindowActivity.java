package com.example.testproject.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.testproject.R;

public class PopupWindowActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_window);
        mBtn = findViewById(R.id.activity_popup_window_btn);
        mBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(this,"点击",Toast.LENGTH_SHORT).show();
        View contentView = getLayoutInflater().inflate(R.layout.layout_popupwindow,null,false);
        PopupWindow popupWindow = new PopupWindow(contentView, WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT,true);
        //测量
        popupWindow.getContentView().measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED);
        popupWindow.setOutsideTouchable(true);
        Log.e("xiaojun","getPopupwindow.getheight="+popupWindow.getContentView().getMeasuredHeight());
        //获取button在屏幕上的位置
        int[] buttonPosition = new int[2];
        v.getLocationInWindow(buttonPosition);//v的左上角坐标
        //计算button Parent在屏幕上的位置，因为popupwindow是在button的Parent作为坐标系的
        int[] buttonParentPosition = new int[2];
        ((View)v.getParent()).getLocationInWindow(buttonParentPosition);//v的parent的左上角坐标
        Log.e("xiaojun","button的y="+buttonPosition[1]);
        //计算popupWindow真正要显示的位置
        int offsetY = buttonPosition[1]-popupWindow.getContentView().getMeasuredHeight();

        popupWindow.showAtLocation(v,Gravity.CENTER_HORIZONTAL|Gravity.TOP,0,offsetY);

    }

}
