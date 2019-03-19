package io.weimu.www.imagepicker.base

import android.os.Bundle
import com.weimu.universalview.core.architecture.mvp.BaseView
import com.weimu.universalview.core.fragment.BaseFragment

/**
 * Author:你需要一台永动机
 * Date:2018/3/8 11:15
 * Description:
 */

abstract class BaseViewFragment : BaseFragment(), BaseView {


    final override fun beforeViewAttachBaseViewAction(savedInstanceState: Bundle?) {
    }

    final override fun afterViewAttachBaseViewAction(savedInstanceState: Bundle?) {
    }


    override fun beforeViewAttach(savedInstanceState: Bundle?) {}

    override fun afterViewAttach(savedInstanceState: Bundle?) {}

}
