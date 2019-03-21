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
import com.weimu.universalib.ktx.getUri4File


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
     *
     * @param activity    Activity
     * @param file        File
     * @param requestCode result requestCode
     */
    fun startActionCapture(activity: Activity, file: File, requestCode: Int) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, activity.getUri4File(file))
        activity.startActivityForResult(intent, requestCode)
    }
}
