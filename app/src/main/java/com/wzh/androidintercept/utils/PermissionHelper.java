package com.wzh.androidintercept.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
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

    public void requestPermission(Action<List<String>> granted, PermissionDeniedCallBack permissionDeniedCallBack, int reqCode, String permissionNames, @PermissionDef String... permissionList) {
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
                                    showSettingDialog(mContext, reqCode, true, permissionNames, permissions);
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

    private void showSettingDialog(Context context, @Nullable int reqCode, boolean exitApp, String permissionNames, final List<String> permissionList) {
//        List<String> permissionNames = Permission.transformText(context, permissionList);
        String content = "请在设置中授予以下权限:\n" + permissionNames;

        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(content)
                .setCancelable(false)
                .setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (exitApp) {
                            ((Activity) context).finish();
                        }
                    }
                })
                .setPositiveButton("前往设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        goSetting(reqCode);
                    }
                }).create();
        dialog.show();
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

    public boolean checkPermission(@PermissionDef String... permissionList) {
        final boolean[] hasPermission = new boolean[1];
        AndPermission.with(mContext)
                .runtime()
                .permission(permissionList)
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        hasPermission[0] = false;
                    }
                })
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        hasPermission[0] = true;
                    }
                });
        return hasPermission[0];
    }
}
