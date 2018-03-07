package com.weimu.library.view;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.weimu.library.R;

import java.io.File;


public class ImagePreviewFragment extends Fragment {
    private PhotoView photo_view;

    public static final String PATH = "path";

    public static ImagePreviewFragment getInstance(String path) {
        ImagePreviewFragment fragment = new ImagePreviewFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PATH, path);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_image_preview, container, false);
        initView(container, contentView);
        return contentView;
    }

    private void initView(ViewGroup container, View contentView) {
        photo_view = (PhotoView) contentView.findViewById(R.id.photo_view);

        Glide.with(container.getContext())
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
                ImagePreviewActivity activity = (ImagePreviewActivity) getActivity();
                activity.switchBarVisibility();
            }
        });

    }


}
