package com.pmm.imagepicker

import java.io.Serializable

/**
 * Author:你需要一台永动机
 * Date:2019-05-25 16:44
 * Description:由于配置属性太多，不得不使用对象
 */
class Config : Serializable {

    companion object {
        const val EXTRA_CONFIG = "config"//配置

        const val MODE_MULTIPLE = 1//多选
        const val MODE_SINGLE = 2//单选
    }


    var maxSelectNum = 9//最大图片选择数

    var selectMode = MODE_MULTIPLE
        //选择模式
        set(value) {
            field = value
            when (field) {
                MODE_MULTIPLE -> {
                    enableCrop = false
                }
                MODE_SINGLE -> {
                    enablePreview = false
                }
            }
        }
    var enableCamera = true//是否启用相机

    var enablePreview = true
        //是否启动预览
        set(value) {
            field = if (selectMode == MODE_SINGLE) false else value
        }

    var enableCrop = false
        //是否裁剪
        set(value) {
            field = if (selectMode == MODE_MULTIPLE) false else value
        }

    var cropAspectRatioX = 0//宽高比 X
    var cropAspectRatioY = 0//宽高比 Y

    var showIsCompress = false//是否显示原图按钮

    var gridSpanCount = 4//RecyclerView的网格数


}