package com.clearpole.videoyou.model

import android.graphics.Bitmap
import android.net.Uri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.clearpole.videoyou.databinding.MediaStoreListItemBinding
import com.clearpole.videoyou.utils.TimeParse
import com.drake.brv.BindingAdapter
import com.drake.brv.item.ItemBind
import java.time.Duration

data class PlayListModel(val uri: String,val title:String?,val duration: String?,val time:String?):ItemBind {
    override fun onBind(holder: BindingAdapter.BindingViewHolder) {
        val binding = MediaStoreListItemBinding.bind(holder.itemView)
        binding.itemName.text = title
        Glide.with(holder.context).load(uri).diskCacheStrategy(DiskCacheStrategy.RESOURCE).transition(
            DrawableTransitionOptions.withCrossFade()).into(binding.itemCover)
        binding.itemSize.text = TimeParse.timeParse(duration!!.toLong())
    }
}