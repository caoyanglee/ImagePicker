package io.weimu.www.imagepicker.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.weimu.library.ImagePicker;
import com.weimu.library.utils.GridSpacingItemDecoration;
import com.weimu.library.utils.ScreenUtils;
import com.weimu.library.view.ImageSelectorActivity;

import java.util.ArrayList;

import io.reactivex.functions.Consumer;
import io.weimu.www.imagepicker.R;
import io.weimu.www.imagepicker.adaper.recycleview.ImageGridadapter;

public class MainActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private ImageGridadapter mAdapter;
    private GridLayoutManager gridManager;


    private int maxImageNumber = 9;
    private int spanCount = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new RxPermissions(this)
                .request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean granted) throws Exception {
                        if (granted)
                            initRecyclerVIew();

                    }
                });


    }

    private void initRecyclerVIew() {
        recyclerView = (RecyclerView) findViewById(R.id.id_RecyclerView);
        mAdapter = new ImageGridadapter(this, maxImageNumber);
        gridManager = new GridLayoutManager(this, spanCount);
        recyclerView.setLayoutManager(gridManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, ScreenUtils.dip2px(this, 8), false));
        //设置Item增加、移除动画
        //recyclerView.setItemAnimator(new NoAlphaItemAnimator());
        recyclerView.setAdapter(mAdapter);

        mAdapter.AddListenter(new ImageGridadapter.AddListenter() {
            @Override
            public void onAddClick(int needNumber) {
                //ImagePicker.getInstance().pickAvatar(MainActivity.this);
                ImagePicker.getInstance().pickImage(MainActivity.this, 9);
                //ImagePicker.getInstance().takePhoto(MainActivity.this,true);//使用摄像头
            }

            @Override
            public void onItemClick(int position) {
                startActivity(PhotoViewPagerActivity.newInstance(MainActivity.this, position, (ArrayList<String>) mAdapter.getDataList()));
            }

            @Override
            public void onItemDeleteClick(int position) {
                mAdapter.deleteData(position);
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == ImageSelectorActivity.REQUEST_IMAGE) {
            ArrayList<String> pics = (ArrayList<String>) data.getSerializableExtra(ImageSelectorActivity.REQUEST_OUTPUT);
            mAdapter.addData(pics);
        }
    }
}
