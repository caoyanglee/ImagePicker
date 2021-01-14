package com.pmm.imagepicker.ui.preview

import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import by.kirich1409.viewbindingdelegate.viewBinding
import com.pmm.imagepicker.R
import com.pmm.imagepicker.databinding.FragmentImagePreviewBinding
import com.pmm.ui.core.fragment.BaseFragmentV2
import com.pmm.ui.ktx.load


internal class ImagePreviewFragment : BaseFragmentV2(R.layout.fragment_image_preview) {
    private val mVB by viewBinding(FragmentImagePreviewBinding::bind, R.id.container)

    private var path = ""
    private var uri: Uri? = null

    companion object {

        val PATH = "path"
        val URI = "uri"

        fun newInstance(path: String?, uri: Uri?): ImagePreviewFragment {
            val fragment = ImagePreviewFragment()
            val bundle = Bundle()
            bundle.putString(PATH, path)
            bundle.putParcelable(URI, uri)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun beforeViewAttach(savedInstanceState: Bundle?) {
        path = "${requireArguments().getString(PATH)}"
        uri = requireArguments().getParcelable(URI)
    }

    override fun getContentView(): ViewGroup = requireView() as ViewGroup

    override fun afterViewAttach(savedInstanceState: Bundle?) {
        mVB.photoView.load(uri!!)
        mVB.photoView.setOnPhotoTapListener { view, x, y ->
            val activity = activity as ImagePreviewActivity
            activity.isShowBar = !activity.isShowBar
        }

        mVB.mTvSelect.text = path + "\n" + uri
    }


}
