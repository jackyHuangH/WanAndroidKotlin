package com.jacky.wanandroidkotlin.ui.adapter

import android.os.Build
import android.text.Html
import android.widget.ImageButton
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.model.entity.ArticleEntity
import com.jacky.wanandroidkotlin.wrapper.isNotNullAndNotEmpty

/**
 * @author:Hzj
 * @date  :2019/7/2/002
 * desc  ：首页列表Adapter
 * record：
 */
class HomeListAdapter(layoutRes: Int = R.layout.recycler_item_home) :
    BaseQuickAdapter<ArticleEntity, BaseViewHolder>(layoutRes) {

    private var mShowStar = true

    /**
     * 是否显示点赞按钮
     */
    fun showStar(show: Boolean) {
        mShowStar = show
    }

    override fun convert(helper: BaseViewHolder, item: ArticleEntity) {
        var title = ""
        title = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(item.title, Html.FROM_HTML_MODE_LEGACY).toString()
        } else {
            Html.fromHtml(item.title).toString()
        }

        val topDesc =
            if (item.type == 1) mContext.getString(R.string.home_article_tag_top) else null
        val newDesc = if (item.fresh) mContext.getString(R.string.home_article_tag_new) else null
        val show = topDesc.isNotNullAndNotEmpty() && newDesc.isNotNullAndNotEmpty()
        helper.setText(R.id.tv_author, item.author)
            .setText(R.id.tv_top_new, "$topDesc $newDesc")
            .setGone(R.id.tv_top_new, show)
            .setText(R.id.tv_tag, "${item.superChapterName ?: ""} ${item.chapterName}")
            .setText(R.id.tv_title, title)
            .setText(R.id.tv_time, item.niceDate)
            .addOnClickListener(R.id.ibt_star)

        //设置点赞的星星
        if (mShowStar) {
            helper.getView<ImageButton>(R.id.ibt_star).apply {
                isSelected = item.collect
            }
        } else {
            helper.setVisible(R.id.ibt_star, false)
        }
    }
}