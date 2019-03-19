package io.weimu.www.imagepicker.activity

import android.graphics.BitmapFactory
import android.os.Bundle
import com.shizhefei.view.largeimage.factory.FileBitmapDecoderFactory
import com.weimu.universalview.core.fragment.BaseFragment
import io.weimu.www.imagepicker.R
import kotlinx.android.synthetic.main.fragment_image_preview_app.*
import java.io.File


class ImagePreviewAppFragment : BaseFragment() {

    companion object {

        val PATH = "path"

        fun newInstance(path: String): ImagePreviewAppFragment {
            val fragment = ImagePreviewAppFragment()
            val bundle = Bundle()
            bundle.putString(PATH, path)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun afterViewAttachBaseViewAction(savedInstanceState: Bundle?) {}

    override fun beforeViewAttachBaseViewAction(savedInstanceState: Bundle?) {}

    override fun getLayoutResID(): Int = R.layout.fragment_image_preview_app

    override fun afterViewAttach(savedInstanceState: Bundle?) {
        showImage(File("${arguments!!.getString(PATH)}"))
        iv_large.setOnClickListener {
            val activity = activity as PhotoViewPagerActivity
            activity.isShowBar = !activity.isShowBar
        }
    }


    //显示图片
    private fun showImage(file: File) {
        if (iv_large == null) return
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
                iv_large?.setImage(FileBitmapDecoderFactory(file))
            } else {
                //加载普通图
                iv_large.setImage(targetBitmap)
            }
        } catch (e: OutOfMemoryError) {
            iv_large?.setImage(FileBitmapDecoderFactory(file))
        } catch (e: Exception) {
            iv_large?.setImage(FileBitmapDecoderFactory(file))
        }
    }


}
