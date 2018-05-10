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
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.weimu.library.ImageHolder;
import com.weimu.library.R;
import com.weimu.library.model.LocalMedia;
import com.weimu.library.widget.PreviewViewPager;

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
    private Toolbar toolbar;
    private TextView doneText;
    private CheckBox checkboxSelect;
    private PreviewViewPager viewPager;


    private int position;
    private int maxSelectNum;
    private List<LocalMedia> images = new ArrayList<>();//所有图片
    private List<LocalMedia> selectImages = new ArrayList<>();//选择的图片


    private boolean isShowBar = true;


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
        images = ImageHolder.getChooseImages();
        selectImages = getIntent().getParcelableArrayListExtra(EXTRA_PREVIEW_SELECT_LIST);
        maxSelectNum = getIntent().getIntExtra(EXTRA_MAX_SELECT_NUM, 9);
        position = getIntent().getIntExtra(EXTRA_POSITION, 1);

        selectBarLayout = (RelativeLayout) findViewById(R.id.select_bar_layout);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle((position + 1) + "/" + images.size());
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_back);


        doneText = (TextView) findViewById(R.id.done_text);
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
                toolbar.setTitle(position + 1 + "/" + images.size());
                onImageSwitch(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDoneClick(false);
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
        doneText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDoneClick(true);
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
        doneText.setEnabled(enable);
        if (enable) {
            doneText.setText(getString(R.string.done_num, selectImages.size(), maxSelectNum));
        } else {
            doneText.setText(R.string.done);
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
        toolbar.setVisibility(isShowBar ? View.GONE : View.VISIBLE);
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
