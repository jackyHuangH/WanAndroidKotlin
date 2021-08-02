package com.jacky.wanandroidkotlin.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.model.entity.TreeParentEntity

/**
 * @author:Hzj
 * @date  :2019/7/5/005
 * desc  ：体系 列表adapter
 * record：
 */
class SystemListAdapter(layoutResId: Int = R.layout.recycler_item_system) :
    BaseQuickAdapter<TreeParentEntity, BaseViewHolder>(layoutResId) {

    override fun convert(helper: BaseViewHolder, item: TreeParentEntity) {
        helper.setText(R.id.tv_parent, item.name)
            .setText(R.id.tv_child, item.children.joinToString("    ", transform = { child ->
                child.name
            }))
    }
}