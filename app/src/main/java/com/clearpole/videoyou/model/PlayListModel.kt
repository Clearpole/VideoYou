package com.clearpole.videoyou.model

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import com.blankj.utilcode.util.ActivityUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.clearpole.videoyou.VideoPlayer
import com.clearpole.videoyou.databinding.MediaStoreListItemBinding
import com.clearpole.videoyou.objects.VideoPlayObjects
import com.clearpole.videoyou.objects.VideoPlayerObjects
import com.clearpole.videoyou.utils.TimeParse
import com.drake.brv.BindingAdapter
import com.drake.brv.item.ItemBind
import org.json.JSONObject
import java.time.Duration

data class PlayListModel(val uri: String,val title:String?,val duration: String?,val time:String?,val list:JSONObject):ItemBind {
    override fun onBind(holder: BindingAdapter.BindingViewHolder) {
        val binding = MediaStoreListItemBinding.bind(holder.itemView)
        binding.itemName.text = title
        Glide.with(holder.context).load(uri).diskCacheStrategy(DiskCacheStrategy.RESOURCE).transition(
            DrawableTransitionOptions.withCrossFade()).into(binding.itemCover)
        binding.itemSize.text = TimeParse.timeParse(duration!!.toLong())
        binding.itemRoot.setOnClickListener {
            VideoPlayObjects.paths = uri
            VideoPlayObjects.list = list
            VideoPlayObjects.title = title.toString()
            VideoPlayObjects.type = "LIST"
            VideoPlayerObjects.newItem = holder.layoutPosition
            val intent = Intent(holder.context, VideoPlayer::class.java)
            ActivityUtils.startActivity(intent)
        }
    }
}