package com.wzh.androidintercept.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.wzh.androidintercept.R;
import com.wzh.androidintercept.base.BaseRecyclerAdapter;
import com.wzh.androidintercept.base.BaseViewHolder;
import com.wzh.androidintercept.bean.PhoneBean;
import com.wzh.androidintercept.databinding.ActivityBlackListBinding;
import com.wzh.androidintercept.utils.PreferceHelper;

import java.util.ArrayList;
import java.util.List;


/**
 * FileName: PhoneListActivity
 * Author: zhihao.wu@ttpai.cn
 * Date: 2019-10-18
 * Description: 黑/白 名单列表
 */
public class PhoneListActivity extends BaseActivity {

    private ActivityBlackListBinding binding;
    private MyAdapter mAdapter;

    public static final String IS_BLACK = "isBlack";

    private boolean isBlackList;
    private PreferceHelper<List<PhoneBean>> mPreferHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isBlackList = getIntent().getBooleanExtra(IS_BLACK, true);//是否为黑名单

        binding = DataBindingUtil.setContentView(this, R.layout.activity_black_list);

        setTitle((isBlackList ? "黑" : "白") + "名单管理");

        initView();
        initDialog();
    }

    private void initView() {
        mPreferHelper = new PreferceHelper<>(PreferceHelper.FILE_MAIN,
                isBlackList ? PreferceHelper.KEY_BLACK_LIST : PreferceHelper.KEY_WHITE_LIST);

        binding.recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mAdapter = new MyAdapter();
        mAdapter.setData(mPreferHelper.getValue(new ArrayList<PhoneBean>()));
        binding.recycler.setAdapter(mAdapter);
    }

    private void initDialog() {
        final View view = getLayoutInflater().inflate(R.layout.add_phone_layout, null);
        final EditText edPhone = (EditText) view.findViewById(R.id.ed_phone);
        final AlertDialog dialog = new AlertDialog.Builder(PhoneListActivity.this)
                .setTitle((isBlackList ? "黑" : "白") + "名单号码添加")
                .setView(view).setNegativeButton("取消", null)
                .setPositiveButton("确定添加", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String phone = edPhone.getText().toString().trim();
                        if (TextUtils.isEmpty(phone)) {
                            Toast.makeText(PhoneListActivity.this, "电话号码不能为空哦~", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (phone.length() < 3) {
                            Toast.makeText(PhoneListActivity.this, "号码太短哦~", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        List<PhoneBean> list = mAdapter.getListData();
                        PhoneBean bean = new PhoneBean(phone);
                        if (!list.contains(bean)) {
                            list.add(0, bean);
                            mPreferHelper.saveValue(list);
                            mAdapter.notifyItemInserted(0);

                            binding.recycler.smoothScrollToPosition(0);
                        }
                    }
                }).create();

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edPhone.setText("");
                dialog.show();

            }
        });
    }

    class MyAdapter extends BaseRecyclerAdapter<MyAdapter.ViewHolder, PhoneBean> {

        public MyAdapter() {
            super(R.layout.item_phone);
        }

        @Override
        public MyAdapter.ViewHolder createViewHolder(View itemView, int viewType) {
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyAdapter.ViewHolder holder, int position) {
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
                        .setTitle("确定从"+(isBlackList ? "黑" : "白") + "名单中移除吗？")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("移除", new DialogInterface.OnClickListener() {
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
    }
}
