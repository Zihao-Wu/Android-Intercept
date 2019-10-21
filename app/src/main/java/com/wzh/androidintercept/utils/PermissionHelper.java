package com.wzh.androidintercept.utils;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;

import com.wzh.androidintercept.common.CommonDialog;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;
import com.yanzhenjie.permission.runtime.PermissionDef;

import java.util.List;

/**
 * @author faqi.tao
 * @time 2019/10/18
 * 申请权限工具类
 */
public class PermissionHelper {
    public static final String TAG = "PermissionHelper";
    private Context mContext;

    public PermissionHelper(Context mContext) {
        this.mContext = mContext;
    }

    public void requestPermission(Action<List<String>> granted, PermissionDeniedCallBack permissionDeniedCallBack, @PermissionDef String... permissionList) {
        AndPermission.with(mContext)
                .runtime()
                .permission(permissionList)
                .onGranted(granted)
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> permissions) {
                        if (AndPermission.hasAlwaysDeniedPermission(mContext, permissions)) {
                            if (permissionDeniedCallBack != null) {
                                if (!permissionDeniedCallBack.onRefuse()) {
                                    showSettingDialog(mContext,0, true,permissions);
                                }
                            }
                        } else {
                            if (permissionDeniedCallBack != null) {
                                permissionDeniedCallBack.onDenied(permissions);
                            }
                        }
                    }
                })
                .start();
    }

    private void showSettingDialog(Context context, @Nullable int reqCode, boolean exitApp, final List<String> permissionList) {
        String content = "请在设置中授予以下权限:\n" + TextUtils.join("\n", permissionList);

        new CommonDialog.Builder(context)
                .setContent(content)
                .setCancel("拒绝", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (exitApp) {
                            ((Activity) context).finish();
                        }
                    }
                })
                .setConfirm("前往设置", v -> {
                    goSetting(reqCode);
                }).create().show();
    }

    public interface PermissionDeniedCallBack {
        boolean onRefuse();   //永久拒绝

        void onDenied(List<String> permissions);
    }

    public void goSetting(int reqCode) {
        AndPermission.with(mContext)
                .runtime()
                .setting().start(reqCode);
    }
}
