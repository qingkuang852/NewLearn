package com.query;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

/**
 * Created by wenming on 2016/6/28.
 */
public class QueryAnimateView extends View {

    private static final int INIT = 0;
    private static final int QUERY = 1;
    private static final int WAITING = 2;
    private static final int COMPLETE = 3;
    //表示当前状态
    private int status = INIT;
    //画笔
    private Paint mPaint;

    //直线偏移量
    private float lineOff;
    //圆半径
    private float circleRadius;
    //View中心
    private float centerX;

    private Path mCirclePath;

    private RectF rectF = new RectF();

    public QueryAnimateView(Context context) {
        this(context,null);
    }

    public QueryAnimateView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public QueryAnimateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
        mCirclePath = new Path();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        lineOff = (float) (getMeasuredWidth()/3 / 1.414);
        circleRadius = getMeasuredWidth()/3;
        centerX = getWidth()/2;
        rectF.set(centerX - circleRadius, centerX - circleRadius, centerX + circleRadius, centerX + circleRadius);
    }

    private void initPaint(){
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        int strokeWidth = (int) dpToPx(3);
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    public boolean isCanQuery() {
        return status == INIT;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        switch (status){
            case INIT:
                canvas.drawCircle(getWidth()/2,getHeight()/2,circleRadius,mPaint);
                canvas.drawLine(getWidth()/2 + lineOff,getHeight()/2 + lineOff,getWidth(),getHeight(),mPaint);
                break;
            case QUERY:
                canvas.drawLine(getWidth()/2 + lineOff,getHeight()/2 + lineOff,lineX,lineX,mPaint);
                canvas.drawArc(rectF, startAngle, sweepAngle,false,mPaint);
                break;
            case WAITING:
                canvas.drawArc(rectF,startAngle,sweepAngle,false,mPaint);
                break;
            case COMPLETE:
                mCirclePath.reset();
                mCirclePath.moveTo(centerX - circleRadius, getHeight() / 2);
                mCirclePath.lineTo(toX1, toY1);
                mCirclePath.lineTo(toX2,toY2);
                canvas.drawPath(mCirclePath,mPaint);
                break;
        }
    }

    private float lineX;

    public void query(){
        status = QUERY;
        queryStatus();
    }

    //查询状态开始的动画
    private void queryStatus(){
        sweepAngle = 360;
        ValueAnimator animator1 = ValueAnimator.ofFloat(getWidth(),getWidth()/2 + lineOff);
        animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                lineX = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        ValueAnimator animator2 = ValueAnimator.ofFloat(-360, 5);

        animator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                sweepAngle = (float) animation.getAnimatedValue();
                invalidate();
            }
        });


        final AnimatorSet set = new AnimatorSet();
        animator1.setDuration(1000);
        animator2.setDuration(1000);
        set.play(animator1);
        set.play(animator2).after(800);
        set.start();
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (status==WAITING){
                    waitToComplete();
                }else if (status == QUERY){
                    waitingStatus();
                }
            }
        });
    }

    private float startAngle = 45,sweepAngle;

    private void waitingStatus(){
        final ValueAnimator animatorWaiting1 = ValueAnimator.ofFloat(5, 300);
        final ValueAnimator animatorWaiting2 = ValueAnimator.ofFloat(-300, 5);
        animatorWaiting1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Log.v("fag","sweep "+sweepAngle);
                sweepAngle = (float) animation.getAnimatedValue();
                Log.v("fag","sweepAngle  "+sweepAngle);
                startAngle += 5;
                invalidate();
            }
        });
        animatorWaiting2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                sweepAngle = (float) animation.getAnimatedValue();
                startAngle += 5;
                invalidate();
            }
        });

        animatorWaiting1.setInterpolator(new AccelerateInterpolator());
        animatorWaiting2.setInterpolator(new AccelerateInterpolator());
        animatorWaiting1.setDuration(500);
        animatorWaiting2.setDuration(500);

        animatorWaiting1.start();
        animatorWaiting1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                startAngle += 300;
                if (status==WAITING){
                    waitToComplete();
                }else  if (status == QUERY){
                    animatorWaiting2.start();
                }
            }
        });

        animatorWaiting2.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (status==WAITING){
                    waitToComplete();
                }else  if (status == QUERY){
                    animatorWaiting1.start();
                }
            }
        });

    }

    public void complete(){
        status = WAITING;
    }

    public void waitToComplete() {
        float curSweep = sweepAngle;

        ValueAnimator animator2 = ValueAnimator.ofFloat(curSweep, 360);
        animator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                startAngle += 5;
                sweepAngle = (float) animation.getAnimatedValue();
                invalidate();
            }
        });

        animator2.setDuration(500);
        animator2.start();

        animator2.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                startAngle = 180;
                completeStatus();
            }
        });

    }
    private float toX1,toX2,toY1,toY2;

    private void completeStatus(){
        ValueAnimator animator1 = ValueAnimator.ofFloat(-360, 0);
        animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                sweepAngle = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        animator1.setDuration(500);
        animator1.start();

        ValueAnimator valueAnimator3 = ValueAnimator.ofFloat(getWidth()/8,getWidth()/3);
        ValueAnimator valueAnimator4 = ValueAnimator.ofFloat(getHeight()/2,getHeight()*3/4);
        valueAnimator3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                toX1 = (float) animation.getAnimatedValue();
                toX2 = toX1;
                invalidate();
            }
        });

        valueAnimator4.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                toY1 = (float) animation.getAnimatedValue();
                toY2 = toY1;
            }
        });
        ValueAnimator valueAnimator5 = ValueAnimator.ofFloat(getWidth()/3,getWidth()- getWidth()/8);
        ValueAnimator valueAnimator6 = ValueAnimator.ofFloat(getHeight()*3/4,getHeight()/4);
        valueAnimator5.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                toX2 = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator6.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                toY2 = (float) animation.getAnimatedValue();
            }
        });
        final AnimatorSet set = new AnimatorSet();
        set.play(valueAnimator3).with(valueAnimator4);
        set.play(valueAnimator5).with(valueAnimator6).after(valueAnimator3);
        set.setDuration(500);

        animator1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (status == INIT){
                    return;
                }
                status = COMPLETE;
                set.start();
            }
        });
    }

    public void reQuery(){
        status = INIT;
        startAngle = 45;
        invalidate();
    }

    /*
       将dp转换为px
    */
    private float dpToPx(int dp){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}
