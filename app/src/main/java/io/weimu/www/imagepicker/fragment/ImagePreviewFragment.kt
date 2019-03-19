package io.weimu.www.imagepicker.fragment


import android.graphics.Bitmap
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.weimu.universalview.ktx.load
import com.weimu.universalview.ktx.load4CenterCrop
import io.weimu.www.imagepicker.R
import io.weimu.www.imagepicker.activity.PhotoViewPagerActivity
import io.weimu.www.imagepicker.base.BaseViewFragment
import kotlinx.android.synthetic.main.fragment_image_preview.*

class ImagePreviewFragment : BaseViewFragment() {

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


    override fun afterViewAttach(savedInstanceState: Bundle?) {
        photo_view.load("${arguments!!.getString(PATH)}")
        photo_view!!.setOnPhotoTapListener { view, x, y ->
            val activity = activity as PhotoViewPagerActivity?
            activity!!.switchBarVisibility()
        }
    }


}
