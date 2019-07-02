package com.jacky.wanandroidkotlin.ui.tabhome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseVMFragment
import com.jacky.wanandroidkotlin.ui.adapter.HomeListAdapter
import com.jacky.wanandroidkotlin.wrapper.glide.GlideBannerImageLoader
import com.jacky.wanandroidkotlin.wrapper.recyclerview.CustomLoadMoreView
import com.jacky.wanandroidkotlin.wrapper.recyclerview.SpaceItemDecoration
import com.youth.banner.BannerConfig
import com.zenchn.support.kit.AndroidKit
import kotlinx.android.synthetic.main.fragment_tab_home.*
import kotlinx.android.synthetic.main.recycle_header_banner_home.*

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

    companion object {
        fun getInstance(): TabHomeFragment {
            val fragment = TabHomeFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun provideViewModelClass(): Class<TabHomeViewModel>? = TabHomeViewModel::class.java

    override fun lazyLoad() {
    }

    override fun getLayoutRes(): Int = R.layout.fragment_tab_home

    override fun initWidget() {
        initRecyclerView()
        initRefreshLayout()
        onRefresh()
    }

    private fun initRefreshLayout() {
        swipe_refresh.setColorSchemeResources(R.color.colorAccent)
        swipe_refresh.setOnRefreshListener {
            //刷新数据
            onRefresh()
        }
    }

    private fun onRefresh() {
        //下拉刷新时禁用加载更多
        mHomeAdapter.setEnableLoadMore(false)
        swipe_refresh.isRefreshing = true
        mViewModel.getBanners()
        mPageNum = 0
        mViewModel.getArticleList(mPageNum)
    }

    override fun startObserve() {
        //LiveData绑定数据到页面
        mViewModel.apply {
            mBannerList.observe(this@TabHomeFragment, Observer {
                it?.let {
                    val imgList = mutableListOf<String>()
                    for (entity in it) {
                        imgList.add(entity.imagePath)
                        mBannerUrls.add(entity.url)
                    }
                    setupBanner(imgList)
                }
            })
            mArticleList.observe(this@TabHomeFragment, Observer {
                it?.let {
                    if (mPageNum == 0) {
                        mHomeAdapter.setNewData(it.datas)
                    } else {
                        mHomeAdapter.addData(it.datas)
                    }
                    setLoadStatus(!it.over)
                }
            })
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
        rlv.layoutManager = LinearLayoutManager(activity)
        rlv.setHasFixedSize(true)
        rlv.addItemDecoration(SpaceItemDecoration(AndroidKit.Dimens.dp2px(10)))
        mHomeAdapter.onItemClickListener = this
        mHomeAdapter.onItemChildClickListener = this
        mHomeAdapter.setOnLoadMoreListener(this, rlv)
        val header = LayoutInflater.from(activity).inflate(R.layout.recycle_header_banner_home, rlv, false)
        mHomeAdapter.addHeaderView(header)
        mHomeAdapter.setLoadMoreView(CustomLoadMoreView())
        rlv.adapter = mHomeAdapter
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        //todo 跳转详情
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        if (view?.id == R.id.ibt_star) {
            //todo 点赞

        }
    }

    override fun onLoadMoreRequested() {
        // 加载更多
        mPageNum++
        mViewModel.getArticleList(mPageNum)
    }

    private fun setupBanner(bannerUrls: List<String>) {
        //填充banner
        banner_home.setImageLoader(GlideBannerImageLoader())
        banner_home.setBannerStyle(BannerConfig.LEFT)
        banner_home.setImages(bannerUrls)
        banner_home.setOnBannerListener { view, position ->
            //todo 跳转详情

        }
        banner_home.start()
    }

    override fun onResume() {
        super.onResume()
        banner_home?.startAutoPlay()
    }

    override fun onStop() {
        super.onStop()
        banner_home?.stopAutoPlay()
    }

    override fun onApiFailure(msg: String) {
        super.onApiFailure(msg)
        swipe_refresh.isRefreshing = false
    }
}