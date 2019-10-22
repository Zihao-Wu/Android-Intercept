package com.wzh.androidintercept.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;

import com.wzh.androidintercept.R;
import com.wzh.androidintercept.base.BaseRecyclerAdapter;
import com.wzh.androidintercept.base.BaseViewHolder;
import com.wzh.androidintercept.bean.PhoneMappingItem;
import com.wzh.androidintercept.common.CommonItemDecoration;
import com.wzh.androidintercept.databinding.ActivityPhoneMappingBinding;
import com.wzh.androidintercept.utils.PreferceHelper;

import java.util.ArrayList;
import java.util.List;

public class PhoneMappingActivity extends BaseActivity {

    private ActivityPhoneMappingBinding binding;
    private PreferceHelper<List<PhoneMappingItem>> mPreferHelper;
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_phone_mapping);
        setTitle("号码映射");
        mPreferHelper = new PreferceHelper<>(PreferceHelper.FILE_MAPPING, PreferceHelper.KEY_MAPPING_LIST);

        initListener();

        mAdapter = new MyAdapter();
        mAdapter.setData(mPreferHelper.getValue(new ArrayList<PhoneMappingItem>()));
        binding.recyclerView.setAdapter(mAdapter);
        binding.recyclerView.addItemDecoration(new CommonItemDecoration(this, R.drawable.divider_shape));
    }

    private void initListener() {
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initDialog("", "").show();
            }
        });
    }

    private AlertDialog initDialog(@Nullable String originPhoneStr, @Nullable String mappingPhoneStr) {
        final View view = getLayoutInflater().inflate(R.layout.add_mapping_phone_layout, null);
        final EditText originPhone = view.findViewById(R.id.origin_ed_phone);
        final EditText mappingPhone = view.findViewById(R.id.mapping_ed_phone);
        originPhone.setText(TextUtils.isEmpty(originPhoneStr) ? "" : originPhoneStr);
        mappingPhone.setText(TextUtils.isEmpty(mappingPhoneStr) ? "" : mappingPhoneStr);
        final AlertDialog dialog = new AlertDialog.Builder(PhoneMappingActivity.this)
                .setTitle("映射号码添加")
                .setView(view).setNegativeButton("取消", null)
                .setPositiveButton("确定添加", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String originStr = originPhone.getText().toString().trim();
                        String mappingStr = mappingPhone.getText().toString().trim();
                        if (TextUtils.isEmpty(originStr)) {
                            Toast.makeText(PhoneMappingActivity.this, "原始电话号码不能为空哦~", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (TextUtils.isEmpty(mappingStr)) {
                            Toast.makeText(PhoneMappingActivity.this, "映射电话号码不能为空哦~", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (originStr.length() < 3) {
                            Toast.makeText(PhoneMappingActivity.this, "原始电话号码太短哦~", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (mappingStr.length() < 3) {
                            Toast.makeText(PhoneMappingActivity.this, "映射电话号码太短哦~", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        List<PhoneMappingItem> list = mAdapter.getListData();
                        PhoneMappingItem bean = new PhoneMappingItem(originStr, mappingStr);
                        if (!list.contains(bean)) {
                            list.add(0, bean);
                            mPreferHelper.saveValue(list);
                            mAdapter.notifyItemInserted(0);
                            binding.recyclerView.smoothScrollToPosition(0);
                        } else {
                            Toast.makeText(PhoneMappingActivity.this, "原始电话号码已存在哦~", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).create();
        return dialog;
    }

    class MyAdapter extends BaseRecyclerAdapter<MyAdapter.ViewHolder, PhoneMappingItem> {

        public MyAdapter() {
            super(R.layout.item_phone_mapping_layout);
        }

        @Override
        public MyAdapter.ViewHolder createViewHolder(View itemView, int viewType) {
            return new MyAdapter.ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyAdapter.ViewHolder holder, final int position) {
            PhoneMappingItem item = getItem(position);
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    new AlertDialog.Builder(PhoneMappingActivity.this)
                            .setTitle("是否删除映射记录？")
                            .setNegativeButton("取消", null)
                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    List<PhoneMappingItem> mappingItems = mAdapter.getListData();
                                    mAdapter.notifyItemRemoved(position);
                                    mappingItems.remove(position);
                                    mPreferHelper.saveValue(mappingItems);
                                }
                            }).create().show();
                    return false;
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initDialog(item.getOriginPhone(), item.getMappingPhone()).show();
                }
            });
            holder.originalTv.setText(item.getOriginPhone());
            holder.mappingTv.setText(item.getMappingPhone());
        }

        public class ViewHolder extends BaseViewHolder {

            public TextView originalTv, mappingTv;

            public ViewHolder(View itemView) {
                super(itemView);
                originalTv = itemView.findViewById(R.id.original_tv);
                mappingTv = itemView.findViewById(R.id.mapping_tv);
            }
        }
    }
}