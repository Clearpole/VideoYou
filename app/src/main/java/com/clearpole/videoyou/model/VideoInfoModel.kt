package com.clearpole.videoyou.model

import com.blankj.utilcode.util.ClipboardUtils
import com.clearpole.videoyou.databinding.VideoInfoItemBinding
import com.drake.brv.BindingAdapter
import com.drake.brv.item.ItemBind
import com.google.android.material.dialog.MaterialAlertDialogBuilder

data class VideoInfoModel(val title: String, val info: String) : ItemBind {
    override fun onBind(holder: BindingAdapter.BindingViewHolder) {
        val binding = holder.getBinding<VideoInfoItemBinding>()
        binding.name.text = title
        binding.info.text = info
        binding.root.setOnClickListener {
            MaterialAlertDialogBuilder(holder.context).setTitle(
                title
            ).setMessage(info).setPositiveButton("复制") { _, _ ->
                ClipboardUtils.copyText(info)
            }.show()
        }
    }
}