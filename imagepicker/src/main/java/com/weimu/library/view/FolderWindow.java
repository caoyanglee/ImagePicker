package com.weimu.library.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.PopupWindow;

import com.weimu.library.R;
import com.weimu.library.adapter.ImageFolderAdapter;
import com.weimu.library.model.LocalMediaFolder;
import com.weimu.library.utils.ScreenUtils;

import java.lang.reflect.Method;
import java.util.List;


public class FolderWindow extends PopupWindow {
    private Context context;
    private View window;
    private RecyclerView recyclerView;
    private ImageFolderAdapter adapter;

    private boolean isDismiss = false;

    public FolderWindow(Context context) {
        this.context = context;
        window = LayoutInflater.from(context).inflate(R.layout.window_folder, null);
        View view = window.findViewById(R.id.lin_parent);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        this.setContentView(window);
        this.setWidth(ScreenUtils.getScreenWidth(context));
        this.setHeight(ScreenUtils.getScreenHeight(context) - ScreenUtils.dip2px(context, 96));
        this.setAnimationStyle(R.style.WindowStyle);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.setClippingEnabled(false);
        this.update();
        this.setBackgroundDrawable(new ColorDrawable(Color.argb(153, 0, 0, 0)));


        initView();
        registerListener();
        setPopupWindowTouchModal(this, false);
    }

    public void initView() {
        adapter = new ImageFolderAdapter(context);

        recyclerView = (RecyclerView) window.findViewById(R.id.folder_list);
        recyclerView.addItemDecoration(new ItemDivider());
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

        recyclerView.setVisibility(View.GONE);
    }

    public void registerListener() {

    }

    public void bindFolder(List<LocalMediaFolder> folders) {
        adapter.bindFolder(folders);
    }

    @Override
    public void showAsDropDown(View anchor) {
        super.showAsDropDown(anchor);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                recyclerView.setVisibility(View.VISIBLE);
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.up_in);
                recyclerView.startAnimation(animation);
            }
        }, 300);

    }

    public void setOnItemClickListener(ImageFolderAdapter.OnItemClickListener onItemClickListener) {
        adapter.setOnItemClickListener(onItemClickListener);
    }

    @Override
    public void dismiss() {
        if (isDismiss) {
            return;
        }
        isDismiss = true;
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.down_out);
        recyclerView.startAnimation(animation);
        dismiss();
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isDismiss = false;
                recyclerView.setVisibility(View.GONE);
                FolderWindow.super.dismiss();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public static void setPopupWindowTouchModal(PopupWindow popupWindow, boolean touchModal) {
        if (null == popupWindow) {
            return;
        }
        Method method;
        try {
            method = PopupWindow.class.getDeclaredMethod("setTouchModal", boolean.class);
            method.setAccessible(true);
            method.invoke(popupWindow, touchModal);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class ItemDivider extends RecyclerView.ItemDecoration {
        private Drawable mDrawable;

        public ItemDivider() {
            mDrawable = context.getResources().getDrawable(R.drawable.item_divider);
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent) {
            final int left = ScreenUtils.dip2px(parent.getContext(), 16);
            final int right = parent.getWidth() - left;

            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount - 1; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                final int top = child.getBottom() + params.bottomMargin;
                final int bottom = top + mDrawable.getIntrinsicHeight();
                mDrawable.setBounds(left, top, right, bottom);
                mDrawable.draw(c);
            }
        }

        @Override
        public void getItemOffsets(Rect outRect, int position, RecyclerView parent) {
            outRect.set(0, 0, 0, mDrawable.getIntrinsicWidth());
        }

    }

}