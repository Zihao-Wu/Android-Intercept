package com.wzh.androidintercept.utils.statusBarUtil;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.IntDef;
import androidx.fragment.app.Fragment;

import com.wzh.androidintercept.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;


public class StatusBarUtil {
    public final static int TYPE_MIUI = 0;
    public final static int TYPE_FLYME = 1;
    public final static int TYPE_M = 2;//6.0
    public final static int TYPE_OPPO = 3;//6.0
    private final static int SYSTEM_UI_FLAG_OP_STATUS_BAR_TINT = 0x00000010;

    @IntDef({TYPE_MIUI,
            TYPE_FLYME,
            TYPE_M,
            TYPE_OPPO})
    @Retention(RetentionPolicy.SOURCE)
    @interface ViewType {
    }

    /**
     * 修改状态栏颜色，支持4.4以上版本
     *
     * @param colorId 颜色
     */
    public static void setStatusBarColor(Activity activity, int colorId) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.setStatusBarColor(colorId);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //使用SystemBarTintManager,需要先将状态栏设置为透明
            //            setTranslucentStatus(activity);
            SystemBarTintManager systemBarTintManager = new SystemBarTintManager(activity);
            systemBarTintManager.setStatusBarTintEnabled(true);//显示状态栏
            systemBarTintManager.setStatusBarTintColor(colorId);//设置状态栏颜色
        }
    }

    /**
     * 设置状态栏透明
     */
    @TargetApi(19)
    private static void setTranslucentStatus(Activity activity) {
        // 5.0以上系统状态栏透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            //清除透明状态栏
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            //设置状态栏颜色必须添加
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);//设置透明
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //19
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * 通过设置全屏，设置状态栏透明,实现沉浸式
     */
    private static void fullScreen(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
                Window window = activity.getWindow();
                View decorView = window.getDecorView();
                //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                decorView.setSystemUiVisibility(option);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
                //导航栏颜色也可以正常设置
                //                window.setNavigationBarColor(Color.TRANSPARENT);
            } else {
                Window window = activity.getWindow();
                WindowManager.LayoutParams attributes = window.getAttributes();
                int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
                int flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
                attributes.flags |= flagTranslucentStatus;
                //导航栏颜色也可以正常设置
                //                attributes.flags |= flagTranslucentNavigation;
                window.setAttributes(attributes);
            }
        }
    }

    /**
     * 设置沉浸式状态栏
     *
     * @param fontIconDark 状态栏字体和图标颜色是否为深色
     */
    public static void setImmersiveStatusBar(Activity activity, boolean fontIconDark) {
        fullScreen(activity);
        if (fontIconDark) {
            //MIUI与Flyme根据rom版本适配
            if (OSUtils.isMiui()) {
                setStatusBarFontIconDark(activity, TYPE_MIUI);
            } else if (OSUtils.isFlyme()) {
                setStatusBarFontIconDark(activity, TYPE_FLYME);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setStatusBarFontIconDark(activity, TYPE_M);
            } else if (OSUtils.isOppo()) {
                setStatusBarFontIconDark(activity, TYPE_OPPO);
            } else {//其他情况下我们将状态栏设置为灰色，就不会看不见字体
                setStatusBarColor(activity, R.color.black_50);//半透明黑色
            }
        }
    }

    /**
     * 设置字体颜色
     *
     * @param fontIconDark 状态栏字体和图标颜色是否为深色
     */
    public static void setImmersiveStatusBar(Fragment fragment, boolean fontIconDark) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setStatusBarFontIconDark(fragment.getActivity(), TYPE_M, fontIconDark);
        } else if (OSUtils.isMiui()) {
            setStatusBarFontIconDark(fragment.getActivity(), TYPE_MIUI, fontIconDark);
        } else if (OSUtils.isFlyme()) {
            setStatusBarFontIconDark(fragment.getActivity(), TYPE_FLYME, fontIconDark);
        } else if (OSUtils.isOppo()) {
            setStatusBarFontIconDark(fragment.getActivity(), TYPE_OPPO, fontIconDark);
        } else {//其他情况下我们将状态栏设置为灰色，就不会看不见字体
            setStatusBarColor(fragment.getActivity(), R.color.black_50);//半透明黑色
        }
    }

    /**
     * 设置文字颜色
     */
    private static void setStatusBarFontIconDark(Activity activity, @ViewType int type, boolean isFountDark) {
        switch (type) {
            case TYPE_MIUI:
                /*
                 * 更改状态栏图标、文字颜色的方案是否是MIUI自家的， MIUI9 && Android 6 之后用回Android原生实现
                 * 见小米开发文档说明：https://dev.mi.com/console/doc/detail?pId=1159
                 */
                if (OSUtils.isMIUIV6() || OSUtils.isMIUIV7() || OSUtils.isMIUIV8()) {
                    setMiuiUI(activity, isFountDark);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        setCommonUI(activity, isFountDark);
                    }
                }
                break;
            case TYPE_M:
                setCommonUI(activity, isFountDark);
                break;
            case TYPE_FLYME:
                setFlymeUI(activity, isFountDark);
                break;
            case TYPE_OPPO:
                setOppoUI(activity, isFountDark);
                break;
        }
    }


    /**
     * 设置文字颜色
     */
    private static void setStatusBarFontIconDark(Activity activity, @ViewType int type) {
        switch (type) {
            case TYPE_MIUI:
                /*
                 * 更改状态栏图标、文字颜色的方案是否是MIUI自家的， MIUI9 && Android 6 之后用回Android原生实现
                 * 见小米开发文档说明：https://dev.mi.com/console/doc/detail?pId=1159
                 */
                if (OSUtils.isMIUIV6() || OSUtils.isMIUIV7() || OSUtils.isMIUIV8()) {
                    setMiuiUI(activity, true);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        setCommonUI(activity);
                    }
                }
                break;
            case TYPE_M:
                setCommonUI(activity);
                break;
            case TYPE_FLYME:
                setFlymeUI(activity, true);
                break;
            case TYPE_OPPO:
                setOppoUI(activity, true);
                break;
        }
    }

    //设置6.0的字体
    private static void setCommonUI(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            return;
        }
    }

    //设置6.0的字体
    private static void setCommonUI(Activity activity, boolean dark) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = activity.getWindow().getDecorView();
            if (dark) {
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            }
        }
    }

    //设置Flyme的字体
    private static void setFlymeUI(Activity activity, boolean dark) {
        try {
            // flyme 在 6.2.0.0A 支持了 Android 官方的实现方案，旧的方案失效
            setCommonUI(activity);

            Window window = activity.getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
            Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
            darkFlag.setAccessible(true);
            meizuFlags.setAccessible(true);
            int bit = darkFlag.getInt(null);
            int value = meizuFlags.getInt(lp);
            if (dark) {
                value |= bit;
            } else {
                value &= ~bit;
            }
            meizuFlags.setInt(lp, value);
            window.setAttributes(lp);
        } catch (Exception e) {
            Log.e("LightModeException", "Failed to match Flyme");
            e.printStackTrace();
            setStatusBarColor(activity, R.color.black_50);//半透明黑色

        }
    }

    //设置MIUI字体
    private static void setMiuiUI(Activity activity, boolean dark) {
        try {
            Window window = activity.getWindow();
            Class clazz = activity.getWindow().getClass();
            Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            int darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            if (dark) {    //状态栏亮色且黑色字体
                extraFlagField.invoke(window, darkModeFlag, darkModeFlag);
            } else {
                extraFlagField.invoke(window, 0, darkModeFlag);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("LightModeException", "Failed to match MIUI");
            setStatusBarColor(activity, R.color.black_50);//半透明黑色

        }
    }

    private static void setOppoUI(Activity activity, boolean dark) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            int vis = window.getDecorView().getSystemUiVisibility();
            if (dark) {
                vis |= SYSTEM_UI_FLAG_OP_STATUS_BAR_TINT;
            } else {
                vis &= ~SYSTEM_UI_FLAG_OP_STATUS_BAR_TINT;
            }
            window.getDecorView().setSystemUiVisibility(vis);
        } else {
            setStatusBarColor(activity, R.color.black_50);//半透明黑色

        }
    }
}