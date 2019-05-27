package com.pmm.imagepicker

import com.theartofdev.edmodo.cropper.CropImageView
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

    //***** 基础 *****
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

    //***** 裁剪 *****
    var enableCrop = false
        //是否裁剪
        set(value) {
            field = if (selectMode == MODE_MULTIPLE) false else value
        }

    var cropAspectRatioX = -1//宽高比 X
    var cropAspectRatioY = -1//宽高比 Y
    var cropMiniWidth = -1//裁剪框的 最小宽度 单位px
    var cropMiniHeight = -1//裁剪框的 最小高度 单位px

    //***** 压缩 *****
    var showIsCompress = false//是否显示原图按钮

    //***** 网格列表 *****
    var gridSpanCount = 4//RecyclerView的网格数


}