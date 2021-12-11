package com.pmm.imagepicker.ui.preview2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.app.SharedElementCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.pmm.imagepicker.R
import com.pmm.imagepicker.databinding.ActivityImagePreviewV2Binding
import com.pmm.ui.core.StatusNavigationBar
import com.pmm.ui.core.activity.BaseActivityV2
import com.pmm.ui.core.pager.BaseFragmentStatePagerAdapter
import com.pmm.ui.interfaces.MyViewPagerChangeListener
import com.rd.animation.type.AnimationType
import kotlin.math.max


class ImagePreviewActivity : BaseActivityV2(R.layout.activity_image_preview_v2) {
    private val mVB by viewBinding(ActivityImagePreviewV2Binding::bind, R.id.container)

    private lateinit var smallPicList: ArrayList<String>
    private var imageList: ArrayList<String> = ArrayList()
    private val fragments = ArrayList<ImagePreviewFragment>()
    private var position = 0

    private val mAdapter: ImagePagerAdapter by lazy { ImagePagerAdapter(supportFragmentManager) }


    companion object {

        const val IMAGE_LIST = "image_list"
        const val IMAGE_INDEX = "image_index"
        const val IMAGE_INDEX_SMALL = "image_index_small"


        fun start(context: Activity, imageList: ArrayList<String>, position: Int = 0) {
            val intent = Intent(context, ImagePreviewActivity::class.java)
            intent.putExtra(IMAGE_LIST, imageList)
            intent.putExtra(IMAGE_INDEX, position)
            context.startActivity(intent)
            context.overridePendingTransition(R.anim.fade_in, -1)
        }


        //小图
        fun startWithSmall(
                context: Activity,
                imageList: ArrayList<String>,
                smallList: ArrayList<String>,
                position: Int = 0
        ) {
            val intent = Intent(context, ImagePreviewActivity::class.java)
            intent.putExtra(IMAGE_LIST, imageList)
            intent.putExtra(IMAGE_INDEX_SMALL, smallList)
            intent.putExtra(IMAGE_INDEX, position)
            context.startActivity(intent)
            context.overridePendingTransition(R.anim.fade_in, -1)
        }

        //共享元素
        fun startWithAnim(
                context: Activity,
                imageList: ArrayList<String>,
                smallList: ArrayList<String>,
                position: Int = 0,
                view: View?
        ) {
            val intent = Intent(context, ImagePreviewActivity::class.java)
            intent.putExtra(IMAGE_LIST, imageList)
            intent.putExtra(IMAGE_INDEX_SMALL, smallList)
            intent.putExtra(IMAGE_INDEX, position)
            val compat: ActivityOptionsCompat
            if (view == null) {
                compat = ActivityOptionsCompat.makeSceneTransitionAnimation(context)
            } else {
                compat = ActivityOptionsCompat.makeSceneTransitionAnimation(context, view, "img")
            }
            ActivityCompat.startActivity(context, intent, compat.toBundle());
        }
    }

    override fun beforeSuperCreate(savedInstanceState: Bundle?) {
        StatusNavigationBar.setStatusNavigationBarTransparent(window)
    }

    override fun beforeViewAttach(savedInstanceState: Bundle?) {
        super.beforeViewAttach(savedInstanceState)
        position = intent.getIntExtra(IMAGE_INDEX, 0)
        imageList = intent.getStringArrayListExtra(IMAGE_LIST) ?: arrayListOf()
        smallPicList = intent.getStringArrayListExtra(IMAGE_INDEX_SMALL) ?: ArrayList()
    }

    override fun afterViewAttach(savedInstanceState: Bundle?) {
        super.afterViewAttach(savedInstanceState)
        initViewPager()
        initPagerIndicator()
        initShareElement()
    }

    private fun initPagerIndicator() {
        mVB.pageIndicatorView.apply {
            this.count = imageList.size
            this.setSelected(position)
            this.radius = 4
            this.setAnimationType(AnimationType.SLIDE)
        }
    }

    private fun initShareElement() {
        setEnterSharedElementCallback(object : SharedElementCallback() {
            override fun onMapSharedElements(names: MutableList<String>?, sharedElements: MutableMap<String, View>?) {
                val view = mAdapter.getItem(mVB.viewpager.currentItem).requireView().findViewById<View>(R.id.iv_large)!!
                sharedElements?.clear()
                sharedElements?.put("img", view)
            }
        })
    }

    private fun initViewPager() {
        if (smallPicList.size > 0) {
            for (i in imageList.indices) {
                fragments.add(ImagePreviewFragment.newInstance(imageList[i], smallPicList[i]))
            }
        } else {
            for (i in imageList.indices) {
                fragments.add(ImagePreviewFragment.newInstance(imageList[i]))
            }
        }

        mVB.viewpager.apply {
            this.offscreenPageLimit = 1
            this.adapter = mAdapter
            this.currentItem = position
            //滚动监听
            this.addOnPageChangeListener(object : MyViewPagerChangeListener() {
                override fun onPageSelected(position: Int) {
                    mVB.pageIndicatorView.setSelected(position)
                }

            })
            //触发接口
            this.setIAnimClose(object : DragViewPager.IAnimClose {
                override fun onPictureClick() {
                    transitionFinish()
                }

                override fun onPictureRelease(view: View?) {
                    transitionFinish()
                }

            })
        }
    }


    inner class ImagePagerAdapter(fm: FragmentManager) : BaseFragmentStatePagerAdapter(fm), DragViewPager.DragViewAdapter {

        override fun getItem(position: Int): Fragment {
            if (smallPicList.size > 0) {
                return ImagePreviewFragment.newInstance(imageList[position], smallPicList[position])
            } else {
                return ImagePreviewFragment.newInstance(imageList[position])
            }
        }

        override fun getCount(): Int = max(smallPicList.size, imageList.size)

        override fun getItemView(position: Int): ViewGroup = getFragment(position).requireView().findViewById(R.id.container)!!

    }

    //动画返回
    fun transitionFinish() {
        val intent = Intent()
        val bundle = Bundle()
        bundle.putInt(IMAGE_INDEX, mVB.viewpager.currentItem)
        intent.putExtras(bundle)
        setResult(Activity.RESULT_OK, intent)
        finish()
        overridePendingTransition(-1, R.anim.fade_out)
        //ActivityCompat.finishAfterTransition(this)
    }

    override fun onBackPressed() {
        transitionFinish()
    }
}
