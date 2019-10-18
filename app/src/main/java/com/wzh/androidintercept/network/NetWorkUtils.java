package com.wzh.androidintercept.network;

import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.wzh.androidintercept.bean.CheckPhoneResult;

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

    static {
        sClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
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
     * @param phone
     * @return
     */
    public CheckPhoneResult checkPhoneSync(String phone) {
        if (TextUtils.isEmpty(phone))
            return null;

        Request request = new Request.Builder().url(CHECK_URL)
                .post(new FormBody.Builder().add("phone", phone).build()).build();
        try {
            long start = SystemClock.uptimeMillis();
            Response response = sClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String body = response.body().string();
                Log.d(TAG, "time:" + (SystemClock.uptimeMillis() - start) + "ms body:" + body);
                CheckPhoneResult result = sGson.fromJson(body, CheckPhoneResult.class);
                return result;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取手机号状态，异步
     * @param phone
     * @param onSuccess
     * @param error
     */
    public void checkPhoneAsync(String phone, Action1<CheckPhoneResult> onSuccess, Action1<Throwable> error) {
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
}
