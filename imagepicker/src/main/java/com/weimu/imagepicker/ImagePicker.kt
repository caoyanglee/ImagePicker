package com.weimu.imagepicker

import android.app.Activity
import android.content.Intent
import com.weimu.imagepicker.Config.Companion.MODE_MULTIPLE

import com.weimu.imagepicker.ui.CameraSelectorActivity
import com.weimu.imagepicker.ui.ImageSelectorActivity

/**
 * Author:你需要一台永动机
 * Date:2018/3/6 10:49
 * Description:
 */

object ImagePicker {

    val REQUEST_IMAGE = 66
    val REQUEST_CAMERA = 67
    val REQUEST_OUTPUT = "outputList"


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
    fun pickImage(
            activity: Activity,
            maxSelectNum: Int = 9,
            mode: Int = MODE_MULTIPLE,
            enableCamera: Boolean = true,
            enablePreview: Boolean = true,
            enableCrop: Boolean = false,
            enableCompress: Boolean = true) {
        val config = Config().apply {
            this.maxSelectNum = maxSelectNum
            this.selectMode = mode
            this.enableCamera = enableCamera
            this.enablePreview = enablePreview
            this.enableCrop = enableCrop
            this.showIsCompress = enableCompress
        }

        ImageSelectorActivity.start(activity, config)
    }


    /**
     * 选择头像
     *
     * @param activity
     */
    fun pickAvatar(activity: Activity) {
        val config = Config().apply {
            this.maxSelectNum = 1
            this.enableCamera = true
            this.enablePreview = true
            this.enableCrop = true
            this.showIsCompress = false
        }
        ImageSelectorActivity.start(activity, config)
    }


    /**
     * 使用摄像头
     *
     * @param activity
     * @param enableCrop 是否启用裁剪
     */
    fun takePhoto(activity: Activity, enableCrop: Boolean = false) {
        val config = Config().apply {
            this.enableCrop = enableCrop
        }
        CameraSelectorActivity.start(activity, config)
    }

    /**
     * 自定义 启动activity
     */
    fun custom(activity: Activity, config: Config) {
        ImageSelectorActivity.start(activity, config)
    }


    /**
     * 自定义 生成Intent
     * 注意：使用此方法 必须自己调用 startActivityForResult
     */
    fun customCreateIntent(activity: Activity, config: Config): Intent {
        return ImageSelectorActivity.newIntent(activity, config)
    }

}
