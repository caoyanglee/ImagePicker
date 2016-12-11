package io.weimu.www.imagepicker.adaper.pageradapter.base;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;

import java.util.List;

import io.weimu.www.imagepicker.fragment.base.BaseFragment;


public class BaseFragmentPagerAdapter extends FragmentPagerAdapter {

    private List<BaseFragment> mFragment;

    public BaseFragmentPagerAdapter(FragmentManager fm, List<BaseFragment> mFragment) {
        super(fm);
        this.mFragment = mFragment;
    }

    @Override
    public Fragment getItem(int arg0) {
        return mFragment.get(arg0);
    }

    @Override
    public int getCount() {
        return mFragment.size();
    }

    /**
     * 每次更新完成ViewPager的内容后，调用该接口，此处复写主要是为了让导航按钮上层的覆盖层能够动态的移动
     */
    @Override
    public void finishUpdate(View container) {
        super.finishUpdate(container);
    }
}
