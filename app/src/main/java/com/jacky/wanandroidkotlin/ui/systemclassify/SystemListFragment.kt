package com.jacky.wanandroidkotlin.ui.systemclassify

import android.app.Application
import android.os.Bundle
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseVMFragment
import com.jacky.wanandroidkotlin.base.BaseViewModel
import com.jacky.wanandroidkotlin.base.executeRequest
import com.jacky.wanandroidkotlin.databinding.FragmentSystemListBinding
import com.jacky.wanandroidkotlin.model.entity.ArticleEntity
import com.jacky.wanandroidkotlin.model.entity.ArticleList
import com.jacky.wanandroidkotlin.model.repositry.SystemRepository
import com.jacky.wanandroidkotlin.ui.adapter.HomeListAdapter
import com.jacky.wanandroidkotlin.ui.browser.BrowserActivity
import com.jacky.wanandroidkotlin.ui.login.LoginActivity
import com.jacky.wanandroidkotlin.util.PreferenceUtil
import com.jacky.wanandroidkotlin.wrapper.recyclerview.CustomLoadMoreView
import com.jacky.wanandroidkotlin.wrapper.recyclerview.updateLoadMoreStatus
import com.zenchn.support.utils.AndroidKit
import com.zenchn.support.utils.LoggerKit
import com.zenchn.support.widget.VerticalItemDecoration

/**
 * @author:Hzj
 * @date  :2019/7/5/005
 * desc  ：体系列表 ,公众号分类页面共用
 * record：
 */
class SystemListFragment : BaseVMFragment<FragmentSystemListBinding, SystemListViewModel>(),
    OnItemClickListener, OnItemChildClickListener {

    private val mHomeAdapter by lazy { HomeListAdapter() }
    private var mPageNum = 0
    private val mIsLogin by PreferenceUtil(PreferenceUtil.KEY_IS_LOGIN, false)

    override fun getLayoutId(): Int = R.layout.fragment_system_list

    companion object {
        private const val EXTRA_CID = "EXTRA_CID"
        private const val EXTRA_IS_BLOG = "EXTRA_IS_BLOG"

        @JvmStatic
        fun getInstance(cid: Int, isBlog: Boolean): SystemListFragment {
            val fragment = SystemListFragment()
            val bundle = Bundle()
            bundle.putInt(EXTRA_CID, cid)
            bundle.putBoolean(EXTRA_IS_BLOG, isBlog)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun initWidget() {
        initRecyclerView()
        initRefreshLayout()
        getData()
    }

    private fun initRefreshLayout() {
        mViewBinding.swipeRefresh.setColorSchemeResources(R.color.colorAccent)
        mViewBinding.swipeRefresh.setOnRefreshListener {
            //刷新数据
            getData()
        }
    }

    private fun getData(refresh: Boolean = true) {
        if (refresh) {
            mPageNum = 0
            mViewBinding.swipeRefresh.isRefreshing = true
        } else {
            // 加载更多
            mPageNum++
        }
        val cid = arguments?.getInt(EXTRA_CID) ?: 0
        val isBlog = arguments?.getBoolean(EXTRA_IS_BLOG, false) ?: false
        // 获取公众号/博客文章
        mViewModel.getSystemArticleListByCid(isBlog, mPageNum, cid)
    }

    private fun initRecyclerView() {
        mViewBinding.rlv.apply {
            layoutManager = LinearLayoutManager(activity)
            if (itemDecorationCount <= 0) {
                addItemDecoration(
                    VerticalItemDecoration(
                        AndroidKit.Dimens.dp2px(10)
                    )
                )
            }
            mHomeAdapter.apply {
                setOnItemClickListener(this@SystemListFragment)
                addChildClickViewIds(R.id.ibt_star)
                setOnItemChildClickListener(this@SystemListFragment)
                loadMoreModule.setOnLoadMoreListener { getData(false) }
                loadMoreModule.loadMoreView = CustomLoadMoreView()
            }
            adapter = mHomeAdapter
        }
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        // 跳转详情
        adapter.run {
            val entity = data[position] as ArticleEntity
            activity?.let { BrowserActivity.launch(it, entity.link) }
        }
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

    override val startObserve: SystemListViewModel.() -> Unit = {
        //LiveData 刷新数据到页面
        mArticleList.observe(this@SystemListFragment) {
            mViewBinding.swipeRefresh.isRefreshing = false
            val currentState = lifecycle.currentState
            LoggerKit.d("${this@SystemListFragment}---currentState:${currentState}--${it.datas.size}")
            if (mPageNum == 0) {
                mHomeAdapter.setList(it.datas)
            } else {
                mHomeAdapter.addData(it.datas)
            }
            setLoadStatus(!it.over)
        }
        mErrorMsg.observe(this@SystemListFragment) {
            it?.let { onApiFailure(it) }
        }
    }


    private fun setLoadStatus(hasNextPage: Boolean) {
        if (mViewBinding.swipeRefresh.isRefreshing) {
            mViewBinding.swipeRefresh.isRefreshing = false
        }
        mHomeAdapter.updateLoadMoreStatus(hasNextPage)
    }

    override fun onApiFailure(msg: String) {
        if (mViewBinding.swipeRefresh.isRefreshing) {
            mViewBinding.swipeRefresh.isRefreshing = false
        }
        super.onApiFailure(msg)
    }
}

class SystemListViewModel(application: Application) : BaseViewModel(application) {
    private val mRepository by lazy { SystemRepository() }

    val mArticleList: MutableLiveData<ArticleList> = MutableLiveData()

    /**
     * 获取体系、公众号文章列表
     */
    fun getSystemArticleListByCid(
        isBlog: Boolean,
        page: Int,
        cid: Int
    ) {
        if (page == 1) {
            mArticleList.value?.datas?.clear()
        }
        executeRequest(request = {
            if (isBlog) {
                //获取公众号文章列表
                mRepository.getBlogListWithId(page, cid)
            } else {
                //获取体系文章列表
                mRepository.getSystemArticleList(page, cid)
            }
        },
            onNext = { ok, data, msg ->
                if (ok) {
                    mArticleList.value = data
                }
            })
    }


    /**
     * 收藏，取消收藏
     */
    fun collectArticle(articleId: Int, collect: Boolean) {
        executeRequest(request = {
            if (collect) {
                mRepository.collectArticle(articleId)
            } else {
                mRepository.unCollectArticle(articleId)
            }
        })
    }
}