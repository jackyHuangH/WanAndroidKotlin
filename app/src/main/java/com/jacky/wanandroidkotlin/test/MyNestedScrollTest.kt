package com.jacky.wanandroidkotlin.test

/**
 * @author:Hzj
 * @date  :2022/9/2
 * desc  ：自定义实现酷安应用详情嵌套滚动，存在诸多问题：fling状态未处理，layoutBottom未处理吸顶
 * record：
 */
import android.app.Application
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.jacky.support.widget.VerticalItemDecoration
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseVMActivity
import com.jacky.wanandroidkotlin.base.BaseViewModel
import com.jacky.wanandroidkotlin.common.PIC_RES_LIST
import com.jacky.wanandroidkotlin.databinding.ActivityMyNestedSheetBinding
import com.jacky.wanandroidkotlin.util.DisplayUtils
import com.jacky.wanandroidkotlin.util.setClipViewCornerRadius


class MyNestedScrollTestActivity :
    BaseVMActivity<ActivityMyNestedSheetBinding, MyNestedScrollTestViewModel>() {

    private var mThresholdY = 0

    override fun getLayoutId(): Int = R.layout.activity_my_nested_sheet

    override fun initWidget() {
        initRv()
        //裁剪bottomlayout圆角
        mViewBinding.scrollBottom.setClipViewCornerRadius(25)
        //计算top layout整体高度
        mViewBinding.layoutTop.measure(0, 0)
        val topLayoutH = mViewBinding.layoutTop.measuredHeight
        val topDy = topLayoutH - DisplayUtils.screenHeight()
        val peekH = DisplayUtils.dp2px(54)
        //联动阈值
        mThresholdY = topDy + peekH
        Log.d(javaClass.simpleName, "mThresholdY:$mThresholdY")
        //监听整体ScrollView滑动
        mViewBinding.scrollView.mHScrollListener = { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            updateNestedScrollState()
        }
        updateNestedScrollState()
    }

    private fun initRv() {
        val testAdapter = TestPicAdapter()
        mViewBinding.scrollBottom.apply {
            layoutManager = LinearLayoutManager(this@MyNestedScrollTestActivity)
            if (itemDecorationCount <= 0) {
                addItemDecoration(VerticalItemDecoration(DisplayUtils.dp2px(5)))
            }
            adapter = testAdapter
        }
        testAdapter.setList(PIC_RES_LIST)
    }

    private fun updateNestedScrollState() {
        val scrollY = mViewBinding.scrollView.scrollY

        if (mViewBinding.bottomSheet.indexOfChild(mViewBinding.scrollBottom) >= 0) {
            if (scrollY >= mThresholdY) {
                if (mViewBinding.bottomSheet.indexOfChild(mViewBinding.scrollBottom) >= 0) {
                    mViewBinding.bottomSheet.removeView(mViewBinding.scrollBottom)
                }
                if (mViewBinding.layoutBottom.indexOfChild(mViewBinding.scrollBottom) < 0) {
                    mViewBinding.layoutBottom.addView(mViewBinding.scrollBottom)
                }
                mViewBinding.bottomSheet.visibility = View.GONE
            }
        } else {
            if (scrollY < mThresholdY) {
                if (mViewBinding.layoutBottom.indexOfChild(mViewBinding.scrollBottom) >= 0) {
                    mViewBinding.layoutBottom.removeView(mViewBinding.scrollBottom)
                }
                if (mViewBinding.bottomSheet.indexOfChild(mViewBinding.scrollBottom) < 0) {
                    mViewBinding.bottomSheet.addView(mViewBinding.scrollBottom)
                }
                mViewBinding.bottomSheet.visibility = View.VISIBLE
            }
        }
    }

    override val startObserve: MyNestedScrollTestViewModel.() -> Unit = {

    }
}

class MyNestedScrollTestViewModel(application: Application) : BaseViewModel(application) {

}