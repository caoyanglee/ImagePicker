package com.pmm.imagepicker.ktx

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.database.Cursor
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Display
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.ViewConfiguration
import com.pmm.ui.ktx.formatDate
import com.pmm.ui.ktx.getCurrentTimeStamp
import com.pmm.ui.ktx.getUri4File
import java.io.File


/**
 * Author:你需要一台永动机
 * Date:2019-05-25 15:39
 * Description:
 */
private fun Context.createMediaFileInApp(childFoldName: String): File {
    val state = Environment.getExternalStorageState()
    val rootDir = if (state == Environment.MEDIA_MOUNTED) "${(externalCacheDir?.absolutePath) ?: ""}/imagePicker_disk_cache" else this.cacheDir
    val folderDir = File("$rootDir/$childFoldName/")
    if (!folderDir.exists() && folderDir.mkdirs()) { }
    val fileName = "${getCurrentTimeStamp().formatDate("yyyyMMdd_HHmmss")}.jpg"//必须使用不同命名
    return File(folderDir, fileName)
}

//创建相机图片
internal fun Context.createCameraFile(): File = createMediaFileInApp("Camera")

//创建裁剪图片
internal fun Context.createCropFile(): File = createMediaFileInApp("Crop")

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

// 是否是Android 10以上手机
private val isAndroidQ = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

/**
 * 创建图片地址uri,用于保存拍照后的照片 Android 10以后使用这种方法
 */
internal fun Activity.createImageUri(): Uri? {
    val status = Environment.getExternalStorageState()
    // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
    return if (status == Environment.MEDIA_MOUNTED) {
        contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, ContentValues())
    } else {
        contentResolver.insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, ContentValues())
    }
}

//路径 -> Uri
internal fun Context.getImageContentUri(path: String): Uri? {
    val cursor: Cursor? = this.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, arrayOf(MediaStore.Images.Media._ID), MediaStore.Images.Media.DATA + "=? ", arrayOf(path), null)
    return if (cursor != null && cursor.moveToFirst()) {
        val id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID))
        val baseUri = Uri.parse("content://media/external/images/media")
        Uri.withAppendedPath(baseUri, "" + id)
    } else { // 如果图片不在手机的共享图片数据库，就先把它插入。
        Log.e("LocalMediaLoader", "最新插入 $path", )
        if (File(path).exists()) {
            val values = ContentValues()
            values.put(MediaStore.Images.Media.DATA, path)
            this.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        } else {
            null
        }
    }
}
