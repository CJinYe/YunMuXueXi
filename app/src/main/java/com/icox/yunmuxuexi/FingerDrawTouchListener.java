package com.icox.yunmuxuexi;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Administrator on 2015/4/23 0023.
 */
public class FingerDrawTouchListener implements View.OnTouchListener {
    private MainActivity mMainActivity;

    private ImageView mIvCanvas;
    private Bitmap mBaseBitmap;
    private Canvas mCanvas;
    private Paint mPaint;

    private float mStartX;
    private float mStartY;

    public FingerDrawTouchListener(MainActivity mainActivity){
        this.mMainActivity = mainActivity;

        mIvCanvas = (ImageView) mainActivity.findViewById(R.id.image03);

        // 初始化一个画笔，笔触宽度为5，颜色为红色
        mPaint = new Paint();
//        mPaint.setStrokeWidth(5);
//        mPaint.setColor(Color.RED);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFFFF0000);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(15);
    }

    public boolean onTouch(View view, MotionEvent event){
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (mBaseBitmap == null){
                    mBaseBitmap = Bitmap.createBitmap(mIvCanvas.getWidth(), mIvCanvas.getHeight(), Bitmap.Config.ARGB_8888);
                    mCanvas = new Canvas(mBaseBitmap);
                }
                mStartX = event.getX();
                mStartY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                mCanvas.drawLine(mStartX, mStartY, event.getX(), event.getY(), mPaint);
                mStartX = event.getX();
                mStartY = event.getY();
                mIvCanvas.setImageBitmap(mBaseBitmap);
                break;
            default:
                break;
        }
        return true;
    }

    // 清除画板
    public void resumeCanvas(){
        if (mBaseBitmap != null) {
            mBaseBitmap = Bitmap.createBitmap(mIvCanvas.getWidth(), mIvCanvas.getHeight(), Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBaseBitmap);
            mIvCanvas.setImageBitmap(mBaseBitmap);
        }
    }
}
