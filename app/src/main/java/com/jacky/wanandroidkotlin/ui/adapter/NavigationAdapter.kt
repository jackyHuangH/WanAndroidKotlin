package com.jacky.wanandroidkotlin.ui.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.model.entity.ArticleEntity
import com.jacky.wanandroidkotlin.model.entity.NavigationEntity
import com.jacky.wanandroidkotlin.ui.browser.BrowserActivity
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import com.zhy.view.flowlayout.TagFlowLayout

/**
 * @author:Hzj
 * @date  :2019-07-18
 * desc  ：导航列表adapter
 * record：
 */
class NavigationAdapter(layoutResId: Int = R.layout.recycler_item_navigation) :
    BaseQuickAdapter<NavigationEntity, BaseViewHolder>(layoutResId) {

    override fun convert(holder: BaseViewHolder, item: NavigationEntity) {
        holder.setText(R.id.nav_name, item.name)
        holder.getView<TagFlowLayout>(R.id.flow_layout).run {
            adapter = object : TagAdapter<ArticleEntity>(item.articles) {
                override fun getView(parent: FlowLayout?, position: Int, t: ArticleEntity): View {
                    val tvTag =
                        LayoutInflater.from(parent?.context)
                            .inflate(R.layout.item_tag, parent, false) as TextView
                    tvTag.text = t.title
                    tvTag.setRandomColorTintBg(position)
                    return tvTag
                }
            }

            setOnTagClickListener { view, position, parent ->
                BrowserActivity.launch(context as Activity, item.articles[position].link)
                true
            }
        }
    }
}

/**
 * 设置随机颜色背景shape
 */
fun View.setRandomColorTintBg(pos: Int) {
    val colorArray = arrayOf(
        R.color.color_69B2F9,
        R.color.color_F54336,
        R.color.color_3372F6,
        R.color.color_4CD964,
        R.color.color_FFDB5C,
        R.color.color_AB82FF,
        R.color.color_FB5656,
        R.color.color_FBA603,
        R.color.color_0288D1,
    )
    val drawable = ContextCompat.getDrawable(context, R.drawable.shape_bg_primary_20)
    drawable?.let {
        //简单的使用tint改变drawable颜色
        val mutatedDrawable = DrawableCompat.wrap(drawable).mutate()
        mutatedDrawable.setTint(
            ContextCompat.getColor(context, colorArray[pos % colorArray.size])
        )
        this.background = mutatedDrawable
    }
}