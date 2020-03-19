package com.example.testproject.customview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;


/**
 * 冻结激活View
 * created by xiaojun at 2019/12/17
 */
public class FreezeLiveButton extends AppCompatTextView {

    private final int COLOR_FREEZE_BACK = Color.parseColor("#2980b9");
    private final int COLOR_LIVE_BACK = Color.parseColor("#d35400");

    private Paint mPaint;
    private Paint mPaintText;
    private LinearGradient mLinearGradient;
    private int mWidth, mHeight;
    private Matrix matrix = new Matrix();
    private int mOffsetX,mOffsetXText;
    private int mAlphaText = 255;
    private String mText = "Freeze";

    public FreezeLiveButton(Context context) {
        this(context, null);
    }

    public FreezeLiveButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FreezeLiveButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.BLACK);
        mPaint.setMaskFilter(new BlurMaskFilter(3, BlurMaskFilter.Blur.INNER));

        mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintText.setColor(Color.WHITE);
        mPaintText.setTextAlign(Paint.Align.CENTER);
        mPaintText.setTextSize(40);

        setLayerType(LAYER_TYPE_SOFTWARE,null);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mPaint.setMaskFilter(new BlurMaskFilter(10,BlurMaskFilter.Blur.INNER));
                break;
            case MotionEvent.ACTION_UP:
                mPaint.setMaskFilter(new BlurMaskFilter(3,BlurMaskFilter.Blur.INNER));
                toggle();
                break;
        }
        invalidate();
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        this.mWidth = getMeasuredWidth();
        this.mHeight = getMeasuredHeight();
        int[] colors = {COLOR_LIVE_BACK, COLOR_FREEZE_BACK};
        float[] radius = {0f,  1.f};
        mLinearGradient = new LinearGradient(-mWidth, 0, 0, mHeight , colors, radius, Shader.TileMode.CLAMP);
        mPaint.setShader(mLinearGradient);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawButton(canvas);
        super.onDraw(canvas);
        drawText(canvas);
    }



    private ValueAnimator animator;
    private boolean isAnimating;

    //开始
    public void toggle() {
        String offsetX = "offsetx";
        String offsetXText = "offsetXtext";
        String textAlpha = "textalpha";
        if (mOffsetX < getMeasuredWidth() + getMeasuredWidth() / 2) {
            PropertyValuesHolder holder_offsetX = PropertyValuesHolder.ofInt(offsetX,0,2*getMeasuredWidth());
            PropertyValuesHolder holder_offsetXtext = PropertyValuesHolder.ofInt(offsetXText,0,10,0);
            PropertyValuesHolder holder_textalpha = PropertyValuesHolder.ofInt(textAlpha,255,0,255);
            animator = ValueAnimator.ofPropertyValuesHolder(holder_offsetX,holder_offsetXtext,holder_textalpha);
        } else {
            PropertyValuesHolder holder_offsetX = PropertyValuesHolder.ofInt(offsetX,2*getMeasuredWidth(),0);
            PropertyValuesHolder holder_offsetXtext = PropertyValuesHolder.ofInt(offsetXText,0,-10,0);
            PropertyValuesHolder holder_textalpha = PropertyValuesHolder.ofInt(textAlpha,255,0,255);
            animator = ValueAnimator.ofPropertyValuesHolder(holder_offsetX,holder_offsetXtext,holder_textalpha);
        }
        animator.addUpdateListener(animation -> {
            mOffsetX = (int) animation.getAnimatedValue(offsetX);
            mOffsetXText = (int) animation.getAnimatedValue(offsetXText);
            mAlphaText = (int) animation.getAnimatedValue(textAlpha);
            invalidate();
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                isAnimating = true;
                if (mText.equals("Freeze"))
                    mText = "Live";
                else
                    mText = "Freeze";
                invalidate();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isAnimating = false;

            }
        });
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(300);
        animator.start();
    }

    private void drawButton(Canvas canvas) {
        RectF rectF = new RectF(0, 0, mWidth, mHeight);
        matrix.setTranslate(mOffsetX, 0);
        mLinearGradient.setLocalMatrix(matrix);
        canvas.drawRoundRect(rectF,mHeight/2,mHeight/2,mPaint);
    }

    private void drawText(Canvas canvas){
        mPaintText.setAlpha(mAlphaText);
        canvas.drawText(mText,mWidth/2+mOffsetXText,mHeight/2+18,mPaintText);
    }

}
