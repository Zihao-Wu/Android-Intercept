package com.wzh.androidintercept;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.ui.AppBarConfiguration;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.wzh.androidintercept.utils.PermissionHelper;
import com.wzh.androidintercept.utils.statusBarUtil.StatusBarUtil;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.runtime.Permission;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private AppBarConfiguration mAppBarConfiguration;
    private static final String[] PHONE = new String[]{Permission.READ_PHONE_STATE, Permission.CALL_PHONE, Permission.READ_CALL_LOG};
    PermissionHelper permissionHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StatusBarUtil.setStatusBarColor(this, getResources().getColor(R.color.colorPrimary));

        setContentView(R.layout.app_main_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "感谢您的点赞！", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                fab.setImageResource(R.drawable.ic_favorite2);
            }
        });

        requestPermission();
    }

    public void requestPermission() {
        permissionHelper = new PermissionHelper(this);
        permissionHelper.requestPermission(new Action<List<String>>() {
            @Override
            public void onAction(List<String> data) {
                //授权了权限  进行下一步操作
            }
        }, new PermissionHelper.PermissionDeniedCallBack() {
            @Override
            public boolean onRefuse() {
                return false;
            }

            @Override
            public void onDenied(List<String> permissions) {
                requestPermission();//重复申请
            }
        }, 0, "电话和通话记录", PHONE);
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (!permissionHelper.checkPermission(PHONE)) {
                finish();
            }
        }
    }
}
