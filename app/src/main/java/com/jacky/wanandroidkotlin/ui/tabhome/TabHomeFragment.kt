package com.jacky.wanandroidkotlin.ui.tabhome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.chad.library.adapter.base.listener.OnLoadMoreListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseVMFragment
import com.jacky.wanandroidkotlin.databinding.FragmentTabHomeBinding
import com.jacky.wanandroidkotlin.model.entity.ArticleEntity
import com.jacky.wanandroidkotlin.model.entity.BannerEntity
import com.jacky.wanandroidkotlin.ui.adapter.HomeListAdapter
import com.jacky.wanandroidkotlin.ui.browser.BrowserActivity
import com.jacky.wanandroidkotlin.ui.girls.GirlsActivity
import com.jacky.wanandroidkotlin.ui.googlemavensearch.GoogleMavenSearchActivity
import com.jacky.wanandroidkotlin.ui.login.LoginActivity
import com.jacky.wanandroidkotlin.ui.music.MusicPlayActivity
import com.jacky.wanandroidkotlin.util.PreferenceUtil
import com.jacky.wanandroidkotlin.util.setOnAntiShakeClickListener
import com.jacky.wanandroidkotlin.widget.FloatPlayLayout
import com.jacky.wanandroidkotlin.wrapper.getView
import com.jacky.wanandroidkotlin.wrapper.glide.GlideBannerImageLoader
import com.jacky.wanandroidkotlin.wrapper.musicplay.*
import com.jacky.wanandroidkotlin.wrapper.recyclerview.CustomLoadMoreView
import com.jacky.wanandroidkotlin.wrapper.recyclerview.RecyclerFabScrollListener
import com.jacky.wanandroidkotlin.wrapper.recyclerview.updateLoadMoreStatus
import com.jacky.wanandroidkotlin.wrapper.viewClickListener
import com.jacky.wanandroidkotlin.wrapper.viewExt
import com.youth.banner.Banner
import com.youth.banner.BannerConfig
import com.zenchn.support.utils.AndroidKit
import com.zenchn.support.widget.VerticalItemDecoration

/**
 * @author:Hzj
 * @date  :2019/7/1/001
 * desc  ：首页Tab
 * record：
 */
class TabHomeFragment : BaseVMFragment<TabHomeViewModel>(), OnItemClickListener,
    OnItemChildClickListener, OnLoadMoreListener, AudioObserver {
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private val mHomeAdapter: HomeListAdapter by lazy { HomeListAdapter() }
    private var mPageNum = 0
    private val mBannerUrls = mutableListOf<String>()
    private val mIsLogin by PreferenceUtil(PreferenceUtil.KEY_IS_LOGIN, false)
    private var mBannerHome: Banner? = null
    private lateinit var binding: FragmentTabHomeBinding

    companion object {
        fun getInstance(): TabHomeFragment {
            val fragment = TabHomeFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayoutId(): Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //DataBinding添加绑定
        binding = DataBindingUtil.inflate<FragmentTabHomeBinding>(
            inflater,
            R.layout.fragment_tab_home,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MusicPlayManager.register(this)
        binding.vm = mViewModel
    }

    override fun initWidget() {
        super.initWidget()
        swipeRefreshLayout = getView<SwipeRefreshLayout>(R.id.swipe_refresh)
        initRecyclerView()
        initRefreshLayout()
        initFab()
        initFloatPlayer()
        onRefresh()
        swipeRefreshLayout.isRefreshing = true
    }

    private fun initFloatPlayer() {
        viewExt<FloatPlayLayout>(R.id.play_layout) {
            onFloatPlayClick {
                //跳转播放音乐界面
                activity?.let { MusicPlayActivity.launch(it) }
            }
            onPlayClick {
                //播放暂停控制
                MusicPlayManager.playOrPause()
            }
        }
    }

    private fun initFab() {
        val rlv = getView<RecyclerView>(R.id.rlv)
        val fabGoogleMavenSearch = getView<FloatingActionButton>(R.id.fab_google_maven_search)
        val fabGirl = getView<FloatingActionButton>(R.id.fab_girl)
        activity?.let { act ->
            viewClickListener(R.id.fab_google_maven_search) {
                GoogleMavenSearchActivity.launch(act)
            }
            viewClickListener(R.id.fab_girl) { GirlsActivity.launch(act) }
            rlv.addOnScrollListener(RecyclerFabScrollListener { visible ->
                fabGoogleMavenSearch.animate().apply {
                    if (visible) {
                        translationY(0F).interpolator = DecelerateInterpolator(3F)
                    } else {
                        val layoutParams =
                            fabGoogleMavenSearch.layoutParams as ConstraintLayout.LayoutParams
                        translationY((fabGoogleMavenSearch.height + layoutParams.bottomMargin).toFloat()).interpolator =
                            AccelerateInterpolator(3F)
                    }
                }
                fabGirl.animate().apply {
                    if (visible) {
                        translationY(0F).interpolator = DecelerateInterpolator(3F)
                    } else {
                        val layoutParams = fabGirl.layoutParams as ConstraintLayout.LayoutParams
                        translationY((fabGirl.height + layoutParams.bottomMargin).toFloat()).interpolator =
                            AccelerateInterpolator(3F)
                    }
                    viewExt<TextView>(R.id.bt_back_top) {
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
        swipeRefreshLayout.apply {
            setColorSchemeResources(R.color.colorAccent)
            setOnRefreshListener {
                //刷新数据
                onRefresh()
            }
        }
    }

    private fun onRefresh() {
        //下拉刷新时禁用加载更多
        mHomeAdapter.loadMoreModule.isEnableLoadMore = false
        mViewModel.getBanners()
        mPageNum = 0
        mViewModel.getArticleList(mPageNum)
    }

    override val startObserve: TabHomeViewModel.() -> Unit = {
        //LiveData 刷新数据到页面
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
        if (swipeRefreshLayout.isRefreshing) {
            swipeRefreshLayout.isRefreshing = false
        }
        mHomeAdapter.updateLoadMoreStatus(hasNextPage)
    }

    private fun initRecyclerView() {
        viewExt<RecyclerView>(R.id.rlv) {
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
            if (itemDecorationCount == 0) {
                addItemDecoration(
                    VerticalItemDecoration(
                        AndroidKit.Dimens.dp2px(10)
                    )
                )
            }
            adapter = mHomeAdapter.apply {
                setOnItemClickListener(this@TabHomeFragment)
                addChildClickViewIds(R.id.ibt_star)
                setOnItemChildClickListener(this@TabHomeFragment)
                loadMoreModule.setOnLoadMoreListener(this@TabHomeFragment)
                loadMoreModule.loadMoreView = CustomLoadMoreView()
                if (headerLayoutCount <= 0) {
                    //避免重复添加header
                    val header =
                        LayoutInflater.from(activity)
                            .inflate(R.layout.recycler_header_banner_home, this@viewExt, false)
                    mBannerHome = header.findViewById(R.id.banner_home)
                    addHeaderView(header)
                }
            }
        }
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        // 跳转详情
        //使用trace 性能分析
//        Debug.startMethodTracing("Van")
        val entity = adapter.data[position] as ArticleEntity
        activity?.let { BrowserActivity.launch(it, entity.link) }
//        Debug.stopMethodTracing()
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        when (view.id) {
            R.id.ibt_star -> {
                if (mIsLogin) {
                    //收藏
                    adapter.run {
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

    override fun onLoadMore() {
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
        swipeRefreshLayout.isRefreshing = false
    }

    override fun onApiFailure(msg: String) {
        if (swipeRefreshLayout.isRefreshing) {
            swipeRefreshLayout.isRefreshing = false
        }
        super.onApiFailure(msg)
    }

    override fun onAudioBean(audioBean: AudioBean) {
        mViewModel.albumId.set(audioBean.albumId)
        mViewModel.name.set(audioBean.name)
    }

    override fun onPlayerStatus(playStatus: Int) {
        val selected =
            playStatus == PlayerStatus.PLAY_START || playStatus == PlayerStatus.PLAY_RESUME
        mViewModel.playStatusSelected.set(selected)
    }

    override fun onProgress(currentDuration: Int, totalDuration: Int) {
    }

    override fun onPlayMode(playMode: Int) {
    }

    override fun onReset() {
    }

    override fun onDestroyView() {
        MusicPlayManager.unregister(this)
        super.onDestroyView()
    }
}