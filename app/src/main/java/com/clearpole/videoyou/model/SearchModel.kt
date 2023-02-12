package com.clearpole.videoyou.model

import android.content.Intent
import android.net.Uri
import com.blankj.utilcode.util.ActivityUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.clearpole.videoyou.VideoPlayerActivity
import com.clearpole.videoyou.databinding.SearchItemBinding
import com.clearpole.videoyou.objects.VideoPlayObjects
import com.clearpole.videoyou.utils.GetVideoThumbnail
import com.drake.brv.BindingAdapter
import com.drake.brv.item.ItemBind
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class SearchModel(
    val title: String,
    val size: String,
    val uri: Uri,
    val path: String
) :
    ItemBind {
    override fun onBind(holder: BindingAdapter.BindingViewHolder) {
        val binding = holder.getBinding<SearchItemBinding>()
        binding.itemText.text = title
        binding.itemRoot.setOnClickListener {
            VideoPlayObjects.paths = path
            VideoPlayObjects.title = title
            VideoPlayObjects.type = "LOCAL"
            VideoPlayObjects.list = mutableListOf(path)
            val intent = Intent(holder.context, VideoPlayerActivity::class.java)
            ActivityUtils.startActivity(intent)
        }
        CoroutineScope(Dispatchers.IO).launch {
            val bitmap =
                GetVideoThumbnail.getVideoThumbnail(holder.context.contentResolver, uri)
            withContext(Dispatchers.Main) {
                Glide.with(holder.context)
                    .load(bitmap)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.itemImg)
            }
            this.cancel()
        }
    }
}