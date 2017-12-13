package com.icox.yunmuxuexi;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.icox.updateapp.BaseAppActivity;

import java.util.HashMap;
import java.util.List;

import cn.icoxedu.app_login.SetupCheck;


public class MainActivity extends BaseAppActivity {
    //private FingerDrawTouchListener mFDTL;

    private MediaPlayer mMediaPlayer;
    private SoundPool mSoundPool;
    private HashMap<Integer, Integer> mSoundPoolMap;


    //注册检测服务名
    private final String ICOX_SERVICE = "icoxapp.checking";
    //注册程序返回信息标记
    private final String ICOX_RETURN = "ICOX_SETUP";
    //注册状态 1为已注册 其他为未注册
    private static int loginState;
    //绑定服务状态
    private static boolean isLink = false;
    //注册检测接口
    private static SetupCheck login_interface = null;
    private ServiceConnection serConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            login_interface = SetupCheck.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            login_interface = null;
        }
    };
    private boolean mIsDestroyed = false;

    public boolean register() {
        if (isLink && login_interface != null) {
            try {
                loginState = login_interface.getState();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            if (loginState == 1) {
                return true;
            }
        }
        Toast.makeText(this, "请先注册！", Toast.LENGTH_LONG).show();
        finish();
        return false;
    }

    // 安卓5.0显示调用service
    public static Intent getExplicitIntent(Context context, Intent implicitIntent) {
        // Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);
        // Make sure only one match was found
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }
        // Get component info and create ComponentName
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);
        // Create a new intent. Use the old one for extras and such reuse
        Intent explicitIntent = new Intent(implicitIntent);
        // Set the component to be explicit
        explicitIntent.setComponent(component);
        return explicitIntent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        GlobalData globalData = (GlobalData) getApplicationContext();
        globalData.setmPreFocusId(R.id.image0);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView imageView;
        for (int i = 0; i < 24; i++) {
            imageView = (ImageView) findViewById(R.id.image0 + i);
            if (i == 0) {
                imageView.setImageResource(R.drawable.c01 + i);
            } else {
                imageView.setImageResource(R.drawable.c01 + i);
            }
            imageView.setOnClickListener(new ClickListener(this));
        }
        ImageView imageView1 = (ImageView) findViewById(R.id.chongxinlianxi);
        imageView1.setOnClickListener(new ClickListener(this));
        ImageView imageView2 = (ImageView) findViewById(R.id.shufayanshi);
        imageView2.setOnClickListener(new ClickListener(this));

        mMediaPlayer = new MediaPlayer();

        mSoundPool = new SoundPool(48, AudioManager.STREAM_MUSIC, 0);
        mSoundPoolMap = new HashMap<Integer, Integer>();
        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                mSoundPool.play(sampleId, 1, 1, 0, 0, 1);
            }
        });
        loadSound(0, 0);

        final ImageView exit = (ImageView) findViewById(R.id.main_exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // exit.setBackgroundResource(R.drawable.k004_001);
                GlobalData globalData = (GlobalData) MainActivity.this.getApplication();
                globalData.setmPreFocusId(R.id.image0);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopMediaPlayer();
        mSoundPool.autoPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopMediaPlayer();
        mSoundPool.autoPause();
    }

    protected void onDestroy() {
        super.onDestroy();
        mIsDestroyed = true;
        if (mSoundPool != null) {
            mSoundPool.autoPause();
            mSoundPool.release();
            mSoundPool = null;
        }
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void loadSound(int number, int i) {
        if (mIsDestroyed)
            return;
        if (0 == i) {
            int keyId = number;
            if (!mSoundPoolMap.containsKey(keyId)) {
                mSoundPoolMap.put(keyId, mSoundPool.load(this, R.raw.pyy24a + number, 1));
            } else {
                mSoundPool.play(mSoundPoolMap.get(keyId), 1, 1, 0, 0, 1);
            }
        }

        // MediaPlayer
        if (1 == i) {
            mMediaPlayer = MediaPlayer.create(this, R.raw.m01 + number);
            mMediaPlayer.start();
        }
        if (2 == i) {
            if (mMediaPlayer != null) {
                mMediaPlayer.stop();
            }
            mMediaPlayer = MediaPlayer.create(this, R.raw.all_right);
            mMediaPlayer.start();
        }
    }

    public void stopMediaPlayer() {
        if (mMediaPlayer != null)
            mMediaPlayer.stop();
    }

    /*public void resumeFingerDraw(){
        mFDTL.resumeCanvas();
    }*/
}
