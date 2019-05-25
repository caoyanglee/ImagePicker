package io.weimu.www.imagepicker.ui

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import com.pmm.imagepicker.Config
import com.pmm.imagepicker.ImagePicker
import com.pmm.imagepicker.ui.preview2.ImagePreviewActivity
import com.weimu.universalview.core.recyclerview.decoration.GridItemDecoration
import com.weimu.universalview.core.toolbar.StatusBarManager
import com.weimu.universalview.ktx.dip2px
import com.weimu.universalview.ktx.getColorPro
import com.weimu.universalview.ktx.requestPermission
import io.weimu.www.imagepicker.R
import io.weimu.www.imagepicker.fragment.adapter.ImageGridAdapter
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val maxImageNumber = 9
    private val spanCount = 4

    private val mAdapter: ImageGridAdapter by lazy {
        ImageGridAdapter(this, maxImageNumber).apply {
            //其他操作
            this.imageActionListener = object : ImageGridAdapter.ImageActionListener {

                override fun onItemClick(position: Int) {
                    ImagePreviewActivity.start(this@MainActivity, this@apply.dataList, position)
                }

                override fun onItemDeleteClick(position: Int) {
                    this@apply.removeItem(position)
                }
            }
            //添加
            this.onFooterClick = {
//                ImagePicker.pickAvatar(this@MainActivity);
                ImagePicker.pickImage(this@MainActivity, 9)
//                ImagePicker.pickImage4One(activity = this@MainActivity)
//                ImagePicker.takePhoto(this@MainActivity, true);//使用摄像头


//                ImagePicker.custom(this@MainActivity, Config().apply {
//                    selectMode = Config.MODE_SINGLE
//                    enableCrop = true
//                    cropAspectRatioX = 1
//                    cropAspectRatioY = 1
//                })
            }
        }
    }

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
        this.id_RecyclerView.apply {
            this.adapter = mAdapter
            this.layoutManager = GridLayoutManager(this@MainActivity, spanCount)
            this.addItemDecoration(GridItemDecoration(spanCount, dip2px(8f), dip2px(8f)))
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == ImagePicker.REQUEST_IMAGE) {
            val target = data?.getSerializableExtra(ImagePicker.REQUEST_OUTPUT) as List<String>
            mAdapter.addData(target)
        }
    }
}
