package com.jacky.wanandroidkotlin.ui.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
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

    override fun convert(helper: BaseViewHolder, item: NavigationEntity) {
        helper.setText(R.id.nav_name, item.name)
        helper.getView<TagFlowLayout>(R.id.flow_layout).run {
            adapter = object : TagAdapter<ArticleEntity>(item.articles) {
                override fun getView(parent: FlowLayout?, position: Int, t: ArticleEntity): View {
                    val tvTag =
                        LayoutInflater.from(parent?.context)
                            .inflate(R.layout.item_tag, parent, false) as TextView
                    tvTag.text = t.title
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