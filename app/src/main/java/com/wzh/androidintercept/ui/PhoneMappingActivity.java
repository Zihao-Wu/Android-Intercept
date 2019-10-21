package com.wzh.androidintercept.ui;

import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.wzh.androidintercept.R;
import com.wzh.androidintercept.databinding.ActivityPhoneMappingBinding;

public class PhoneMappingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityPhoneMappingBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_phone_mapping);
//        binding.recyclerView.

    }
}
