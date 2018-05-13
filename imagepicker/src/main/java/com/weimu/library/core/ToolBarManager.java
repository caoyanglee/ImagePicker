package com.weimu.library.core;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.weimu.library.R;
import com.weimu.library.utils.ScreenUtils;


/**
 * Created by 艹羊 on 2017/5/26.
 */

public class ToolBarManager {
    private AppCompatActivity mActivity;
    private ViewGroup mContent;

    private ViewGroup mParent;
    private View mStatusCover;//状态栏遮罩
    private Toolbar mToolBar;
    private TextView mTitleTextView;

    private TextView mMenuTextViewLeft;
    private TextView mMenuTextView;
    private ImageView mMenuIconRight;
    private ImageView mMenuIconLeft;

    private int BackGroundColor = -1;


    public ToolBarManager(AppCompatActivity mActivity, ViewGroup mContent) {
        this.mActivity = mActivity;

        this.mContent = mContent;
        initToolbar(this.mContent);
    }

    public static ToolBarManager with(AppCompatActivity mActivity, ViewGroup mContent) {
        return new ToolBarManager(mActivity, mContent);
    }


    private void initToolbar(ViewGroup mContent) {
        if (mContent == null) {
            return;
        }
        //toolbar
        mToolBar = myFindViewsById(mContent, R.id.toolbar);
        if (mToolBar == null)
            return;


        //mParent
        mParent = myFindViewsById(mContent, R.id.toolbar_parent);

        //status cover
        mStatusCover = myFindViewsById(mContent, R.id.view_cover);
        //auto set height
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mStatusCover.setMinimumHeight(0);
            mStatusCover.setVisibility(View.GONE);
        } else {
            int statusBarHight = ScreenUtils.getStatusBarHeight(mActivity);
            mStatusCover.setMinimumHeight(statusBarHight);
        }

        //title
        mTitleTextView = myFindViewsById(mContent, R.id.toolbar_title);
        mMenuIconRight = myFindViewsById(mContent, R.id.toolbar_menu_icon_right);
        mMenuIconLeft = myFindViewsById(mContent, R.id.toolbar_menu_icon_left);


        //textmenu
        mMenuTextView = myFindViewsById(mContent, R.id.toolbar_menu_text);
        mMenuTextViewLeft = myFindViewsById(mContent, R.id.toolbar_menu_text_left);


        //setup
        mActivity.setSupportActionBar(mToolBar);
        ActionBar actionBar = mActivity.getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(false);
        setNavigationIcon(-1);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onNavigationClickListener != null)
                    onNavigationClickListener.onClick(v);
                else
                    mActivity.onBackPressed();
            }
        });

        mToolBar.setNavigationIcon(null);

    }

    private View.OnClickListener onNavigationClickListener;

    public ToolBarManager setOnNavigationClickListener(View.OnClickListener onNavigationClickListener) {
        this.onNavigationClickListener = onNavigationClickListener;
        return this;
    }

    /**
     * 设置Toolbar的背景颜色--资源文件
     */
    public ToolBarManager setBackgroundColor(@ColorRes int color) {
        int BgColor = ContextCompat.getColor(mActivity, color);
        mParent.setBackgroundColor(BgColor);
        BackGroundColor = BgColor;
        return this;
    }


    /**
     * 设置Tollbar的背景-drawable
     */
    public ToolBarManager setBackground(Drawable background) {
        mParent.setBackground(background);
        return this;
    }

    /**
     * 设置Tollbar的背景-DrawableRes
     */
    public ToolBarManager setBackground(@DrawableRes int drawableRes) {

        mParent.setBackground(ContextCompat.getDrawable(mActivity, drawableRes));
        return this;
    }


    /**
     * 设置左边的按钮Icon
     * 备注：若不想显示图片，可以输入-1或者0
     */
    public ToolBarManager setNavigationIcon(@DrawableRes int resId) {
        if (resId != 0 && resId != -1) {
            mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mToolBar.setNavigationIcon(resId);
        } else {
            mToolBar.setNavigationIcon(null);
        }
        return this;
    }

    /**
     * 设置标题内容
     */
    public ToolBarManager setTitle(CharSequence charsequence) {
        mTitleTextView.setText(charsequence);
        return this;
    }


    /**
     * 设置标题颜色
     */
    public ToolBarManager setTitleColor(@ColorRes int color) {
        int TextColor = ContextCompat.getColor(mActivity, color);
        mTitleTextView.setTextColor(TextColor);
        return this;
    }

    /**
     * 获取标题视图
     *
     *  若有特殊处理，请获取此视图进行修改
     */
    public TextView getTitle() {
        return mTitleTextView;
    }


    /**
     * 设置【右侧文本菜单】的内容
     */
    public ToolBarManager setMenuTextContent(CharSequence charsequence) {
        if (TextUtils.isEmpty(charsequence.toString())) {
            mMenuTextView.setVisibility(View.GONE);
            return this;
        }
        mMenuTextView.setVisibility(View.VISIBLE);
        mMenuTextView.setText(charsequence);
        return this;
    }

    /**
     * 设置【右侧文本菜单】的是否可点
     */
    public ToolBarManager setMenuTextEnable(boolean enable) {
        mMenuTextView.setEnabled(enable);
        return this;
    }


    /**
     * 设置【右侧文本菜单】标题颜色
     */
    public ToolBarManager setMenuTextColor(@ColorRes int color) {
        mMenuTextView.setVisibility(View.VISIBLE);
        int TextColor = ContextCompat.getColor(mActivity, color);
        mMenuTextView.setTextColor(TextColor);
        return this;
    }

    public ToolBarManager setMenuTextColors(@ColorRes int colors) {
        ColorStateList colorStateList = ContextCompat.getColorStateList(mActivity, colors);
        mMenuTextView.setTextColor(colorStateList);
        return this;
    }

    /**
     * 设置【右侧文本菜单】点击事件
     */
    public ToolBarManager setMenuTextClick(final OnMenuTextClickListener onMenuTextClickListener) {
        mMenuTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onMenuTextClickListener != null)
                    onMenuTextClickListener.onMenuTextClick();
            }
        });
        return this;
    }


    /**
     * 设置【左侧文本菜单】的内容
     */
    public ToolBarManager setLeftMenuTextContent(CharSequence charsequence) {
        mMenuTextViewLeft.setVisibility(View.VISIBLE);
        mMenuTextViewLeft.setOnClickListener(null);
        mMenuTextViewLeft.setText(charsequence);
        return this;
    }


    /**
     * 设置【左侧文本菜单】标题颜色
     */
    public ToolBarManager setLeftMenuTextColor(@ColorRes int color) {
        mMenuTextViewLeft.setVisibility(View.VISIBLE);
        int TextColor = ContextCompat.getColor(mActivity, color);
        mMenuTextViewLeft.setTextColor(TextColor);
        return this;
    }

    /**
     * 设置【左侧文本菜单】点击事件
     */
    public ToolBarManager setLeftMenuTextClick(final OnMenuTextClickListener onMenuTextClickListener) {
        mMenuTextViewLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onMenuTextClickListener != null)
                    onMenuTextClickListener.onMenuTextClick();
            }
        });
        return this;
    }


    /**
     * @return ToolBarManager
     * 设置右侧icon
     */
    public ToolBarManager setRightMenuIconRes(@DrawableRes int resId) {
        mMenuIconRight.setVisibility(View.VISIBLE);
        mMenuIconRight.setImageResource(resId);
        return this;
    }

    /**
     * @param listener
     * @return ToolBarManager
     * 设置右侧icon点击事件
     */
    public ToolBarManager setRightMenuIconClickListener(View.OnClickListener listener) {
        mMenuIconRight.setOnClickListener(listener);
        return this;
    }

    /**
     * @return ToolBarManager
     * 设置左侧icon
     */
    public ToolBarManager setLeftMenuIconRes(@DrawableRes int resId) {
        mMenuIconLeft.setVisibility(View.VISIBLE);
        mMenuIconLeft.setImageResource(resId);
        return this;
    }

    public View getLeftMenuIcon() {
        return mMenuIconLeft;
    }

    /**
     * @param listener
     * @return ToolBarManager
     * 设置左侧icon点击事件
     */
    public ToolBarManager setLeftMenuIconClickListener(View.OnClickListener listener) {
        mMenuIconLeft.setOnClickListener(listener);
        return this;
    }


    /**
     * 【右侧文本菜单】的接口
     */
    public interface OnMenuTextClickListener {
        void onMenuTextClick();
    }

    /**
     * 获取【右侧文本菜单】视图
     * <p>
     * 若有特殊处理，请获取此视图进行修改
     */
    public TextView getmMenuTextView() {
        return mMenuTextView;
    }


    /**
     * 获取控件的方法
     */
    public <T extends View> T myFindViewsById(View content, int viewId) {
        View view = content.findViewById(viewId);
        return (T) view;
    }


    /**
     * 设置兼容系统窗口
     *
     * @param activity
     */
    public void fitSystemWindow(Activity activity) {
        mStatusCover.setMinimumHeight(0);
        mStatusCover.setVisibility(View.GONE);
    }

    /**
     * toolbar本身的点击事件
     */
    public void setOnclickListener(@Nullable View.OnClickListener l) {
        mToolBar.setOnClickListener(l);
    }


    public void showToolBar() {
        mParent.setVisibility(View.VISIBLE);
    }

    public void hideToolBar() {
        mParent.setVisibility(View.GONE);
    }

    public View getToolBarView() {
        return mToolBar;
    }


}
