package com.icox.yunmuxuexi;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.icox.updateapp.utils.AES;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2015/4/23 0023.
 */
public class AutoDrawView extends View {
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

    private boolean isPlaying = false;
    private int index = 0;
    private Timer mTimer;
    private AES mAes;

    public AutoDrawView(Context context) {
        super(context);
        init(context);
    }

    public AutoDrawView(Context context, AttributeSet attrs) {
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

        mAes = new AES();

        initBitmaps(0);
    }

    public void initBitmaps(int number) {
        mCurrentNumber = number;
        m_b_DrawOver = true;
        index = 0;
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        mMainActivity.stopMediaPlayer();
        isPlaying = false;

        mShowBitmap = Bitmap.createBitmap(mNewBitmapWidth, mNewBitmapHeight, Bitmap.Config.ARGB_8888);
        setBackgroundDrawable(new BitmapDrawable(mShowBitmap));
        try {
            InputStream inputStream = mContext.getAssets().open("YunMuXueXi" + number + ".txt");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bf = new BufferedReader(inputStreamReader);
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = bf.readLine()) != null) {
                sb.append(line);
            }
            inputStream.close();
            inputStreamReader.close();
            bf.close();

            String json = mAes.stringFromJNIDecrypt(mContext, sb.toString());

            mPixels = new ArrayList<>();
            JSONObject root = new JSONObject(json);
            JSONArray array = root.getJSONArray("pixels");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                int x = object.getInt("x");
                int y = object.getInt("y");
                mPixels.add(new TestBean(x, y));
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        m_b_DrawOver = false;
        index = 0;
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {

                if (mPixels == null || mPixels.size() < 1 || index > mPixels.size())
                    return;

                TestBean bean = mPixels.get(index);
                mShowBitmap.setPixel(bean.x, bean.y, Color.BLUE);
                postInvalidate();

                if (index == mPixels.size() - 1) {
                    m_b_DrawOver = true;
                    Message message = new Message();
                    message.what = 1;
                    mHandler.sendMessage(message);
                    //                    nextDraw();
                    if (mTimer != null) {
                        mTimer.cancel();
                        mTimer = null;
                    }
                    return;
                }

                index++;

            }
        }, 100, 3);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!isPlaying) {
                // 加载声音并播放
                mMainActivity.loadSound(mCurrentNumber, 1);
                isPlaying = true;
            }
            super.handleMessage(msg);
        }
    };


    // 动画演示
    public void playAnimation() {
        if (System.currentTimeMillis() - mPlayTime >= 600) {
            mPlayTime = System.currentTimeMillis();

            ImageView imageView = (ImageView) mMainActivity.findViewById(R.id.playView);
            imageView.setBackgroundResource(R.drawable.k003_000 + mCurrentNumber * 4 + mPlayImage);
            mPlayImage++;
            if (mPlayImage > 3) {
                mPlayImage = 0;
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        playAnimation();

        invalidate();
    }

}