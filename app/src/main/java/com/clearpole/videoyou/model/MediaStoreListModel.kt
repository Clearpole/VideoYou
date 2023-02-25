package com.clearpole.videoyou.model

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import com.blankj.utilcode.util.ActivityUtils.startActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.clearpole.videoyou.VideoInfoActivity
import com.clearpole.videoyou.VideoPlayerActivity
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
    val mainBind: ActivityMainBinding?,
    val activity: Activity
) : ItemBind {
    override fun onBind(holder: BindingAdapter.BindingViewHolder) {
        val binding = MediaStoreListItemBinding.bind(holder.itemView)
        binding.itemName.text = title
        binding.itemSize.text = size
        binding.itemRoot.setOnClickListener {
            VideoPlayObjects.paths = path
            VideoPlayObjects.title = title
            VideoPlayObjects.type = "LOCAL"
            VideoPlayObjects.list = mutableListOf(path)
            VideoPlayObjects.uri = uri
            val intent = Intent(holder.context, VideoInfoActivity::class.java)
            val bundle: Bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                activity,
                binding.card,
                "VideoYouInfoImageName"
            ).toBundle()!!
            intent.data = uri
            intent.putExtra("int","0")
            startActivity(intent, bundle)
        }

        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                Glide.with(holder.context).load(uri)
                    .transition(DrawableTransitionOptions.withCrossFade()).into(binding.itemCover)
            }
            this.cancel()
        }

    }
}