package com.jimi.map;

import com.baidu.mapapi.map.Overlay;

/**
 * Created by longjintang on 2016/8/29.
 */
public class MyCircleOverlay {

    private Overlay mCircle;

    public Overlay getmCircle() {
        return mCircle;
    }

    public void setmCircle(Overlay mCircle) {
        this.mCircle = mCircle;
    }


    public void remove(){
        if (mCircle != null){
            mCircle.remove();
        }
    }
}
