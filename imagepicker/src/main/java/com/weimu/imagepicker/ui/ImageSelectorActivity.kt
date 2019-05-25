package com.weimu.imagepicker.ui


import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.weimu.imagepicker.ImagePicker
import com.weimu.imagepicker.ImageStaticHolder
import com.weimu.imagepicker.R
import com.weimu.imagepicker.adapter.ImageFolderAdapter
import com.weimu.imagepicker.adapter.ImageListAdapter
import com.weimu.imagepicker.ktx.createCameraFile
import com.weimu.imagepicker.ktx.startActionCapture
import com.weimu.imagepicker.Config
import com.weimu.imagepicker.model.LocalMedia
import com.weimu.imagepicker.model.LocalMediaFolder
import com.weimu.imagepicker.ui.preview.ImagePreviewActivity
import com.weimu.imagepicker.LocalMediaLoader
import com.weimu.universalview.core.activity.BaseActivity
import com.weimu.universalview.core.recyclerview.decoration.GridItemDecoration
import com.weimu.universalview.core.toolbar.StatusBarManager
import com.weimu.universalview.ktx.dip2px
import com.weimu.universalview.ktx.init
import com.weimu.universalview.ktx.setOnClickListenerPro
import com.weimu.universalview.ktx.setTextColorV2
import kotlinx.android.synthetic.main.activity_imageselector.*
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.File
import java.util.*


internal class ImageSelectorActivity : BaseActivity() {

    private lateinit var config: Config

    //ui
    private val recyclerView: RecyclerView by lazy { findViewById<RecyclerView>(R.id.folder_list) }
    private val imageAdapter: ImageListAdapter by lazy { ImageListAdapter(this, config) }
    private val folderLayout: LinearLayout by lazy { findViewById<LinearLayout>(R.id.folder_layout) }
    private val folderName: TextView by lazy { findViewById<TextView>(R.id.folder_name) }
    private val folderWindow: FolderWindow by lazy { FolderWindow(this) }
    private val cbOrigin: CheckBox by lazy { findViewById<CheckBox>(R.id.cb_origin) }

    private var cameraPath: String? = null
    private var allFolders: List<LocalMediaFolder> = arrayListOf()//所有图片文件夹
    private var isUseOrigin = false//是否使用原图


    companion object {
        const val BUNDLE_CAMERA_PATH = "CameraPath"


        //直接开启activity
        fun start(activity: Activity, config: Config) {
            val intent = Intent(activity, ImageSelectorActivity::class.java)
            intent.putExtra(Config.EXTRA_CONFIG, config)
            activity.startActivityForResult(intent, ImagePicker.REQUEST_IMAGE)
        }

        //生成新的Intent
        fun newIntent(context: Context, config: Config): Intent {
            val intent = Intent(context, ImageSelectorActivity::class.java)
            intent.putExtra(Config.EXTRA_CONFIG, config)
            return intent
        }
    }


    override fun getLayoutResID(): Int = R.layout.activity_imageselector


    override fun beforeViewAttach(savedInstanceState: Bundle?) {
        config = intent.getSerializableExtra(Config.EXTRA_CONFIG) as Config
        if (savedInstanceState != null) {
            cameraPath = savedInstanceState.getString(BUNDLE_CAMERA_PATH)
        }
    }

    override fun afterViewAttach(savedInstanceState: Bundle?) {
        initView()
        registerListener()
        loadData()
    }

    private fun initView() {
        //StatusBar
        StatusBarManager.apply {
            this.setColor(window, Color.WHITE)
            this.setLightMode(window, false)
        }
        //ToolBar
        mToolBar.apply {
            this.setBackgroundColor(Color.WHITE)
            this.navigationIcon {
                this.setImageResource(R.drawable.toolbar_arrow_back_black)
                this.setOnClickListenerPro { onBackPressed() }
            }
            this.centerTitle {
                this.text = "选择图片"
                this.setTextColor(Color.BLACK)
            }
            this.menuText1 {
                this.text = if (config.selectMode == Config.MODE_MULTIPLE) (getString(R.string.done)) else ""
                this.setTextColorV2(R.color.colorAccent)
                this.setTextColor(ContextCompat.getColorStateList(context, R.color.black_text_selector))
                this.isEnabled = false
                this.setOnClickListenerPro {
                    //点击完成
                    onSelectDone(imageAdapter.selectedImages)
                }
            }

        }
        //CheckBox use Origin Pic
        cbOrigin.apply {
            if (!config.showIsCompress) this.visibility = View.GONE
            this.isChecked = isUseOrigin
            this.setOnClickListener { isUseOrigin = !this.isChecked }
        }
        //RecyclerView
        recyclerView.apply {
            this.init()
            this.layoutManager = GridLayoutManager(this@ImageSelectorActivity, config.gridSpanCount)
            this.setHasFixedSize(true)
            this.addItemDecoration(GridItemDecoration(config.gridSpanCount, dip2px(2f), dip2px(2f)))
            this.adapter = imageAdapter
        }
    }

    private fun loadData() {
        LocalMediaLoader(this, LocalMediaLoader.TYPE_IMAGE).loadAllImage(object : LocalMediaLoader.LocalMediaLoadListener {
            override fun loadComplete(folders: List<LocalMediaFolder>) {
                allFolders = folders
                folderWindow.bindFolder(allFolders)
                //load all images first
                imageAdapter.bindImages(allFolders[0].images as ArrayList<LocalMedia>)
            }
        })
    }

    private fun registerListener() {
        folderLayout.setOnClickListener(View.OnClickListener {
            //Toast.makeText(ImageSelectorActivity.this, "文件夹长度  " + allFolders.size() + "  内部图片数量  " + allFolders.get(0).getImages().size(), Toast.LENGTH_SHORT).show();
            if (allFolders.size == 0 || allFolders[0].images.size == 0) {
                Toast.makeText(this@ImageSelectorActivity, "没有可选择的图片", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            if (folderWindow.isShowing) {
                folderWindow.dismiss()
            } else {

                folderWindow.showAsDropDown(findViewById<ConstraintLayout>(R.id.cl_toolbar))
            }
        })

        //recyclerView点击事件
        imageAdapter.setOnImageSelectChangedListener(object : ImageListAdapter.OnImageSelectChangedListener {
            @SuppressLint("SetTextI18n")
            override fun onChange(selectImages: List<LocalMedia>) {
                mToolBar.menuText1 {
                    val enable = selectImages.isNotEmpty()
                    if (enable) {
                        this.text = "${getString(R.string.done_num)}(${selectImages.size}/${config.maxSelectNum})"
                        this.isEnabled = enable
                    } else {
                        this.text = getString(R.string.done)
                        this.isEnabled = enable
                    }
                }
            }

            override fun onTakePhoto() {
                startCamera()
            }

            override fun onPictureClick(media: LocalMedia, position: Int, view: View) {
                when {
                    config.enablePreview -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            startPreviewWithAnim(imageAdapter.images, position, view)
                        } else {
                            startPreview(imageAdapter.images, position)
                        }
                    }
                    config.enableCrop -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            startCrop("${media.path}")
                        } else {
                            startCrop(media.path)
                        }
                    }
                    else -> {
                        onSelectDone(media.path)
                    }
                }
            }
        })
        //点击某个文件件
        folderWindow.setOnItemClickListener(object : ImageFolderAdapter.OnItemClickListener {
            override fun onItemClick(folderName: String?, images: List<LocalMedia>) {
                folderWindow.dismiss()
                imageAdapter.bindImages(images as ArrayList<LocalMedia>)
                this@ImageSelectorActivity.folderName.text = folderName
                recyclerView.smoothScrollToPosition(0)
            }

        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            // on take photo success
            if (requestCode == ImagePicker.REQUEST_CAMERA) {
                sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(File(cameraPath))))
                if (config.enableCrop) {
                    startCrop(cameraPath)
                } else {
                    onSelectDone(cameraPath)
                }
            } else if (requestCode == ImagePreviewActivity.REQUEST_PREVIEW) {
                val isDone = data?.getBooleanExtra(ImagePreviewActivity.OUTPUT_ISDONE, false)
                        ?: false
                val images = data?.getSerializableExtra(ImagePreviewActivity.OUTPUT_LIST) as List<LocalMedia>
                if (isDone) {
                    onSelectDone(images)
                } else {
                    if (images.isEmpty()) return
                    imageAdapter.bindSelectImages(images as ArrayList<LocalMedia>)
                }
            } else if (requestCode == ImageCropActivity.REQUEST_CROP) {
                val path = data?.getStringExtra(ImageCropActivity.OUTPUT_PATH) ?: ""
                onSelectDone(path)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(BUNDLE_CAMERA_PATH, cameraPath)
    }

    /**
     * 打开相机，预览，裁剪
     */
    fun startCamera() {
        val cameraFile = createCameraFile()
        cameraPath = cameraFile.absolutePath
        startActionCapture(cameraFile, ImagePicker.REQUEST_CAMERA)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun startPreviewWithAnim(previewImages: List<LocalMedia>, position: Int, view: View) {
        ImagePreviewActivity.startPreviewWithAnim(this, imageAdapter.selectedImages, config.maxSelectNum, position, view)
    }

    fun startPreview(previewImages: List<LocalMedia>, position: Int) {
        ImagePreviewActivity.startPreview(this, imageAdapter.selectedImages, config.maxSelectNum, position)
    }

    @SuppressLint("RestrictedApi")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun startCropWithAnim(path: String, view: View) {
        startActivityForResult(ImageCropActivity.newIntent(this, path,config), ImageCropActivity.REQUEST_CROP,
                ActivityOptions.makeSceneTransitionAnimation(this, view, "share_image").toBundle())
    }

    fun startCrop(path: String?) {
        startActivityForResult(ImageCropActivity.newIntent(this, "$path",config), ImageCropActivity.REQUEST_CROP)
    }

    //选择完成
    private fun onSelectDone(medias: List<LocalMedia>) {
        val images = ArrayList<String>()
        for (media in medias) {
            images.add("${media.path}")
        }
        onResult(images)
    }

    fun onSelectDone(path: String?) {
        val images = ArrayList<String>()
        images.add("$path")
        onResult(images)
    }

    //返回图片
    private fun onResult(images: ArrayList<String>) {
        if (isUseOrigin) {
            setResult(Activity.RESULT_OK, Intent().putStringArrayListExtra(ImagePicker.REQUEST_OUTPUT, images))
            onBackPressed()
        } else {
            compressImage(images)
        }
    }

    //压缩图片
    private fun compressImage(photos: ArrayList<String>) {
        //Toast.makeText(this, "压缩中...", Toast.LENGTH_SHORT).show();
        val newImageList = ArrayList<String>()
        Luban.with(this)
                .load(photos)                                   // 传入要压缩的图片列表
                .ignoreBy(100)                            // 忽略不压缩图片的大小
                .setCompressListener(object : OnCompressListener { //设置回调
                    override fun onStart() {
                        //Log.d("weimu", "开始压缩")
                    }

                    override fun onSuccess(file: File) {
                        //Log.d("weimu", "压缩成功 地址为：$file")
                        newImageList.add(file.toString())
                        //所有图片压缩成功
                        if (newImageList.size == photos.size) {
                            setResult(Activity.RESULT_OK, Intent().putStringArrayListExtra(ImagePicker.REQUEST_OUTPUT, newImageList))
                            onBackPressed()
                        }
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }
                }).launch()    //启动压缩
    }

    override fun onDestroy() {
        super.onDestroy()
        ImageStaticHolder.clearImages()
    }

}
