package com.wzh.androidintercept.network;

import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.wzh.androidintercept.bean.CheckPhoneResult;
import com.wzh.androidintercept.bean.PhoneLocationResult;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * FileName: NetWorkUtils
 * Author: zhihao.wu@ttpai.cn
 * Date: 2019-10-18
 * Description:
 */
public class NetWorkUtils {

    private static final String TAG = "NetWorkUtils";
    static OkHttpClient sClient;
    static Gson sGson;

    public static final String CHECK_URL = "https://www.iamwawa.cn/home/saoraodianhua/ajax";
    public static final String PHONE_LOCATION = "https://apis.juhe.cn/mobile/get?phone=%s&key=7988e3b870e15a1fb35579b437baa14e";

    static {
        sClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
        sGson = new Gson();
    }

    private NetWorkUtils() {
    }

    public OkHttpClient getOkHttpClient() {
        return sClient;
    }

    /**
     * 获取手机号状态，同步
     *
     * @param phone
     * @return
     */
    public static CheckPhoneResult checkPhoneSync(String phone) {
        if (TextUtils.isEmpty(phone))
            return null;

        Request request = new Request.Builder().url(CHECK_URL)
                .post(new FormBody.Builder()
                        .add("phone", phone)
                        .build())
                //更改为浏览器标识，否则403
                .header("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36")
                .build();
        try {
            long start = SystemClock.uptimeMillis();
            Response response = sClient.newCall(request).execute();
            Log.d(TAG, "time:" + (SystemClock.uptimeMillis() - start) + "ms body:" + response.code() + " r=" + response);

            if (response.isSuccessful()) {
                String body = response.body().string();
                Log.d(TAG, "time:" + (SystemClock.uptimeMillis() - start) + "ms body:" + body);
                CheckPhoneResult result = sGson.fromJson(body, CheckPhoneResult.class);
                return result;
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return null;
    }

    private static PhoneLocationResult getPhoneLocation(String phone) {
        if (TextUtils.isEmpty(phone)) {
            return null;
        }
        Request request = new Request.Builder().get().url(String.format(PHONE_LOCATION, phone))
                .build();
        try {
            long start = SystemClock.uptimeMillis();
            Response response = sClient.newCall(request).execute();
            Log.d(TAG, "time:" + (SystemClock.uptimeMillis() - start) + "ms body:" + response.code() + " r=" + response);

            if (response.isSuccessful()) {
                String body = response.body().string();
                Log.d(TAG, "time:" + (SystemClock.uptimeMillis() - start) + "ms body:" + body);
                PhoneLocationResult result = sGson.fromJson(body, PhoneLocationResult.class);
                result.setPhone(phone);
                return result;
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * 获取手机号状态，异步
     *
     * @param phone
     * @param onSuccess
     * @param error
     */
    public static void checkPhoneAsync(String phone, Action1<CheckPhoneResult> onSuccess, Action1<Throwable> error) {
        if (phone == null || onSuccess == null)
            throw new NullPointerException("phone == null or onSuccess==null");
        Single<CheckPhoneResult> single = Single.just(phone).map(new Func1<String, CheckPhoneResult>() {
            @Override
            public CheckPhoneResult call(String s) {
                return checkPhoneSync(s);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        if (error == null)
            single.subscribe(onSuccess);
        else
            single.subscribe(onSuccess, error);
    }

    /**
     * 获取手机号状态，异步
     *
     * @param phone
     * @param onSuccess
     * @param error
     */
    public static void getPhoneLocation(String phone, Action1<PhoneLocationResult> onSuccess, Action1<Throwable> error) {
        if (phone == null || onSuccess == null)
            throw new NullPointerException("phone == null or onSuccess==null");
        Single<PhoneLocationResult> single = Single.just(phone).map(new Func1<String, PhoneLocationResult>() {
            @Override
            public PhoneLocationResult call(String s) {
                return getPhoneLocation(s);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        if (error == null)
            single.subscribe(onSuccess);
        else
            single.subscribe(onSuccess, error);
    }
}
