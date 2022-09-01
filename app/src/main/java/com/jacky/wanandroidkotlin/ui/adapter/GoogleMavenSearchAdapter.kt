package com.jacky.wanandroidkotlin.ui.adapter

import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.model.entity.ArtifactEntity
import com.jacky.wanandroidkotlin.model.entity.ArtifactMapDTO
import com.jacky.wanandroidkotlin.model.entity.GoogleMavenEntity
import com.jacky.support.utils.AndroidKit
import com.jacky.support.widget.VerticalItemDecoration

/**
 * @author:Hzj
 * @date  :2020/5/15
 * desc  ：google maven search 列表分组adapter
 * record：
 */
class GoogleMavenSearchAdapter(layoutId: Int = R.layout.recycler_item_google_maven_search) :
    BaseQuickAdapter<GoogleMavenEntity, BaseViewHolder>(layoutId) {

    override fun convert(helper: BaseViewHolder, item: GoogleMavenEntity) {
        helper.setText(
            R.id.tv_group_name,
            "${context.getString(R.string.package_name)}：${item.groupName}"
        )
        (helper.getView<TextView>(R.id.tv_group_name) as TextView).apply {
            setCompoundDrawablesRelativeWithIntrinsicBounds(
                0,
                0,
                if (item.groupExpand) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down,
                0
            )
        }
        helper.setGone(R.id.rv_group_list, item.groupExpand)
        val rvGroup = helper.getView<RecyclerView>(R.id.rv_group_list)
        val artifactList = item.artifactMap?.map { ArtifactMapDTO(it.key, it.value) } as MutableList
        rvGroup.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            if (itemDecorationCount <= 0) {
                addItemDecoration(VerticalItemDecoration(AndroidKit.Dimens.dp2px(1)))
            }
            adapter = GoogleMavenGroupListAdapter().apply {
                addChildClickViewIds(R.id.tv_artifact_name)
                setOnItemChildClickListener { adapter, view, position ->
                    val artifactMapDTO = artifactList[position]
                    if (view.id == R.id.tv_artifact_name) {
                        artifactMapDTO.expand = artifactMapDTO.expand.not()
                        notifyDataSetChanged()
                    }
                }
                setList(artifactList)
            }
        }
    }
}


class GoogleMavenGroupListAdapter(layoutId: Int = R.layout.recycler_item_google_maven_group) :
    BaseQuickAdapter<ArtifactMapDTO, BaseViewHolder>(layoutId) {

    override fun convert(helper: BaseViewHolder, item: ArtifactMapDTO) {
        helper.setText(R.id.tv_artifact_name, item.mapKeyName)
        (helper.getView<TextView>(R.id.tv_artifact_name) as TextView).apply {
            setCompoundDrawablesRelativeWithIntrinsicBounds(
                0,
                0,
                if (item.expand) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down,
                0
            )
        }

        helper.setGone(R.id.rv_artifact_list, item.expand)
        val rvArtifact = helper.getView<RecyclerView>(R.id.rv_artifact_list)
        rvArtifact.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            if (itemDecorationCount <= 0) {
                addItemDecoration(VerticalItemDecoration(AndroidKit.Dimens.dp2px(2), true))
            }

            adapter =
                GoogleMavenArtifactListAdapter().apply { setList(item.artifactList?.asReversed()) }
        }
    }
}

class GoogleMavenArtifactListAdapter(layoutId: Int = R.layout.recycler_item_google_maven_artifact) :
    BaseQuickAdapter<ArtifactEntity, BaseViewHolder>(layoutId) {

    override fun convert(helper: BaseViewHolder, item: ArtifactEntity) {
        helper.setText(R.id.tv_artifact_content, "content:${item.content}")
            .setText(R.id.tv_artifact_version, "version:${item.version}")
    }
}