package com.pmm.imagepicker.ui.preview2

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import by.kirich1409.viewbindingdelegate.viewBinding
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import com.pmm.imagepicker.R
import com.pmm.imagepicker.databinding.FragmentImagePreviewV2Binding
import com.pmm.ui.OriginAppData
import com.pmm.ui.core.fragment.BaseFragmentV2
import com.pmm.ui.helper.AnimHelper
import com.pmm.ui.helper.FileHelper
import com.pmm.ui.helper.MediaScanner
import com.pmm.ui.helper.security.MD5Helper
import com.pmm.ui.ktx.*
import com.shizhefei.view.largeimage.factory.FileBitmapDecoderFactory
import java.io.File
import java.io.FileNotFoundException
import java.util.*

/**
 * Author:你需要一台永动机
 * Date:2018/4/19 23:34
 * Description:
 * 参考网站：https://blog.csdn.net/jq_motee/article/details/52180218
 *
 * 普通图片->PhotoView
 * 长图->WebView
 */
internal class ImagePreviewFragment : BaseFragmentV2(R.layout.fragment_image_preview_v2) {
    private val mVB by viewBinding(FragmentImagePreviewV2Binding::bind, R.id.container)

    val FILE_IMAGE_PREVIEW = "${OriginAppData.context.externalCacheDir}/images/"//  cache/images 下的图片


    companion object {
        val PATH = "path"
        val PATH_SMALL = "path_small"

        fun newInstance(path: String): ImagePreviewFragment {
            val fragment = ImagePreviewFragment()
            val bundle = Bundle()
            bundle.putString(PATH, path)
            fragment.arguments = bundle
            return fragment
        }

        fun newInstance(path: String, path_small: String): ImagePreviewFragment {
            val fragment = ImagePreviewFragment()
            val bundle = Bundle()
            bundle.putString(PATH, path)
            bundle.putString(PATH_SMALL, path_small)
            fragment.arguments = bundle
            return fragment
        }

    }

    var url: String = ""
    var smallUrl: String = ""
    var targetPath = ""

    override fun beforeViewAttach(savedInstanceState: Bundle?) {
        super.beforeViewAttach(savedInstanceState)
        //arguments
        arguments?.apply {
            url = this.getString(PATH) ?: ""
            smallUrl = this.getString(PATH_SMALL) ?: ""
        }
    }

    override fun afterViewAttach(savedInstanceState: Bundle?) {
        //单点
        mVB.ivLarge.setOnClickListener {
            if (activity == null || requireActivity().isDestroyed) return@setOnClickListener
            (activity as ImagePreviewActivity).transitionFinish()
        }
        //长按
        mVB.ivLarge.setOnLongClickListener { longClick() }

        initDownload()

        //图片加载分发
        dispatchImageLoad()
    }

    private fun initDownload() {
        FileDownloader.setup(activity)//下载管理器的初始化
    }


    private var task: BaseDownloadTask? = null

    private fun dispatchImageLoad() {
        //如果是本地图片的处理
        if (!url.contains("http") && !url.contains("https")) {
            showImage(File(url))
            targetPath = url
            return
        }


        val targetDir = FILE_IMAGE_PREVIEW
        //Logger.e("目标路径=$targetDir")
        val fileName = MD5Helper.sign(url, "weimu")

        val fileType = try {
            url.substring(url.lastIndexOf(".") + 1, url.length)
        } catch (e: Exception) {
            ""
        }

        //md5 名称唯一性
        targetPath = "$targetDir$fileName.$fileType"


        //先显示小图
        showThumbnailImage {
            task = FileDownloader.getImpl().create(url)
                    .setPath(targetPath)
                    .setListener(object : FileDownloadListener() {


                        override fun pending(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                            //Logger.e("pending")
                        }

                        override fun started(task: BaseDownloadTask?) {
                            super.started(task)
                            //Logger.e("started")
                            mVB.crv.visible()
                        }

                        override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                            //Logger.e("progress soFarBytes=$soFarBytes totalBytes=$totalBytes")
                            //val percent = soFarBytes * 100 / totalBytes
                            //crv?.setProgressValue(percent)
                        }


                        override fun completed(task: BaseDownloadTask?) {
                            //Logger.e("completed")
                            showImage(File(targetPath))
                            mVB.crv.invisible()
                        }

                        override fun warn(task: BaseDownloadTask?) {
                            //在下载队列中(正在等待/正在下载)已经存在相同下载连接与相同存储路径的任务
                            //Logger.e("warn=${task!!.status}")
                            mVB.crv.invisible()
                        }


                        override fun error(task: BaseDownloadTask?, e: Throwable?) {
                            //Logger.e("error")
                            mVB.crv.invisible()
                        }


                        override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                            //Logger.e("paused")
                            mVB.crv.invisible()
                        }
                    })
            task?.start()
        }
    }

    //显示缩略图
    private fun showThumbnailImage(fn: (() -> Unit)) {
        mVB.ivLargeThumbnail.apply {
            val that = this
            this.visible()
            if (!TextUtils.isEmpty(smallUrl)) {
//                Glide.with(context).asBitmap().load(smallUrl).into(object : SimpleTarget<Bitmap>() {
//                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
//                        that.setImage(resource)
//                        fn.invoke()
//                    }
//                })
                that.load(smallUrl)
            } else {
                fn.invoke()
            }
        }
    }


    //显示图片
    private fun showImage(file: File) {
        //加载gif
        if (file.name.endsWith(".gif")) {
            mVB.ivNormal.visible()
            mVB.ivNormal.load(file)
            return
        }
        //Logger.e("目标地址=${file.toString()}")
        mVB.ivLarge.visible()
        try {
            val options = BitmapFactory.Options()
            /**
             * 最关键在此，把options.inJustDecodeBounds = true;
             * 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了
             */
            options.inJustDecodeBounds = true

            val targetBitmap = BitmapFactory.decodeFile(file.toString())// 此时返回的bitmap为null

            val bitmapHeight = targetBitmap.height
            val bitmapWidth = targetBitmap.width
            if (bitmapHeight >= 4 * bitmapWidth || bitmapWidth >= 4 * bitmapHeight) {
                //加载大图
                mVB.ivLarge.setImage(FileBitmapDecoderFactory(file))
                //做一下过渡动画比较不会太生硬
                AnimHelper.alphaAnim(mVB.ivLarge, 1000, onAnimEnd = {
                    //                    Handler().postDelayed({iv_large_thumbnail?.gone()},1000)
                    mVB.container.removeView(mVB.ivLargeThumbnail)
                })
            } else {
                //加载普通图
                mVB.ivLarge.setImage(targetBitmap)
            }
        } catch (e: OutOfMemoryError) {
            mVB.ivLarge.setImage(FileBitmapDecoderFactory(file))
        } catch (e: Exception) {
            mVB.ivLarge.setImage(FileBitmapDecoderFactory(file))
        }
    }


    //TODO 长按处理
    private fun longClick(): Boolean {
        val menus: ArrayList<String> = arrayListOf()
//        if (url.startsWith("http:") || url.startsWith("https:")) {
//            menus.add("发送给朋友")
//        }
//        menus.add("保存图片")
//        val dialog = MenuDialog().transmitMenu(menus).show(this) as MenuDialog
//        dialog.onMenuClickV2 = {
//            when (it) {
//                "发送给朋友" -> {
//                    //发送给朋友
//                    UmengCenter.shareImage(getCurrentActivity(), url, SHARE_MEDIA.WEIXIN)
//                }
//                "保存图片" -> {
//                    //保存图片
//                    //val drawable = photo_view.drawable as BitmapDrawable
//                    //savePictures(drawable.bitmap)
//                    saveImageToLocal();
//                }
//            }
//        }


        return true
    }

    //保存图片
    private fun saveImageToLocal(context: Context) {
        val picturePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)//大部分图片都存储在这个路径里
        val target = "IMAGE${(Date().time / 1000).formatDate("yyyyMMddHHmmss")}.png"

        val sourceFile = targetPath
        val saveFile = "$picturePath/$target"
        try {
            FileHelper.copyFile(sourceFile, saveFile)//直接复制即可
            context.toast("保存成功")
            //让图片可以扫描
            val filePaths = arrayOf(saveFile)
            val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("png") ?: ""
            val mimeTypes = arrayOf(mimeType)
            MediaScanner(context).scanFiles(filePaths, mimeTypes)
        } catch (e: FileNotFoundException) {
            context.toast("文件未找到，请重试")
        }

    }

    override fun onDetach() {
        super.onDetach()
        task?.pause()
    }

}




