package com.pmm.imagepicker

import android.app.Activity
import android.content.Intent
import com.pmm.imagepicker.ui.CameraSelectorActivity
import com.pmm.imagepicker.ui.ImageSelectorActivity

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
            enableCamera: Boolean = true,
            enablePreview: Boolean = true,
            showIsCompress: Boolean = true
    ) {
        val config = Config().apply {
            this.maxSelectNum = maxSelectNum
            this.selectMode = Config.MODE_MULTIPLE
            this.enableCamera = enableCamera
            this.enablePreview = enablePreview
            this.showIsCompress = showIsCompress
        }

        ImageSelectorActivity.start(activity, config)
    }

    fun pickImage4One(
            activity: Activity,
            enableCamera: Boolean = true,
            enableCrop: Boolean = true,
            cropAspectRatioX: Int = 0,
            cropAspectRatioY: Int = 0,
            cropMiniWidth: Int = 0,
            cropMiniHeight: Int = 0,
            showIsCompress: Boolean = true
    ) {
        val config = Config().apply {
            this.selectMode = Config.MODE_SINGLE
            this.enableCamera = enableCamera
            this.enableCrop = enableCrop
            this.cropAspectRatioX = cropAspectRatioX
            this.cropAspectRatioY = cropAspectRatioY
            this.cropMiniWidth = cropMiniWidth
            this.cropMiniHeight = cropMiniHeight
            if (this.cropAspectRatioX > 0 && this.cropAspectRatioY > 0) {
                this.enableCrop = true
            }
            if (this.cropMiniWidth > 0 && this.cropMiniHeight > 0)
                this.enableCrop = true
            this.showIsCompress = showIsCompress
        }

        ImageSelectorActivity.start(activity, config)
    }


    /**
     * 选择头像
     *
     * @param activity
     */
    fun pickAvatar(activity: Activity) {
        pickImage4One(activity)
    }


    /**
     * 使用摄像头
     *
     * @param activity
     * @param enableCrop 是否启用裁剪
     */
    fun takePhoto(activity: Activity,
                  enableCrop: Boolean = false,
                  cropAspectRatioX: Int = 0,
                  cropAspectRatioY: Int = 0) {
        val config = Config().apply {
            this.selectMode = Config.MODE_SINGLE
            this.enableCrop = enableCrop
            this.cropAspectRatioX = cropAspectRatioX
            this.cropAspectRatioY = cropAspectRatioY
        }
        CameraSelectorActivity.start(activity, config)
    }

    /**
     * 自定义 启动activity
     * @param activity
     * @param config 配置对象
     */
    fun custom(activity: Activity, config: Config) {
        ImageSelectorActivity.start(activity, config)
    }


    /**
     * 自定义 生成Intent
     * 注意：使用此方法 必须自己调用 startActivityForResult
     * @param activity
     * @param config 配置对象
     */
    fun customCreateIntent(activity: Activity, config: Config): Intent {
        return ImageSelectorActivity.newIntent(activity, config)
    }

}
