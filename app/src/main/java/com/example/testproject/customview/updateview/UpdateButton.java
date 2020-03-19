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
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.testproject.R;


/**
 * 升级Button
 * created by xiaojun at 2019/10/25 9:54
 */
public class UpdateButton extends View implements View.OnClickListener {

    private Paint mPaintBackground;
    private Paint mPaintForeground;
    private Paint mPaintButton;
    private Paint mPaintText;
    private Paint mPaintTextProgress;
    private LinearGradient mLinearGradient;
    private Matrix mMatrixGradient;//线性渐变矩阵
    private int mWidth, mHeight;
    private int mOffset;//线性渐变的移动位置
    private int mPaintButtonWidth;
    private int mLengthButton;
    private String mStr = getContext().getString(R.string.click_to_update);
    private int mAlphaText = 255;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public static int mDefaultColorFail = 0xFFEF5350;
    public static int mDefaultColorSuccess = 0xFF00E676;

    private static int mDefaultInitColor = 0xFFFFAB00;

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
        mPaintForeground.setColor(mDefaultInitColor);
        mPaintForeground.setStrokeCap(Paint.Cap.ROUND);

        mPaintButton = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintButton.setStyle(Paint.Style.STROKE);
        mPaintButton.setColor(mDefaultInitColor);
        mPaintButton.setStrokeCap(Paint.Cap.ROUND);

        mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintText.setColor(Color.WHITE);
        mPaintText.setTextAlign(Paint.Align.CENTER);
        mPaintText.setTextSize(36);

        mPaintTextProgress = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintTextProgress.setColor(mDefaultInitColor);
        mPaintTextProgress.setTextAlign(Paint.Align.CENTER);
        mPaintTextProgress.setTextSize(24);

        mMatrixGradient = new Matrix();

        mState = Status.Button;

        setOnClickListener(this);

        //关闭硬件加速
//        setLayerType(LAYER_TYPE_SOFTWARE, null);
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
        mPercentageTextPosY = calculatePercentagePosY();
    }

    private float mPercentageTextPosY;

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
            //画进度数字文字
            drawProgressText(canvas);
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

    private String mPercentage = "0%";
    private float mOffsetProgressText;

    private void drawProgressText(Canvas canvas){
        canvas.drawText(mPercentage,mOffsetProgressText, mPercentageTextPosY,mPaintTextProgress);
    }

    private ValueAnimator mAnimatorGradient;

    private void animateGradient() {
        mAnimatorGradient = ValueAnimator.ofInt(0, 2 * getMeasuredWidth());
        mAnimatorGradient.addUpdateListener(animation -> {
            mOffset = (int) animation.getAnimatedValue();
            postInvalidate();
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


    /**
     * @param str
     * @param endColor
     * @param cancelGradientAnimate 是否取消渐变动画
     */
    public void animateButtonEnd(String str, int endColor, boolean cancelGradientAnimate) {
        mStr = str;
        if (mAnimatorGradient != null && mAnimatorGradient.isRunning()) {
            mHandler.post(() -> mAnimatorGradient.cancel());
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
                PropertyValuesHolder colorHolder = PropertyValuesHolder.ofInt(color, 0xFFF9C370, 0xFFFF0000);
                final ValueAnimator animator = ValueAnimator.ofPropertyValuesHolder(alphaHolder, heightHolder, lengthHolder, colorHolder);
                animator.addUpdateListener(animation -> {
                    mAlphaText = (int) animation.getAnimatedValue(alpha);
                    mPaintButtonWidth = (int) animation.getAnimatedValue(height);//画笔宽度
                    mLengthButton = (int) animation.getAnimatedValue(length);//画笔长度
//                        mPaintButton.setColor(Color.parseColor("#"+Integer.toHexString((int) animation.getAnimatedValue(color))));
                    postInvalidate();
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
                mHandler.post(() -> animator.start());
            }
        }).start();

        ValueAnimator colorAnimator = ValueAnimator.ofInt(0xFFF9C370, endColor);
        colorAnimator.setEvaluator(new ArgbEvaluator());
        colorAnimator.setDuration(400);
        colorAnimator.addUpdateListener(animation -> {
            int color = (int) animation.getAnimatedValue();
            mPaintButton.setColor(color);
        });
        mHandler.post(colorAnimator::start);
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
        animator.addUpdateListener(animation -> {
            mPaintButtonWidth = (int) animation.getAnimatedValue(height);
            mAlphaText = (int) animation.getAnimatedValue(alpha);
            mLengthButton = (int) animation.getAnimatedValue(length) + mPaintButtonWidth / 2;
            postInvalidate();
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mState = Status.ProgressBar;
                mOffsetProgressBar = mPaintButtonWidth / 2;
                animator.removeAllListeners();
                animator.removeAllUpdateListeners();
                calculatePercentagePosX(mPercentage+"%",mOffsetProgressBar);
                animateGradient();//渐变动画
                if (mListener != null)
                    mListener.onClick(UpdateButton.this);
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

    /**
     * 由外部动态设置百分比
     *
     * @param ratio
     */
    public void setPercentage(float ratio) {
        if (ratio > 1)
            ratio = 1;
        final float realWidth = (mWidth - mPaintButtonWidth) * ratio;//真实长度
        mOffsetProgressBar = realWidth + mPaintButtonWidth / 2;
        String percentage = ratio*100+"";
        mPercentage = percentage.substring(0,percentage.indexOf("."))+"%";
        calculatePercentagePosX(mPercentage,mOffsetProgressBar);//重新确定百分比文本的位置
        postInvalidate();
    }

    //计算百分比文本的y坐标
    private float calculatePercentagePosY(){
        Paint.FontMetrics fontMetrics = mPaintTextProgress.getFontMetrics();
        float top = fontMetrics.top;
        float bottom = fontMetrics.bottom;//baseline默认为0，baseline之上为负，之下为正
        float heightText = bottom - top;//文字高度
        return mHeight/2+mStrokeHeight/2+heightText;
    }

    //计算百分比文本的x坐标
    private void calculatePercentagePosX(String text,float offsetX){
        float width = mPaintTextProgress.measureText(text);
        float halfWidth = width/2.f;
        mOffsetProgressText = mOffsetProgressBar;
        if (halfWidth>offsetX){
            mOffsetProgressText += (halfWidth-offsetX);
        }
        if ((offsetX+halfWidth)>mWidth){
            mOffsetProgressText -= offsetX+halfWidth-mWidth;
        }
    }

    enum Status {
        Button,
        ProgressBar,
        Complete,
    }

    private OnClickListener mListener;

    public void setListener(OnClickListener listener) {
        this.mListener = listener;
    }
}
