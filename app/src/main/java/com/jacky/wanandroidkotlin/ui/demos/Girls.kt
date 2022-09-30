package com.jacky.wanandroidkotlin.ui.demos

/**
 * @author:Hzj
 * @date  :2020-01-19
 * desc  ：
 * record：
 */
import android.Manifest
import android.app.Application
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.chad.library.adapter.base.listener.OnLoadMoreListener
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hjq.permissions.Permission
import com.hjq.toast.ToastUtils
import com.jacky.support.permission.IPermission
import com.jacky.support.permission.openPermissionSetting
import com.jacky.support.permission.requestSelfPermissions
import com.jacky.support.router.Router
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.base.BaseVMActivity
import com.jacky.wanandroidkotlin.base.BaseViewModel
import com.jacky.wanandroidkotlin.databinding.ActivityGirlsBinding
import com.jacky.wanandroidkotlin.model.api.WanRetrofitClient
import com.jacky.wanandroidkotlin.model.entity.GirlEntity
import com.jacky.wanandroidkotlin.util.DisplayUtils
import com.jacky.wanandroidkotlin.util.setOnAntiShakeClickListener
import com.jacky.wanandroidkotlin.widget.IGallery
import com.jacky.wanandroidkotlin.widget.RemoteImageSource
import com.jacky.wanandroidkotlin.widget.previewPicture
import com.jacky.wanandroidkotlin.wrapper.DownloadUtils
import com.jacky.wanandroidkotlin.wrapper.getView
import com.jacky.wanandroidkotlin.wrapper.glide.GlideApp
import com.jacky.wanandroidkotlin.wrapper.recyclerview.CustomLoadMoreView
import com.jacky.wanandroidkotlin.wrapper.recyclerview.RecyclerViewHelper
import com.jacky.wanandroidkotlin.wrapper.recyclerview.StaggeredDividerItemDecoration
import com.jacky.wanandroidkotlin.wrapper.viewExt
import com.jacky.wanandroidkotlin.wrapper.viewVisibleExt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class GirlsActivity : BaseVMActivity<ActivityGirlsBinding, GirlsViewModel>(), OnItemClickListener,
    OnLoadMoreListener, OnItemChildClickListener, IGallery, IPermission {
    private val girlAdapter by lazy { GirlsAdapter() }

    //暂不适配Android10存储权限，申请存储权限如下
    private val mPermissionArray = arrayOf(
//        Permission.MANAGE_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

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
        initPermissions()
    }

    private fun initPermissions() {
        // 申请存储权限,适配Android13
        requestSelfPermissions(
            *mPermissionArray,
            callback = { granted, never ->
                if (never) {
                    showMessage("请授予存储权限")
                }
            })
    }

    private fun initRecyclerView() {
        val rlv = getView<RecyclerView>(R.id.rlv)
        val staggeredLayoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL).apply {
                //可防止Item切换
                gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
            }
        rlv.layoutManager = staggeredLayoutManager
        //去除item动画
        rlv.itemAnimator = null
        if (rlv.itemDecorationCount <= 0) {
            rlv.addItemDecoration(StaggeredDividerItemDecoration(this, 5))
        }
        rlv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val first = IntArray(2)
                staggeredLayoutManager.findFirstCompletelyVisibleItemPositions(first)
                if (newState == RecyclerView.SCROLL_STATE_IDLE && (first[0] == 0 || first[1] == 1)) {
                    //重新布局
                    staggeredLayoutManager.invalidateSpanAssignments()
                }
                viewVisibleExt(R.id.bt_back_top, first[0] > 0)
            }
        })
        viewExt<TextView>(R.id.bt_back_top) {
            setOnAntiShakeClickListener {
                rlv.scrollToPosition(0)
                visibility = View.GONE
            }
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
        previewPicture(RemoteImageSource(item.download_url), 0)
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        when (view.id) {
            R.id.ibt_download_img -> {
                requestSelfPermissions(
                    *mPermissionArray,
                    callback = { granted, never ->
                        if (granted) {
                            //下载图片到本地
                            val item = adapter.data[position] as GirlEntity
                            DownloadUtils.download(item, onDownloadSuccess = { file ->
                                ToastUtils.show("下载完成，文件已保存:${file.path}")
                            }, onDownloadProgress = { p ->
                                ToastUtils.show("下载中:$p%")
                            }, onDownloadFail = { e ->
                                ToastUtils.show("下载失败,请重试!")
                            })
                        } else if (never) {
                            showMessage("请授予存储权限")
                            openPermissionSetting(*mPermissionArray)
                        }
                    })
//                checkPermanentDenied(*mPermissionArray) { never ->
//                    if (never) {
//                        //跳转到开启权限
//                        showMessage("请授予存储权限")
//                        openPermissionSetting(*mPermissionArray)
//                    } else {
//                        //下载图片到本地
//                        val item = adapter.data[position] as GirlEntity
//                        DownloadUtils.download(item, onDownloadSuccess = { file ->
//                            ToastUtils.show("下载完成，文件已保存:${file.path}")
//                        }, onDownloadProgress = { p ->
//                            ToastUtils.show("下载中:$p%")
//                        }, onDownloadFail = { e ->
//                            ToastUtils.show("下载失败,请重试!")
//                        })
//                    }
//                }
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
        if (hasNextPage) {
            girlAdapter.loadMoreModule.loadMoreComplete()
        } else {
            girlAdapter.loadMoreModule.loadMoreEnd()
        }
    }

    override val startObserve: GirlsViewModel.() -> Unit = {
        girlsList.observe(this@GirlsActivity, Observer {
            if (it.first == 0) {
                girlAdapter.setList(it.second)
                //局部刷新，防止瀑布流错乱
                girlAdapter.notifyItemRangeChanged(0, it.second.size)
            } else {
                val start = girlAdapter.data.size
                girlAdapter.addData(it.second)
                //局部刷新，防止瀑布流错乱
                girlAdapter.notifyItemRangeInserted(start, girlAdapter.data.size)
            }
            setLoadStatus(it.third)
        })
        onRefresh()
    }

    override fun onApiFailure(msg: String) {
        getView<SwipeRefreshLayout>(R.id.swipe_refresh).isRefreshing = false
        super.onApiFailure(msg)
    }
}

private class GirlsAdapter :
    BaseQuickAdapter<GirlEntity, BaseViewHolder>(R.layout.recycler_item_girls), LoadMoreModule {

    override fun convert(holder: BaseViewHolder, item: GirlEntity) {
        val position = holder.absoluteAdapterPosition
        holder.setText(R.id.tv_girl_desc, item.author)
        val imageView = holder.getView<ImageView>(R.id.iv_girl)
        imageView.layoutParams.apply {
            height = if (position % 2 == 0) {
                DisplayUtils.dp2px(200)
            } else {
                DisplayUtils.dp2px(350)
            }
        }
        GlideApp.with(context)
            .load(item.download_url)
            .transition(DrawableTransitionOptions.withCrossFade())
            .transform(CenterCrop(), RoundedCorners(DisplayUtils.dp2px(5)))
            .error(R.drawable.ic_pic_error)
            .into(imageView)
    }
}

class GirlsViewModel(application: Application) : BaseViewModel(application) {
    val girlsList: MutableLiveData<Triple<Int, MutableList<GirlEntity>, Boolean>> =
        MutableLiveData()
    private var page = 0
    private val pageSize = 20
    fun getGirlsList(isRefresh: Boolean = true) {
        launchOnUI {
            val result = withContext(Dispatchers.IO) {
                try {
                    if (isRefresh) {
                        page = 0
                    } else {
                        page++
                    }
                    val params = arrayOf("shibes", "birds", "cats")
                    val keyword = params[page % params.size]
                    val jsonArray = WanRetrofitClient.mService.getGirlsList(keyword, pageSize)
                    if (jsonArray != null && jsonArray.size() > 0) {
                        val typeToken = object : TypeToken<MutableList<String>>() {}.type
                        val urlList = Gson().fromJson<MutableList<String>>(jsonArray, typeToken)
                        urlList.mapIndexed { index, url ->
                            val desc = url.uppercase().subSequence(url.length - 14, url.length)
                            GirlEntity("IMG_$desc", url, 0, index.toString(), url, 0)
                        }.toMutableList()
                    } else {
                        mutableListOf<GirlEntity>()
                    }
                } catch (e: Exception) {
                    mErrorMsg.postValue(e.message)
                    mutableListOf<GirlEntity>()
                }
            }
            val hasNextPage = result.size >= pageSize
            girlsList.value = Triple(page, result, hasNextPage)
        }
    }
}