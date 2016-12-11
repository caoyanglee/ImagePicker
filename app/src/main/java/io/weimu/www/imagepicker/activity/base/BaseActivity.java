package io.weimu.www.imagepicker.activity.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;

import io.weimu.www.imagepicker.activity.MainActivity;

public abstract class BaseActivity extends AppCompatActivity {
    protected Context mContext;
    public static ArrayList<BaseActivity> activityList = new ArrayList<>();
    protected FragmentManager fragmentManager = getSupportFragmentManager();

    protected ImageView btn_back;//顶部栏返回键
    protected View mCloseBtn;//右侧关闭
    protected TextView tv_title;//顶部栏标题
    protected ProgressDialog mProgressDialog;
    protected AlertDialog mAlertDialog;

    //寻找控件
    abstract protected void findViewByIDS();

    //获取布局文件
    abstract protected int getLayoutId();


    //所有的初始化操作
    abstract protected void onGenerate();


    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        if (layoutResID<1){
            throw new IllegalStateException("activitty content ID not use");
        }
        super.setContentView(layoutResID);
        findViewByIDS();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //**********基础配置*********
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//请求为竖直屏幕
        activityList.add(this);
        mContext = this;
        //**********获取布局**********
        int layoutResId = getLayoutId();
        setContentView(layoutResId);
        //**********初始化数据*********
        onGenerate();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityList.remove(this);
    }


    protected TextView getTitleView() {
        if (tv_title != null) {
            return tv_title;
        } else {
            return null;
        }
    }




    public <T extends View> T myFindViewsById(int viewId) {
        View view = findViewById(viewId);
        return (T) view;
    }


    /**
     * 简单的跳转Activity
     */
    public void startActivity(Class<?> cls) {
        Intent intent = new Intent(BaseActivity.this, cls);
        startActivity(intent);
    }


    /**
     * 跳转Intent
     * 包含过度动画的实现
     */
    public void startActivity(Intent intent, int enterAnim) {
        startActivity(intent);
        overridePendingTransition(enterAnim, -1);
    }


    /**
     * 跳转Intent
     * 包含过度动画的实现
     */
    public void startActivityForResult(Intent intent, int requestCode, int enterAnim, int exitAnim) {
        startActivityForResult(intent, requestCode);
        overridePendingTransition(enterAnim, exitAnim);
    }

    private int exitAnimation = -1;

    protected void setExitAnimation(int exitAnimation) {
        this.exitAnimation = exitAnimation;
    }


    //默认左上角为返回，若有特殊需求请覆盖此方法
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setTitle(String title) {
        if (title != null && tv_title != null)
            tv_title.setText(title);
    }


    /**
     * finish掉所有activity
     */
    public void closeAllctivity() {
        for (Activity activity : activityList) {
            //Log.e("", "close:" + activity.getLocalClassName());
            if (activity != null) {
                activity.finish();
            }
        }
    }

    /**
     * finish掉所有activity  除了mainActivity
     */
    public void closeAllActivityWithOutMainActivity() {
        for (Activity activity : activityList) {
            if (activity != null && !activity.getClass().getSimpleName().equals(MainActivity.class.getSimpleName())) {
                activity.finish();
            }
        }
    }


    //获取bundle
    protected Bundle getBundle() {
        return getIntent().getExtras();
    }
}
