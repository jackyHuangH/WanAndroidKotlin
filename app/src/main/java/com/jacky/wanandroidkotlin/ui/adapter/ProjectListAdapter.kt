package com.jacky.wanandroidkotlin.ui.adapter

import android.os.Build
import android.text.Html
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.model.entity.ArticleEntity
import com.jacky.wanandroidkotlin.wrapper.glide.GlideApp

/**
 * @author:Hzj
 * @date  :2019/7/4/004
 * desc  ：项目列表Adapter
 * record：
 */
class ProjectListAdapter(layoutResId: Int = R.layout.recycler_item_project) :
    BaseQuickAdapter<ArticleEntity, BaseViewHolder>(layoutResId),LoadMoreModule {

    override fun convert(helper: BaseViewHolder, item: ArticleEntity) {
        var title = ""
        title = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(item.title, Html.FROM_HTML_MODE_LEGACY).toString()
        } else {
            Html.fromHtml(item.title).toString()
        }

        helper.setText(R.id.tv_author, item.author)
            .setText(R.id.tv_desc, item.desc)
            .setText(R.id.tv_title, title)
            .setText(R.id.tv_time, item.niceDate)

        GlideApp.with(context)
            .load(item.envelopePic)
            .placeholder(R.drawable.pic_default)
            .error(R.drawable.pic_default)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(helper.getView(R.id.iv))

        //设置点赞的星星
        helper.setImageResource(
            R.id.ibt_star, if (item.collect) {
                R.drawable.timeline_like_pressed
            } else {
                R.drawable.timeline_like_normal
            }
        )
    }
}