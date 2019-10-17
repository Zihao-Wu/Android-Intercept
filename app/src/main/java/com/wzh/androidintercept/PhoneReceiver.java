package com.wzh.androidintercept;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * FileName: PhoneReceiver
 * Author: zhihao.wu@ttpai.cn
 * Date: 2019-10-17
 * Description:
 */
public class PhoneReceiver extends BroadcastReceiver {
    private static final String TAG = "PhoneReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive context=" + intent.getAction() + " intent=" + intent);
        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            // 如果是去电（拨出）
        } else {
            Bundle bundle=intent.getExtras();
            Set<String> set=bundle.keySet();
            if(set!=null){
                for(String key:set){
                    Log.d(TAG,key+" : "+bundle.get(key));
                }
            }
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
            // 设置一个监听器
            tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

    PhoneStateListener listener = new PhoneStateListener() {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            // state 当前状态 incomingNumber,貌似没有去电的API
            super.onCallStateChanged(state, incomingNumber);
            Log.d(TAG,"onCallStateChanged state="+state+" "+incomingNumber);
            switch (state) {
                //手机空闲了
                case TelephonyManager.CALL_STATE_IDLE:
                    break;
                //电话被挂起
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    break;
                // 当电话呼入时
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.e(TAG, "来电号码是：" + incomingNumber);
                    stopCall();
                    Log.e(TAG, "挂掉成功：");

                    // 如果该号码属于黑名单
                    if (incomingNumber.equals("*********")) {
                        // TODO:如果是黑名单，就进行屏蔽
                    }
                    break;
            }
        }
    };

    public void stopCall() {

        try {
            Method method = Class.forName("android.os.ServiceManager").getMethod("getService", String.class);
            // 获取远程TELEPHONY_SERVICE的IBinder对象的代理
            IBinder binder = (IBinder) method.invoke(null, new Object[]{"phone"});
            // 将IBinder对象的代理转换为ITelephony对象
            ITelephony telephony = ITelephony.Stub.asInterface(binder);
            // 挂断电话
            telephony.endCall();
            //telephony.cancelMissedCallsNotification();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
