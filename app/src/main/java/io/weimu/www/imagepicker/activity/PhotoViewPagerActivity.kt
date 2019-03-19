package io.weimu.www.imagepicker.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v4.view.ViewPager
import android.view.View
import android.view.WindowManager
import com.shizhefei.view.largeimage.LargeImageView
import com.weimu.library.widget.DragViewPager
import com.weimu.universalib.ktx.getColorPro
import com.weimu.universalib.ktx.getStatusBarHeight
import com.weimu.universalview.core.activity.BaseActivity
import com.weimu.universalview.core.fragment.BaseFragment
import com.weimu.universalview.core.pager.BaseFragmentStatePagerAdapter
import com.weimu.universalview.core.toolbar.StatusBarManager
import com.weimu.universalview.core.toolbar.ToolBarManager
import com.weimu.universalview.ktx.setMargins
import io.weimu.www.imagepicker.R
import java.util.*
import kotlin.properties.Delegates

class PhotoViewPagerActivity : BaseActivity() {


    companion object {

        val POSITION = "position"
        val PICLIST = "piclist"

        fun newInstance(context: Context, position: Int, imagList: ArrayList<String>): Intent {
            val i = Intent(context, PhotoViewPagerActivity::class.java)
            i.putExtra(POSITION, position)
            i.putStringArrayListExtra(PICLIST, imagList)
            return i
        }
    }

    private val mViewPager: DragViewPager by lazy { findViewById<DragViewPager>(R.id.id_vp) }

    var isShowBar by Delegates.observable(false) { property, oldValue, newValue ->
        //todo是否显示
        if (newValue) {
            showStatusBar()
        } else {
            hideStatusBar()
        }
        if (newValue) {
            toolBarManager.showToolBar()
        } else {
            toolBarManager.hideToolBar()
        }
    }

    private var imagList: List<String> = ArrayList()
    private val fragments = ArrayList<BaseFragment>()
    private var position = 0
    private lateinit var mAdapter: ImagePagerAdapter

    private lateinit var toolBarManager: ToolBarManager


    override fun getLayoutResID(): Int = R.layout.activity_photo_view_pager

    override fun afterViewAttach(savedInstanceState: Bundle?) {
        initData()
        initToolBar()
        initViewPager()
    }


    private fun initData() {
        position = intent.getIntExtra(POSITION, 0)
        imagList = intent.getStringArrayListExtra(PICLIST)
    }


    private fun initToolBar() {
        StatusBarManager.setColor(window, getColorPro(R.color.white))
        StatusBarManager.setLightMode(window)
        toolBarManager = ToolBarManager.with(this, getContentView())
                .bg {
                    this.setBackgroundResource(com.weimu.library.R.color.white)
                }
                .leftMenuIcon {
                    this.setImageResource(com.weimu.library.R.drawable.toolbar_arrow_back_black)
                }
                .title {
                    this.text = "${(position + 1).toString() + "/" + imagList.size}"
                }
    }


    private fun initViewPager() {
        mViewPager.apply {
            for (i in imagList.indices) {
                fragments.add(ImagePreviewAppFragment.newInstance(imagList[i]))
            }
            mAdapter = ImagePagerAdapter(supportFragmentManager)
            this.adapter = mAdapter
            mAdapter.setFragments(fragments)
            this.currentItem = position
            this.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

                }

                override fun onPageSelected(position: Int) {
                    toolBarManager.title {
                        this.text = (position + 1).toString() + "/" + imagList.size
                    }
                }

                override fun onPageScrollStateChanged(state: Int) {

                }
            })
            //触发接口
            this.setIAnimClose(object : DragViewPager.IAnimClose {
                override fun onPictureClick() {

                }

                override fun onPictureRelease(view: View?) {
                    onBackPressed()
                }

            })


        }

    }


    private fun hideStatusBar() {
        val attrs = window.attributes
        attrs.flags = attrs.flags or WindowManager.LayoutParams.FLAG_FULLSCREEN
        window.attributes = attrs
    }

    private fun showStatusBar() {
        val attrs = window.attributes
        attrs.flags = attrs.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN.inv()
        window.attributes = attrs
    }


    class ImagePagerAdapter(fm: FragmentManager) : BaseFragmentStatePagerAdapter(fm), DragViewPager.DragViewAdapter {
        override fun getImageView(position: Int): LargeImageView = getItem(position).view!!.findViewById(R.id.iv_large)
    }


}
