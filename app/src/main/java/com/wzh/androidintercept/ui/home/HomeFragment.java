package com.wzh.androidintercept.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.wzh.androidintercept.R;
import com.wzh.androidintercept.databinding.FragmentHomeBinding;
import com.wzh.androidintercept.utils.PreferceHelper;

public class HomeFragment extends Fragment implements View.OnClickListener {

    FragmentHomeBinding binding;
    private PreferceHelper<Boolean> mPreferce;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        initData();
        initView();
        return binding.getRoot();
    }

    private void initData() {
        mPreferce=new PreferceHelper<Boolean>(PreferceHelper.FILE_MAIN,PreferceHelper.KEY_INTERCEPT_ENABLE);

    }

    private void initView() {
        binding.setClick(this);

        binding.phoneSwitch.setChecked(mPreferce.getValue(true));

        binding.phoneSwitch.setText("骚扰电话拦截(" + (binding.phoneSwitch.isChecked() ? "已开启" : "已关闭") + ")");

        binding.phoneSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                binding.phoneSwitch.setText("骚扰电话拦截(" + (isChecked ? "已开启" : "已关闭") + ")");
                mPreferce.saveValue(isChecked);

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.phone_switch:

                break;
            case R.id.tv_black:

                break;
            case R.id.tv_white:

                break;
            case R.id.tv_mapping:

                break;
            case R.id.tv_record:

                break;
            default:

                break;
        }
    }
}