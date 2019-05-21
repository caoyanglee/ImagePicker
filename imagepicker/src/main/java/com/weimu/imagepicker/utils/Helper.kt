package com.weimu.imagepicker.utils


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.provider.MediaStore
import com.weimu.universalview.ktx.formatDate
import com.weimu.universalview.ktx.getCurrentTimeStamp
import com.weimu.universalview.ktx.getUri4File
import java.io.File


internal object Helper {

    fun createCameraFile(context: Context): File = createMediaFile(context, "Camera")

    fun createCropFile(context: Context): File = createMediaFile(context, "Crop")

    private fun createMediaFile(context: Context, childFoldName: String): File {
        val state = Environment.getExternalStorageState()
        val rootDir = if (state == Environment.MEDIA_MOUNTED) Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) else context.cacheDir
        val folderDir = File("$rootDir/$childFoldName/")
        if (!folderDir.exists() && folderDir.mkdirs()) {
        }
        val fileName = "${getCurrentTimeStamp().formatDate("yyyyMMdd_HHmmss")}.jpg"
        return File(folderDir, fileName)
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
