package io.weimu.www.imagepicker.widget

import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import com.weimu.universalib.origin.BaseB
import com.weimu.universalview.core.dialog.BottomUpDialog
import com.weimu.universalview.core.recyclerview.BaseRecyclerAdapter
import com.weimu.universalview.core.recyclerview.BaseRecyclerViewHolder
import io.weimu.www.imagepicker.R
import kotlinx.android.synthetic.main.dialog_menu.*
import kotlinx.android.synthetic.main.list_item_member_operaion.view.*

/**
 * Author:你需要一台永动机
 * Date:2018/5/2 10:51
 * Description:通用的菜单弹窗
 */
class MenuDialog : BottomUpDialog() {


    override fun getTagName() = "menu"


    private var dataList: MutableLiveData<ArrayList<String>> = MutableLiveData()
    var onMenuClick: ((position: Int) -> Unit)? = null
    var onMenuClickV2: ((str: String) -> Unit)? = null


    fun transmitMenu(dataList: ArrayList<String>): MenuDialog {
        this.dataList.value = dataList
        return this
    }


    override fun getLayoutResID(): Int = R.layout.dialog_menu


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        this.tv_cancel.setOnClickListener {
            dismiss()
        }
        //adapter
        val mAdapter = MenuAdapter(context!!)

        //list
        this.mRecyclerView.itemAnimator = DefaultItemAnimator()//设置Item增加、移除动画
        this.mRecyclerView.layoutManager = GridLayoutManager(context, 1)
        this.mRecyclerView.adapter = mAdapter
        dataList.observeForever {
            mAdapter.setDataToAdapter(it)
        }

        mAdapter.onItemClick = { item, position ->
            dismiss()
            onMenuClick?.invoke(position)
            onMenuClickV2?.invoke(item)
        }

    }


    inner class MenuAdapter(mContext: Context) : BaseRecyclerAdapter<BaseB, String>(mContext) {
        override fun getItemLayoutRes() = R.layout.list_item_member_operaion

        override fun itemViewChange(holder: BaseRecyclerViewHolder, position: Int) {
            val item = getItem(position)
            holder.itemView.apply {
                this.tv_action.text = item
            }
        }

    }
}