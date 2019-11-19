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
import com.pmm.ui.core.activity.BaseActivity
import com.pmm.ui.core.toolbar.StatusBarManager
import com.pmm.ui.ktx.gone
import com.pmm.ui.ktx.isLightColor
import com.pmm.ui.ktx.click
import com.pmm.ui.ktx.toast
import com.pmm.ui.widget.ToolBarPro
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


    private var cropType = 0//0：自由裁剪 1：限制宽高比裁剪

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


    override fun beforeViewAttach(savedInstanceState: Bundle?) {
        //data
        path = intent.getStringExtra(DATA_EXTRA_PATH)
        sourceUri = Uri.fromFile(File(path))
        config = intent.getSerializableExtra(Config.EXTRA_CONFIG) as Config
    }

    override fun getLayoutResID(): Int = R.layout.activity_image_crop

    override fun afterViewAttach(savedInstanceState: Bundle?) {
        //ToolBar
        mToolBar.apply {
            this.navigationIcon {
                if (ToolBarPro.GlobalConfig.navigationDrawable == null) {
                    this.setImageResource(R.drawable.ic_nav_back_24dp)
                    val lightColor = this@apply.getToolBarBgColor().isLightColor()
                    this.setColorFilter(if (lightColor) Color.BLACK else Color.WHITE)
                }
                this.click { onBackPressed() }
            }
            this.centerTitle {
                this.text = getString(R.string.crop_picture)
            }
            this.menuText1 {
                this.text = getString(R.string.use)
                this.isEnabled = true
                this.click {
                    //点击完成
                    if (cropType == 1) {
                        saveOutput(cropImageView1.croppedImage)
                    } else {
                        saveOutput(cropImageView0.croppedImage)
                    }
                }
            }
        }
        //StatusBar
        StatusBarManager.apply {
            val statusColor = mToolBar.getToolBarBgColor()
            this.setColor(window, statusColor)
            if (statusColor.isLightColor()) {
                this.setLightMode(window)
            } else {
                this.setDarkMode(window)
            }
        }


        if ((config.cropMiniWidth > 0 && config.cropMiniHeight > 0)) {
            cropImageView0.gone()
            cropType = 1
            crop1init()
        } else {
            cropImageView1.gone()
            cropType = 0
            crop0init()
        }

    }

    private fun crop0init() {
        cropImageView0.apply {
            //配置
            this.isAutoZoomEnabled = true//是否自动缩放
            //设置宽高比
            if (config.cropAspectRatioX > 0 && config.cropAspectRatioY > 0)
                this.setAspectRatio(config.cropAspectRatioX, config.cropAspectRatioY)
            this.setImageUriAsync(sourceUri)
        }
    }

    private fun crop1init() {
        cropImageView1.apply {
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


    private fun saveOutput(croppedImage: Bitmap?) {
        if (croppedImage == null) {
            toast("图片无效,请重新选择！")
        } else {
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
        }
        onBackPressed()
    }

}
