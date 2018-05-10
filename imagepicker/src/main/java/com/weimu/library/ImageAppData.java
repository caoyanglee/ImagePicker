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
public class ImageAppData extends Application {
    //共享选择图片列表
    List<LocalMedia> chooseImages;

    public List<LocalMedia> getChooseImages() {
        return chooseImages;
    }

    public void setChooseImages(List<LocalMedia> chooseImages) {
        this.chooseImages = chooseImages;
    }

    public void clearImages() {
        if (this.chooseImages != null) {
            this.chooseImages.clear();
            this.chooseImages = null;
        }

    }

}
