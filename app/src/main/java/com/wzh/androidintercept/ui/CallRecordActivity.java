package com.wzh.androidintercept.ui;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;

import com.wzh.androidintercept.R;
import com.wzh.androidintercept.base.BaseRecyclerAdapter;
import com.wzh.androidintercept.base.BaseViewHolder;
import com.wzh.androidintercept.bean.PhoneBean;
import com.wzh.androidintercept.databinding.ActivityCallRecordBinding;
import com.wzh.androidintercept.utils.PreferceHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CallRecordActivity extends BaseActivity {

    private ActivityCallRecordBinding binding;
    private ContentResolver resolver;
    private MyAdapter mAdapter;

    private Uri callUri = CallLog.Calls.CONTENT_URI;
    private String[] columns = {
            CallLog.Calls.CACHED_NAME// 通话记录的联系人
            , CallLog.Calls.NUMBER// 通话记录的电话号码
            , CallLog.Calls.DATE// 通话记录的日期
            , CallLog.Calls.DURATION// 通话时长
            , CallLog.Calls.TYPE// 通话类型
            , CallLog.Calls.GEOCODED_LOCATION//归属地

    };

    private PreferceHelper<List<PhoneBean>> mPreferHelper;

    private List<Map<String, String>> callRecordList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();

    }

    private void initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_call_record);
        setTitle("通话记录");
        callRecordList = getCallRecordList();

        mAdapter = new MyAdapter();
        binding.recyclerView.setAdapter(mAdapter);
        mAdapter.setData(callRecordList);
//        binding.recyclerView.addItemDecoration(new CommonItemDecoration(this, R.drawable.divider_shape));
    }

    private List<Map<String, String>> getCallRecordList() {
        SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
        //1.获取ContentResolver
        resolver = getContentResolver();
        //2.利用ContentResolver的query方法查询通话记录数据库
        Cursor cursor = resolver.query(callUri, columns, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
        //3.通过Cursor获取数据
        List<Map<String, String>> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
            String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
            long dateLong = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));
            int duration = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION));
            int type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));
            String location = cursor.getString(cursor.getColumnIndex(CallLog.Calls.GEOCODED_LOCATION));


            if (dateLong > dateLong - 5 * 60 * 60 * 24) {//只显示5天以内通话记录，防止通话记录数据过多影响加载速度
                Map<String, String> map = new HashMap<>();
                //"未备注联系人"
                map.put("name", (name == null) ? "未备注联系人" : name);//姓名
                map.put("number", number);//手机号
                map.put("date", format.format(new Date(dateLong)));//通话日期
                map.put("duration", (duration / 60) + "分钟");//时长
                map.put("type", type + "");//类型
                map.put("location", location);//类型
                list.add(map);
            } else {
                return list;
            }
        }
        return list;
    }

    private void showToast(String s) {
        Toast.makeText(CallRecordActivity.this, s, Toast.LENGTH_SHORT).show();
    }

    class MyAdapter extends BaseRecyclerAdapter<MyAdapter.ViewHolder, Map<String, String>> {

        public MyAdapter() {
            super(R.layout.item_call_record_layout);
        }

        @Override
        public MyAdapter.ViewHolder createViewHolder(View itemView, int viewType) {
            return new MyAdapter.ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyAdapter.ViewHolder holder, final int position) {
            Map<String, String> item = getItem(position);
            switch (Integer.parseInt(item.get("type"))) {
                case CallLog.Calls.INCOMING_TYPE:
                    //"打入"
                    holder.typeIv.setImageDrawable(getResources().getDrawable(R.drawable.ic_icon_call_in));
                    holder.phoneTv.setTextColor(getResources().getColor(R.color.black_333333));
                    break;
                case CallLog.Calls.OUTGOING_TYPE:
                    //"打出"
                    holder.typeIv.setImageDrawable(getResources().getDrawable(R.drawable.ic_icon_call_out));
                    holder.phoneTv.setTextColor(getResources().getColor(R.color.black_333333));
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    //"未接"
                    holder.typeIv.setImageDrawable(getResources().getDrawable(R.drawable.ic_icon_call_fail));
                    holder.phoneTv.setTextColor(getResources().getColor(R.color.tv_red2));
                    break;
                case CallLog.Calls.REJECTED_TYPE:
                    //"拒接"
                    holder.typeIv.setImageDrawable(getResources().getDrawable(R.drawable.ic_icon_call_rejected));
                    holder.phoneTv.setTextColor(getResources().getColor(R.color.tv_red2));
                    break;
                default:
                    break;
            }
            holder.phoneTv.setText(item.get("number"));
            holder.locationTv.setText(item.get("location"));
            holder.timeTv.setText(item.get("date"));

            holder.settingIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //加入黑名单/白名单
                    final View view = getLayoutInflater().inflate(R.layout.add_phone_opertate, null);
                    final TextView whiteTv = view.findViewById(R.id.white_tv);
                    final TextView blackTv = view.findViewById(R.id.black_tv);
                    AlertDialog alertDialog = new AlertDialog.Builder(CallRecordActivity.this)
                            .setView(view)
                            .show();
                    whiteTv.setOnClickListener(v1 -> {
                        mPreferHelper = new PreferceHelper<>(PreferceHelper.FILE_MAIN, PreferceHelper.KEY_WHITE_LIST);
                        List<PhoneBean> phoneBeanList = mPreferHelper.getValue(new ArrayList<>());
                        PhoneBean bean = new PhoneBean(item.get("number"));
                        if (!phoneBeanList.contains(bean)) {
                            phoneBeanList.add(0, bean);
                            mPreferHelper.saveValue(phoneBeanList);
                            showToast("白名单添加成功");
                        } else {
                            showToast("白名单该号码已存在哦~");
                        }
                        alertDialog.dismiss();
                    });
                    blackTv.setOnClickListener(v12 -> {
                        mPreferHelper = new PreferceHelper<>(PreferceHelper.FILE_MAIN, PreferceHelper.KEY_BLACK_LIST);
                        List<PhoneBean> phoneBeanList = mPreferHelper.getValue(new ArrayList<>());
                        PhoneBean bean = new PhoneBean(item.get("number"));
                        if (!phoneBeanList.contains(bean)) {
                            phoneBeanList.add(0, bean);
                            mPreferHelper.saveValue(phoneBeanList);
                            showToast("黑名单添加成功");
                        } else {
                            showToast("黑名单该号码已存在哦~");
                        }
                        alertDialog.dismiss();
                    });
                }
            });
        }


        public class ViewHolder extends BaseViewHolder {

            public TextView phoneTv, locationTv, timeTv;
            public ImageView typeIv, settingIv;

            public ViewHolder(View itemView) {
                super(itemView);
                typeIv = itemView.findViewById(R.id.type_iv);
                phoneTv = itemView.findViewById(R.id.tv_phone);
                locationTv = itemView.findViewById(R.id.tv_location);
                timeTv = itemView.findViewById(R.id.tv_time);
                settingIv = itemView.findViewById(R.id.setting_iv);
            }
        }
    }
}