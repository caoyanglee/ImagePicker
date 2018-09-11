package com.weimu.library.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.weimu.library.R;
import com.weimu.library.utils.FileUtilsIP;

import java.io.File;
import java.util.ArrayList;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * 相机选择
 */
public class CameraSelectorActivity extends SelectorBaseActivity {
    public final static int REQUEST_IMAGE = 66;
    public final static int REQUEST_CAMERA = 67;
    public final static String REQUEST_OUTPUT = "outputList";

    public final static String BUNDLE_CAMERA_PATH = "CameraPath";


    public final static String EXTRA_ENABLE_CROP = "EnableCrop";//是否需要裁剪

    private String cameraPath;

    private boolean enableCrop = false;


    public static void start(Activity activity, boolean enableCrop) {
        Intent intent = new Intent(activity, CameraSelectorActivity.class);
        intent.putExtra(EXTRA_ENABLE_CROP, enableCrop);
        activity.startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(BUNDLE_CAMERA_PATH, cameraPath);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_selector);
        enableCrop = getIntent().getBooleanExtra(EXTRA_ENABLE_CROP, false);
        if (savedInstanceState != null) {
            cameraPath = savedInstanceState.getString(BUNDLE_CAMERA_PATH);
        }
        startCamera();
    }


    /**
     * start to camera、preview、crop
     */
    public void startCamera() {
        File cameraFile = FileUtilsIP.createCameraFile(this);
        cameraPath = cameraFile.getAbsolutePath();
        FileUtilsIP.startActionCapture(this, cameraFile, REQUEST_CAMERA);
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
            } // on crop success
            else if (requestCode == ImageCropActivity.REQUEST_CROP) {
                String path = data.getStringExtra(ImageCropActivity.OUTPUT_PATH);
                onSelectDone(path);
            }
        } else {
            onBackPressed();
        }
    }


    public void startCrop(String path) {
        startActivityForResult(ImageCropActivity.newIntent(this, path), ImageCropActivity.REQUEST_CROP);
    }

    public void onSelectDone(String path) {
        ArrayList<String> images = new ArrayList<>();
        images.add(path);
        compressImage(images);//默认压缩
    }


    //压缩图片
    private void compressImage(final ArrayList<String> photos) {
        Toast.makeText(this, "压缩中...", Toast.LENGTH_SHORT).show();
        final ArrayList<String> newImageList = new ArrayList<>();
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
                            setResult(RESULT_OK, new Intent().putStringArrayListExtra(REQUEST_OUTPUT, newImageList));
                            onBackPressed();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                }).launch();    //启动压缩
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, R.anim.transparent);
    }
}
