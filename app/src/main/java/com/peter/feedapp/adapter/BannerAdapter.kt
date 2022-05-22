package com.peter.feedapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.peter.feedapp.R
import com.peter.feedapp.bean.Banner
import com.peter.feedapp.databinding.BannerImgBinding
import com.squareup.picasso.Picasso

class BannerAdapter(var context: Context, private var bannerList: MutableList<Banner>, private var clickListener: ClickListener): RecyclerView.Adapter<BannerViewHolder>() {
    private lateinit var binding: BannerImgBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        binding = BannerImgBinding.inflate(LayoutInflater.from(context), parent, false)
        return BannerViewHolder(binding.root)
    }


    override fun getItemCount(): Int {
        return bannerList.size
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        val banner = bannerList[position]

        // 绑定数据
        Picasso.get()
            .load(banner.imagePath)
            .placeholder(R.drawable.image_placeholder)
            .error(R.drawable.image_placeholder)
            .into(holder.bannerImg)

        // 监听点击事件
        holder.bannerImg.setOnClickListener { clickListener.onClick(banner) }
    }
}


class BannerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    var bannerImg: ImageView = itemView.findViewById(R.id.banner_img)
}

interface ClickListener{
   fun  onClick(banner:Banner)
}