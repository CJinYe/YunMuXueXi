package com.icox.yunmuxuexi;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Administrator on 2015/4/22 0022.
 */
public class DrawView extends View {
    private int mDrawColor = Color.GRAY;
    private final int mBitmapResId = R.raw.track;

    private MainActivity mMainActivity;

    private Bitmap mBitmapRes;
    private Bitmap mNewBitmap;
    private Bitmap mShowBitmap;

    private Bitmap mTempBitmap;

    private int mNewBitmapWidth;
    private int mNewBitmapHeight;

    private int mNewBitmapMaxR;
    private int mNewBitmapMaxG;
    private int mNewBitmapMaxB;
    private int mNewBitmapMaxColor;

    private int mSetPixelR;
    private int mSetPixelG;
    private int mSetPixelB;

    private int mCurrentNumber;
    private int mPlayImage;
    private long mPlayTime;
    int color1;
    private boolean m_b_DrawOver;
    private boolean isPlaying = false;


    public DrawView(Context context) {
        super(context);
        init(context);
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void init(Context context) {
        mMainActivity = (MainActivity) context;

        Resources res = getResources();
        InputStream is = res.openRawResource(R.raw.track);
        mBitmapRes = BitmapFactory.decodeStream(is);

        mNewBitmapWidth = mBitmapRes.getWidth() / 5;
        mNewBitmapHeight = 400 / 5;

        initBitmaps(0);
    }

    public void initBitmaps(int number) {
        int flogNumber = 0;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (flogNumber == number) {
                    mNewBitmap = Bitmap.createBitmap(mBitmapRes, j * mNewBitmapWidth, i * mNewBitmapHeight, mNewBitmapWidth, mNewBitmapHeight);
                    mShowBitmap = Bitmap.createBitmap(mNewBitmapWidth, mNewBitmapHeight, Bitmap.Config.ARGB_8888);
                    setBackgroundDrawable(new BitmapDrawable(mShowBitmap));
                    m_b_DrawOver = true;

                    mTempBitmap = mShowBitmap;

                    mCurrentNumber = number;
                    mPlayTime = 0;
                    mPlayImage = 0;

                    isPlaying = false;

                    initRGB();
                    return;
                }

                flogNumber++;
            }
        }
    }

    private List<Integer> maxRList;
    private List<Integer> maxGList;
    private List<Integer> maxBList;
    private int indexR = 0;
    private int indexG = 0;
    private int indexB = 0;

    public void initRGB() {

        int color;
        int minColor = Color.rgb(255, 255, 255);
        maxRList = new ArrayList<>();
        maxGList = new ArrayList<>();
        maxBList = new ArrayList<>();
        indexR = 0;
        indexG = 0;
        indexB = 0;
        mNewBitmapMaxR = 0;
        mNewBitmapMaxG = 0;
        mNewBitmapMaxB = 0;
        mNewBitmapMaxColor = Color.rgb(0, 0, 0);
        for (int i = 0; i < mNewBitmapHeight; i++) {
            for (int j = 0; j < mNewBitmapWidth; j++) {
                color = mNewBitmap.getPixel(j, i);
                if (Color.red(color) > mNewBitmapMaxR && color != Color.BLACK) {
                    mNewBitmapMaxR = Color.red(color);
                }
                if (Color.green(color) > mNewBitmapMaxG && color != Color.BLACK) {
                    mNewBitmapMaxG = Color.green(color);
                }
                if (Color.blue(color) > mNewBitmapMaxB && color != Color.BLACK) {
                    mNewBitmapMaxB = Color.blue(color);
                }

                if (color > mNewBitmapMaxColor && color != Color.BLACK) {
                    mNewBitmapMaxColor = color;
                }

                if (color < minColor && color != Color.BLACK) {
                    minColor = color;
                }

                int r = Color.red(color);
                if (r != 0 && !maxRList.contains(r))
                    //                if (r != 0)
                    maxRList.add(r);


                int g = Color.green(color);
                if (g != 0 && !maxGList.contains(g))
                    //                if (g != 0)
                    maxGList.add(g);


                int b = Color.blue(color);
                if (b != 0 && !maxBList.contains(b))
                    //                if (b != 0)
                    maxBList.add(b);
            }
        }
        mSetPixelR = Color.red(minColor);
        mSetPixelG = Color.green(minColor);
        mSetPixelB = Color.blue(minColor);
        minColor = Color.rgb(mSetPixelR, mSetPixelG, mSetPixelB);
        for (int i = 0; i < mNewBitmapHeight; i++) {
            for (int j = 0; j < mNewBitmapWidth; j++) {
                color1 = mNewBitmap.getPixel(j, i);
                if (color1 != Color.BLACK) {
                    mShowBitmap.setPixel(j, i, mDrawColor);
                }
            }
        }

        Collections.sort(maxGList, new Comparator<Integer>() {
            @Override
            public int compare(Integer lhs, Integer rhs) {
                return lhs - rhs;
            }
        });
        Collections.sort(maxRList, new Comparator<Integer>() {
            @Override
            public int compare(Integer lhs, Integer rhs) {
                return lhs - rhs;
            }
        });
        Collections.sort(maxBList, new Comparator<Integer>() {
            @Override
            public int compare(Integer lhs, Integer rhs) {
                return lhs - rhs;
            }
        });

        indexR = maxRList.size() - 1;
    }

    float mStartX;
    float mStartY;

    int mMoveX;
    int mMoveY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = event.getX();
                mStartY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                mMoveX = (int) event.getX();
                mMoveY = (int) event.getY();
                bitmap(mMoveX, mMoveY);

                break;
            default:
                break;
        }
        return true;
    }

    public Bitmap bimapshow() {

        for (int i = 0; i < mNewBitmapHeight; i++) {
            for (int j = 0; j < mNewBitmapWidth; j++) {
                color1 = mNewBitmap.getPixel(j, i);
                if (color1 != Color.BLACK) {
                    mShowBitmap.setPixel(j, i, mDrawColor);
                }
            }
        }
        return mShowBitmap;
    }

    public void bitmap(int mStartX, int mStartY) {
        mStartX = mStartX * mNewBitmap.getWidth() / getWidth();
        mStartY = mStartY * mNewBitmap.getHeight() / getHeight();
        int nextDrawColor = Color.rgb(mSetPixelR, mSetPixelG, mSetPixelB);
        if (nextDrawColor > -8374150 && nextDrawColor < -8373000) {
            m_b_DrawOver = false;
        }

        int nextX = 0;
        int nextY = 0;
        for (int i = 0; i < mNewBitmapHeight; i++) {
            for (int j = 0; j < mNewBitmapWidth; j++) {
                color1 = mNewBitmap.getPixel(j, i);
                if (color1 == nextDrawColor) {
                    nextX = j;
                    nextY = i;
                    i = mNewBitmapHeight;
                    break;
                }
                int r = Color.red(color1);
                int g = Color.green(color1);
                int b = Color.blue(color1);
                if (maxRList.contains(r) && maxGList.contains(g) && maxBList.contains(b)
                        && maxRList.size() > 1
                        && indexG == maxGList.size() - 1) {
                    if (mStartX > (Math.abs(j - 5)) && mStartX < (j + 5)
                            && mStartY > (Math.abs(i - 5)) && mStartY < (i + 5)) {
                        m_b_DrawOver = false;
                        break;
                    }
                }
            }
        }
        if (mStartX > (Math.abs(nextX - 13)) && mStartX < (nextX + 13)
                && mStartY > (Math.abs(nextY - 13)) && mStartY < (nextY + 13)) {
            m_b_DrawOver = false;
        }

    }

    int current = 0;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        autoDraw();
        invalidate();
    }

    public void autoDraw() {
        if (m_b_DrawOver) {
            return;
        }

        int color;
        //        while (m_b_DrawOver == false) {
        for (int i = 0; i < mNewBitmapHeight; i++) {
            for (int j = 0; j < mNewBitmapWidth; j++) {
                color = mNewBitmap.getPixel(j, i);
                if (Color.red(color) == mSetPixelR && Color.green(color) == mSetPixelG && Color.blue(color) == mSetPixelB) {
                    mShowBitmap.setPixel(j, i, Color.RED);
                    //Log.e("TAG", "Y=" + i + " " + "X=" + j);
                }
               /* if(color!=Color.BLACK){
                    mShowBitmap.setPixel(j,i,Color.RED);
                }*/
            }
        }
        invalidate();

        if (mSetPixelR == Color.red(mNewBitmapMaxColor) && mSetPixelG == Color.green(mNewBitmapMaxColor) && mSetPixelB == Color.blue(mNewBitmapMaxColor)) {
            // 绘制完毕

            if (!isPlaying) {
                mMainActivity.loadSound(mCurrentNumber, 2);
                isPlaying = true;
            }
            m_b_DrawOver = true;
            return;
        }

        //            mSetPixelB = mSetPixelB + 8;
        //            if (mSetPixelB > mNewBitmapMaxB){
        //                mSetPixelB = 0;
        //                mSetPixelG = mSetPixelG + 8;
        //                if (mSetPixelG > mNewBitmapMaxG){
        //                    mSetPixelG = 0;
        //                    mSetPixelR = mSetPixelR + 8;
        //                }
        //            }
        getNextRGB();

        current++;
        if (current == 20) {
            current = 0;
            m_b_DrawOver = true;

        }
    }

    //    }

    private void getNextRGB() {
        if (mSetPixelB == mNewBitmapMaxB)
            mSetPixelB += 8;
        else
            mSetPixelB = maxBList.get(indexB);

        if (indexB < maxBList.size() - 1)
            indexB++;

        if (mSetPixelB > mNewBitmapMaxB) {
            mSetPixelB = 0;
            indexB = 0;

            if (mSetPixelG == mNewBitmapMaxG)
                mSetPixelG += 8;
            else
                mSetPixelG = maxGList.get(indexG);

            if (indexG < maxGList.size() - 1)
                indexG++;

            if (mSetPixelG > mNewBitmapMaxG) {
                //                mSetPixelB = 0;
                //                indexB = 0;
                mSetPixelG = 0;
                indexG = 0;
                if (mSetPixelR == mNewBitmapMaxR)
                    mSetPixelR += 8;
                else
                    mSetPixelR = maxRList.get(indexR);

                if (indexR < maxRList.size() - 1)
                    indexR++;

                if (mSetPixelR > mNewBitmapMaxR) {
                    indexR = 1;
                    indexB = 0;
                    indexG = 0;
                }

            }
        }

    }

}
