package io.weimu.www.imagepicker.adaper.recycleview.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public abstract class BaseRecycerAdapter<T> extends RecyclerView.Adapter<BaseRecyclerViewHolder> {

    protected int itemLayoutRes = -1;


    public BaseRecycerAdapter(Context mContext) {
        this.mContext = mContext;
    }

    private List<T> dataList = new ArrayList<>();//不让子类直接使用
    protected Context mContext;
    protected OnItemClick onItemClick;

    protected static final int TYPE_HEADER = 0, TYPE_ITEM = 1, TYPE_FOOT = 2;
    protected View headView;
    protected View footView;
    protected int headViewSize = 0;
    protected int footViewSize = 0;
    protected boolean isAddFoot = false;
    protected boolean isAddHead = false;

    public void addHeadView(View view) {
        headView = view;
        headViewSize = 1;
        isAddHead = true;
    }


    public void addFootView(View view) {
        footView = view;
        footViewSize = 1;
        isAddFoot = true;
    }


    public void removeFootView() {
        footView = null;
        footViewSize = 0;
        isAddFoot = false;
    }

    @Override
    public int getItemViewType(int position) {
        int type = TYPE_ITEM;
        if (headViewSize == 1 && position == 0) {
            type = TYPE_HEADER;
        } else if (footViewSize == 1 && position == getItemCount() - 1) {
            //最后一个位置
            type = TYPE_FOOT;
        }
        return type;
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View picNewsView = null;
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case TYPE_HEADER:
                picNewsView = headView;
                break;

            case TYPE_ITEM:
                picNewsView = mInflater.inflate(getItemLayoutRes(), parent, false);//防止item显示不全问题
                break;

            case TYPE_FOOT:
                picNewsView = footView;
                break;
        }
        return getViewHolder(picNewsView);
    }


    //获取Viewholder
    protected abstract BaseRecyclerViewHolder getViewHolder(View picNewsView);

    //获取布局
    protected abstract int getItemLayoutRes();

    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder recyclerholder, final int position) {
        switch (getItemViewType(position)) {
            case TYPE_HEADER:
                headerViewChange(recyclerholder);
                break;

            case TYPE_ITEM:
                //initOnClickListener(recyclerholder, position);
                ItemVIewChange(recyclerholder, position);
                break;

            case TYPE_FOOT:
                footerViewChange(recyclerholder);
                break;
        }
    }


    protected void headerViewChange(BaseRecyclerViewHolder recyclerholder){

    }

    protected void footerViewChange(BaseRecyclerViewHolder recyclerholder){

    }



    //初始化Item点击事件
    private void initOnClickListener(BaseRecyclerViewHolder recyclerholder, final int position) {
        //点击事件
        recyclerholder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick.onClick(position);
            }
        });
    }

    protected abstract void ItemVIewChange(BaseRecyclerViewHolder recyclerholder, final int position);

    @Override
    public int getItemCount() {
        return dataList.size() + headViewSize + footViewSize;
    }

    public List<T> getDataList() {
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }

    public void setDataToAdapter(List<T> data) {
        if (data == null) {
            return;
        }
        dataList.clear();
        dataList.addAll(data);
        notifyDataSetChanged();
    }

    public int getCount() {
        return dataList.size();
    }

    public void addData(List<T> data) {
        dataList.addAll(data);
        notifyDataSetChanged();
    }


    public void deleteData(int position) {
        dataList.remove(dataList.get(position));
        notifyDataSetChanged();
    }


    protected T getItem(int posstion) {
        return dataList.get(posstion - headViewSize);
    }

    //获取item所对应的position
    protected int getItemPosition(int posstion) {
        return posstion - headViewSize;
    }


    //item的点击接口
    public interface OnItemClick {
        void onClick(int position);

    }

    public void setOnItemClickListener(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }


    private ChangeGridLayoutManagerSpance changeGridLayoutManager;

    public interface ChangeGridLayoutManagerSpance {
        void change(boolean isAddHead, boolean isAddFoot);
    }

    //提供接口给 让LayoutManager根据添加尾部 头部与否来做判断 显示头部与底部的SpanSize要在添加头部和尾部之后
    public void setChangeGridLayoutManager(ChangeGridLayoutManagerSpance changeGridLayoutManager) {
        this.changeGridLayoutManager = changeGridLayoutManager;
        changeGridLayoutManager.change(isAddHead, isAddFoot);
    }
}
