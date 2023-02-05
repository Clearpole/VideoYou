package com.clearpole.videoyou.model

import android.content.Intent
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

data class PlayListModel(
    val uri: String,
    val title: String?,
    val duration: String?,
    val time: String?,
    val list: JSONObject
) : ItemBind {
    override fun onBind(holder: BindingAdapter.BindingViewHolder) {
        val binding = MediaStoreListItemBinding.bind(holder.itemView)
        binding.itemName.text = title
        Glide.with(holder.context).load(uri).diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .transition(
                DrawableTransitionOptions.withCrossFade()
            ).into(binding.itemCover)
        binding.itemSize.text = TimeParse.timeParse(duration!!.toLong())
        binding.itemRoot.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val array = arrayListOf<String>()
                val keys = list.keys()
                while (keys.hasNext()) {
                    val key = keys.next().toString()
                    val uri = list.getString(key)
                    array.add(uri)
                    VideoPlayObjects.list = array
                }
                VideoPlayObjects.title = title.toString()
                VideoPlayObjects.type = "LOCAL"
                VideoPlayerObjects.newItem = holder.layoutPosition
                withContext(Dispatchers.Main) {
                    val intent = Intent(holder.context, VideoPlayer::class.java)
                    ActivityUtils.startActivity(intent)
                }
            }
        }
    }
}