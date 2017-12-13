package com.icox.yunmuxuexi;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Administrator on 2015/4/23 0023.
 */
public class AutoDrawViewCopy extends View {
    private final int mDrawColor = Color.BLUE;
    private final int mBitmapResId = R.raw.track;

    private MainActivity mMainActivity;

    private Bitmap mBitmapRes;
    private Bitmap mNewBitmap;
    private Bitmap mShowBitmap;

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

    private boolean m_b_DrawOver;

    private List<TestBean> mPixels;
    private File mFileJson;
    private Context mContext;


    public AutoDrawViewCopy(Context context){
        super(context);
        init(context);
    }

    public AutoDrawViewCopy(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void init(Context context) {
        mContext = context;
        mMainActivity = (MainActivity) context;

        Resources res = getResources();
        InputStream is = res.openRawResource(mBitmapResId);
        mBitmapRes = BitmapFactory.decodeStream(is);

        mNewBitmapWidth = mBitmapRes.getWidth() / 5;
        mNewBitmapHeight = 400 / 5;

        initBitmaps(0);
    }

    public void initBitmaps(int number){

        Log.e("mytest","number = "+number);

        mFileJson = new File(Environment.getExternalStorageDirectory().getPath() + "/YunMuXueXi"+Constants.getIndex()+".txt");
        if (!mFileJson.exists())
            try {
                mFileJson.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        mPixels = new ArrayList();

        int flogNumber = 0;
        for (int i = 0; i < 5; i++){
            for (int j = 0; j < 5; j++){
                if (flogNumber == number){
                    mNewBitmap = Bitmap.createBitmap(mBitmapRes, j*mNewBitmapWidth, i*mNewBitmapHeight, mNewBitmapWidth, mNewBitmapHeight);
                    mShowBitmap = Bitmap.createBitmap(mNewBitmapWidth, mNewBitmapHeight, Bitmap.Config.ARGB_8888);
                    setBackgroundDrawable(new BitmapDrawable(mShowBitmap));

                    m_b_DrawOver = false;

                    mCurrentNumber = number;
                    mPlayTime = 0;
                    mPlayImage = 0;

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
    private int indexB = 1;

    public void initRGB(){
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
        for (int i = 0; i < mNewBitmapHeight; i++){
            for (int j = 0; j < mNewBitmapWidth; j++){
                color = mNewBitmap.getPixel(j, i);
                if (Color.red(color) > mNewBitmapMaxR && color != Color.BLACK){
                    mNewBitmapMaxR = Color.red(color);
                }
                if (Color.green(color) > mNewBitmapMaxG && color != Color.BLACK){
                    mNewBitmapMaxG = Color.green(color);
                }
                if (Color.blue(color) > mNewBitmapMaxB && color != Color.BLACK){
                    mNewBitmapMaxB = Color.blue(color);
                }

                if (color > mNewBitmapMaxColor && color != Color.BLACK){
                    mNewBitmapMaxColor = color;
                }

                if (color < minColor && color != Color.BLACK){
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

    public void autoDraw(){
        if (m_b_DrawOver){
            return;
        }

        int color;
        for (int i = 0; i < mNewBitmapHeight; i++){
            for (int j = 0; j < mNewBitmapWidth; j++){
                color = mNewBitmap.getPixel(j, i);
                if (Color.red(color) == mSetPixelR && Color.green(color) == mSetPixelG && Color.blue(color) == mSetPixelB){
                    mShowBitmap.setPixel(j, i, mDrawColor);
                    mPixels.add(new TestBean(j, i));
                }
            }
        }

        if (mSetPixelR == Color.red(mNewBitmapMaxColor) && mSetPixelG == Color.green(mNewBitmapMaxColor) && mSetPixelB == Color.blue(mNewBitmapMaxColor)){


            try {
                JSONObject root = new JSONObject();
                JSONArray pixels = new JSONArray();
                for (TestBean bean : mPixels) {
                    JSONObject pixel = new JSONObject();
                    pixel.put("x", bean.x);
                    pixel.put("y", bean.y);
                    pixels.put(pixel);
                }
                root.put("pixels", pixels);

                FileOutputStream fos = new FileOutputStream(mFileJson);
                fos.write(root.toString().getBytes());
                fos.close();
                //                Log.i("mytest", "成功 : " + root.toString());
                Toast.makeText(mContext, "成功", Toast.LENGTH_SHORT).show();
                Log.i("mytest", "成功 : "+mFileJson.getPath());
            } catch (JSONException e) {
                Log.e("mytest", "JSON 失败 " + e);
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                Log.e("mytest", "FileNotFoundException 失败 " + e);
                e.printStackTrace();
            } catch (IOException e) {
                Log.e("mytest", "IOException 失败 " + e);
                e.printStackTrace();
            }

            // 绘制完毕
            m_b_DrawOver = true;

            /*// 准备动画演示
            ImageView imageView = (ImageView) mMainActivity.findViewById(R.id.image02);
            imageView.setBackgroundResource(R.drawable.k002_002 + mCurrentNumber);*/

            // 加载声音并播放
            mMainActivity.loadSound(mCurrentNumber, 1);
            return;
        }

//        mSetPixelB = mSetPixelB + 8;
//        if (mSetPixelB > mNewBitmapMaxB){
//            mSetPixelB = 0;
//            mSetPixelG = mSetPixelG + 8;
//            if (mSetPixelG > mNewBitmapMaxG){
//                mSetPixelG = 0;
//                mSetPixelR = mSetPixelR + 8;
//            }
//        }
        getNextRGB();
    }


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

    // 动画演示
    public void playAnimation(){
        if (System.currentTimeMillis() - mPlayTime >= 600){
            mPlayTime = System.currentTimeMillis();

            ImageView imageView = (ImageView) mMainActivity.findViewById(R.id.playView);
            imageView.setBackgroundResource(R.drawable.k003_000 + mCurrentNumber * 4 + mPlayImage);
            mPlayImage++;
            if (mPlayImage > 3){
                mPlayImage = 0;
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        autoDraw();
        playAnimation();
        /*if (m_b_DrawOver){
            playAnimation();
        }*/

        invalidate();
    }

}