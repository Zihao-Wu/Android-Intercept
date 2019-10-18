package com.wzh.androidintercept.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;

import com.wzh.androidintercept.Profile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 存储对象的SharedPreferences工具类，对象一定要 implements Serializable序例化
 *
 * @param <T>
 */
public class PreferceHelper<T> {

    public static final String FILE_MAIN = "app_main";//

    public static final String KEY_INTERCEPT_ENABLE = "intercept_enable";//是否拦截

    private static Context ctx;

    private String fileName;
    private String key;

    static {
        ctx = Profile.sAppContext;
    }

    public PreferceHelper() {
    }

    public PreferceHelper(String fileName, String key) {
        this.fileName = fileName;
        this.key = key;
    }

    public boolean saveValue(T object) {
        if (TextUtils.isEmpty(this.fileName) || TextUtils.isEmpty(this.key)) {
            throw new NullPointerException("fileName or key is Null");
        }
        return saveValue(object, this.fileName, this.key);
    }

    /**
     * 对象一定要 implements Serializable序例化
     */
    public boolean saveValue(T object, String fileName, String key) {
        ObjectOutputStream oos = null;
        ByteArrayOutputStream baos = null;
        boolean commit = false;
        try {
            SharedPreferences sharedPreferences = ctx.getSharedPreferences(
                    fileName, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            baos = new ByteArrayOutputStream();

            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            String s_obj = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
            editor.putString(key, s_obj);
            commit = editor.commit();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (oos != null)
                    oos.close();
                if (baos != null)
                    baos.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return commit;
    }

    // 清除指定key的内容
    public static boolean removeKey(String fileName, String key) {
        try {
            return ctx.getSharedPreferences(fileName, Activity.MODE_PRIVATE).edit().remove(key).commit();
        } catch (Exception e) {
            return false;
        }
    }

    // 清除文件内所有的数据
    public static boolean clearFile(String fileName) {
        try {
            return ctx.getSharedPreferences(fileName, Activity.MODE_PRIVATE).edit().clear().commit();
        } catch (Exception e) {
            return false;
        }
    }

    public T getValue(T defaultValue) {
        T t = getValue();
        if (t == null)
            return defaultValue;
        return t;
    }

    public T getValue() {
        if (TextUtils.isEmpty(this.fileName) || TextUtils.isEmpty(this.key)) {
            throw new NullPointerException("fileName or key is Null");
        }
        return getValue(this.fileName, this.key);
    }

    public T getValue(String fileName, String key, T defaultValue) {
        T t = getValue(fileName, key);
        if (t == null)
            return defaultValue;
        return t;
    }

    /**
     * @param fileName 文件名
     * @param key      key
     * @return 指定文件名内的key对应的对象
     */
    public T getValue(String fileName, String key) {
        T obj = null;
        try {
            SharedPreferences sharedPreferences = ctx.getSharedPreferences(fileName, Activity.MODE_PRIVATE);
            if (sharedPreferences == null || !sharedPreferences.contains(key))
                return obj;
            String value = sharedPreferences.getString(key, "");
            return valueToObject(value);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @return 取得指定文件名的所有内容
     */
    public ArrayList<T> getAllValue(String fileName) {
        ArrayList<T> list = null;
        try {
            SharedPreferences sharedPreferences = ctx.getSharedPreferences(fileName, Activity.MODE_PRIVATE);
            HashMap<String, String> map = (HashMap<String, String>) sharedPreferences.getAll();
            if (map == null || map.size() == 0) return list;
            list = new ArrayList<T>();
            for (String key : map.keySet()) {
                list.add(valueToObject(map.get(key)));
            }
        } catch (Exception e) {
            return null;
        }
        return list;
    }

    /**
     * 通过值转对象
     *
     * @param value 取出来的值
     * @return 转换好的对象
     */
    private T valueToObject(String value) {
        T obj;
        ObjectInputStream ois = null;
        ByteArrayInputStream bais = null;
        try {
            byte[] buffer = Base64.decode(value.getBytes(), Base64.DEFAULT);
            bais = new ByteArrayInputStream(buffer);
            ois = new ObjectInputStream(bais);
            obj = (T) ois.readObject();
            return obj;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            return null;
        } finally {
            try {
                if (ois != null)
                    ois.close();
                bais.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }
}
