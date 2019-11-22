package com.pmm.imagepicker

import com.pmm.imagepicker.model.LocalMedia
import com.pmm.ui.ktx.clearAllContent

/**
 * Author:你需要一台永动机
 * Date:2018/5/10 11:38
 * Description:
 */
internal object ImageStaticHolder {
    //共享选择图片列表
    private var chooseImages: ArrayList<LocalMedia> = arrayListOf()

    fun getChooseImages(): List<LocalMedia> {
        return chooseImages
    }

    fun setChooseImages(images: List<LocalMedia>) {
        this.chooseImages.clear()
        this.chooseImages.addAll(images)
    }

    fun clearImages() {
        chooseImages.clear()
    }

}
