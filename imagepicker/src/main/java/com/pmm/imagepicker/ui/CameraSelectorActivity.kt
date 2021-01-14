package com.pmm.imagepicker.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.FrameLayout
import com.pmm.imagepicker.Config
import com.pmm.imagepicker.ImagePicker
import com.pmm.imagepicker.R
import com.pmm.imagepicker.ktx.createCameraFile
import com.pmm.imagepicker.ktx.startActionCapture
import com.pmm.ui.core.activity.BaseActivity
import com.pmm.ui.helper.FileHelper
import com.pmm.ui.helper.MediaScanner
import com.pmm.ui.ktx.gone
import id.zelory.compressor.Compressor
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

/**
 * 相机选择
 */
internal class CameraSelectorActivity : BaseActivity() {
    companion object {
        val REQUEST_OUTPUT = "outputList"

        val BUNDLE_CAMERA_PATH = "CameraPath"

        fun start(activity: Activity, config: Config) {
            val intent = Intent(activity, CameraSelectorActivity::class.java)
            intent.putExtra(Config.EXTRA_CONFIG, config)
            activity.startActivityForResult(intent, ImagePicker.REQUEST_IMAGE)
        }
    }

    private var cameraPath: String? = null

    private lateinit var config: Config

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(BUNDLE_CAMERA_PATH, cameraPath)
    }

    override fun getLayoutUI(): ViewGroup = FrameLayout(this@CameraSelectorActivity).apply { this.gone() }

    override fun beforeViewAttach(savedInstanceState: Bundle?) {
        config = intent.getSerializableExtra(Config.EXTRA_CONFIG) as Config
        if (savedInstanceState != null) {
            cameraPath = savedInstanceState.getString(BUNDLE_CAMERA_PATH)
        }
    }

    override fun afterViewAttach(savedInstanceState: Bundle?) {
        startCamera()
    }

    /**
     * start to camera、preview、crop
     */
    private fun startCamera() {
        val cameraFile = createCameraFile()
        cameraPath = cameraFile.absolutePath
        startActionCapture(cameraFile, ImagePicker.REQUEST_CAMERA)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            // on take photo success
            if (requestCode == ImagePicker.REQUEST_CAMERA) {
                sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(File(cameraPath?:""))))
                if (config.enableCrop) {
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


    private fun startCrop(path: String?) {
        if (path.isNullOrBlank()) return
        startActivityForResult(ImageCropActivity.newIntent(this, path, config), ImageCropActivity.REQUEST_CROP)
    }

    private fun onSelectDone(path: String?) {
        val images = ArrayList<String>()
        images.add(path!!)
        compressImage(images)//默认压缩
    }


    //压缩图片
    private fun compressImage(photos: ArrayList<String>) {
        val newImageList = ArrayList<String>()
        MainScope().launch {
            for (image in photos){
                Log.d("imagePicker", "--------------------------------------------- >>>")
                Log.d("imagePicker", "压缩前：")
                Log.d("imagePicker", "地址：$image")
                Log.d("imagePicker", "文件大小：${FileHelper.getFileSize(File(image))}")
                val compressedImg = Compressor.compress(this@CameraSelectorActivity, File(image))
                Log.d("imagePicker", "压缩后：")
                Log.d("imagePicker", "地址：$compressedImg")
                Log.d("imagePicker", "文件大小：${FileHelper.getFileSize(compressedImg)}")
                Log.d("imagePicker", "<<< ---------------------------------------------")
                newImageList.add(compressedImg.toString())
            }

            setResult(Activity.RESULT_OK, Intent().putStringArrayListExtra(REQUEST_OUTPUT, newImageList))
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(0, R.anim.transparent)
    }


}
