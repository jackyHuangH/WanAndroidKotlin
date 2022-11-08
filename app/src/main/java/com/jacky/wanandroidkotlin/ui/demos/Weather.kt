package com.jacky.wanandroidkotlin.ui.demos


import android.app.Activity
import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.gyf.immersionbar.ImmersionBar
import com.jacky.support.router.Router
import com.jacky.support.utils.LoggerKit
import com.jacky.support.utils.LunarUtils
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseVMActivity
import com.jacky.wanandroidkotlin.base.BaseViewModel
import com.jacky.wanandroidkotlin.databinding.ActivityWeatherBinding
import com.jacky.wanandroidkotlin.model.api.WanRetrofitClient
import com.jacky.wanandroidkotlin.model.api.dispatch
import com.jacky.wanandroidkotlin.model.entity.WeatherEntity
import com.jacky.wanandroidkotlin.ui.adapter.Weather15DaysAdapter
import com.jacky.wanandroidkotlin.util.StatusBarUtil
import com.jacky.wanandroidkotlin.util.setOnAntiShakeClickListener
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import java.util.*

/**
 * @author:Hzj
 * @date  :2022/10/27
 * desc  ：天气模块，自定义view绘制图表
 * record：
 */
class WeatherActivity : BaseVMActivity<ActivityWeatherBinding, WeatherViewModel>() {

    private val mWeather15Adapter by lazy { Weather15DaysAdapter() }

    override fun getLayoutId(): Int = R.layout.activity_weather

    companion object {

        fun launch(from: Activity) {
            Router
                .newInstance()
                .from(from)
                .to(WeatherActivity::class.java)
                .launch()
        }
    }

    override fun initStatusBar() {
        ImmersionBar.with(this).apply {
            transparentStatusBar()
            statusBarDarkFont(false)
            init()
        }
        StatusBarUtil.setStatusBarMargin(this, mViewBinding.ibtBack)
    }

    override fun initWidget() {
        //显示当前天气城市
        mViewBinding.tvCity.text="杭州"
        //显示当天日期，包含农历
        val calendar = Calendar.getInstance()
        val lunar = LunarUtils(calendar)
        val today = calendar.get(Calendar.DAY_OF_MONTH)
        mViewBinding.tvTodayDate.text =
            "${calendar.get(Calendar.MONTH) + 1}月${today}日 农历${lunar.toString()} ${lunar.getWeek(true)}"

        mViewBinding.ibtBack.setOnAntiShakeClickListener { onBackPressed() }
        mViewBinding.swipeRefresh.setOnRefreshListener { getWeatherData() }
        init15DaysRv()
        getWeatherData()
    }

    private fun init15DaysRv() {
        mViewBinding.rv15Days.apply {
            layoutManager=LinearLayoutManager(this@WeatherActivity,LinearLayoutManager.HORIZONTAL,false)
            setHasFixedSize(true)
            adapter=mWeather15Adapter
        }
    }

    private fun getWeatherData() {
        //实时天气
        mViewModel.getWeatherNow().observe(this) { data ->
            if (data?.code == 200) {
                data.now?.let {
                    mViewBinding.tvNowTemp.text = it.temp
                    mViewBinding.tvText.text = it.text
                }
            }
        }
        //未来15天天气
        mViewModel.getWeather15Days().observe(this) { data ->
            if (data?.code == 200) {
                data.daily?.let {
                    LoggerKit.d(it)
                    mWeather15Adapter.setList(it)
                }
            }
        }
    }

    override val startObserve: WeatherViewModel.() -> Unit = {
        mShowRefreshLayout.observe(this@WeatherActivity) {
            mViewBinding.swipeRefresh.isRefreshing = it
        }
    }

}

class WeatherViewModel(application: Application) : BaseViewModel(application) {
    val mTodayWeatherData: MutableLiveData<WeatherEntity> = MutableLiveData()
    val mShowRefreshLayout: MutableLiveData<Boolean> = MutableLiveData()

    //定位坐标，暂定杭州市
    private val location = "120.2524,30.2554"

    /**
     * 获取当天天气
     */
    fun getWeatherNow() = flow {
        val weatherTodayEntity = WanRetrofitClient.mService.getTodayWeather(location)
        emit(weatherTodayEntity)
    }.onStart {
        mShowRefreshLayout.value = true
    }.onCompletion {

    }.catch { throwable ->
        //处理异常
        throwable.dispatch(msgResult = { msg ->
            mErrorMsg.value = msg
        })
    }.asLiveData()

    /**
     * 获取最近15天天气
     */
    fun getWeather15Days() = flow {
        val weatherEntity = WanRetrofitClient.mService.getWeatherIn15Days(location)
        emit(weatherEntity)
    }.onStart {

    }.onCompletion {
        mShowRefreshLayout.value = false
    }.catch { throwable ->
        //处理异常
        throwable.dispatch(msgResult = { msg ->
            mErrorMsg.value = msg
        })
    }.asLiveData()
}