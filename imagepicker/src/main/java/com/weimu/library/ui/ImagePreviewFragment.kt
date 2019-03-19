package com.weimu.library.ui

import android.os.Bundle
import com.weimu.library.R
import com.weimu.universalview.core.fragment.BaseFragment
import com.weimu.universalview.ktx.load
import com.weimu.universalview.ktx.load4CenterCrop
import kotlinx.android.synthetic.main.fragment_image_preview.*


internal class ImagePreviewFragment : BaseFragment() {
    override fun afterViewAttachBaseViewAction(savedInstanceState: Bundle?) {}

    override fun beforeViewAttachBaseViewAction(savedInstanceState: Bundle?) {}

    override fun getLayoutResID(): Int = R.layout.fragment_image_preview

    override fun afterViewAttach(savedInstanceState: Bundle?) {
        photo_view.load("${arguments!!.getString(PATH)}")
        photo_view.setOnPhotoTapListener { view, x, y ->
            val activity = activity as ImagePreviewActivity
            activity.isShowBar = !activity.isShowBar
        }
    }


    companion object {

        val PATH = "path"

        fun getInstance(path: String): ImagePreviewFragment {
            val fragment = ImagePreviewFragment()
            val bundle = Bundle()
            bundle.putString(PATH, path)
            fragment.arguments = bundle
            return fragment
        }
    }


}
