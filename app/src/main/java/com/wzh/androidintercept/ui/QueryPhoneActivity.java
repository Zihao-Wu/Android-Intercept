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
import com.wzh.androidintercept.bean.QueryPhoneResult;
import com.wzh.androidintercept.databinding.ActivityInterceptRecordBinding;
import com.wzh.androidintercept.utils.PreferceHelper;

import java.text.SimpleDateFormat;
import java.util.List;


/**
 * FileName: InterceptRecordActivity
 * Author: zhihao.wu@ttpai.cn
 * Date: 2019-10-18
 * Description:号码查询
 */
public class QueryPhoneActivity extends BaseActivity {

    private ActivityInterceptRecordBinding binding;
    private PreferceHelper<List<QueryPhoneResult>> mPreferHelper;
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=DataBindingUtil.setContentView(this,R.layout.activity_query_phone);

        initView();
    }

    private void initView() {
        setTitle("号码查询");
        mPreferHelper = new PreferceHelper<>(PreferceHelper.FILE_QUERY,PreferceHelper.KEY_QUERY_RECORD);

        binding.recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mAdapter = new MyAdapter();
        List<QueryPhoneResult> list=mPreferHelper.getValue();
        mAdapter.setData(list);
        binding.recycler.setAdapter(mAdapter);
    }

    class MyAdapter extends BaseRecyclerAdapter<QueryPhoneActivity.MyAdapter.ViewHolder, QueryPhoneResult> {

        SimpleDateFormat format;
        public MyAdapter() {
            super(R.layout.item_query_record);
            format=new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        }

        @Override
        public QueryPhoneActivity.MyAdapter.ViewHolder createViewHolder(View itemView, int viewType) {
            return new QueryPhoneActivity.MyAdapter.ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull QueryPhoneActivity.MyAdapter.ViewHolder holder, int position) {
               /* QueryPhoneResult item = getItem(position);
                holder.tvPhone.setText(item.phone);
                holder.tvIdentity.setText(item.identity);
                holder.tvLocation.setText(item.location);
                holder.tvTime.setText(format.format(new Date(item.time)));*/
        }

        public class ViewHolder extends BaseViewHolder {

            public TextView tvPhone,tvLocation,tvIdentity,tvTime;

            public ViewHolder(View itemView) {
                super(itemView);
                tvPhone = itemView.findViewById(R.id.tv_phone);
                tvLocation = itemView.findViewById(R.id.tv_location);
                tvIdentity = itemView.findViewById(R.id.tv_identity);
                tvTime = itemView.findViewById(R.id.tv_time);

            }

        }
    }
}
