package com.weimu.imagepicker.ktx

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import com.weimu.universalview.ktx.formatDate
import com.weimu.universalview.ktx.getCurrentTimeStamp
import com.weimu.universalview.ktx.getUri4File
import java.io.File

/**
 * Author:你需要一台永动机
 * Date:2019-05-25 15:39
 * Description:
 */

//创建图片文件
private fun Context.createMediaFile(childFoldName: String): File {
    val state = Environment.getExternalStorageState()
    val rootDir = if (state == Environment.MEDIA_MOUNTED) Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) else this.cacheDir
    val folderDir = File("$rootDir/$childFoldName/")
    if (!folderDir.exists() && folderDir.mkdirs()) {
    }
    val fileName = "${getCurrentTimeStamp().formatDate("yyyyMMdd_HHmmss")}.jpg"
    return File(folderDir, fileName)
}

//创建相机图片
internal fun Context.createCameraFile(): File = createMediaFile("Camera")

//创建裁剪图片
internal fun Context.createCropFile(): File = createMediaFile("Crop")

//相机图片
internal fun Context.getUri4Camera(): Uri = Uri.fromFile(createCameraFile())

//裁剪图片
internal fun Context.getUri4Crop(): Uri = Uri.fromFile(createCropFile())

/**
 * 打开相机
 *
 * @param activity    Activity
 * @param file        File
 * @param requestCode result requestCode
 */
internal fun Activity.startActionCapture(file: File, requestCode: Int) {
    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    intent.putExtra(MediaStore.EXTRA_OUTPUT, this.getUri4File(file))
    this.startActivityForResult(intent, requestCode)
}