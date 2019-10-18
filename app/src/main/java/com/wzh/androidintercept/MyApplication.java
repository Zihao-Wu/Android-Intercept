package com.wzh.androidintercept;

import android.app.Application;

/**
 * FileName: MyApplication
 * Author: zhihao.wu@ttpai.cn
 * Date: 2019-10-18
 * Description:
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Profile.sAppContext=this;
    }
}
