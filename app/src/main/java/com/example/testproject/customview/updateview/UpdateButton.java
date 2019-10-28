package com.example.testproject.customview.updateview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

/**
 * 升级Button
 * created by xiaojun at 2019/10/25 9:54
 */
public class UpdateButton extends View implements View.OnClickListener {

    private Paint mPaintBackground;
    private Paint mPaintForeground;
    private Paint mPaintButton;
    private Paint mPaintText;
    private LinearGradient mLinearGradient;
    private Matrix mMatrixGradient;//线性渐变矩阵
    private int mWidth, mHeight;
    private int mOffset;//线性渐变的移动位置
    private int mPaintButtonWidth;
    private int mLengthButton;
    private String mStr = "点击升级";
    private int mAlphaText = 255;

    public static int mDefaultColorFail = 0xFFEF5350;
    public static int mDefaultColorSuccess = 0xFF00E676;

    private Status mState;

    public UpdateButton(Context context) {
        this(context, null);
    }

    public UpdateButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UpdateButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private int mStrokeWidth = 80;

    private int mStrokeHeight;

    private void init() {
        mPaintBackground = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBackground.setStyle(Paint.Style.STROKE);
        mPaintBackground.setStrokeWidth(mStrokeWidth / 5);
        mPaintBackground.setColor(Color.GRAY);
        mPaintBackground.setStrokeCap(Paint.Cap.ROUND);

        mPaintForeground = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintForeground.setStyle(Paint.Style.STROKE);
        mPaintForeground.setStrokeWidth(mStrokeWidth / 5);
        mPaintForeground.setColor(Color.parseColor("#F9C370"));
        mPaintForeground.setStrokeCap(Paint.Cap.ROUND);

        mPaintButton = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintButton.setStyle(Paint.Style.STROKE);
        mPaintButton.setColor(Color.parseColor("#F9C370"));
        mPaintButton.setStrokeCap(Paint.Cap.ROUND);

        mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintText.setColor(Color.WHITE);
        mPaintText.setTextAlign(Paint.Align.CENTER);
        mPaintText.setTextSize(36);

        mMatrixGradient = new Matrix();

        mState = Status.Button;

        setOnClickListener(this);

        //关闭硬件加速
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mWidth = w;
        this.mHeight = h;
        this.mStrokeWidth = h / 2;
        mStrokeHeight = mStrokeWidth / 3;
        mPaintButtonWidth = mStrokeWidth * 2;
        mLengthButton = mWidth - mPaintButtonWidth / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画底层进度条背景
        drawBackgroundProgressBar(canvas);
        if (mState == Status.ProgressBar) {
            //画线性渐变
            drawLinearGradient();
            //画上层的进度条
            drawForegroundProgressBar(canvas);
        } else {
            //画按钮
            drawButton(canvas);
            //画文字
            drawText(canvas);
        }

    }

    //底层进度条背景
    private void drawBackgroundProgressBar(Canvas canvas) {
        mPaintBackground.setStrokeWidth(mPaintButtonWidth);
        canvas.drawLine(mPaintButtonWidth / 2, mHeight / 2, mWidth - mPaintButtonWidth / 2, mHeight / 2, mPaintBackground);
    }

    private void drawButton(Canvas canvas) {
        mPaintButton.setStrokeWidth(mPaintButtonWidth);
        canvas.drawLine(mPaintButtonWidth / 2, mHeight / 2, mLengthButton, mHeight / 2, mPaintButton);
    }

    //画线性渐变
    private void drawLinearGradient() {
        //通过外部动态改变mOffset的值，这里通过矩阵动态平移线性渐变的位置，以达到动态效果
        mMatrixGradient.setTranslate(mOffset, 0);//通过矩阵移动线性渐变
        mLinearGradient.setLocalMatrix(mMatrixGradient);
        mPaintForeground.setShader(mLinearGradient);
    }

    private float mOffsetProgressBar = mStrokeWidth;

    //上层进度条背景
    private void drawForegroundProgressBar(Canvas canvas) {
        mPaintForeground.setStrokeWidth(mPaintButtonWidth);
        canvas.drawLine(mPaintButtonWidth / 2, mHeight / 2, mOffsetProgressBar, mHeight / 2, mPaintForeground);
    }

    private ValueAnimator mAnimatorGradient;

    private void animateGradient() {
        mAnimatorGradient = ValueAnimator.ofInt(0, 2 * getMeasuredWidth());
        mAnimatorGradient.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mOffset = (int) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        mAnimatorGradient.setRepeatMode(ValueAnimator.RESTART);
        mAnimatorGradient.setRepeatCount(ValueAnimator.INFINITE);
        mAnimatorGradient.setDuration(1500);
        mAnimatorGradient.start();
        //初始化渐变的位置
        int[] colors = {mPaintForeground.getColor(), Color.WHITE, mPaintForeground.getColor()};
        float[] positions = {0, 0.1f, 0.2f};
        mLinearGradient = new LinearGradient(-getMeasuredWidth(), mHeight / 2, 0, mHeight / 2, colors, positions, Shader.TileMode.CLAMP);//填充模式为边缘填充
    }

    public void animateButtonEnd(String str,int endColor) {
        mStr = str;
        if (mAnimatorGradient!=null&&mAnimatorGradient.isRunning()) {
            mAnimatorGradient.cancel();
            mAnimatorGradient.removeAllUpdateListeners();
            mAnimatorGradient.removeAllListeners();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                setOnClickListener(null);
                mState = Status.Complete;
                final String alpha = "alpha";
                final String height = "height";
                final String length = "length";
                final String color = "color";
                PropertyValuesHolder alphaHolder = PropertyValuesHolder.ofInt(alpha, 0, 255);
                PropertyValuesHolder heightHolder = PropertyValuesHolder.ofInt(height, mPaintButtonWidth, mHeight);
                PropertyValuesHolder lengthHolder = PropertyValuesHolder.ofInt(length, (int) mOffsetProgressBar, mWidth - mHeight / 2);//mStrokeWidth = mHeight/2;
                PropertyValuesHolder colorHolder = PropertyValuesHolder.ofInt(color,0xFFF9C370,0xFFFF0000);
                final ValueAnimator animator = ValueAnimator.ofPropertyValuesHolder(alphaHolder, heightHolder, lengthHolder,colorHolder);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        mAlphaText = (int) animation.getAnimatedValue(alpha);
                        mPaintButtonWidth = (int) animation.getAnimatedValue(height);//画笔宽度
                        mLengthButton = (int) animation.getAnimatedValue(length);//画笔长度
                        Log.e("xiaojun","颜色值为:"+Integer.toHexString((int) animation.getAnimatedValue(color)));
//                        mPaintButton.setColor(Color.parseColor("#"+Integer.toHexString((int) animation.getAnimatedValue(color))));
                        postInvalidate();
                    }
                });
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mState = Status.Complete;
                        animator.removeAllUpdateListeners();
                        animator.removeAllListeners();
                    }
                });
                animator.setEvaluator(new ArgbEvaluator());
                animator.setDuration(400);
                Looper.prepare();
                animator.start();
                Looper.loop();
            }
        }).start();

        ValueAnimator colorAnimator = ValueAnimator.ofInt(0xFFF9C370,endColor);
        colorAnimator.setEvaluator(new ArgbEvaluator());
        colorAnimator.setDuration(400);
        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int color = (int) animation.getAnimatedValue();
                mPaintButton.setColor(color);
            }
        });
        colorAnimator.start();
    }

    private void animateButtonStart() {
        setOnClickListener(null);
        final String alpha = "alpha";
        final String height = "height";
        final String length = "length";
        PropertyValuesHolder alphaHolder = PropertyValuesHolder.ofInt(alpha, 255, 0);
        PropertyValuesHolder heightHolder = PropertyValuesHolder.ofInt(height, mPaintButtonWidth, mStrokeHeight);
        PropertyValuesHolder lengthHolder = PropertyValuesHolder.ofInt(length, mWidth - mPaintButtonWidth, 0);
        final ValueAnimator animator = ValueAnimator.ofPropertyValuesHolder(alphaHolder, heightHolder, lengthHolder);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mPaintButtonWidth = (int) animation.getAnimatedValue(height);
                mAlphaText = (int) animation.getAnimatedValue(alpha);
                mLengthButton = (int) animation.getAnimatedValue(length) + mPaintButtonWidth / 2;
                postInvalidate();
            }

        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mState = Status.ProgressBar;
                mOffsetProgressBar = mPaintButtonWidth / 2;
                animator.removeAllListeners();
                animator.removeAllUpdateListeners();
                animateGradient();//渐变动画
            }
        });
        animator.setDuration(600);
        animator.start();
    }

    private void drawText(Canvas canvas) {
        mPaintText.setAlpha(mAlphaText);
        canvas.drawText(mStr, mWidth / 2, mHeight / 2 + 15, mPaintText);
    }

    @Override
    public void onClick(View v) {
        animateButtonStart();
    }

    ValueAnimator mAnimatorPercentage;

    /**
     * 由外部动态设置百分比
     *
     * @param ratio
     */
    public void setPercentage(float ratio) {
        if (ratio > 1)
            ratio = 1;
        final float realWidth = (mWidth - mPaintButtonWidth) * ratio;//真实长度

        if (mAnimatorPercentage == null) {
            mAnimatorPercentage = ValueAnimator.ofFloat(mOffsetProgressBar, realWidth);
            final float finalRatio = ratio;
            mAnimatorPercentage.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mOffsetProgressBar = (float) animation.getAnimatedValue() + mPaintButtonWidth / 2;
                    postInvalidate();
                }
            });
        } else {
            mAnimatorPercentage.cancel();
            mAnimatorPercentage.setFloatValues(mOffsetProgressBar, realWidth);
        }
        mAnimatorPercentage.setDuration(300);
        mAnimatorPercentage.start();
    }

    enum Status {
        Button,
        ProgressBar,
        Complete,
    }
}
