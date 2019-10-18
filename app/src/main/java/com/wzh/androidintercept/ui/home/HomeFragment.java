package com.wzh.androidintercept.ui.home;

import android.content.Intent;
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
import com.wzh.androidintercept.ui.PhoneListActivity;
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
        Intent intent;
        switch (v.getId()) {
            case R.id.phone_switch:

                break;
            case R.id.tv_black:
                intent=new Intent(getContext(), PhoneListActivity.class);
                intent.putExtra(PhoneListActivity.IS_BLACK,true);
                startActivity(intent);
                break;
            case R.id.tv_white:
                intent=new Intent(getContext(), PhoneListActivity.class);
                intent.putExtra(PhoneListActivity.IS_BLACK,false);
                startActivity(intent);
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