package com.pmm.imagepicker.ui

import android.app.Application
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.pmm.imagepicker.LocalMediaLoader
import com.pmm.imagepicker.model.MediaFolder

/**
 * Author:你需要一台永动机
 * Date:2020/7/31 11:11
 * Description:
 */
internal class ImageSelectorViewModel(application: Application) : AndroidViewModel(application) {

    val foldersLiveData = MutableLiveData<List<MediaFolder>>()//所有文件夹

    //加载图片
    fun loadImages(activity: FragmentActivity) {
        LocalMediaLoader(activity, LocalMediaLoader.TYPE_IMAGE).loadAllImage {
            foldersLiveData.postValue(it)
        }
    }
}