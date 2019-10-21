package com.wzh.androidintercept.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.wzh.androidintercept.R;
import com.wzh.androidintercept.base.BaseRecyclerAdapter;
import com.wzh.androidintercept.base.BaseViewHolder;
import com.wzh.androidintercept.bean.PhoneBean;
import com.wzh.androidintercept.databinding.ActivityInterceptRecordBinding;
import com.wzh.androidintercept.utils.PreferceHelper;

import java.util.ArrayList;
import java.util.List;


/**
 * FileName: InterceptRecordActivity
 * Author: zhihao.wu@ttpai.cn
 * Date: 2019-10-18
 * Description:拦截记录
 */
public class InterceptRecordActivity extends BaseActivity {

    private ActivityInterceptRecordBinding binding;
    private PreferceHelper<List<PhoneBean>> mPreferHelper;
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=DataBindingUtil.setContentView(this,R.layout.activity_intercept_record);

        initView();
    }

    private void initView() {
        setTitle("拦截记录");
        mPreferHelper = new PreferceHelper<>(PreferceHelper.FILE_RECORD,PreferceHelper.KEY_INTERCEPT_LIST);

        binding.recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mAdapter = new MyAdapter();
        mAdapter.setData(mPreferHelper.getValue(new ArrayList<PhoneBean>()));
        binding.recycler.setAdapter(mAdapter);
    }

    class MyAdapter extends BaseRecyclerAdapter<InterceptRecordActivity.MyAdapter.ViewHolder, PhoneBean> {

        public MyAdapter() {
            super(R.layout.item_phone);
        }

        @Override
        public InterceptRecordActivity.MyAdapter.ViewHolder createViewHolder(View itemView, int viewType) {
            return new InterceptRecordActivity.MyAdapter.ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull InterceptRecordActivity.MyAdapter.ViewHolder holder, int position) {
            PhoneBean item = getItem(position);
            holder.tvPhone.setText(item.phone);
            holder.tvIdentity.setText(item.identity);
        }

        public class ViewHolder extends BaseViewHolder {

            public TextView tvPhone,tvIdentity;

            public ViewHolder(View itemView) {
                super(itemView);
                tvPhone = itemView.findViewById(R.id.tv_phone);
                tvIdentity = itemView.findViewById(R.id.tv_identity);

            }

        }
    }
}
