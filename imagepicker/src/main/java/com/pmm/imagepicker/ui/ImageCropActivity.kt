package com.pmm.imagepicker.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import com.pmm.imagepicker.Config
import com.pmm.imagepicker.R
import com.pmm.imagepicker.ktx.getUri4Crop
import com.weimu.universalview.core.activity.BaseActivity
import com.weimu.universalview.core.toolbar.StatusBarManager
import com.weimu.universalview.ktx.getColorPro
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

    //config
    private lateinit var config: Config


    companion object {
        const val REQUEST_CROP = 69

        const val DATA_EXTRA_PATH = "data_extra_path"
        const val OUTPUT_PATH = "outputPath"

        fun newIntent(context: Context, path: String, config: Config): Intent {
            val intent = Intent(context, ImageCropActivity::class.java)
            intent.putExtra(DATA_EXTRA_PATH, path)
            intent.putExtra(Config.EXTRA_CONFIG, config)
            return intent
        }
    }

    override fun getLayoutResID(): Int = R.layout.activity_image_crop

    override fun beforeViewAttach(savedInstanceState: Bundle?) {
        //data
        path = intent.getStringExtra(DATA_EXTRA_PATH)
        sourceUri = Uri.fromFile(File(path))
        config = intent.getSerializableExtra(Config.EXTRA_CONFIG) as Config
    }

    override fun afterViewAttach(savedInstanceState: Bundle?) {
        //StatusBar
        StatusBarManager.apply {
            val statusColor = getColorPro(R.color.colorPrimaryDark)
            this.setColor(window, statusColor)
            if (statusColor == Color.WHITE) {
                this.setLightMode(window)
            } else {
                this.setDarkMode(window)
            }
        }
        //ToolBar
        mToolBar.apply {
            this.setBackgroundColor(getColorPro(R.color.colorPrimary))
            this.navigationIcon {
                this.setImageResource(R.drawable.ic_nav_back_24dp)
                this.setColorFilter(getColorPro(R.color.toolbar_navigation))
                this.setOnClickListenerPro { onBackPressed() }
            }
            this.centerTitle {
                this.text = getString(R.string.crop_picture)
                this.setTextColor(getColorPro(R.color.toolbar_title))
            }
            this.menuText1 {
                this.setTextColor(getColorPro(R.color.toolbar_menu))
                this.text = getString(R.string.use)
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
            this.isAutoZoomEnabled = true//是否自动缩放

            //设置宽高比
            if (config.cropAspectRatioX > 0 && config.cropAspectRatioY > 0)
                this.setAspectRatio(config.cropAspectRatioX, config.cropAspectRatioY)
            //设置裁剪框最小的宽度高度
            if (config.cropMiniWidth > 0 && config.cropMiniHeight > 0)
                this.setMinCropResultSize(config.cropMiniWidth, config.cropMiniHeight)

            this.setImageUriAsync(sourceUri)
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
