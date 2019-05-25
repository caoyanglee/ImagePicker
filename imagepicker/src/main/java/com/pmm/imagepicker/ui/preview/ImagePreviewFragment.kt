package com.pmm.imagepicker.ui.preview

import android.os.Bundle
import com.pmm.imagepicker.R
import com.weimu.universalview.core.fragment.BaseFragment
import com.weimu.universalview.ktx.load
import kotlinx.android.synthetic.main.fragment_image_preview.*


internal class ImagePreviewFragment : BaseFragment() {

    private var path = ""

    companion object {

        val PATH = "path"

        fun newInstance(path: String): ImagePreviewFragment {
            val fragment = ImagePreviewFragment()
            val bundle = Bundle()
            bundle.putString(PATH, path)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayoutResID(): Int = R.layout.fragment_image_preview

    override fun beforeViewAttach(savedInstanceState: Bundle?) {
        path = "${arguments!!.getString(PATH)}"
    }

    override fun afterViewAttach(savedInstanceState: Bundle?) {
        photo_view.load(path)
        photo_view.setOnPhotoTapListener { view, x, y ->
            val activity = activity as ImagePreviewActivity
            activity.isShowBar = !activity.isShowBar
        }
    }


}
