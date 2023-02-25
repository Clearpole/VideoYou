@file:Suppress("DEPRECATION")

package com.clearpole.videoyou.model

import android.annotation.SuppressLint
import android.content.res.Resources
import com.blankj.utilcode.util.ToastUtils
import com.clearpole.videoyou.R
import com.clearpole.videoyou.databinding.PlayListVideosItemBinding
import com.clearpole.videoyou.objects.AppObjects
import com.clearpole.videoyou.objects.VideoPlayObjects
import com.clearpole.videoyou.objects.VideoPlayerObjects
import com.drake.brv.BindingAdapter
import com.drake.brv.item.ItemBind
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.mutable

data class PlayerSideSheetModel(
    val path: String,
    val resources: Resources,
    val item:Int
) : ItemBind {
    @SuppressLint("ResourceAsColor", "UseCompatLoadingForDrawables")
    override fun onBind(holder: BindingAdapter.BindingViewHolder) {
        val binding = PlayListVideosItemBinding.bind(holder.itemView)
        val chose = when (AppObjects.theme) {
            0 -> {
                resources.getColor(R.color.color3)
            }

            1 -> {
                resources.getColor(R.color.hzt_theme_dark_primary)
            }

            2 -> {
                resources.getColor(R.color.cxw_theme_dark_primary)
            }

            3 -> {
                resources.getColor(R.color.szy_theme_dark_primary)
            }

            4 -> {
                resources.getColor(R.color.xfy_theme_dark_primary)
            }

            else -> {
                resources.getColor(R.color.chose)
            }
        }
        if (item==holder.layoutPosition){
            binding.title.setTextColor(chose)
            VideoPlayerObjects.chose = binding.title
        }else{
            binding.title.setTextColor(android.graphics.Color.parseColor("#ffffff"))
        }
        binding.title.text = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."))
        binding.title.maxLines = 3
        binding.title.textSize = 14f
        binding.root.setOnClickListener {
            if (item!=holder.layoutPosition) {
                VideoPlayerObjects.player.seekToDefaultPosition(holder.layoutPosition)
                VideoPlayerObjects.chose = binding.title
            }
        }
        binding.close.setOnClickListener {
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

    }
}