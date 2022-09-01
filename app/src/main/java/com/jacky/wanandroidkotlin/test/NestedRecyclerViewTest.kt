package com.jacky.wanandroidkotlin.test


import android.app.Application
import android.util.Log
import android.view.View
import android.view.ViewPropertyAnimator
import android.widget.ImageView
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.jacky.support.widget.behavior.BehavioralScrollListener
import com.jacky.support.widget.behavior.BehavioralScrollView
import com.jacky.support.widget.behavior.BottomSheetLayout
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseVMActivity
import com.jacky.wanandroidkotlin.base.BaseViewModel
import com.jacky.wanandroidkotlin.databinding.ActivityNestedBehaviorScrollBinding
import com.jacky.wanandroidkotlin.util.DisplayUtils
import com.jacky.wanandroidkotlin.wrapper.glide.GlideApp

/**
 * @author:Hzj
 * @date  :2022/8/30
 * desc  ：仿酷安应用详情页 bottomSheetBehavior联动滑动效果
 * record：参考：https://juejin.cn/post/6976443779762880549
 * https://juejin.cn/post/6854573214161436679（推荐）
 */
class NestedRecyclerViewTestActivity :
    BaseVMActivity<ActivityNestedBehaviorScrollBinding, NestedRecyclerViewTestViewModel>() {

    private val mPeekHeight by lazy { DisplayUtils.dp2px(50) }
    private var mAnimator: ViewPropertyAnimator? = null
    private var mHasInitFirstScrollY = false

    //第一种实现方式
//    override fun getLayoutId(): Int = R.layout.activity_nested_rv_test
    //第二种实现方式 behaviorScrollView
    override fun getLayoutId(): Int = R.layout.activity_nested_behavior_scroll

    override fun initWidget() {
        val testAdapter = TestPicAdapter()
//        mViewBinding.rvTop.apply {
//            layoutManager = LinearLayoutManager(this@NestedRecyclerViewTestActivity)
//            if (itemDecorationCount <= 0) {
//                addItemDecoration(VerticalItemDecoration(DisplayUtils.dp2px(5)))
//            }
//            adapter = testAdapter
//        }
        val picResList = listOf<Int>(
            R.drawable.bing0,
            R.drawable.bing1,
            R.drawable.bing2,
            R.drawable.bing3,
            R.drawable.bing4,
            R.drawable.bing5
        )
        val testAdapter2 = TestPicAdapter()
//        mViewBinding.scrollViewBottom.apply {
//            layoutManager = LinearLayoutManager(this@NestedRecyclerViewTestActivity)
//            if (itemDecorationCount <= 0) {
//                addItemDecoration(VerticalItemDecoration(DisplayUtils.dp2px(15)))
//            }
//            adapter = testAdapter2
//        }
        testAdapter.setList(picResList)
        testAdapter2.setList(picResList)
        nestedScrollViewWay2()
    }

    /**
     * 第一种实现方式，不太顺畅
     */
    private fun nestedScrollWay1() {
//        val bottomSheetBehavior = SNBBottomSheetBehavior.from<View>(mViewBinding.topScrollView)
//        //绑定behavior，实现联动滑动效果
//        mViewBinding.snbScrollView.post {
//            mViewBinding.snbScrollView.bindSNBBottomSheetBehavior(bottomSheetBehavior)
//        }
    }

    /**
     * 第二种方式，推荐，最接近酷安详情效果
     */
    private fun nestedScrollViewWay2() {
        //绑定顶部滑动内容
        mViewBinding.linkageScroll.topScrollTarget = {
            mViewBinding.scrollViewTop
//            mViewBinding.rvTop
        }

        //添加嵌套滑动监听
        mViewBinding.linkageScroll.listeners.add(object : BehavioralScrollListener {
            override fun onScrollChanged(v: BehavioralScrollView, from: Int, to: Int) {
                Log.d(javaClass.simpleName, "onScrollChanged:$from---to:$to")
                updateBottomSheet(false)
            }
        })
        //设置bottomSheet初始化
        mViewBinding.bottomSheet.setup(BottomSheetLayout.POSITION_MIN, mPeekHeight)
        var firstScrollY = 0
        mViewBinding.bottomSheet.listeners.add(object : BehavioralScrollListener {
            override fun onScrollChanged(v: BehavioralScrollView, from: Int, to: Int) {
                if (!mHasInitFirstScrollY) {
                    firstScrollY = to
                    Log.d(javaClass.simpleName, "bottomSheet firstScrollY:$firstScrollY")
                    mHasInitFirstScrollY = true
                }
                //向上滑动，下滑fab，否则恢复fab
                val dy = to - firstScrollY
                if (dy > 0) {
                    moveFab(mPeekHeight.toFloat())
                } else {
                    moveFab(0F)
                }
                Log.d(javaClass.simpleName, "bottomSheet onScrollChanged:$to")
            }
        })
        //初始化BottomSheet状态
        updateBottomSheet(true)
    }

    /**
     * 滑动过程更新bottomsheet状态，当向上滑动到联动时，隐藏bottomsheet，向下滑动解除联动时，显示bottomsheet
     * bottomsheet作为 bottomLayout的容器
     */
    private fun updateBottomSheet(init: Boolean) {
        val linkageScrollY = mViewBinding.linkageScroll.scrollY
        Log.d(javaClass.simpleName, "linkageScrollY:$linkageScrollY")
        if (mViewBinding.bottomSheet.indexOfChild(mViewBinding.scrollViewBottom) >= 0) {
            //已经添加layoutBottom到容器时
            //当linkageScrollY>=peekHeight时，触发联动滑动
            if (linkageScrollY >= mPeekHeight) {
                if (mViewBinding.bottomSheet.indexOfChild(mViewBinding.scrollViewBottom) >= 0) {
                    mViewBinding.bottomSheet.removeView(mViewBinding.scrollViewBottom)
                }
                mViewBinding.bottomSheet.visibility = View.GONE
                if (mViewBinding.layoutBottom.indexOfChild(mViewBinding.scrollViewBottom) < 0) {
                    mViewBinding.layoutBottom.addView(mViewBinding.scrollViewBottom)
                }
                //绑定底部滑动内容
                mViewBinding.linkageScroll.bottomScrollTarget = { mViewBinding.scrollViewBottom }

                if (!init) {
                    //移动fab按钮
                    moveFab(mPeekHeight.toFloat())
                }
            }
        } else {
            //未将layoutBottom添加容器
            //当linkageScrollY<peekHeight时，解除联动滑动
            if (linkageScrollY < mPeekHeight) {
                //移除底部滑动内容
                mViewBinding.linkageScroll.bottomScrollTarget = null
                if (mViewBinding.layoutBottom.indexOfChild(mViewBinding.scrollViewBottom) >= 0) {
                    mViewBinding.layoutBottom.removeView(mViewBinding.scrollViewBottom)
                }
                if (mViewBinding.bottomSheet.indexOfChild(mViewBinding.scrollViewBottom) < 0) {
                    mViewBinding.bottomSheet.addView(mViewBinding.scrollViewBottom)
                }
                mViewBinding.bottomSheet.visibility = View.VISIBLE
                if (!init) {
                    //移动回到原位fab按钮
                    moveFab(0F)
                }
            }
        }
    }

    /**
     * 移动fab按钮
     */
    private fun moveFab(distance: Float) {
        Log.d(javaClass.simpleName, "moveFab:$distance")
        mAnimator = mViewBinding.fab.animate().apply {
            translationY(distance)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        mAnimator?.cancel()
    }

    override val startObserve: NestedRecyclerViewTestViewModel.() -> Unit = {

    }


}

class TestPicAdapter :
    BaseQuickAdapter<Int, BaseViewHolder>(layoutResId = R.layout.recycler_item_test) {

    override fun convert(holder: BaseViewHolder, item: Int) {
        val position = holder.bindingAdapterPosition

        GlideApp.with(context)
            .load(item)
            .transition(DrawableTransitionOptions.withCrossFade())
            .transform(CenterCrop(), RoundedCorners(DisplayUtils.dp2px(5)))
            .error(R.drawable.ic_pic_error)
            .into(holder.getView(R.id.iv_pic) as ImageView)
    }
}

class NestedRecyclerViewTestViewModel(application: Application) : BaseViewModel(application) {

}