package com.weimu.library.view

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.github.chrisbanes.photoview.OnPhotoTapListener
import com.github.chrisbanes.photoview.PhotoView
import com.weimu.library.R

import java.io.File


class ImagePreviewFragment : Fragment() {
    private var photo_view: PhotoView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val contentView = inflater.inflate(R.layout.fragment_image_preview, container, false)
        initView(container!!, contentView)
        return contentView
    }

    private fun initView(container: ViewGroup, contentView: View) {
        photo_view = contentView.findViewById<View>(R.id.photo_view) as PhotoView

        Glide.with(container.context)
                .asBitmap()
                .load(arguments!!.getString(PATH))
                //.apply(new RequestOptions().centerCrop())
                .transition(BitmapTransitionOptions.withCrossFade())
                .into(photo_view!!)
        //                .into(new SimpleTarget<Bitmap>(480, 800) {
        //                    @Override
        //                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
        //                        photo_view.setImageBitmap(resource);
        //                    }
        //                });

        photo_view!!.setOnPhotoTapListener { view, x, y ->
            val activity = activity as ImagePreviewActivity?
            activity!!.switchBarVisibility()
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
