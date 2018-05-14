package com.weimu.library.view;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;


public class SelectorBaseActivity extends AppCompatActivity implements BaseView {
    private ViewGroup contentView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//请求为竖直屏幕
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        contentView = getWindow().getDecorView().findViewById(android.R.id.content);
    }


    //获取内容视图
    @Override
    public ViewGroup getContentView() {
        return contentView;
    }
}
