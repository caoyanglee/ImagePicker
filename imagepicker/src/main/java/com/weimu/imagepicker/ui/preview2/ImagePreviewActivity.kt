package com.weimu.imagepicker.ui.preview2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.FragmentManager
import android.support.v4.app.SharedElementCallback
import android.view.View
import com.rd.animation.type.AnimationType
import com.shizhefei.view.largeimage.LargeImageView
import com.weimu.imagepicker.R
import com.weimu.universalview.core.activity.BaseActivity
import com.weimu.universalview.core.pager.BaseFragmentStatePagerAdapter
import com.weimu.universalview.interfaces.MyViewPagerChangeListener
import kotlinx.android.synthetic.main.activity_image_preview_v2.*


class ImagePreviewActivity : BaseActivity() {

    override fun getLayoutResID() = R.layout.activity_image_preview_v2

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

    override fun beforeViewAttach(savedInstanceState: Bundle?) {
        super.beforeViewAttach(savedInstanceState)
        position = intent.getIntExtra(IMAGE_INDEX, 0)
        imageList = intent.getStringArrayListExtra(IMAGE_LIST)
        smallPicList = intent.getStringArrayListExtra(IMAGE_INDEX_SMALL) ?: ArrayList()
    }

    override fun afterViewAttach(savedInstanceState: Bundle?) {
        super.afterViewAttach(savedInstanceState)
        initViewPager()
        initPagerIndicator()
        initShareElement()
    }

    private fun initPagerIndicator() {
        pageIndicatorView.count = imageList.size
        pageIndicatorView.setSelected(position)
        pageIndicatorView.radius = 4
        pageIndicatorView.setAnimationType(AnimationType.SLIDE)
    }

    private fun initShareElement() {
        setEnterSharedElementCallback(object : SharedElementCallback() {
            override fun onMapSharedElements(names: MutableList<String>?, sharedElements: MutableMap<String, View>?) {
                val view = mAdapter.getItem(viewpager.currentItem).view?.findViewById<View>(R.id.iv_large)!!
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

        viewpager.apply {
            this.offscreenPageLimit = 1
            this.adapter = mAdapter
            mAdapter.setFragments(fragments)
            this.currentItem = position
            //滚动监听
            this.addOnPageChangeListener(object : MyViewPagerChangeListener() {
                override fun onPageSelected(position: Int) {
                    pageIndicatorView.setSelected(position)
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


    class ImagePagerAdapter(fm: FragmentManager) : BaseFragmentStatePagerAdapter(fm), DragViewPager.DragViewAdapter {
        override fun getImageView(position: Int): LargeImageView = getItem(position).view!!.findViewById(R.id.iv_large)
    }

    //动画返回
    fun transitionFinish() {
        val intent = Intent()
        val bundle = Bundle()
        bundle.putInt(IMAGE_INDEX, viewpager.currentItem)
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
