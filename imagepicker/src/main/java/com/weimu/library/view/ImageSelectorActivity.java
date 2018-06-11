package com.weimu.library.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.weimu.library.ImageStaticHolder;
import com.weimu.library.R;
import com.weimu.library.adapter.ImageFolderAdapter;
import com.weimu.library.adapter.ImageListAdapter;
import com.weimu.library.core.StatusManager;
import com.weimu.library.core.ToolBarManager;
import com.weimu.library.model.LocalMedia;
import com.weimu.library.model.LocalMediaFolder;
import com.weimu.library.utils.FileUtilsIP;
import com.weimu.library.utils.GridSpacingItemDecoration;
import com.weimu.library.utils.LocalMediaLoader;
import com.weimu.library.utils.ScreenUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;


public class ImageSelectorActivity extends SelectorBaseActivity {
    public final static int REQUEST_IMAGE = 66;
    public final static int REQUEST_CAMERA = 67;

    public final static String BUNDLE_CAMERA_PATH = "CameraPath";

    public final static String REQUEST_OUTPUT = "outputList";


    public final static String EXTRA_MAX_SELECT_NUM = "MaxSelectNum";//最大选择数
    public final static String EXTRA_SELECT_MODE = "SelectMode";//选择模式
    public final static String EXTRA_SHOW_CAMERA = "ShowCamera";//是否显示摄像头
    public final static String EXTRA_ENABLE_PREVIEW = "EnablePreview";//是否需要预览
    public final static String EXTRA_ENABLE_CROP = "EnableCrop";//是否需要裁剪
    public final static String EXTRA_ENABLE_COMPRESS = "EnableCompress";//是否需要压缩

    public final static int MODE_MULTIPLE = 1;
    public final static int MODE_SINGLE = 2;

    private int maxSelectNum = 9;
    private int selectMode = MODE_MULTIPLE;
    private boolean enableCamera = true;
    private boolean enablePreview = true;
    private boolean enableCrop = false;
    private boolean enableCompress = false;//是否显示原图按钮

    private int spanCount = 4;
    //ui
    private RecyclerView recyclerView;
    private ImageListAdapter imageAdapter;
    private LinearLayout folderLayout;
    private TextView folderName;
    private FolderWindow folderWindow;
    private CheckBox cbOrigin;
    private ToolBarManager toolBarManager;

    private String cameraPath;

    private List<LocalMediaFolder> allFolders;//所有图片文件夹

    private boolean isUseOrigin = false;//是否使用原图

    public static void start(Activity activity, int maxSelectNum, int mode, boolean enableCamera, boolean enablePreview, boolean enableCrop, boolean enableCompress) {
        Intent intent = new Intent(activity, ImageSelectorActivity.class);
        intent.putExtra(EXTRA_MAX_SELECT_NUM, maxSelectNum);
        intent.putExtra(EXTRA_SELECT_MODE, mode);
        intent.putExtra(EXTRA_SHOW_CAMERA, enableCamera);
        intent.putExtra(EXTRA_ENABLE_PREVIEW, enablePreview);
        intent.putExtra(EXTRA_ENABLE_CROP, enableCrop);
        intent.putExtra(EXTRA_ENABLE_COMPRESS, enableCompress);
        activity.startActivityForResult(intent, REQUEST_IMAGE);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageselector);

        maxSelectNum = getIntent().getIntExtra(EXTRA_MAX_SELECT_NUM, 9);
        selectMode = getIntent().getIntExtra(EXTRA_SELECT_MODE, MODE_MULTIPLE);
        enableCamera = getIntent().getBooleanExtra(EXTRA_SHOW_CAMERA, true);
        enablePreview = getIntent().getBooleanExtra(EXTRA_ENABLE_PREVIEW, true);
        enableCrop = getIntent().getBooleanExtra(EXTRA_ENABLE_CROP, false);
        enableCompress = getIntent().getBooleanExtra(EXTRA_ENABLE_COMPRESS, false);


        if (selectMode == MODE_MULTIPLE) {
            enableCrop = false;
        } else {
            enablePreview = false;
        }
        if (savedInstanceState != null) {
            cameraPath = savedInstanceState.getString(BUNDLE_CAMERA_PATH);
        }
        initView();
        registerListener();

        ///load data
        new LocalMediaLoader(this, LocalMediaLoader.TYPE_IMAGE).loadAllImage(new LocalMediaLoader.LocalMediaLoadListener() {

            @Override
            public void loadComplete(List<LocalMediaFolder> folders) {
                allFolders = folders;
                folderWindow.bindFolder(allFolders);
                //load all images first
                imageAdapter.bindImages(allFolders.get(0).getImages());
            }
        });

    }

    public void initView() {
        StatusManager.getInstance().setColor(this, R.color.white);
        toolBarManager = ToolBarManager.with(this, getContentView())
                .setBackgroundColor(R.color.white)
                .setTitle("选择图片")
                .setNavigationIcon(R.drawable.toolbar_arrow_back_black)
                .setMenuTextContent(getString(R.string.done))
                .setMenuTextColors(R.color.black_text_selector)
                .setMenuTextEnable(false)
                .setMenuTextClick(new ToolBarManager.OnMenuTextClickListener() {
                    @Override
                    public void onMenuTextClick() {
                        //点击完成
                        onSelectDone(imageAdapter.getSelectedImages());
                    }
                });


        folderWindow = new FolderWindow(this);

        //todo 完成按钮
        if (selectMode == MODE_MULTIPLE) {
            toolBarManager.setMenuTextContent(getString(R.string.done));
        } else {
            toolBarManager.setMenuTextContent("");
        }


        //是否使用原图
        cbOrigin = findViewById(R.id.cb_origin);

        if (!enableCompress) {
            cbOrigin.setVisibility(View.GONE);
        }

        cbOrigin.setChecked(isUseOrigin);
        cbOrigin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isUseOrigin = !cbOrigin.isChecked();
            }
        });

        folderLayout = (LinearLayout) findViewById(R.id.folder_layout);
        folderName = (TextView) findViewById(R.id.folder_name);

        recyclerView = (RecyclerView) findViewById(R.id.folder_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, ScreenUtils.dip2px(this, 2), false));
        recyclerView.setLayoutManager(new GridLayoutManager(this, spanCount));

        imageAdapter = new ImageListAdapter(this, maxSelectNum, selectMode, enableCamera, enablePreview);
        recyclerView.setAdapter(imageAdapter);
    }

    public void registerListener() {
        folderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(ImageSelectorActivity.this, "文件夹长度  " + allFolders.size() + "  内部图片数量  " + allFolders.get(0).getImages().size(), Toast.LENGTH_SHORT).show();
                if (allFolders.size() == 0 || allFolders.get(0).getImages().size() == 0) {
                    Toast.makeText(ImageSelectorActivity.this, "没有可选择的图片", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (folderWindow.isShowing()) {
                    folderWindow.dismiss();
                } else {
                    folderWindow.showAsDropDown(toolBarManager.getToolBarView());
                }
            }
        });
        //recyclerView点击事件
        imageAdapter.setOnImageSelectChangedListener(new ImageListAdapter.OnImageSelectChangedListener() {
            @Override
            public void onChange(List<LocalMedia> selectImages) {
                boolean enable = selectImages.size() != 0;
                toolBarManager.setMenuTextEnable(enable);
                if (enable) {
                    toolBarManager.setMenuTextContent(getString(R.string.done_num, selectImages.size() + "", maxSelectNum + ""));
                } else {
                    toolBarManager.setMenuTextContent(getString(R.string.done));
                }
            }

            @Override
            public void onTakePhoto() {
                startCamera();
            }

            @Override
            public void onPictureClick(LocalMedia media, int position, View view) {
                if (enablePreview) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        startPreviewWithAnim(imageAdapter.getImages(), position, view);
                    } else {
                        startPreview(imageAdapter.getImages(), position);
                    }

                } else if (enableCrop) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        startCropWithAnim(media.getPath(), view);
                    } else {
                        startCrop(media.getPath());
                    }
                } else {
                    onSelectDone(media.getPath());
                }
            }
        });
        //点击某个文件件
        folderWindow.setOnItemClickListener(new ImageFolderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String name, List<LocalMedia> images) {
                folderWindow.dismiss();
                imageAdapter.bindImages(images);
                folderName.setText(name);
                recyclerView.smoothScrollToPosition(0);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            // on take photo success
            if (requestCode == REQUEST_CAMERA) {
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(cameraPath))));
                if (enableCrop) {
                    startCrop(cameraPath);
                } else {
                    onSelectDone(cameraPath);
                }
            }
            //on preview select change
            else if (requestCode == ImagePreviewActivity.REQUEST_PREVIEW) {
                boolean isDone = data.getBooleanExtra(ImagePreviewActivity.OUTPUT_ISDONE, false);
                List<LocalMedia> images = (List<LocalMedia>) data.getSerializableExtra(ImagePreviewActivity.OUTPUT_LIST);
                if (isDone) {
                    onSelectDone(images);
                } else {
                    imageAdapter.bindSelectImages(images);
                }
            }
            // on crop success
            else if (requestCode == ImageCropActivity.REQUEST_CROP) {
                String path = data.getStringExtra(ImageCropActivity.OUTPUT_PATH);
                onSelectDone(path);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(BUNDLE_CAMERA_PATH, cameraPath);
    }

    /**
     * start to camera、preview、crop
     */
    public void startCamera() {
        File cameraFile = FileUtilsIP.createCameraFile(this);
        cameraPath = cameraFile.getAbsolutePath();
        FileUtilsIP.startActionCapture(this, cameraFile, REQUEST_CAMERA);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void startPreviewWithAnim(List<LocalMedia> previewImages, int position, View view) {
        ImagePreviewActivity.startPreviewWithAnim(this, previewImages, imageAdapter.getSelectedImages(), maxSelectNum, position, view);
    }

    public void startPreview(List<LocalMedia> previewImages, int position) {
        ImagePreviewActivity.startPreview(this, previewImages, imageAdapter.getSelectedImages(), maxSelectNum, position);
    }

    @SuppressLint("RestrictedApi")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void startCropWithAnim(String path, View view) {
        startActivityForResult(ImageCropActivity.newIntent(this, path), ImageCropActivity.REQUEST_CROP,
                ActivityOptions.makeSceneTransitionAnimation(this, view, "share_image").toBundle());
    }

    public void startCrop(String path) {
        startActivityForResult(ImageCropActivity.newIntent(this, path), ImageCropActivity.REQUEST_CROP);
    }

    /**
     * on select done
     *
     * @param medias
     */
    public void onSelectDone(List<LocalMedia> medias) {
        ArrayList<String> images = new ArrayList<>();
        for (LocalMedia media : medias) {
            images.add(media.getPath());
        }
        onResult(images);
    }

    public void onSelectDone(String path) {
        ArrayList<String> images = new ArrayList<>();
        images.add(path);
        onResult(images);
    }

    //返回图片
    public void onResult(ArrayList<String> images) {
        if (isUseOrigin) {
            setResult(RESULT_OK, new Intent().putStringArrayListExtra(REQUEST_OUTPUT, images));
            finish();
        } else {
            compressImage(images);

        }
    }

    //压缩图片
    private void compressImage(final ArrayList<String> photos) {
        //Toast.makeText(this, "压缩中...", Toast.LENGTH_SHORT).show();
        final List<String> newImageList = new ArrayList<>();
        Luban.with(this)
                .load(photos)                                   // 传人要压缩的图片列表
                .ignoreBy(100)                                  // 忽略不压缩图片的大小
                .setCompressListener(new OnCompressListener() { //设置回调
                    @Override
                    public void onStart() {
                        Log.d("weimu", "开始压缩");
                    }

                    @Override
                    public void onSuccess(File file) {
                        Log.d("weimu", "压缩成功 地址为：" + file.toString());
                        newImageList.add(file.toString());
                        //所有图片压缩成功
                        if (newImageList.size() == photos.size()) {
                            setResult(RESULT_OK, new Intent().putStringArrayListExtra(REQUEST_OUTPUT, photos));
                            finish();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                }).launch();    //启动压缩
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImageStaticHolder.clearImages();
    }
}
