package com.homechart.app.getwebinfo;

import android.app.Application;

/**
 * Created by gumenghao on 17/5/17.
 */

public class MyApplication extends Application {

    private static MyApplication myApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;

        initImageLoader();

    }

    private void initImageLoader() {
        ImageUtils.initImageLoader(this);
    }

    public static synchronized MyApplication getInstance() {
        return myApplication;
    }

}
