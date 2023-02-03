package com.clearpole.videoyou.model

import android.content.Intent
import android.net.Uri
import com.blankj.utilcode.util.ActivityUtils.startActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.clearpole.videoyou.VideoPlayer
import com.clearpole.videoyou.databinding.ActivityMainBinding
import com.clearpole.videoyou.databinding.MediaStoreListItemBinding
import com.clearpole.videoyou.objects.VideoPlayObjects
import com.clearpole.videoyou.utils.GetVideoThumbnail
import com.drake.brv.BindingAdapter
import com.drake.brv.item.ItemBind
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class MediaStoreListModel(
    val title: String,
    val size: String,
    val uri: Uri,
    val path: String,
    val mainBind: ActivityMainBinding?
) : ItemBind {
    override fun onBind(holder: BindingAdapter.BindingViewHolder) {
        val binding = MediaStoreListItemBinding.bind(holder.itemView)
        binding.itemName.text = title
        binding.itemSize.text = size
        binding.itemRoot.setOnClickListener {
            VideoPlayObjects.paths = path
            VideoPlayObjects.title = title
            VideoPlayObjects.type = "LOCAL"
            val intent = Intent(holder.context, VideoPlayer::class.java)
            startActivity(intent)
        }

        CoroutineScope(Dispatchers.IO).launch {
            val bitmap = GetVideoThumbnail.getVideoThumbnail(holder.context.contentResolver, uri)
            withContext(Dispatchers.Main) {
                Glide.with(holder.context).load(bitmap)
                    .transition(DrawableTransitionOptions.withCrossFade()).into(binding.itemCover)
            }
            this.cancel()
        }

    }
}