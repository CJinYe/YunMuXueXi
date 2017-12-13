package com.icox.yunmuxuexi;

import android.view.View;
import android.widget.ImageView;

/**
 * Created by Administrator on 2015/4/23 0023.
 */
public class ClickListener implements View.OnClickListener {
    private MainActivity mMainActivity;
    AutoDrawView autoDrawView;
    DrawView drawView;
    GlobalData globalData;

    public ClickListener(MainActivity mainActivity){
        this.mMainActivity = mainActivity;
        autoDrawView = (AutoDrawView) mMainActivity.findViewById(R.id.adView);
        drawView= (DrawView) mMainActivity.findViewById(R.id.adView1);
        globalData = (GlobalData)mainActivity.getApplicationContext();
    }

    public void onClick(View view){

        switch (view.getId()){
            case R.id.image0:
            case R.id.image1:
            case R.id.image2:
            case R.id.image3:
            case R.id.image4:
            case R.id.image5:
            case R.id.image6:
            case R.id.image7:
            case R.id.image8:
            case R.id.image9:
            case R.id.image10:
            case R.id.image11:
            case R.id.image12:
            case R.id.image13:
            case R.id.image14:
            case R.id.image15:
            case R.id.image16:
            case R.id.image17:
            case R.id.image18:
            case R.id.image19:
            case R.id.image20:
            case R.id.image21:
            case R.id.image22:
            case R.id.image23:
                // 暂停动画演示声音
                mMainActivity.stopMediaPlayer();
                // 改变图片按钮背景
                // 之前焦点按钮

                ImageView imageView = (ImageView) mMainActivity.findViewById(globalData.getmPreFocusId());
                imageView.setImageResource(R.drawable.c01 + (globalData.getmPreFocusId() - R.id.image0));
                // 当前焦点
                imageView = (ImageView) mMainActivity.findViewById(view.getId());
                imageView.setImageResource(R.drawable.c01 + (view.getId() - R.id.image0));
                // 更新焦点值
                globalData.setmPreFocusId(view.getId());
                // 清除动画演示
                imageView = (ImageView) mMainActivity.findViewById(R.id.playView);
                imageView.setBackgroundResource(R.drawable.kj);
                /*imageView = (ImageView) mMainActivity.findViewById(R.id.image2);
                imageView.setBackgroundResource(R.drawable.k002_001);*/
                // 清除手绘
               // mMainActivity.resumeFingerDraw();
                // 加载声音
                mMainActivity.loadSound(view.getId() - R.id.image0, 0);
                // 描绘
                autoDrawView.initBitmaps(view.getId() - R.id.image0);
                drawView.initBitmaps(view.getId()-R.id.image0);
                break;
            case R.id.shufayanshi:
                autoDrawView.initBitmaps(globalData.getmPreFocusId()-R.id.image0);
                // 清除手绘
               // mMainActivity.resumeFingerDraw();
                break;
            case R.id.chongxinlianxi:
                drawView.initBitmaps(globalData.getmPreFocusId()-R.id.image0);
                break;
            default:
                break;
        }
    }
}
