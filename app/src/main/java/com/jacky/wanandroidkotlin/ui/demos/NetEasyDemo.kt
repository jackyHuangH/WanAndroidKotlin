package com.jacky.wanandroidkotlin.ui.demos

/**
 * @author:Hzj
 * @date  :2021/11/22
 * desc  ：仿网易云歌单 pager
 * record：
 */
import android.app.Application
import android.graphics.Matrix
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.SeekBar
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseVMActivity
import com.jacky.wanandroidkotlin.base.BaseViewModel
import com.jacky.wanandroidkotlin.test.ADashboard
import com.jacky.wanandroidkotlin.wrapper.viewExt


class NetEasyDemoActivity : BaseVMActivity<NetEasyDemoViewModel>() {

    private lateinit var mViewGroup: RelativeLayout
    private lateinit var mV1: View

    companion object {
        const val TAG = "NetEase"
    }

    override fun getLayoutId(): Int = R.layout.activity_net_easy

    override fun initWidget() {
        mViewGroup = findViewWithId(R.id.vg)
        mV1 = findViewWithId<View>(R.id.v1)
        viewExt<SeekBar>(R.id.seekBar) {
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    mV1.rotation = progress.toFloat() * 3.6F
                    mV1.translationX=-progress.toFloat()*2
                    mV1.translationY=-progress.toFloat()*2
                    val points:FloatArray = FloatArray(2)
                    getViewMatrix(mV1)?.mapPoints(points)
                    Log.d(TAG,"$points")
                    showMessage("蓝点在View中吗？${if (pointInView(mV1, points)) "在在在" else "不不不不"}")
                    //动态设置进度
                    viewExt<ADashboard>(R.id.a_dash){
                        this.updateProgress(progress/100.toFloat())
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }

            })
        }

    }

    /**
     * 反射调用方法
     */
    private fun getViewMatrix(view: View): Matrix? {
        try {
            val declaredMethod = View::class.java.getDeclaredMethod("getInverseMatrix")
            declaredMethod.isAccessible = true
            return declaredMethod.invoke(view) as? Matrix
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun pointInView(view: View, points: FloatArray): Boolean {
        try {
            val declaredMethod = View::class.java.getDeclaredMethod(
                "pointInView", Float::class.java, Float::class.java, Float::class.java)
            declaredMethod.isAccessible = true
            return declaredMethod.invoke(view, points[0], points[1],0F) as Boolean
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * AddView比attach更安全，内部做了重复添加校验，但是效率更低
     */
  /*  private fun moveToTop(target: View) {
        //先确定现在在哪个位置
        val startIndex = mViewGroup.indexOfChild(target)
        //计算一共要切换几次，就可以到达顶部
        val count = mViewGroup.childCount - 1 - startIndex
        for (i in 0..count) {
            Log.d(TAG, "startIndex:$startIndex")

            //更新索引
            val fromIndex = mViewGroup.indexOfChild(target)
            //目标是它的上层
            val toIndex = fromIndex + 1
            //获取需要交换位置的2个View
            val fromView = target
            val toView = mViewGroup.getChildAt(toIndex)

            //先把2个view拿出来
//            mViewGroup.detachViewFromParent(toIndex)
//            mViewGroup.detachViewFromParent(fromIndex)-
            mViewGroup.removeViewAt(toIndex)
            mViewGroup.removeViewAt(fromIndex)

            //再把2个view放进去,索引互换
//            mViewGroup.attachViewToParent(toView, fromIndex, toView.layoutParams)
//            mViewGroup.attachViewToParent(fromView, toIndex, fromView.layoutParams)
            mViewGroup.addView(toView, fromIndex, toView.layoutParams)
            mViewGroup.addView(fromView, toIndex, fromView.layoutParams)
        }
        //刷新
        mViewGroup.invalidate()
    }
*/
    override val startObserve: NetEasyDemoViewModel.() -> Unit = {

    }
}

class NetEasyDemoViewModel(application: Application) : BaseViewModel(application) {

}