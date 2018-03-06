package com.yongchun.library;

import android.app.Activity;

import com.yongchun.library.view.CameraSelectorActivity;
import com.yongchun.library.view.ImageSelectorActivity;

/**
 * Author:你需要一台永动机
 * Date:2018/3/6 10:49
 * Description:
 */

public class ImagePicker {


    private static final ImagePicker ourInstance = new ImagePicker();

    public static ImagePicker getInstance() {
        return ourInstance;
    }

    private ImagePicker() {

    }

    //打开图库
    public void pickImage(Activity activity, int maxSelectNum, int mode, boolean enableCamera, boolean enablePreview, boolean enableCrop) {
        ImageSelectorActivity.start(activity, maxSelectNum, mode, enableCamera, enablePreview, enableCrop);
    }

    //使用摄像头
    public void takePhoto(Activity activity, boolean enableCrop) {
        CameraSelectorActivity.start(activity, enableCrop);
    }


}
