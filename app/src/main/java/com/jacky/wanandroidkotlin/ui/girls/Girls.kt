package com.jacky.wanandroidkotlin.ui.girls

/**
 * @author:Hzj
 * @date  :2020-01-19
 * desc  ：
 * record：
 */
import androidx.fragment.app.FragmentActivity
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseVMActivity
import com.jacky.wanandroidkotlin.base.BaseViewModel
import com.zenchn.support.router.Router


class GirlsActivity : BaseVMActivity<GirlsViewModel>() {

    companion object {
        fun launch(from: FragmentActivity) {
            Router
                .newInstance()
                .from(from)
                .to(GirlsActivity::class.java)
                .launch()
        }
    }

    override fun provideViewModelClass(): Class<GirlsViewModel>? = GirlsViewModel::class.java

    override fun getLayoutRes(): Int = R.layout.activity_girls

    override fun initWidget() {

    }

    override fun startObserve() {}
}

class GirlsViewModel : BaseViewModel() {

}