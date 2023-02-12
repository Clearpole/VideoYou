@file:Suppress("DEPRECATION")

package com.clearpole.videoyou.model

import android.annotation.SuppressLint
import android.content.res.Resources
import android.view.View
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.clearpole.videoyou.R
import com.clearpole.videoyou.databinding.MediaStoreListItemBinding
import com.clearpole.videoyou.objects.VideoPlayObjects
import com.clearpole.videoyou.objects.VideoPlayerObjects
import com.drake.brv.BindingAdapter
import com.drake.brv.item.ItemBind
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.mutable

data class PlayerSideSheetModel(
    val path: String,
    val resources: Resources,
    val theme:Int,
    val item:Int
) : ItemBind {
    @SuppressLint("ResourceAsColor", "UseCompatLoadingForDrawables")
    override fun onBind(holder: BindingAdapter.BindingViewHolder) {
        val binding = MediaStoreListItemBinding.bind(holder.itemView)
        val chose = when (theme) {
            0 -> {
                R.color.color7
            }

            R.style.hzt -> {
                R.color.hzt_theme_light_primary
            }

            R.style.cxw -> {
                R.color.cxw_theme_light_primary
            }

            R.style.szy -> {
                R.color.szy_theme_light_primary
            }

            R.style.xfy -> {
                R.color.xfy_theme_light_primary
            }

            else -> {
                R.color.chose
            }
        }
        if (item==holder.layoutPosition){
            binding.itemBackground.background = resources.getDrawable(chose)
            VideoPlayerObjects.chose = binding.itemBackground
        }else{
            binding.itemBackground.background = resources.getDrawable(R.color.tm)
        }
        binding.itemName.text = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."))
        binding.itemName.maxLines = 3
        binding.itemName.textSize = 14f
        binding.itemName.setTextColor(android.graphics.Color.parseColor("#ffffff"))
        binding.itemSize.visibility = View.GONE
        binding.itemRoot.setOnClickListener {
            if (item!=holder.layoutPosition) {
                VideoPlayerObjects.player.seekToDefaultPosition(holder.layoutPosition)
                VideoPlayerObjects.chose!!.background = resources.getDrawable(R.color.tm)
                VideoPlayerObjects.chose = binding.itemBackground
            }
        }
        binding.itemRoot.setOnLongClickListener {
            try {
                VideoPlayerObjects.player.removeMediaItem(holder.layoutPosition)
                VideoPlayerObjects.rv!!.mutable.removeAt(holder.layoutPosition)
                VideoPlayerObjects.rv!!.bindingAdapter.notifyItemRemoved(holder.layoutPosition)
                VideoPlayObjects.list.removeAt(holder.layoutPosition)
                if (VideoPlayerObjects.player.mediaItemCount==0){
                    ToastUtils.showLong("无播放项目")
                }
            }catch (_:Exception){
                ToastUtils.showShort("删除失败，请重试")
            }
            false
        }

        Glide.with(holder.context).load(path).diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .transition(
                DrawableTransitionOptions.withCrossFade()
            ).into(binding.itemCover)

    }
}