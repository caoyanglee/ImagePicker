package com.weimu.imagepicker.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import com.bumptech.glide.Glide
import com.isseiaoki.simplecropview.CropImageView
import com.weimu.imagepicker.R
import com.weimu.imagepicker.utils.Helper
import com.weimu.universalview.core.activity.BaseActivity
import com.weimu.universalview.core.toolbar.StatusBarManager
import com.weimu.universalview.ktx.setOnClickListenerPro
import kotlinx.android.synthetic.main.activity_image_crop.*
import java.io.File
import java.io.IOException
import java.io.OutputStream


internal class ImageCropActivity : BaseActivity() {

    private val cropImageView: CropImageView by lazy { findViewById<CropImageView>(R.id.cropImageView) }

    private var path = ""
    private var sourceUri: Uri? = null//源URI
    private var saveUri: Uri? = null//存储URI


    companion object {
        val REQUEST_CROP = 69

        val DATA_EXTRA_PATH = "data_extra_path"
        val OUTPUT_PATH = "outputPath"

        fun newIntent(context: Context, path: String): Intent {
            val intent = Intent(context, ImageCropActivity::class.java)
            intent.putExtra(DATA_EXTRA_PATH, path)
            return intent
        }
    }

    override fun getLayoutResID(): Int = R.layout.activity_image_crop

    override fun beforeViewAttach(savedInstanceState: Bundle?) {
        //data
        path = intent.getStringExtra(DATA_EXTRA_PATH)
        sourceUri = Uri.fromFile(File(path))
    }


    override fun afterViewAttach(savedInstanceState: Bundle?) {
        StatusBarManager.setColor(this.window, ContextCompat.getColor(this, R.color.white))
        StatusBarManager.setLightMode(this.window, false)
        mToolBar.apply { this.setBackgroundColor(Color.WHITE) }
                .navigationIcon {
                    this.setImageResource(R.drawable.toolbar_arrow_back_black)
                    this.setOnClickListenerPro { onBackPressed() }
                }
                .centerTitle {
                    this.text = "${getString(R.string.crop_picture)}"
                    this.setTextColor(Color.BLACK)
                }
                .menuText1 {
                    this.text = getString(R.string.use)
                    this.setTextColor(ContextCompat.getColorStateList(context, R.color.black_text_selector))
                    this.isEnabled = true
                    this.setOnClickListenerPro {
                        //点击完成
                        saveOutput(cropImageView.croppedBitmap)
                    }
                }


        cropImageView.apply {
            //配置
            this.setHandleSizeInDp(8)//设置裁剪四周小圆球的大小
            this.setFrameStrokeWeightInDp(1)
            this.setGuideStrokeWeightInDp(1)
            this.setInitialFrameScale(0.5f)//裁剪区域为原图的一半
            this.setCropMode(CropImageView.CropMode.SQUARE)//设置裁剪方式为圆形，可换
        }
        //加载
        Glide.with(this).load(sourceUri).into(cropImageView)
    }


    private fun saveOutput(croppedImage: Bitmap) {
        saveUri = Uri.fromFile(Helper.createCropFile(this@ImageCropActivity))
        if (saveUri != null) {
            var outputStream: OutputStream? = null
            try {
                outputStream = contentResolver.openOutputStream(saveUri!!)
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
            setResult(RESULT_OK, Intent().putExtra(OUTPUT_PATH, saveUri!!.path))
        }
        Handler().post { croppedImage.recycle() }
        onBackPressed()
    }

}
