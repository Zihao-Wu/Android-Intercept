package com.wzh.androidintercept.ui;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.wzh.androidintercept.R;
import com.wzh.androidintercept.databinding.ActivityInterceptRecordBinding;
import com.wzh.androidintercept.utils.PreferceHelper;


/**
 * FileName: InterceptRecordActivity
 * Author: zhihao.wu@ttpai.cn
 * Date: 2019-10-18
 * Description:拦截记录
 */
public class InterceptRecordActivity extends BaseActivity {

    private ActivityInterceptRecordBinding binding;
    private PreferceHelper<Object> mPreferHelper;
//    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=DataBindingUtil.setContentView(this,R.layout.activity_intercept_record);
        initView();
    }

    private void initView() {
        setTitle("拦截记录");
        mPreferHelper = new PreferceHelper<>(PreferceHelper.FILE_RECORD,PreferceHelper.KEY_LIST);

        binding.recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

//        mAdapter = new MyAdapter();
//        mAdapter.setData(mPreferHelper.getValue(new ArrayList<PhoneBean>()));
//        binding.recycler.setAdapter(mAdapter);
    }

/*
    class MyAdapter extends BaseRecyclerAdapter<PhoneListActivity.MyAdapter.ViewHolder, PhoneBean> {

        public MyAdapter() {
            super(R.layout.item_phone);
        }

        @Override
        public PhoneListActivity.MyAdapter.ViewHolder createViewHolder(View itemView, int viewType) {
            return new PhoneListActivity.MyAdapter.ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull PhoneListActivity.MyAdapter.ViewHolder holder, int position) {
            PhoneBean item = getItem(position);
            holder.tvPhone.setText(item.phone);
        }

        public class ViewHolder extends BaseViewHolder {

            public TextView tvPhone;

            public ViewHolder(View itemView) {
                super(itemView);
                tvPhone = itemView.findViewById(R.id.tv_phone);
                itemView.findViewById(R.id.tv_delete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDeleteDialog();
                    }
                });
            }

            private void showDeleteDialog(){
                new AlertDialog.Builder(PhoneListActivity.this)
                        .setTitle("确定删除吗？")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                int position = getAdapterPosition();
                                PhoneBean item = getItem(position);
                                mAdapter.getListData().remove(item);
                                mAdapter.notifyItemRemoved(position);

                                mPreferHelper.saveValue(mAdapter.getListData());
                            }
                        }).show();
            }
        }
    }*/
}
