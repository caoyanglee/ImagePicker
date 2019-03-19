package com.weimu.library.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log

import com.weimu.library.R
import com.weimu.library.utils.FileUtilsIP

import java.io.File
import java.util.ArrayList

import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener

/**
 * 相机选择
 */
class CameraSelectorActivity : SelectorBaseActivity() {

    private var cameraPath: String? = null

    private var enableCrop = false

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(BUNDLE_CAMERA_PATH, cameraPath)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_selector)
        enableCrop = intent.getBooleanExtra(EXTRA_ENABLE_CROP, false)
        if (savedInstanceState != null) {
            cameraPath = savedInstanceState.getString(BUNDLE_CAMERA_PATH)
        }
        startCamera()
    }


    /**
     * start to camera、preview、crop
     */
    fun startCamera() {
        val cameraFile = FileUtilsIP.createCameraFile(this)
        cameraPath = cameraFile.absolutePath
        FileUtilsIP.startActionCapture(this, cameraFile, REQUEST_CAMERA)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            // on take photo success
            if (requestCode == REQUEST_CAMERA) {
                sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(File(cameraPath))))
                if (enableCrop) {
                    startCrop(cameraPath)
                } else {
                    onSelectDone(cameraPath)
                }
            } // on crop success
            else if (requestCode == ImageCropActivity.REQUEST_CROP) {
                val path = data!!.getStringExtra(ImageCropActivity.OUTPUT_PATH)
                onSelectDone(path)
            }
        } else {
            onBackPressed()
        }
    }


    fun startCrop(path: String?) {
        startActivityForResult(ImageCropActivity.newIntent(this, path!!), ImageCropActivity.REQUEST_CROP)
    }

    fun onSelectDone(path: String?) {
        val images = ArrayList<String>()
        images.add(path!!)
        compressImage(images)//默认压缩
    }


    //压缩图片
    private fun compressImage(photos: ArrayList<String>) {
        //Toast.makeText(this, "压缩中...", Toast.LENGTH_SHORT).show();
        val newImageList = ArrayList<String>()
        Luban.with(this)
                .load(photos)                                   // 传人要压缩的图片列表
                .ignoreBy(100)                                  // 忽略不压缩图片的大小
                .setCompressListener(object : OnCompressListener { //设置回调
                    override fun onStart() {
                        Log.d("weimu", "开始压缩")
                    }

                    override fun onSuccess(file: File) {
                        Log.d("weimu", "压缩成功 地址为：$file")
                        newImageList.add(file.toString())
                        //所有图片压缩成功
                        if (newImageList.size == photos.size) {
                            setResult(Activity.RESULT_OK, Intent().putStringArrayListExtra(REQUEST_OUTPUT, newImageList))
                            onBackPressed()
                        }
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }
                }).launch()    //启动压缩
    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(0, R.anim.transparent)
    }

    companion object {
        val REQUEST_IMAGE = 66
        val REQUEST_CAMERA = 67
        val REQUEST_OUTPUT = "outputList"

        val BUNDLE_CAMERA_PATH = "CameraPath"


        val EXTRA_ENABLE_CROP = "EnableCrop"//是否需要裁剪


        fun start(activity: Activity, enableCrop: Boolean) {
            val intent = Intent(activity, CameraSelectorActivity::class.java)
            intent.putExtra(EXTRA_ENABLE_CROP, enableCrop)
            activity.startActivityForResult(intent, REQUEST_IMAGE)
        }
    }
}
