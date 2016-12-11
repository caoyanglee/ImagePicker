package io.weimu.www.imagepicker.adaper.recycleview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import io.weimu.www.imagepicker.R;
import io.weimu.www.imagepicker.adaper.recycleview.base.BaseRecycerAdapter;
import io.weimu.www.imagepicker.adaper.recycleview.base.BaseRecyclerViewHolder;


public class ImageGridadapter extends BaseRecycerAdapter<String> {

    private int maxImageNumber = 9;

    protected View footView;


    private AddListenter addListenter;

    public ImageGridadapter(Context mContext) {
        super(mContext);
        new ImageGridadapter(mContext, maxImageNumber);
    }


    public ImageGridadapter(Context mContext, int maxImageNumber) {
        super(mContext);
        this.maxImageNumber = maxImageNumber;
        footView = LayoutInflater.from(mContext).inflate(R.layout.grid_item_footer, null);
        addFootView(footView);
    }

    @Override
    public void addData(List<String> data) {
        if (getCount() + data.size() == maxImageNumber) {
            removeFootView();
        }
        super.addData(data);
    }


    @Override
    public void deleteData(int position) {
        super.deleteData(position);
        if (getCount() < maxImageNumber) {
            addFootView(footView);
        }
    }


    @Override
    protected BaseRecyclerViewHolder getViewHolder(View picNewsView) {
        return new ViewHolder(picNewsView);
    }

    @Override
    protected int getItemLayoutRes() {
        return R.layout.grid_item;
    }

    @Override
    protected void ItemVIewChange(BaseRecyclerViewHolder recyclerholder, final int position) {
        ViewHolder holder = (ViewHolder) recyclerholder;
        final String imgURL = getItem(position);
        Glide.with(mContext).load(imgURL).asBitmap().into(holder.iv_cover);
        //点击事件
        if (addListenter != null) {
            holder.iv_cover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addListenter.onItemClick(position);
                }
            });
            holder.iv_cover_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addListenter.onItemDeleteClick(position);
                }
            });
        }
    }

    @Override
    protected void footerViewChange(BaseRecyclerViewHolder recyclerholder) {
        super.footerViewChange(recyclerholder);
        footView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addListenter != null) {
                    addListenter.onAddClick(ImageGridadapter.this.maxImageNumber - getCount());
                }
            }
        });
    }

    public class ViewHolder extends BaseRecyclerViewHolder {
        ImageView iv_cover;
        ImageView iv_cover_delete;

        public ViewHolder(View view) {
            super(view);
            iv_cover = findViewById(R.id.iv_cover);
            iv_cover_delete = findViewById(R.id.iv_cover_delete);
        }
    }

    public void AddListenter(AddListenter addListenter) {
        this.addListenter = addListenter;
    }

    public interface AddListenter {
        void onAddClick(int needNumber);

        void onItemClick(int position);

        void onItemDeleteClick(int position);
    }


}
