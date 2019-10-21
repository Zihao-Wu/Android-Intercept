package com.wzh.androidintercept;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;
import com.wzh.androidintercept.bean.CheckPhoneResult;
import com.wzh.androidintercept.bean.InterceptItem;
import com.wzh.androidintercept.bean.PhoneBean;
import com.wzh.androidintercept.network.NetWorkUtils;
import com.wzh.androidintercept.utils.PreferceHelper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import rx.functions.Action1;

/**
 * FileName: PhoneReceiver
 * Author: zhihao.wu@ttpai.cn
 * Date: 2019-10-17
 * Description:
 */
public class PhoneReceiver extends BroadcastReceiver {
    private static final String TAG = "PhoneReceiver";
    private Context context;

    private long lastTime;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive context=" + intent.getAction() + " intent=" + intent + " t=" + Thread.currentThread() + " " + this);
        this.context = context;
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Toast.makeText(context, "开机完毕~", Toast.LENGTH_LONG).show();
        } else if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            // 如果是去电（拨出）
        } else if ("android.intent.action.PHONE_STATE".equals(intent.getAction())) {
            if (SystemClock.uptimeMillis() - lastTime < 5000) {//5 秒内多个广播，忽略
                return;
            }
            lastTime = SystemClock.uptimeMillis();

            Bundle bundle = intent.getExtras();
            Set<String> set = bundle.keySet();
            if (set != null) {
                for (String key : set) {
                    Log.d(TAG, key + " : " + bundle.get(key));
                }
            }
            String state = bundle.getString("state");
            final String incomingNumber = bundle.getString("incoming_number");
            if ("RINGING".equals(state) && !TextUtils.isEmpty(incomingNumber)) {

                Log.d(TAG, "开始判断="+incomingNumber);
                boolean isEnable = new PreferceHelper<Boolean>(PreferceHelper.FILE_MAIN, PreferceHelper.KEY_INTERCEPT_ENABLE).getValue(true);
                if (!isEnable) {
                    showToast("拦截器已关闭");
                    return;
                }

                final PhoneBean phoneBean = new PhoneBean(incomingNumber);
                List<PhoneBean> whiteList = getWhiteList();
                if (whiteList.contains(phoneBean)) {
                    showToast("白名单号码: " + incomingNumber + " 不拦截");
                    return;
                }
                List<PhoneBean> blackList = getBlackList();
                if (blackList.contains(phoneBean)) {
                    showToast("黑名单号码: " + incomingNumber + " 拦截");
                    stopCall();

                    phoneBean.identity = "黑名单 号码拦截";
                    saveIntercept(phoneBean);
                    return;
                } else {//
                    NetWorkUtils.checkPhoneAsync(incomingNumber, new Action1<CheckPhoneResult>() {
                        @Override
                        public void call(CheckPhoneResult result) {
                            Log.d(TAG, "success:" + result);
                            if (result != null && result.getStatus() == 1) {
                                showToast("骚扰电话: " + incomingNumber + " 拦截");
                                stopCall();

                                phoneBean.identity = "骚扰号码 拦截";
                                saveIntercept(phoneBean);
                            } else {
                                showToast("非骚扰电话: " + incomingNumber + " 不拦截");
                            }
                        }
                    }, null);

                }

                /*TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
                // 设置一个监听器
                tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);*/
            }
        }
    }

    PhoneStateListener listener = new PhoneStateListener() {

        @Override
        public void onCallStateChanged(int state, final String incomingNumber) {
            // state 当前状态 incomingNumber,貌似没有去电的API
            super.onCallStateChanged(state, incomingNumber);
            Log.d(TAG, "onCallStateChanged state=" + state + " " + incomingNumber);
            switch (state) {
                //手机空闲了
                case TelephonyManager.CALL_STATE_IDLE:
                    break;
                //电话被挂起
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    break;
                // 当电话呼入时
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.e(TAG, "来电号码是：" + incomingNumber + " t=" + Thread.currentThread());
                    if (TextUtils.isEmpty(incomingNumber))
                        return;

                    boolean isEnable = new PreferceHelper<Boolean>(PreferceHelper.FILE_MAIN, PreferceHelper.KEY_INTERCEPT_ENABLE).getValue(true);
                    if (!isEnable) {
//                        showToast("拦截器已关闭");
                        return;
                    }

                    final PhoneBean phoneBean = new PhoneBean(incomingNumber);
                    List<PhoneBean> whiteList = getWhiteList();
                    if (whiteList.contains(phoneBean)) {
                        showToast("白名单号码: " + incomingNumber + " 不拦截");
                        return;
                    }
                    List<PhoneBean> blackList = getBlackList();
                    if (blackList.contains(phoneBean)) {
                        showToast("黑名单号码: " + incomingNumber + " 拦截");
                        stopCall();

                        phoneBean.identity = "黑名单 号码拦截";
                        saveIntercept(phoneBean);
                        return;
                    } else {//
                        NetWorkUtils.checkPhoneAsync(incomingNumber, new Action1<CheckPhoneResult>() {
                            @Override
                            public void call(CheckPhoneResult result) {
                                Log.d(TAG, "success:" + result);
                                if (result != null && result.getStatus() == 1) {
                                    showToast("骚扰电话: " + incomingNumber + " 拦截");
                                    stopCall();

                                    phoneBean.identity = "骚扰号码 拦截";
                                    saveIntercept(phoneBean);
                                } else {
                                    showToast("非骚扰电话: " + incomingNumber + " 不拦截");
                                }
                            }
                        }, null);

                    }
                    break;
            }
        }
    };

    private void showToast(String s) {
        if (context != null) {
            Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
        }
    }

    private List<PhoneBean> getWhiteList() {
        return new PreferceHelper<List<PhoneBean>>(PreferceHelper.FILE_MAIN, PreferceHelper.KEY_WHITE_LIST).getValue(new ArrayList<PhoneBean>());
    }

    private List<PhoneBean> getBlackList() {
        return new PreferceHelper<List<PhoneBean>>(PreferceHelper.FILE_MAIN, PreferceHelper.KEY_BLACK_LIST).getValue(new ArrayList<PhoneBean>());
    }

    private void saveIntercept(PhoneBean phoneBean) {
        PreferceHelper<List<InterceptItem>> interceptList = new PreferceHelper<>(PreferceHelper.FILE_RECORD, PreferceHelper.KEY_INTERCEPT_LIST);
        List<InterceptItem> list = interceptList.getValue(new ArrayList<InterceptItem>());
        InterceptItem item = new InterceptItem();
        item.phone = phoneBean.phone;
        item.identity = phoneBean.identity;
        item.time = System.currentTimeMillis();
        list.add(item);
        interceptList.saveValue(list);
    }

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
