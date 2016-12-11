package io.weimu.www.imagepicker.adaper.pageradapter.base;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * 加载View视图
 */
public class BasePagerAdapter extends PagerAdapter {
    List<View> viewList;
    Context mContext;

    public BasePagerAdapter(Context mContext, List<View> viewList) {
        this.mContext = mContext;
        this.viewList = viewList;
    }

    @Override
    public int getCount() {
        return viewList.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(viewList.get(position));

    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = viewList.get(position);
        container.addView(view, 0);//添加页卡
        return view;
    }
}
