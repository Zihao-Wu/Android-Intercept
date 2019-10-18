package com.wzh.androidintercept.base;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

/**
 * Created by wzh on 2016/8/1.
 * Base RecyclerView class for an Adapter
 * {@link RecyclerView}.
 */
public abstract class BaseRecyclerAdapter<VH extends BaseViewHolder,E> extends RecyclerView.Adapter<VH>{

    private static final String TAG = "BaseRecyclerAdapter";
    private OnItemClickListener mOnItemClickListener;
    protected List<E> mListData;
    protected LayoutInflater mLayoutInflater;
    protected Context mContext;
    private int resource;

    public BaseRecyclerAdapter(){
        this(0);
    }

    public BaseRecyclerAdapter(@LayoutRes int resource) {
        this.resource = resource;
        initGenericClass();
    }

    private void initGenericClass(){
        /*ParameterizedType genType = FrameworkUtils.getParameterizedType(getClass());
        if (genType != null) {
            Type[] params = genType.getActualTypeArguments();
            if (params != null && params.length > 0){
                mVHClass = (Class) params[0]; //获取 ViewHolder class
            }
        }*/
    }

    public void setData(List<E> listData) {
        this.mListData = listData;
        notifyDataSetChanged();
    }

    public void addAll(int location, List<E> listData) {
        if(mListData==null){
            setData(listData);
        }else if(listData!=null){
            this.mListData.addAll(location,listData);
            notifyItemRangeInserted(location,listData.size());//刷新新数据区域
        }
    }
    
    public void addAll(List<E> listData) {
        addAll(mListData.size(),listData);
    }

    public List<E> getListData() {
        return mListData;
    }

    /**
     * 构造方法为 BaseRecyclerAdapter() 时，必须 重写此方法
     */
    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        try {
            if(resource==0){
                throw new RuntimeException("resource is 0 , please override onCreateViewHolder(ViewGroup parent, int viewType)");
            }
            if (mLayoutInflater == null) {
                mContext=parent.getContext();
                mLayoutInflater = LayoutInflater.from(mContext);
            }
            View layoutView = mLayoutInflater.inflate(resource, parent, false);
            VH viewHolder=createViewHolder(layoutView,viewType);
            viewHolder.mClickListener =mOnItemClickListener;
            return viewHolder;
        } catch (Exception e) {
            Log.e(TAG, "onCreateViewHolder: ", e);
            e.printStackTrace();
        }
        return null;
    }
    public abstract VH createViewHolder(View itemView, int viewType);

    @Override
    public int getItemCount() {
        return mListData ==null ? 0 : mListData.size();
    }

    public E getItem(int position){
        return mListData==null ? null : position>=mListData.size() ? null :mListData.get(position);
    }
    
    public void setOnItemClickListener(final OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    /**
     * 释放
     */
    public void release(){
        mOnItemClickListener=null;
        if(mListData!=null){
            mListData.clear();
        }
        mListData=null;
        mLayoutInflater=null;
        resource=0;
    }



}
