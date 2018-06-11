package com.weimu.library;

import android.app.Activity;

import com.weimu.library.view.CameraSelectorActivity;
import com.weimu.library.view.ImageSelectorActivity;

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

    /**
     * 打开图库【默认模式】
     *
     * @param activity
     */
    public void pickImage(Activity activity) {
        pickImage(activity, 9, true);
    }


    public void pickImage(Activity activity, int selectNum) {
        pickImage(activity, selectNum, true);
    }


    public void pickImage(Activity activity, int selectNum, boolean enableCompress) {
        ImageSelectorActivity.start(activity, selectNum, ImageSelectorActivity.MODE_MULTIPLE, true, true, false, enableCompress);
    }


    /**
     * 打开图库
     *
     * @param activity
     * @param maxSelectNum  最大选择图片数
     * @param mode          图库模式【单选】【多选】
     * @param enableCamera  是否启用摄像头
     * @param enablePreview 是否打开预览
     * @param enableCrop    是否进行裁剪【单选可用】
     */
    public void pickImage(Activity activity, int maxSelectNum, int mode, boolean enableCamera, boolean enablePreview, boolean enableCrop, boolean enableCompress) {
        ImageSelectorActivity.start(activity, maxSelectNum, mode, enableCamera, enablePreview, enableCrop, enableCompress);
    }


    /**
     * 选择头像
     *
     * @param activity
     */
    public void pickAvatar(Activity activity) {
        ImageSelectorActivity.start(activity, 1, ImageSelectorActivity.MODE_SINGLE, true, true, true, false);
    }

    /**
     * 使用摄像头【默认模式】
     *
     * @param activity
     */
    public void takePhoto(Activity activity) {
        CameraSelectorActivity.start(activity, false);
    }

    /**
     * 使用摄像头
     *
     * @param activity
     * @param enableCrop 是否启用裁剪
     */
    public void takePhoto(Activity activity, boolean enableCrop) {
        CameraSelectorActivity.start(activity, enableCrop);
    }


}
