package io.weimu.www.imagepicker.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;


import io.weimu.www.imagepicker.activity.PhotoViewPagerActivity;
import io.weimu.www.imagepicker.R;
import io.weimu.www.imagepicker.fragment.base.BaseFragment;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ImagePreviewFragment extends BaseFragment {
    private ImageView imageView;

    @Override
    protected void findViewByIDS() {
        imageView = myFindViewsById(R.id.preview_image);
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
        final PhotoViewAttacher mAttacher = new PhotoViewAttacher(imageView);

        Glide.with(mContext)
                .load(getArguments().getString(PATH))
                .asBitmap()
                .into(new SimpleTarget<Bitmap>(480, 800) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        imageView.setImageBitmap(resource);
                        mAttacher.update();
                    }
                });

        mAttacher.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                PhotoViewPagerActivity activity = (PhotoViewPagerActivity) getActivity();
                activity.switchBarVisibility();
            }
        });
    }

}
