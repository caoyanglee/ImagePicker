package com.pmm.imagepicker.ui


import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pmm.imagepicker.*
import com.pmm.imagepicker.adapter.ImageListAdapter
import com.pmm.imagepicker.ktx.createCameraFile
import com.pmm.imagepicker.ktx.startActionCapture
import com.pmm.imagepicker.model.LocalMedia
import com.pmm.imagepicker.model.LocalMediaFolder
import com.pmm.imagepicker.ui.preview.ImagePreviewActivity
import com.pmm.ui.core.activity.BaseActivity
import com.pmm.ui.core.dialog.ProgressDialog
import com.pmm.ui.core.recyclerview.decoration.GridItemDecoration
import com.pmm.ui.core.toolbar.StatusBarManager
import com.pmm.ui.ktx.*
import com.pmm.ui.widget.ToolBarPro
import kotlinx.android.synthetic.main.activity_imageselector.*
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.File
import java.util.*
import kotlin.properties.Delegates
import kotlin.reflect.KProperty


internal class ImageSelectorActivity : BaseActivity() {

    private lateinit var config: Config

    //ui
    private val recyclerView: RecyclerView by lazy { findViewById<RecyclerView>(R.id.folder_list) }
    private val imageAdapter: ImageListAdapter by lazy { ImageListAdapter(this, config) }
    private val folderLayout: LinearLayout by lazy { findViewById<LinearLayout>(R.id.folder_layout) }
    private val folderName: TextView by lazy { findViewById<TextView>(R.id.folder_name) }
    private val folderWindow: FolderWindow by lazy { FolderWindow(this) }

    private var cameraPath: String? = null
    private var allFolders: List<LocalMediaFolder> = arrayListOf()//所有图片文件夹
    private var isUseOrigin by Delegates.observable(false) { property: KProperty<*>, oldValue: Boolean, newValue: Boolean ->
        tvOrigin.isActivated = newValue
    }//是否使用原图

    private var isLoadImgIng = false//是否正在加载图片->返回给app


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
        //ToolBar
        mToolBar.apply {
            this.navigationIcon {
                if (ToolBarPro.GlobalConfig.navigationDrawable == null) {
                    this.setImageResource(R.drawable.ic_nav_back_24dp)
                    val lightColor = this@apply.getToolBarBgColor().isLightColor()
                    this.setColorFilter(if (lightColor) Color.BLACK else Color.WHITE)
                }
                this.setOnClickListenerPro { onBackPressed() }
            }
            this.centerTitle {
                this.text = getString(R.string.select_image)
            }
            this.menuText1 {
                this.text = if (config.selectMode == Config.MODE_MULTIPLE) (getString(R.string.done)) else ""
                this.setOnClickListenerPro {
                    //点击完成
                    onSelectDone(imageAdapter.selectedImages)
                }
                this.invisible()
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


        //CheckBox use Origin Pic
        tvOrigin.apply {
            if (!config.showIsCompress) {
                this.visibility = View.GONE
            } else {
                this.isActivated = isUseOrigin
                this.setOnClickListenerPro {
                    isUseOrigin = !isUseOrigin
                }
            }
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
        folderLayout.setOnClickListenerPro {
            //Toast.makeText(ImageSelectorActivity.this, "文件夹长度  " + allFolders.size() + "  内部图片数量  " + allFolders.get(0).getImages().size(), Toast.LENGTH_SHORT).show();
            if (allFolders.isEmpty() || allFolders[0].images.isEmpty()) {
                Toast.makeText(this@ImageSelectorActivity, "没有可选择的图片", Toast.LENGTH_SHORT).show()
                return@setOnClickListenerPro
            }
            if (folderWindow.isShowing) {
                folderWindow.dismiss()
            } else {
                folderWindow.showAsDropDown(mToolBar)
            }
        }

        //recyclerView点击事件
        imageAdapter.setOnImageSelectChangedListener(object : ImageListAdapter.OnImageSelectChangedListener {
            @SuppressLint("SetTextI18n")
            override fun onChange(selectImages: List<LocalMedia>) {
                mToolBar.menuText1 {
                    val enable = selectImages.isNotEmpty()
                    if (enable) {
                        this.text = "${getString(R.string.done_num)}(${selectImages.size}/${config.maxSelectNum})"
                        this.visible()
                    } else {
                        this.text = getString(R.string.done)
                        this.invisible()
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
        folderWindow.setOnFolderClickListener { folderName, images ->
            folderWindow.dismiss()
            imageAdapter.bindImages(images as ArrayList<LocalMedia>)
            this@ImageSelectorActivity.folderName.text = folderName
            recyclerView.smoothScrollToPosition(0)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
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
        startActivityForResult(ImageCropActivity.newIntent(this, path, config), ImageCropActivity.REQUEST_CROP,
                ActivityOptions.makeSceneTransitionAnimation(this, view, "share_image").toBundle())
    }

    fun startCrop(path: String?) {
        startActivityForResult(ImageCropActivity.newIntent(this, "$path", config), ImageCropActivity.REQUEST_CROP)
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
        if (isLoadImgIng) return
        isLoadImgIng = true
        if (isUseOrigin) {
            setResult(Activity.RESULT_OK, Intent().putStringArrayListExtra(ImagePicker.REQUEST_OUTPUT, images))
            onBackPressed()
        } else {
            compressImage(images)
        }
    }

    //压缩图片
    private fun compressImage(photos: ArrayList<String>) {
        if (photos.size > 9) ProgressDialog.show(this@ImageSelectorActivity, message = "加载中")
        val newImageList = ArrayList<String>()
        Luban.with(this)
                .load(photos)                                   // 传入要压缩的图片列表
                .ignoreBy(100)                            // 忽略不压缩图片的大小
                .setCompressListener(object : OnCompressListener { //设置回调
                    override fun onStart() {}

                    override fun onSuccess(file: File) {
                        //Log.d("weimu", "压缩成功 地址为：$file")
                        newImageList.add(file.toString())
                        //所有图片压缩成功
                        if (newImageList.size == photos.size) {
                            ProgressDialog.hide()
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
