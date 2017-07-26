package com.yongchun.library.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.yongchun.library.R;
import com.yongchun.library.model.LocalMedia;
import com.yongchun.library.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
        File cameraFile = FileUtils.createCameraFile(this);
        cameraPath = cameraFile.getAbsolutePath();
        FileUtils.startActionCapture(this, cameraFile, REQUEST_CAMERA);
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
        }else{
            onBackPressed();
        }
    }



    public void startCrop(String path) {
        startActivityForResult(ImageCropActivity.newIntent(this, path), ImageCropActivity.REQUEST_CROP);
    }

    public void onSelectDone(String path) {
        ArrayList<String> images = new ArrayList<>();
        images.add(path);
        onResult(images);
    }

    public void onResult(ArrayList<String> images) {
        setResult(RESULT_OK, new Intent().putStringArrayListExtra(REQUEST_OUTPUT, images));
        onBackPressed();
    }
}
