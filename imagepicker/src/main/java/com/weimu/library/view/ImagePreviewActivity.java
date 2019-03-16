package com.weimu.library.view;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.weimu.library.ImageStaticHolder;
import com.weimu.library.R;
import com.weimu.library.model.LocalMedia;
import com.weimu.library.widget.PreviewViewPager;
import com.weimu.universalview.core.toolbar.StatusBarManager;

import java.util.ArrayList;
import java.util.List;


public class ImagePreviewActivity extends SelectorBaseActivity {
    public static final int REQUEST_PREVIEW = 68;
    public static final String EXTRA_PREVIEW_SELECT_LIST = "previewSelectList";
    public static final String EXTRA_MAX_SELECT_NUM = "maxSelectNum";
    public static final String EXTRA_POSITION = "position";

    public static final String OUTPUT_LIST = "outputList";
    public static final String OUTPUT_ISDONE = "isDone";

    private RelativeLayout selectBarLayout;
    private CheckBox checkboxSelect;
    private PreviewViewPager viewPager;


    private int position;
    private int maxSelectNum=1;
    private List<LocalMedia> images = new ArrayList<>();//所有图片
    private List<LocalMedia> selectImages = new ArrayList<>();//选择的图片


    private boolean isShowBar = true;

    private ToolBarManager toolBarManager;


    public static void startPreview(Activity context, List<LocalMedia> images, List<LocalMedia> selectImages, int maxSelectNum, int position) {
        Intent intent = new Intent(context, ImagePreviewActivity.class);
        intent.putParcelableArrayListExtra(EXTRA_PREVIEW_SELECT_LIST, (ArrayList) selectImages);

        intent.putExtra(EXTRA_POSITION, position);
        intent.putExtra(EXTRA_MAX_SELECT_NUM, maxSelectNum);
        context.startActivityForResult(intent, REQUEST_PREVIEW);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void startPreviewWithAnim(Activity context, List<LocalMedia> images, List<LocalMedia> selectImages, int maxSelectNum, int position, View view) {
        Intent intent = new Intent(context, ImagePreviewActivity.class);
        intent.putParcelableArrayListExtra(EXTRA_PREVIEW_SELECT_LIST, (ArrayList) selectImages);

        intent.putExtra(EXTRA_POSITION, position);
        intent.putExtra(EXTRA_MAX_SELECT_NUM, maxSelectNum);
        context.startActivityForResult(intent, REQUEST_PREVIEW, ActivityOptions.makeSceneTransitionAnimation(context, view, "share_image").toBundle());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        initView();
        registerListener();
    }

    public void initView() {

        //images = getIntent().getParcelableArrayListExtra(EXTRA_PREVIEW_LIST);
        images = ImageStaticHolder.getChooseImages();
        selectImages = getIntent().getParcelableArrayListExtra(EXTRA_PREVIEW_SELECT_LIST);
        maxSelectNum = getIntent().getIntExtra(EXTRA_MAX_SELECT_NUM, 9);
        position = getIntent().getIntExtra(EXTRA_POSITION, 1);

        selectBarLayout = (RelativeLayout) findViewById(R.id.select_bar_layout);

        //状态栏和Toolbar
        StatusBarManager.INSTANCE.setColor(this.getWindow(), ContextCompat.getColor(this,R.color.white));
        StatusBarManager.INSTANCE.setLightMode(this.getWindow(),false);
        toolBarManager = ToolBarManager.with(this, getContentView())
                .setBackgroundColor(R.color.white)
                .setTitle((position + 1) + "/" + images.size())
                .setNavigationIcon(R.drawable.toolbar_arrow_back_black)
                .setOnNavigationClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onDoneClick(false);
                    }
                })
                .setMenuTextContent(getString(R.string.done))
                .setMenuTextColors(R.color.black_text_selector)
                .setMenuTextEnable(false)
                .setMenuTextClick(new ToolBarManager.OnMenuTextClickListener() {
                    @Override
                    public void onMenuTextClick() {
                        //点击完成
                        onDoneClick(true);
                    }
                });



        onSelectNumChange();

        checkboxSelect = (CheckBox) findViewById(R.id.checkbox_select);
        onImageSwitch(position);


        viewPager = (PreviewViewPager) findViewById(R.id.preview_pager);
        viewPager.setAdapter(new SimpleFragmentAdapter(getSupportFragmentManager()));
        viewPager.setCurrentItem(position);
    }

    public void registerListener() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                 toolBarManager.setTitle((position + 1) + "/" + images.size());
                onImageSwitch(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        checkboxSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = checkboxSelect.isChecked();
                if (selectImages.size() >= maxSelectNum && isChecked) {
                    Toast.makeText(ImagePreviewActivity.this, getString(R.string.message_max_num, maxSelectNum), Toast.LENGTH_LONG).show();
                    checkboxSelect.setChecked(false);
                    return;
                }
                LocalMedia image = images.get(viewPager.getCurrentItem());
                if (isChecked) {
                    selectImages.add(image);
                } else {
                    for (LocalMedia media : selectImages) {
                        if (media.getPath().equals(image.getPath())) {
                            selectImages.remove(media);
                            break;
                        }
                    }
                }
                onSelectNumChange();
            }
        });
    }

    public class SimpleFragmentAdapter extends FragmentPagerAdapter {
        public SimpleFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return ImagePreviewFragment.getInstance(images.get(position).getPath());
        }

        @Override
        public int getCount() {
            return images.size();
        }
    }

    public void onSelectNumChange() {
        boolean enable = selectImages.size() != 0;
        toolBarManager.setMenuTextEnable(enable);

        if (enable) {
            toolBarManager.setMenuTextContent(getString(R.string.done_num, selectImages.size(), maxSelectNum));
        } else {
            toolBarManager.setMenuTextContent(getString(R.string.done));
        }
    }

    public void onImageSwitch(int position) {
        checkboxSelect.setChecked(isSelected(images.get(position)));
    }

    public boolean isSelected(LocalMedia image) {
        for (LocalMedia media : selectImages) {
            if (media.getPath().equals(image.getPath())) {
                return true;
            }
        }
        return false;
    }

    private void hideStatusBar() {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
    }

    private void showStatusBar() {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
    }

    public void switchBarVisibility() {
        //todo是否显示
        if (isShowBar){
            toolBarManager.hideToolBar();
        }else{
            toolBarManager.showToolBar();
        }
        selectBarLayout.setVisibility(isShowBar ? View.GONE : View.VISIBLE);
        if (isShowBar) {
            hideStatusBar();
        } else {
            showStatusBar();
        }
        isShowBar = !isShowBar;
    }

    public void onDoneClick(boolean isDone) {
        Intent intent = new Intent();
        intent.putExtra(OUTPUT_LIST, (ArrayList) selectImages);
        intent.putExtra(OUTPUT_ISDONE, isDone);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        onDoneClick(false);
    }
}
