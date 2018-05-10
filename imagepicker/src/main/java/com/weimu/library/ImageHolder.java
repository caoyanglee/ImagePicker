package com.weimu.library;

import android.app.Application;
import android.util.Log;

import com.weimu.library.model.LocalMedia;

import java.util.List;

/**
 * Author:你需要一台永动机
 * Date:2018/5/10 11:38
 * Description:
 */
public class ImageHolder {
    //共享选择图片列表
    private static List<LocalMedia> chooseImages;

    public static List<LocalMedia> getChooseImages() {
        return chooseImages;
    }

    public static void setChooseImages(List<LocalMedia> chooseImages) {
        ImageHolder.chooseImages = chooseImages;
    }

    public static void clearImages() {
        if (chooseImages != null) {
            chooseImages.clear();
            chooseImages = null;
        }

    }

}
