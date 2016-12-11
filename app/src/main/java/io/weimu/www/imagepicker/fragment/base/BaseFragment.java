package io.weimu.www.imagepicker.fragment.base;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public abstract class BaseFragment extends Fragment {
    //寻找控件
    abstract protected void findViewByIDS();

    //获取布局文件
    abstract protected int getLayoutId();


    //所有的初始化操作
    abstract protected void onGenerate();


    protected Context mContext;
    protected View mView;//frgment的主界面

    protected ImageView btn_back;//顶部栏返回键
    protected TextView tv_title;//顶部栏标题

    protected AlertDialog baseDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        createView(getLayoutId(), inflater, container, savedInstanceState);
        onGenerate();
        return mView;
    }

    /**
     * 创建视图
     */
    protected void createView(int layoutResID, LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (layoutResID < 1) {
            throw new IllegalStateException("activitty content ID not use");
        }
        mContext = getActivity();
        mView = inflater.inflate(layoutResID, container, false);
        findViewByIDS();
    }

    @Override
    public void onResume() {
        super.onResume();
        // 判断当前fragment是否显示
        if (getUserVisibleHint()) {
            onVisible();
            //初始化数据
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        // 每次切换fragment时调用的方法
        if (isVisibleToUser) {
            //初始化数据
            onVisible();
        } else {
            onInvisible();
        }
    }



    /**
     * 显示的时候
     */
    protected void onVisible() {
        //Logger.d("fragment出现");
    }


    /***
     * 隐藏的时候
     */
    protected void onInvisible() {
        //Logger.d("fragment隐藏");
    }

    public <T extends View> T myFindViewsById(int viewId) {
        View view = mView.findViewById(viewId);
        return (T) view;
    }


    protected TextView getTitleView() {
        if (tv_title != null) {
            return tv_title;
        } else {
            return null;
        }
    }


    public void setTitle(String title) {
        if (title != null && tv_title != null)
            tv_title.setText(title);
    }

    /**
     * fragment中的开启activity
     */
    protected void myStartActivity(Intent intent) {
        getActivity().startActivity(intent);
    }


    //左上角返回
    protected void onBack() {
        getActivity().onBackPressed();
    }



}
