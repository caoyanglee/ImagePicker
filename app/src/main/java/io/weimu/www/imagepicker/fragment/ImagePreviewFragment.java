package io.weimu.www.imagepicker.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;


import io.weimu.www.imagepicker.activity.PhotoViewPagerActivity;
import io.weimu.www.imagepicker.R;
import io.weimu.www.imagepicker.fragment.base.BaseFragment;

public class ImagePreviewFragment extends BaseFragment {
    private PhotoView photo_view;


    @Override
    protected void findViewByIDS() {
        photo_view = myFindViewsById(R.id.photo_view);
    }


    public static final String PATH = "path";

    public static ImagePreviewFragment newInstance(String path) {
        ImagePreviewFragment fragment = new ImagePreviewFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PATH, path);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_image_preview;
    }

    @Override
    protected void onGenerate() {

        Glide.with(mContext)
                .asBitmap()
                .load(getArguments().getString(PATH))
                .apply(new RequestOptions().centerCrop())
                .transition(BitmapTransitionOptions.withCrossFade())
                .into(new SimpleTarget<Bitmap>(480, 800) {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        photo_view.setImageBitmap(resource);
                    }
                });

        photo_view.setOnPhotoTapListener(new OnPhotoTapListener() {
            @Override
            public void onPhotoTap(ImageView view, float x, float y) {
                PhotoViewPagerActivity activity = (PhotoViewPagerActivity) getActivity();
                activity.switchBarVisibility();
            }
        });

    }

}
