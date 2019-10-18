package com.wzh.androidintercept.base;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by wzh on 2016/8/15.
 * 可以添加 HeadView 和FootView 的Recycler Adapter
 */
public abstract class HeadFootRecyclerAdapter<VH extends BaseViewHolder,E> extends BaseRecyclerAdapter<BaseViewHolder, E> {

    private ArrayList<View> mHeaderViews;
    private ArrayList<View> mFooterViews;
    private final String TAG="HeadFootRecyclerAdapter";

    public HeadFootRecyclerAdapter() {
    }

    public HeadFootRecyclerAdapter(@LayoutRes int resource) {
        super(resource);
    }

    public void addHeaderView(View headView) {
        if(mHeaderViews==null){
            mHeaderViews=new ArrayList<>();
        }
        mHeaderViews.add(0,headView);
        // in the case of re-adding a header view, or adding one later on,
        // we need to notify the observer
        notifyItemInserted(0);
    }

    public void addFooterView(View footView) {
        if(mFooterViews==null){
            mFooterViews=new ArrayList<>();
        }
        mFooterViews.add(footView);
        notifyItemInserted(getItemCount());
    }

    public View getHeadView(int index){
        return mHeaderViews.get(index);
    }

    public View getFooterView(int index){
        return mFooterViews.get(index);
    }

    public boolean removeHeader(View v) {
        if(mHeaderViews==null){
            return false;
        }
        for (int i = 0; i < mHeaderViews.size(); i++) {
            View view = mHeaderViews.get(i);
            if (view == v) {
                mHeaderViews.remove(view);
                notifyItemRemoved(i);
                return true;
            }
        }
        return false;
    }

    public boolean removeFooter(View v) {
        if(mFooterViews==null){
            return false;
        }
        for (int i = 0; i < mFooterViews.size(); i++) {
            View view = mFooterViews.get(i);
            if (view == v) {
                mFooterViews.remove(i);
                notifyItemRemoved(getHeadersCount()+getDataCount()+i);
                return true;
            }
        }
        return false;
    }

    public boolean clearHeader() {
        if (mHeaderViews == null) {
            return false;
        }
        for (int i = 0; i < mHeaderViews.size(); i++) {
            View headView=mHeaderViews.get(i);
            notifyItemRemoved(i);
            if(headView.getParent()!=null){
                ((ViewGroup)headView.getParent()).removeView(headView);
            }
        }
        mHeaderViews.clear();
        return true;
    }
    public boolean clearFooter() {
        if (mFooterViews == null) {
            return false;
        }
        for (int i = 0; i < mFooterViews.size(); i++) {
            notifyItemRemoved(getHeadersCount() + getDataCount() + i);
        }
        mFooterViews.clear();
        return true;
    }

    //重写插入数据，刷新新数据区域
    @Override
    public void addAll(int location, List<E> listData) {
        if(mListData==null){
            setData(listData);
        }else if(listData!=null){
            this.mListData.addAll(location,listData);
            notifyItemRangeInserted(location+getHeadersCount(),listData.size());//刷新新数据区域
        }
    }

    @Override
    public int getItemViewType(int position) {
        int headCount=getHeadersCount();
        if(position<headCount){
            return TypeSpec.makeTypeSpec(position, TypeSpec.HEADER_VIEW_TYPE);
        }else if(position - headCount >= getDataCount()){
            return TypeSpec.makeTypeSpec(position, TypeSpec.FOOTER_VIEW_TYPE);
        }else{
            return getNormalItemViewType(position);
        }
    }

    protected int getNormalItemViewType(int position) {
       return TypeSpec.makeTypeSpec(position,  TypeSpec.ITEM_VIEW_TYPE);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int typeSpec) {
        int viewType=TypeSpec.getViewType(typeSpec);
        int position=TypeSpec.getPosition(typeSpec);

        if(viewType == TypeSpec.HEADER_VIEW_TYPE){//headView
            View itemView=mHeaderViews.get(position);
            return new BaseViewHolder(itemView);
        }else if(viewType == TypeSpec.FOOTER_VIEW_TYPE){//footerView
            View itemView=mFooterViews.get(position-getDataCount()-getHeadersCount());
            return new BaseViewHolder(itemView);
        }else{
            return onCreateNormalViewHolder(parent,position,viewType);
        }
    }

    protected BaseViewHolder onCreateNormalViewHolder(ViewGroup parent,int position, int viewType){
        return super.onCreateViewHolder(parent, viewType);
    }


    public abstract VH createViewHolder(View itemView, int viewType);

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int headCount=getHeadersCount();
        if((position<headCount) || (position - headCount >= getDataCount())){//head footer position
            return ;
        }else{
            onBindViewHolder((VH) holder,position,holder.getItemViewType());
        }
    }

    public abstract void onBindViewHolder(VH holder, int position,int viewType);

    @Override
    public int getItemCount() {
        return getFootersCount() + getHeadersCount() + getDataCount();
    }

    /**
     * 获取数据长度，不包含 head Footer
     * @return
     */
    public int getDataCount() {
        return super.getItemCount();
    }

    @Override
    public E getItem(int position) {
        return super.getItem(position-getHeadersCount());
    }

    public int getHeadersCount() {
        return mHeaderViews == null ? 0 : mHeaderViews.size();
    }

    public int getFootersCount() {
        return mFooterViews == null ? 0 : mFooterViews.size();
    }


    private GridLayoutManager.SpanSizeLookup mSpanSizeLookup;

    public GridLayoutManager.SpanSizeLookup getSpanSizeLookup() {
        return mSpanSizeLookup;
    }

    public void setSpanSizeLookup(GridLayoutManager.SpanSizeLookup spanSizeLookup) {
        mSpanSizeLookup = spanSizeLookup;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager layoutManager=recyclerView.getLayoutManager();
        if(layoutManager instanceof GridLayoutManager){//兼容GridLayoutManager
            final GridLayoutManager gridManager= (GridLayoutManager) layoutManager;
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {//返回指定position的view所占的cell数
                    int headCount=getHeadersCount();
                    if(position<headCount){
                        return gridManager.getSpanCount();
                    }else if(position - headCount >= getDataCount()){
                        return gridManager.getSpanCount();
                    }else{
                        if(mSpanSizeLookup!=null)
                            return mSpanSizeLookup.getSpanSize(position);
                        else
                            return 1;
                    }
                }
            });
        }
    }

    @Override
    public void onViewAttachedToWindow(BaseViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {////兼容StaggeredGridLayoutManager
            int headCount = getHeadersCount();
            int position = holder.getLayoutPosition();
            if ((position < headCount) || (position - headCount >= getDataCount())) {//head footer view full span
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }

        }
    }

    /**
     * 写法参照 MeasureSpec，把两个int值存在一个int中
     * 模仿 {@link View.MeasureSpec}.
     */
    public static class TypeSpec {
        private static final int TYPE_SHIFT = 30;
        private static final int TYPE_MASK = 0x3 << TYPE_SHIFT;

        public static final int HEADER_VIEW_TYPE=0 << TYPE_SHIFT;
        public static final int FOOTER_VIEW_TYPE=1 << TYPE_SHIFT;
        public static final int ITEM_VIEW_TYPE  =2 << TYPE_SHIFT;


        public static int makeTypeSpec(int position,int viewType) {
            return (position & ~TYPE_MASK) | (viewType & TYPE_MASK);
        }

        public static int getViewType(int typeSpec) {
            return (typeSpec & TYPE_MASK);
        }

        public static int getPosition(int typeSpec) {
            return (typeSpec & ~TYPE_MASK);
        }

    }
}
