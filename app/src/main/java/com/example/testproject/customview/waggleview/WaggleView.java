package com.example.testproject.customview.waggleview;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

/**
 * 摇晃的View
 * created by xiaojun at 2019/10/25 17:43
 */
public class WaggleView extends AppCompatImageView {

    @Override
    public float getRotation() {
        return super.getRotation();
    }

    private ObjectAnimator mAnimator;

    public WaggleView(Context context) {
        this(context,null);
    }

    public WaggleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public WaggleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        PropertyValuesHolder rotationHolder = PropertyValuesHolder.ofFloat("Rotation",15f, -15f, 10f, -10f, -5f, 5f, 2.5f, -2.5f, 0,0,0,0,0,0,0,0,0,0,0);
        mAnimator = ObjectAnimator.ofPropertyValuesHolder(this,rotationHolder);
        mAnimator.setDuration(1500);
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.setRepeatMode(ValueAnimator.REVERSE);
    }

    public void startWaggleLoop(){
        mAnimator.start();
    }

    public void stopWaggleLoop(){
        mAnimator.cancel();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
