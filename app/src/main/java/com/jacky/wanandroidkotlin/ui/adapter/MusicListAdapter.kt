package com.jacky.wanandroidkotlin.ui.adapter

import android.annotation.SuppressLint
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.util.formatMusicTime
import com.jacky.wanandroidkotlin.wrapper.musicplay.AudioBean
import com.jacky.wanandroidkotlin.wrapper.orNotNullNotEmpty

/**
 * @author:Hzj
 * @date  :2020/12/21
 * desc  ：音乐列表adapter
 * record：
 */
class MusicListAdapter(layoutId: Int = R.layout.recycler_item_audio_list) :
    BaseQuickAdapter<AudioBean, BaseViewHolder>(layoutId) {

    /**
     * 更新选中状态
     */
    fun updateSelect(selectAudio: AudioBean) {
        data.forEach {
            it.hasSelected = it == selectAudio
        }
        notifyDataSetChanged()
    }

    @SuppressLint("SetTextI18n")
    override fun convert(helper: BaseViewHolder, item: AudioBean) {
        helper.apply {
            getView<TextView>(R.id.tv_audio_name).apply {
                isSelected = item.hasSelected
                setCompoundDrawablesRelativeWithIntrinsicBounds(
                    if (item.hasSelected) R.drawable.ic_playing_small else 0,
                    0,
                    0,
                    0
                )
                text = item.name.orNotNullNotEmpty("未知曲目")
            }
            getView<TextView>(R.id.tv_singer).apply {
                isSelected = item.hasSelected
                text = "-- ${item.singer.orNotNullNotEmpty("未知歌手")}"
            }
            getView<TextView>(R.id.tv_duration).apply {
                isSelected = item.hasSelected
                text = formatMusicTime(item.duration)
            }
        }
    }
}