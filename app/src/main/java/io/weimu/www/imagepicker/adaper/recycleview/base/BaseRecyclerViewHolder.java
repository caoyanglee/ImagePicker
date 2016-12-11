package io.weimu.www.imagepicker.adaper.recycleview.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;


public class BaseRecyclerViewHolder extends RecyclerView.ViewHolder {
    public View itemView;

    public BaseRecyclerViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
    }

    public <T extends View> T findViewById(int viewId) {
        View view = itemView.findViewById(viewId);
        return (T) view;
    }
}
