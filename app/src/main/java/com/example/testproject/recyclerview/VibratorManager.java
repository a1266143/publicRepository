package com.example.testproject.recyclerview;

import android.app.Application;
import android.content.Context;
import android.os.Vibrator;

/**
 * 震动管理器类
 * created by xiaojun at 2020/3/24
 */
public class VibratorManager {

    private static VibratorManager sVibratorManager = new VibratorManager();
    private Vibrator mVibrator;
    private Context mContext;

    public static VibratorManager getInstance(){
        return sVibratorManager;
    }

    /**
     * 初始化
     * @param context
     */
    public void init(Context context){
        if (context instanceof Application){
            this.mContext = context;
        }else{
            throw new RuntimeException("init must with Application Context");
        }
    }

    /**
     * 检查是否初始化
     */
    private void checkContextException(){
        if (mContext == null)
            throw new RuntimeException("you must init first");
    }

    private VibratorManager(){
        mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
    }

    /**
     * 开始震动
     */
    public void start(){
        if (mVibrator.hasVibrator())
            mVibrator.vibrate(40);
    }

}
