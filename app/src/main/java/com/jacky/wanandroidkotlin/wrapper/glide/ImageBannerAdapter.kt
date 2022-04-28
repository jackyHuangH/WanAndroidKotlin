package com.jacky.wanandroidkotlin.wrapper.glide

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jacky.wanandroidkotlin.R
import com.jacky.wanandroidkotlin.model.entity.BannerEntity
import com.youth.banner.adapter.BannerAdapter

/**
 * @author:Hzj
 * @date  :2022-04-27 18:15:36
 * desc  ：youth banner Adapter
 * record：
 */
class ImageBannerAdapter(bannerData: List<BannerEntity>) :
    BannerAdapter<BannerEntity, ImageBannerAdapter.ViewHolder>(bannerData) {

    override fun onCreateHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.banner_image_title, parent, false)
        )
    }

    override fun onBindView(holder: ViewHolder?, data: BannerEntity?, position: Int, size: Int) {
        holder?.apply {
            holder.tvTitle.text = data?.title.orEmpty()
            GlideApp
                .with(holder.itemView.context)
                .load(data?.imagePath.orEmpty())
                .placeholder(R.drawable.holder_banner)
                .into(holder.ivBanner)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivBanner: ImageView = itemView.findViewById(R.id.image)
        var tvTitle: TextView = itemView.findViewById(R.id.bannerTitle)
    }
}