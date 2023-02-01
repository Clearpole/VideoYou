package com.clearpole.videoyou.model

import android.content.Intent
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.clearpole.videoyou.SettingActivity
import com.clearpole.videoyou.databinding.MainPageSettingItemBinding
import com.drake.brv.BindingAdapter
import com.drake.brv.item.ItemBind

data class MainSettingModel(val title: String, val info: String, val img: Drawable) : ItemBind {
    override fun onBind(holder: BindingAdapter.BindingViewHolder) {
        val binding = holder.getBinding<MainPageSettingItemBinding>()
        binding.title.text = title
        binding.info.text = info
        Glide.with(holder.context).load(img).transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.sLI)
        binding.root.setOnClickListener {
            val intent = Intent(holder.context,SettingActivity::class.java)
            intent.putExtra("name",title)
            holder.context.startActivity(intent)
        }
    }
}