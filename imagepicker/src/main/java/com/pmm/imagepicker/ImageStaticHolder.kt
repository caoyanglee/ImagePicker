package com.pmm.imagepicker

import com.pmm.imagepicker.model.MedialFile


/**
 * Author:你需要一台永动机
 * Date:2018/5/10 11:38
 * Description:
 */
internal object ImageStaticHolder {
    //共享选择图片列表
    private var chooseImages: ArrayList<MedialFile> = arrayListOf()

    fun getChooseImages(): List<MedialFile> {
        return chooseImages
    }

    fun setChooseImages(images: List<MedialFile>) {
        this.chooseImages.clear()
        this.chooseImages.addAll(images)
    }

    fun clearImages() {
        chooseImages.clear()
    }

}
