package com.weimu.imagepicker.utils

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider


import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


internal object FileUtilsIP {
    val POSTFIX = ".JPEG"
    val APP_NAME = "ImageSelector"
    val CAMERA_PATH = "/$APP_NAME/CameraImage/"
    val CROP_PATH = "/$APP_NAME/CropImage/"

    fun createCameraFile(context: Context): File {
        return createMediaFile(context, CAMERA_PATH)
    }

    fun createCropFile(context: Context): File {
        return createMediaFile(context, CROP_PATH)
    }

    private fun createMediaFile(context: Context, parentPath: String): File {
        val state = Environment.getExternalStorageState()
        val rootDir = if (state == Environment.MEDIA_MOUNTED) Environment.getExternalStorageDirectory() else context.cacheDir

        val folderDir = File(rootDir.absolutePath + parentPath)
        if (!folderDir.exists() && folderDir.mkdirs()) {

        }

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(Date())
        val fileName = APP_NAME + "_" + timeStamp + ""
        return File(folderDir, fileName + POSTFIX)
    }


    /**
     * 打开相机
     * 兼容7.0
     *
     * @param activity    Activity
     * @param file        File
     * @param requestCode result requestCode
     */
    fun startActionCapture(activity: Activity?, file: File, requestCode: Int) {
        if (activity == null) {
            return
        }
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        intent.putExtra(MediaStore.EXTRA_OUTPUT, getUriForFile(activity, file))
        activity.startActivityForResult(intent, requestCode)
    }

    /**
     * @param
     * @return
     * @description 兼容7.0的文件操作
     * @remark
     */
    private fun getUriForFile(context: Context?, file: File?): Uri {
        if (context == null || file == null) {
            throw NullPointerException()
        }
        val uri: Uri
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val auth = context.packageName + ".fileprovider"
            uri = FileProvider.getUriForFile(context.applicationContext, auth, file)
        } else {
            uri = Uri.fromFile(file)
        }
        return uri
    }


    /**
     * 打开文件
     * 兼容7.0
     *
     * @param context     activity
     * @param file        File
     * @param contentType 文件类型如：文本（text/html）
     * 当手机中没有一个app可以打开file时会抛ActivityNotFoundException
     */
    @Throws(ActivityNotFoundException::class)
    fun startActionFile(context: Context?, file: File, contentType: String) {
        if (context == null) {
            return
        }
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.setDataAndType(getUriForFile(context, file), contentType)
        if (context !is Activity) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}
