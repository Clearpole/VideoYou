package com.clearpole.videoyou.model

import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.clearpole.videoyou.databinding.VideoInfoUtilsItemBinding
import com.drake.brv.BindingAdapter
import com.drake.brv.item.ItemBind

data class VideoUtilsModel(val icon:Drawable,val name:String, val util:()->Unit):ItemBind {
    override fun onBind(holder: BindingAdapter.BindingViewHolder) {
        val binding = holder.getBinding<VideoInfoUtilsItemBinding>()
        Glide.with(holder.context).load(icon).into(binding.icon)
        binding.name.text = name
        binding.root.setOnClickListener {
            util()
        }
    }
}