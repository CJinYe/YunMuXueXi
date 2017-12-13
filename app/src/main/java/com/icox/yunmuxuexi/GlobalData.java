package com.icox.yunmuxuexi;

import com.icox.updateapp.CrashApplication;

/**
 * Created by Administrator on 2015/4/23 0023.
 */
public class GlobalData extends CrashApplication {
    private int mPreFocusId;

    public void onCreate(){
        super.onCreate();
        mPreFocusId = R.id.image0;
    }

    public void setmPreFocusId(int preFocusId){
        this.mPreFocusId = preFocusId;
    }
    public int getmPreFocusId(){
        return this.mPreFocusId;
    }
}
