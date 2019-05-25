package com.weimu.imagepicker.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import com.weimu.imagepicker.R
import com.weimu.imagepicker.ktx.getUri4Crop
import com.weimu.universalview.core.activity.BaseActivity
import com.weimu.universalview.core.toolbar.StatusBarManager
import com.weimu.universalview.helper.FileHelper
import com.weimu.universalview.ktx.setOnClickListenerPro
import kotlinx.android.synthetic.main.activity_image_crop.*
import java.io.File
import java.io.IOException
import java.io.OutputStream

/**
 * Author:你需要一台永动机
 * Date:2019-05-25 15:24
 * Description:图片裁剪
 */
internal class ImageCropActivity : BaseActivity() {

    private var path = ""
    private var sourceUri: Uri? = null//源URI
    private var saveUri: Uri? = null//存储URI

    //corp config
    private var aspectRatioX = 0
    private var aspectRatioY = 0


    companion object {
        const val REQUEST_CROP = 69

        const val DATA_EXTRA_PATH = "data_extra_path"
        const val OUTPUT_PATH = "outputPath"
        private const val ASPECT_RATION_X = "aspectRatioX"
        private const val ASPECT_RATION_Y = "aspectRatioY"

        fun newIntent(context: Context, path: String, aspectRatioX: Int = 0, aspectRatioY: Int = 0): Intent {
            val intent = Intent(context, ImageCropActivity::class.java)
            intent.putExtra(DATA_EXTRA_PATH, path)
            intent.putExtra(ASPECT_RATION_X, aspectRatioX)
            intent.putExtra(ASPECT_RATION_Y, aspectRatioY)
            return intent
        }
    }

    override fun getLayoutResID(): Int = R.layout.activity_image_crop

    override fun beforeViewAttach(savedInstanceState: Bundle?) {
        //data
        path = intent.getStringExtra(DATA_EXTRA_PATH)
        sourceUri = Uri.fromFile(File(path))
        aspectRatioX = intent.getIntExtra(ASPECT_RATION_X, 0)
        aspectRatioY = intent.getIntExtra(ASPECT_RATION_Y, 0)
    }


    override fun afterViewAttach(savedInstanceState: Bundle?) {
        StatusBarManager.setColor(this.window, ContextCompat.getColor(this, R.color.white))
        StatusBarManager.setLightMode(this.window, false)

        mToolBar.apply {
            this.setBackgroundColor(Color.WHITE)
            this.navigationIcon {
                this.setImageResource(R.drawable.toolbar_arrow_back_black)
                this.setOnClickListenerPro { onBackPressed() }
            }
            this.centerTitle {
                this.text = "${getString(R.string.crop_picture)}"
                this.setTextColor(Color.BLACK)
            }
            this.menuText1 {
                this.text = getString(R.string.use)
                this.setTextColor(ContextCompat.getColorStateList(context, R.color.black_text_selector))
                this.isEnabled = true
                this.setOnClickListenerPro {
                    //点击完成
                    saveOutput(cropImageView.croppedImage)
                }
            }
        }
        //裁剪视图
        cropImageView.apply {
            //配置
            this.setImageUriAsync(sourceUri)
            if (aspectRatioX > 0 || aspectRatioY > 0) this.setAspectRatio(aspectRatioX, aspectRatioY)
        }
    }


    private fun saveOutput(croppedImage: Bitmap) {
        saveUri = getUri4Crop()
        saveUri?.let {
            var outputStream: OutputStream? = null
            try {
                outputStream = contentResolver.openOutputStream(it)
                if (outputStream != null) {
                    croppedImage.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                if (outputStream == null) return
                try {
                    outputStream.close()
                } catch (t: Throwable) {
                    // Do nothing
                }
            }
            setResult(RESULT_OK, Intent().putExtra(OUTPUT_PATH, it.path))
        }
        Handler().post { croppedImage.recycle() }
        onBackPressed()
    }

}
