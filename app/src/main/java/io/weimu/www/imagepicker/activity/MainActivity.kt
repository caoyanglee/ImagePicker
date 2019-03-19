package io.weimu.www.imagepicker.activity

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import com.weimu.library.ImagePicker
import com.weimu.library.view.ImageSelectorActivity
import com.weimu.universalib.ktx.dip2px
import com.weimu.universalib.ktx.getColorPro
import com.weimu.universalview.core.recyclerview.decoration.GridItemDecoration
import com.weimu.universalview.core.toolbar.StatusBarManager
import com.weimu.universalview.ktx.requestPermission
import io.weimu.www.imagepicker.R
import io.weimu.www.imagepicker.fragment.adapter.ImageGridAdapter
import java.util.*

class MainActivity : AppCompatActivity() {


    private var recyclerView: RecyclerView? = null
    private var mAdapter: ImageGridAdapter? = null
    private var gridManager: GridLayoutManager? = null


    private val maxImageNumber = 9
    private val spanCount = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        StatusBarManager.setColor(window, getColorPro(R.color.white))
        StatusBarManager.setLightMode(window)

        requestPermission(
                permissions = *arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA),
                granted = { initRecyclerVIew() },
                dialogMessage = "请给我权限！"
        )
    }

    private fun initRecyclerVIew() {
        recyclerView = findViewById(R.id.id_RecyclerView)
        mAdapter = ImageGridAdapter(this, maxImageNumber)
        gridManager = GridLayoutManager(this, spanCount)
        recyclerView!!.layoutManager = gridManager
        recyclerView?.addItemDecoration(GridItemDecoration(spanCount, dip2px(8f), dip2px(8f)))
        //设置Item增加、移除动画
        //recyclerView.setItemAnimator(new NoAlphaItemAnimator());
        recyclerView!!.adapter = mAdapter


        mAdapter?.imageActionListener = object : ImageGridAdapter.ImageActionListener {

            override fun onItemClick(position: Int) {
                startActivity(PhotoViewPagerActivity.newInstance(this@MainActivity, position, mAdapter!!.dataList as ArrayList<String>))
            }

            override fun onItemDeleteClick(position: Int) {
                mAdapter!!.removeItem(position)
            }
        }
        mAdapter?.onFooterClick={
            //                ImagePicker.pickAvatar(this@MainActivity);
            ImagePicker.pickImage(this@MainActivity, 9)
//                ImagePicker.takePhoto(this@MainActivity,true);//使用摄像头
        }
    }


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == ImageSelectorActivity.REQUEST_IMAGE) {
            val pics = data!!.getSerializableExtra(ImageSelectorActivity.REQUEST_OUTPUT) as ArrayList<String>
            mAdapter!!.addData(pics)
        }
    }
}
