package com.wzh.androidintercept;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;

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

    public static final String TAG = "MainActivity";
    private AppBarConfiguration mAppBarConfiguration;
    private static final String[] PHONE = new String[]{Permission.READ_PHONE_STATE, Permission.CALL_PHONE, Permission.READ_CALL_LOG};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StatusBarUtil.setStatusBarColor(this, getResources().getColor(R.color.colorPrimary));

        setContentView(R.layout.app_main_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "感谢您的点赞！", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        });

        requestPermission();
    }

    public void requestPermission() {
        new PermissionHelper(this).requestPermission(new Action<List<String>>() {
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
        }, PHONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
