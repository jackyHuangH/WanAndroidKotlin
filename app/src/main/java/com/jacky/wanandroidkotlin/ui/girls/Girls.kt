package com.jacky.wanandroidkotlin.ui.girls

/**
 * @author:Hzj
 * @date  :2020-01-19
 * desc  ：
 * record：
 */
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseVMActivity
import com.jacky.wanandroidkotlin.base.BaseViewModel
import com.jacky.wanandroidkotlin.model.api.WanRetrofitClient
import com.jacky.wanandroidkotlin.model.entity.GirlEntity
import com.jacky.wanandroidkotlin.widget.IGallery
import com.jacky.wanandroidkotlin.widget.RemoteImageSource
import com.jacky.wanandroidkotlin.widget.previewPicture
import com.jacky.wanandroidkotlin.wrapper.glide.GlideApp
import com.jacky.wanandroidkotlin.wrapper.recyclerview.CustomLoadMoreView
import com.zenchn.support.router.Router
import kotlinx.android.synthetic.main.activity_girls.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class GirlsActivity : BaseVMActivity<GirlsViewModel>(), BaseQuickAdapter.OnItemClickListener,
    BaseQuickAdapter.RequestLoadMoreListener, IGallery {
    private val girlAdapter by lazy { GirlsAdapter() }

    companion object {
        fun launch(from: FragmentActivity) {
            Router
                .newInstance()
                .from(from)
                .to(GirlsActivity::class.java)
                .launch()
        }
    }

    override fun getLayoutRes(): Int = R.layout.activity_girls

    override fun initWidget() {
        toolbar.setNavigationOnClickListener { onBackPressed() }
        toolbar.title = getString(R.string.girl_title)

        initRecyclerView()
        initRefreshLayout()
    }

    private fun initRecyclerView() {
        rlv.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL).apply {
                //可防止Item切换
                gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
            }
        girlAdapter.onItemClickListener = this
        girlAdapter.setOnLoadMoreListener(this, rlv)
        girlAdapter.setLoadMoreView(CustomLoadMoreView())
        rlv.adapter = girlAdapter
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
        swipe_refresh.isRefreshing = true
        //下拉刷新时禁用加载更多
        mViewModel.getGirlsList()
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View?, position: Int) {
        val item = adapter.data[position] as GirlEntity
        previewPicture(RemoteImageSource(item.url), 0)
    }

    override fun onLoadMoreRequested() {
        mViewModel.getGirlsList(false)
    }

    private fun setLoadStatus(hasNextPage: Boolean) {
        if (swipe_refresh.isRefreshing) {
            swipe_refresh.isRefreshing = false
        }
        if (hasNextPage) {
            girlAdapter.loadMoreComplete()
        } else {
            girlAdapter.loadMoreEnd()
        }
    }

    override fun startObserve() {
        mViewModel.apply {
            girlsList.observe(this@GirlsActivity, Observer {
                if (it.first == 1) {
                    girlAdapter.setNewData(it.second)
                } else {
                    girlAdapter.addData(it.second)
                }
                setLoadStatus(it.third)
            })
        }
        onRefresh()
    }

    override fun onApiFailure(msg: String) {
        swipe_refresh.isRefreshing = false
        super.onApiFailure(msg)
    }
}

private class GirlsAdapter :
    BaseQuickAdapter<GirlEntity, BaseViewHolder>(R.layout.recycler_item_girls) {
    override fun convert(helper: BaseViewHolder, item: GirlEntity) {
        helper.setText(R.id.tv_girl_desc, item.desc)
        val imageView = helper.getView<ImageView>(R.id.iv_girl)
        imageView.layoutParams.apply {
            height = if (helper.adapterPosition == 0) 500 else ViewGroup.LayoutParams.WRAP_CONTENT
        }
        GlideApp.with(mContext)
            .load(item.url)
            .override(500, 800)
            .transition(DrawableTransitionOptions().crossFade())
            .placeholder(R.drawable.girl)
            .error(R.drawable.girl)
            .into(imageView)
    }
}

class GirlsViewModel : BaseViewModel() {
    val girlsList: MutableLiveData<Triple<Int, List<GirlEntity>, Boolean>> = MutableLiveData()
    private var page = 1

    fun getGirlsList(isRefresh: Boolean = true) {
        launch {
            val result = withContext(Dispatchers.IO) {
                if (isRefresh) {
                    page = 1
                } else {
                    page++
                }
                val jsonObject = WanRetrofitClient.mService.getGirlsList(page)
                if (jsonObject["error"].asBoolean.not()) {
                    val jsonArray = jsonObject["results"].asJsonArray
                    val typeToken = object : TypeToken<List<GirlEntity>>() {}.type
                    Gson().fromJson<List<GirlEntity>>(jsonArray, typeToken)
                } else {
                    mutableListOf()
                }
            }
            val hasNextPage = result.size >= 10
            girlsList.value = Triple(page, result, hasNextPage)
        }
    }
}