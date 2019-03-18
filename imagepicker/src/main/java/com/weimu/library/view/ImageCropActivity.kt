package com.weimu.library.view

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.opengl.GLES10
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.widget.ImageView

import com.bumptech.glide.Glide
import com.isseiaoki.simplecropview.CropImageView
import com.weimu.library.R
import com.weimu.library.utils.CropUtil
import com.weimu.library.utils.FileUtilsIP
import com.weimu.universalib.ktx.getColorPro
import com.weimu.universalview.core.toolbar.StatusBarManager
import com.weimu.universalview.core.toolbar.ToolBarManager
import com.weimu.universalview.ktx.setOnClickListenerPro

import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream


class ImageCropActivity : SelectorBaseActivity() {

    private var ivBg: ImageView? = null
    private var cropImageView: CropImageView? = null


    private var sourceUri: Uri? = null//源URI
    private var saveUri: Uri? = null//存储URI

    private val handler = Handler()

    private lateinit var toolBarManager: ToolBarManager

    private val maxImageSize: Int
        get() {
            val textureLimit = maxTextureSize
            return if (textureLimit == 0) {
                SIZE_DEFAULT
            } else {
                Math.min(textureLimit, SIZE_LIMIT)
            }
        }

    private val maxTextureSize: Int
        get() {
            val maxSize = IntArray(1)
            GLES10.glGetIntegerv(GLES10.GL_MAX_TEXTURE_SIZE, maxSize, 0)
            return maxSize[0]
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_crop)
        initBase()
        initView()
    }

    private fun initBase() {
        //data
        val path = intent.getStringExtra(DATA_EXTRA_PATH)
        sourceUri = Uri.fromFile(File(path))
        cropImageView = findViewById(R.id.cropImageView)
        ivBg = findViewById(R.id.iv_bg)
        //crop setup
        cropImageView!!.setHandleSizeInDp(8)//设置裁剪四周小圆球的大小
        cropImageView!!.setFrameStrokeWeightInDp(1)
        cropImageView!!.setGuideStrokeWeightInDp(1)
        cropImageView!!.setInitialFrameScale(0.5f)//裁剪区域为原图的一半
        cropImageView!!.setCropMode(CropImageView.CropMode.SQUARE)//设置裁剪方式为圆形，可换
    }

    fun initView() {
        StatusBarManager.setColor(this.window, ContextCompat.getColor(this, R.color.white))
        StatusBarManager.setLightMode(this.window, false)
        toolBarManager = ToolBarManager.with(this, contentView)
                .bg {
                    this.setBackgroundResource(R.color.white)
                }
                .leftMenuIcon {
                    this.setImageResource(R.drawable.toolbar_arrow_back_black)
                }
                .title {
                    this.text = "${getString(R.string.crop_picture)}"
                }
                .rightMenuText {
                    this.text = getString(R.string.use)
                    this.setTextColor(ContextCompat.getColorStateList(context, R.color.black_text_selector))
                    this.isEnabled = true
                    this.setOnClickListenerPro {
                        //点击完成
                        //ProgressDialog.show(ImageCropActivity.this, null, getString(R.string.save_ing), true, false);
                        saveUri = Uri.fromFile(FileUtilsIP.createCropFile(this@ImageCropActivity))
                        saveOutput(cropImageView!!.croppedBitmap)
                    }
                }

        Glide.with(this).asBitmap().load(sourceUri).into(ivBg!!)
        //获取源图片的旋转角度
        val exifRotation = CropUtil.getExifRotation(CropUtil.getFromMediaUri(this, contentResolver, sourceUri))

        var `is`: InputStream? = null
        try {
            val sampleSize = calculateBitmapSampleSize(sourceUri)
            `is` = contentResolver.openInputStream(sourceUri!!)
            val option = BitmapFactory.Options()
            option.inSampleSize = sampleSize
            val sizeBitmap = BitmapFactory.decodeStream(`is`, null, option) ?: return
            val matrix = getRotateMatrix(sizeBitmap, exifRotation % 360)
            val rotated = Bitmap.createBitmap(sizeBitmap, 0, 0, sizeBitmap.width, sizeBitmap.height, matrix, true)
            cropImageView!!.imageBitmap = rotated
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
        } finally {
            CropUtil.closeSilently(`is`)
        }
    }


    fun getRotateMatrix(bitmap: Bitmap?, rotation: Int): Matrix {
        val matrix = Matrix()
        if (bitmap != null && rotation != 0) {
            val cx = bitmap.width / 2
            val cy = bitmap.height / 2
            matrix.preTranslate((-cx).toFloat(), (-cy).toFloat())
            matrix.postRotate(rotation.toFloat())
            matrix.postTranslate(cx.toFloat(), cy.toFloat())
        }
        return matrix
    }

    @Throws(IOException::class)
    private fun calculateBitmapSampleSize(bitmapUri: Uri?): Int {
        var `is`: InputStream? = null
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        try {
            `is` = contentResolver.openInputStream(bitmapUri!!)
            BitmapFactory.decodeStream(`is`, null, options) // Just get image size
        } finally {
            CropUtil.closeSilently(`is`)
        }

        val maxSize = maxImageSize
        var sampleSize = 1
        while (options.outHeight / sampleSize > maxSize || options.outWidth / sampleSize > maxSize) {
            sampleSize = sampleSize shl 1
        }
        return sampleSize
    }

    private fun saveOutput(croppedImage: Bitmap) {
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
                CropUtil.closeSilently(outputStream)
            }
            setResult(RESULT_OK, Intent().putExtra(OUTPUT_PATH, saveUri!!.path))
        }
        handler.post { croppedImage.recycle() }
        onBackPressed()
    }

    companion object {
        val REQUEST_CROP = 69

        val DATA_EXTRA_PATH = "data_extra_path"
        val OUTPUT_PATH = "outputPath"

        private val SIZE_DEFAULT = 2048
        private val SIZE_LIMIT = 4096

        fun newIntent(context: Context, path: String): Intent {
            val intent = Intent(context, ImageCropActivity::class.java)
            intent.putExtra(DATA_EXTRA_PATH, path)
            return intent
        }
    }
}
