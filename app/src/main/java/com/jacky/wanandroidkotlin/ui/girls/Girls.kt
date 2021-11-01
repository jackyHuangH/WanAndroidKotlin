package com.jacky.wanandroidkotlin.ui.girls

/**
 * @author:Hzj
 * @date  :2020-01-19
 * desc  ：
 * record：
 */
import android.app.Application
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.chad.library.adapter.base.listener.OnLoadMoreListener
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hjq.toast.ToastUtils
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseVMActivity
import com.jacky.wanandroidkotlin.base.BaseViewModel
import com.jacky.wanandroidkotlin.model.api.WanRetrofitClient
import com.jacky.wanandroidkotlin.model.entity.GirlEntity
import com.jacky.wanandroidkotlin.widget.IGallery
import com.jacky.wanandroidkotlin.widget.RemoteImageSource
import com.jacky.wanandroidkotlin.widget.previewPicture
import com.jacky.wanandroidkotlin.wrapper.AriaDownload
import com.jacky.wanandroidkotlin.wrapper.DownloadUtils
import com.jacky.wanandroidkotlin.wrapper.getView
import com.jacky.wanandroidkotlin.wrapper.glide.GlideApp
import com.jacky.wanandroidkotlin.wrapper.recyclerview.CustomLoadMoreView
import com.jacky.wanandroidkotlin.wrapper.recyclerview.RecyclerViewHelper
import com.jacky.wanandroidkotlin.wrapper.recyclerview.updateLoadMoreStatus
import com.jacky.wanandroidkotlin.wrapper.viewExt
import com.yanzhenjie.permission.runtime.Permission
import com.zenchn.support.permission.IPermission
import com.zenchn.support.permission.applySelfPermissions
import com.zenchn.support.router.Router
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class GirlsActivity : BaseVMActivity<GirlsViewModel>(), OnItemClickListener,
    OnLoadMoreListener, OnItemChildClickListener, IGallery, IPermission {
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

    override fun getLayoutId(): Int = R.layout.activity_girls

    override fun initWidget() {
        viewExt<Toolbar>(R.id.toolbar) {
            setNavigationOnClickListener { onBackPressed() }
            title = getString(R.string.girl_title)
        }
        initRecyclerView()
        initRefreshLayout()
        applySelfPermissions(Permission.WRITE_EXTERNAL_STORAGE)
    }

    private fun initRecyclerView() {
        val rlv = getView<RecyclerView>(R.id.rlv)
        rlv.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL).apply {
                //可防止Item切换
                gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
            }
        rlv.adapter = girlAdapter.apply {
            setOnItemClickListener(this@GirlsActivity)
            loadMoreModule.setOnLoadMoreListener(this@GirlsActivity)
            loadMoreModule.loadMoreView = CustomLoadMoreView()
            addChildClickViewIds(R.id.ibt_download_img)
            setOnItemChildClickListener(this@GirlsActivity)
            setEmptyView(RecyclerViewHelper.getCommonEmptyView(rlv))
        }
    }

    private fun initRefreshLayout() {
        viewExt<SwipeRefreshLayout>(R.id.swipe_refresh) {
            setColorSchemeResources(R.color.colorAccent)
            setOnRefreshListener {
                //刷新数据
                onRefresh()
            }
        }
    }

    private fun onRefresh() {
        viewExt<SwipeRefreshLayout>(R.id.swipe_refresh) {
            isRefreshing = true
        }
        //下拉刷新时禁用加载更多
        mViewModel.getGirlsList()
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        val item = adapter.data[position] as GirlEntity
        previewPicture(RemoteImageSource(item.url), 0)
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        when (view.id) {
            R.id.ibt_download_img -> {
                //下载图片到本地
                val item = adapter.data[position] as GirlEntity
//                AriaDownload.downloadFile(item)
                DownloadUtils.download(item,onDownloadSuccess = {file->
                    ToastUtils.show("下载完成，文件已保存:${file.path}")
                },onDownloadFail = {e->
                    ToastUtils.show("下载失败,请重试!")
                })
            }
        }
    }

    override fun onLoadMore() {
        mViewModel.getGirlsList(false)
    }

    private fun setLoadStatus(hasNextPage: Boolean) {
        if (getView<SwipeRefreshLayout>(R.id.swipe_refresh).isRefreshing) {
            getView<SwipeRefreshLayout>(R.id.swipe_refresh).isRefreshing = false
        }
        girlAdapter.updateLoadMoreStatus(hasNextPage)
    }

    override val startObserve: GirlsViewModel.() -> Unit = {
        girlsList.observe(this@GirlsActivity, Observer {
            if (it.first == 1) {
                girlAdapter.setNewInstance(it.second)
            } else {
                girlAdapter.addData(it.second)
            }
            setLoadStatus(it.third)
        })
        onRefresh()
    }

    override fun onApiFailure(msg: String) {
        getView<SwipeRefreshLayout>(R.id.swipe_refresh).isRefreshing = false
        super.onApiFailure(msg)
    }

    override fun onDestroy() {
        AriaDownload.clearDownloadTask(this)
        super.onDestroy()
    }
}

private class GirlsAdapter :
    BaseQuickAdapter<GirlEntity, BaseViewHolder>(R.layout.recycler_item_girls), LoadMoreModule {
    override fun convert(helper: BaseViewHolder, item: GirlEntity) {
        helper.setText(R.id.tv_girl_desc, item.desc)
        val imageView = helper.getView<ImageView>(R.id.iv_girl)
        imageView.layoutParams.apply {
            height = if (helper.adapterPosition == 0) 500 else ViewGroup.LayoutParams.WRAP_CONTENT
        }
        GlideApp.with(context)
            .load(item.url)
            .override(500, 800)
            .transition(DrawableTransitionOptions().crossFade())
            .placeholder(R.drawable.girl)
            .error(R.drawable.girl)
            .into(imageView)
    }
}

class GirlsViewModel(application: Application) : BaseViewModel(application) {
    val girlsList: MutableLiveData<Triple<Int, MutableList<GirlEntity>, Boolean>> =
        MutableLiveData()
    private var page = 1

    fun getGirlsList(isRefresh: Boolean = true) {
        launchOnUI {
            val result = withContext(Dispatchers.IO) {
                try {
                    if (isRefresh) {
                        page = 1
                    } else {
                        page++
                    }
                    val jsonObject = WanRetrofitClient.mService.getGirlsList(page)
                    if (jsonObject["error"].asBoolean.not()) {
                        val jsonArray = jsonObject["results"].asJsonArray
                        val typeToken = object : TypeToken<MutableList<GirlEntity>>() {}.type
                        Gson().fromJson<MutableList<GirlEntity>>(jsonArray, typeToken)
                    } else {
                        mutableListOf()
                    }
                } catch (e: Exception) {
                    mErrorMsg.postValue(e.message)
                    mutableListOf<GirlEntity>()
                }
            }
            val hasNextPage = result.size >= 10
            girlsList.value = Triple(page, result, hasNextPage)
        }
    }
}