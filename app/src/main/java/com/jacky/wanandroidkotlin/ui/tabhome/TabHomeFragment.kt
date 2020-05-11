package com.jacky.wanandroidkotlin.ui.tabhome

import android.os.Bundle
import android.os.Debug
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseVMFragment
import com.jacky.wanandroidkotlin.model.entity.ArticleEntity
import com.jacky.wanandroidkotlin.model.entity.BannerEntity
import com.jacky.wanandroidkotlin.ui.adapter.HomeListAdapter
import com.jacky.wanandroidkotlin.ui.browser.BrowserActivity
import com.jacky.wanandroidkotlin.ui.girls.GirlsActivity
import com.jacky.wanandroidkotlin.ui.login.LoginActivity
import com.jacky.wanandroidkotlin.util.PreferenceUtil
import com.jacky.wanandroidkotlin.util.setOnAntiShakeClickListener
import com.jacky.wanandroidkotlin.wrapper.glide.GlideBannerImageLoader
import com.jacky.wanandroidkotlin.wrapper.recyclerview.CustomLoadMoreView
import com.jacky.wanandroidkotlin.wrapper.recyclerview.RecyclerFabScrollListener
import com.youth.banner.Banner
import com.youth.banner.BannerConfig
import com.zenchn.support.utils.AndroidKit
import com.zenchn.support.widget.SpaceItemDecoration
import kotlinx.android.synthetic.main.fragment_tab_home.*

/**
 * @author:Hzj
 * @date  :2019/7/1/001
 * desc  ：首页Tab
 * record：
 */
class TabHomeFragment : BaseVMFragment<TabHomeViewModel>(), BaseQuickAdapter.OnItemClickListener,
    BaseQuickAdapter.OnItemChildClickListener, BaseQuickAdapter.RequestLoadMoreListener {

    private val mHomeAdapter: HomeListAdapter by lazy { HomeListAdapter() }
    private var mPageNum = 0
    private val mBannerUrls = mutableListOf<String>()
    private val mIsLogin by PreferenceUtil(PreferenceUtil.KEY_IS_LOGIN, false)
    private var mBannerHome: Banner? = null

    companion object {
        fun getInstance(): TabHomeFragment {
            val fragment = TabHomeFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayoutRes(): Int = R.layout.fragment_tab_home

    override fun lazyLoad() {
        Log.d("TabHome", "lazyLoad")
        //禁用滑动返回
        slidrInterface?.lock()
        initRecyclerView()
        initRefreshLayout()
        initFab()
        onRefresh()
        swipe_refresh.isRefreshing = true
    }

    private fun initFab() {
        activity?.let { act ->
            fab.setOnAntiShakeClickListener { GirlsActivity.launch(act) }
            rlv.addOnScrollListener(RecyclerFabScrollListener { visible ->
                fab.animate().apply {
                    if (visible) {
                        translationY(0F).interpolator = DecelerateInterpolator(3F)
                    } else {
                        val layoutParams = fab.layoutParams as ConstraintLayout.LayoutParams
                        translationY((fab.height + layoutParams.bottomMargin).toFloat()).interpolator =
                            AccelerateInterpolator(3F)
                    }
                    bt_back_top.apply {
                        visibility = if (visible) View.GONE else View.VISIBLE
                        setOnAntiShakeClickListener {
                            rlv.smoothScrollToPosition(0)
                            visibility = View.GONE
                        }
                    }
                }
            })
        }
    }

    private fun initRefreshLayout() {
        swipe_refresh.apply {
            setColorSchemeResources(R.color.colorAccent)
            setOnRefreshListener {
                //刷新数据
                onRefresh()
            }
        }
    }

    private fun onRefresh() {
        //下拉刷新时禁用加载更多
        mHomeAdapter.setEnableLoadMore(false)
        mViewModel.getBanners()
        mPageNum = 0
        mViewModel.getArticleList(mPageNum)
    }

    override fun startObserve() {
        //LiveData 刷新数据到页面
        mViewModel.apply {
            mBannerList.observe(this@TabHomeFragment, Observer {
                it?.let { setupBanner(it) }
            })
            mArticleList.observe(this@TabHomeFragment, Observer {
                it?.let {
                    if (mPageNum == 0) {
                        mHomeAdapter.setNewData(it.datas)
                    } else {
                        mHomeAdapter.addData(it.datas)
                    }
                    setLoadStatus(it.over.not())
                }
            })
            mErrorMsg.observe(this@TabHomeFragment, Observer {
                it?.let { onApiFailure(it) }
            })
        }
    }

    private fun setupBanner(bannerEntities: List<BannerEntity>) {
        val imgList = mutableListOf<String>()
        val titles = mutableListOf<String>()
        for (entity in bannerEntities) {
            imgList.add(entity.imagePath)
            titles.add(entity.title)
            mBannerUrls.add(entity.url)
        }
        //填充banner
        mBannerHome?.apply {
            setImageLoader(GlideBannerImageLoader())
            setBannerStyle(BannerConfig.LEFT)
            setImages(imgList)
            setBannerTitles(titles)
            setOnBannerListener { view, position ->
                // 跳转详情
                val url = mBannerUrls[position]
                activity?.let { BrowserActivity.launch(it, url) }
            }
            start()
        }
    }

    private fun setLoadStatus(hasNextPage: Boolean) {
        if (swipe_refresh.isRefreshing) {
            swipe_refresh.isRefreshing = false
        }
        if (hasNextPage) {
            mHomeAdapter.loadMoreComplete()
        } else {
            mHomeAdapter.loadMoreEnd()
        }
        mHomeAdapter.notifyDataSetChanged()
    }

    private fun initRecyclerView() {
        rlv.apply {
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
            if (itemDecorationCount == 0) {
                addItemDecoration(
                    SpaceItemDecoration(
                        AndroidKit.Dimens.dp2px(10)
                    )
                )
            }
        }
        mHomeAdapter.apply {
            onItemClickListener = this@TabHomeFragment
            onItemChildClickListener = this@TabHomeFragment
            setOnLoadMoreListener(this@TabHomeFragment, rlv)
            val header =
                LayoutInflater.from(activity)
                    .inflate(R.layout.recycle_header_banner_home, rlv, false)
            mBannerHome = header.findViewById(R.id.banner_home)
            addHeaderView(header)
            setLoadMoreView(CustomLoadMoreView())
            rlv.adapter = this
        }
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        // 跳转详情
        adapter?.run {
            //使用trace 性能分析
            Debug.startMethodTracing("Van")
            val entity = data[position] as ArticleEntity
            activity?.let { BrowserActivity.launch(it, entity.link) }
            Debug.stopMethodTracing()
        }
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        when (view?.id) {
            R.id.ibt_star -> {
                if (mIsLogin) {
                    //收藏
                    adapter?.run {
                        val entity = data[position] as ArticleEntity
                        entity.run {
                            collect = !collect
                            mViewModel.collectArticle(id, collect)
                        }
                        notifyDataSetChanged()
                    }
                } else {
                    //未登录，跳转登录
                    activity?.let { LoginActivity.launch(it) }
                }
            }
        }
    }

    override fun onLoadMoreRequested() {
        // 加载更多
        mPageNum++
        mViewModel.getArticleList(mPageNum)
    }

    override fun onResume() {
        super.onResume()
        mBannerHome?.startAutoPlay()
    }

    override fun onStop() {
        super.onStop()
        mBannerHome?.stopAutoPlay()
        swipe_refresh.isRefreshing = false
    }

    override fun onApiFailure(msg: String) {
        if (swipe_refresh.isRefreshing) {
            swipe_refresh.isRefreshing = false
        }
        super.onApiFailure(msg)
    }
}