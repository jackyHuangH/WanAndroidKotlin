package com.jacky.wanandroidkotlin.ui.adapter

import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.model.entity.ArtifactEntity
import com.jacky.wanandroidkotlin.model.entity.ArtifactMapDTO
import com.jacky.wanandroidkotlin.model.entity.GoogleMavenEntity
import com.zenchn.support.utils.AndroidKit
import com.zenchn.support.widget.VerticalItemDecoration

/**
 * @author:Hzj
 * @date  :2020/5/15
 * desc  ：
 * record：
 */
class GoogleMavenSearchAdapter(layoutId: Int = R.layout.recycler_item_google_maven_search) :
    BaseQuickAdapter<GoogleMavenEntity, BaseViewHolder>(layoutId) {

    override fun convert(helper: BaseViewHolder, item: GoogleMavenEntity) {
        helper.setText(
            R.id.tv_group_name,
            "${mContext.getString(R.string.package_name)}：${item.groupName}"
        )
            .addOnClickListener(R.id.tv_group_name)
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
            layoutManager = LinearLayoutManager(mContext)
            setHasFixedSize(true)
            if (itemDecorationCount <= 0) {
                addItemDecoration(VerticalItemDecoration(AndroidKit.Dimens.dp2px(1)))
            }
            adapter = GoogleMavenGroupListAdapter().apply {
                setOnItemChildClickListener { adapter, view, position ->
                    val artifactMapDTO = artifactList[position]
                    if (view.id == R.id.tv_artifact_name) {
                        artifactMapDTO.expand = artifactMapDTO.expand.not()
                        notifyDataSetChanged()
                    }
                }
                setNewData(artifactList)
            }
        }
    }
}


class GoogleMavenGroupListAdapter(layoutId: Int = R.layout.recycler_item_google_maven_group) :
    BaseQuickAdapter<ArtifactMapDTO, BaseViewHolder>(layoutId) {

    override fun convert(helper: BaseViewHolder, item: ArtifactMapDTO) {
        helper.setText(R.id.tv_artifact_name, item.mapKeyName)
            .addOnClickListener(R.id.tv_artifact_name)
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
            layoutManager = LinearLayoutManager(mContext)
            setHasFixedSize(true)
            if (itemDecorationCount <= 0) {
                addItemDecoration(VerticalItemDecoration(AndroidKit.Dimens.dp2px(2),true))
            }
            adapter = GoogleMavenArtifactListAdapter().apply { setNewData(item.artifactList?.reversed()) }
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