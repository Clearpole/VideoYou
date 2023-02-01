package com.clearpole.videoyou.model

import android.annotation.SuppressLint
import com.blankj.utilcode.util.AppUtils
import com.clearpole.videoyou.R
import com.clearpole.videoyou.databinding.SettingThemeItemBinding
import com.drake.brv.BindingAdapter
import com.drake.brv.item.ItemBind
import com.google.android.material.R.style.MaterialAlertDialog_Material3
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tencent.mmkv.MMKV

data class SettingThemeModel(val name: String, val type: Int) : ItemBind {
    @SuppressLint("ResourceAsColor", "UseCompatLoadingForDrawables")
    override fun onBind(holder: BindingAdapter.BindingViewHolder) {
        val binding = holder.getBinding<SettingThemeItemBinding>()
        binding.name.text = name
        val theme = MMKV.mmkvWithID("theme").decodeInt("theme")
        if (type == theme) {
            binding.cardView.isChecked = true
        }
        when (type) {
            0 -> {
                binding.mainBack.background = holder.context.getDrawable(R.color.color1)
                binding.floating.background = holder.context.getDrawable(R.color.color4)
            }

            R.style.hzt -> {
                binding.mainBack.background = holder.context.getDrawable(R.color.hzt_theme_dark_primary)
                binding.floating.background = holder.context.getDrawable(R.color.hzt_theme_light_primary)
            }

            R.style.cxw -> {
                binding.mainBack.background = holder.context.getDrawable(R.color.cxw_theme_dark_primary)
                binding.floating.background = holder.context.getDrawable(R.color.cxw_theme_light_primary)
            }

            R.style.szy -> {
                binding.mainBack.background = holder.context.getDrawable(R.color.szy_theme_dark_primary)
                binding.floating.background = holder.context.getDrawable(R.color.szy_theme_light_primary)
            }

            R.style.xfy -> {
                binding.mainBack.background = holder.context.getDrawable(R.color.xfy_theme_dark_primary)
                binding.floating.background = holder.context.getDrawable(R.color.xfy_theme_light_primary)
            }
        }
        binding.cardView.setOnClickListener {
            if (theme != type) {
                MMKV.mmkvWithID("theme").encode("theme", type)
                MaterialAlertDialogBuilder(holder.context, MaterialAlertDialog_Material3)
                    .setTitle(name)
                    .setMessage("您已选择主题：$name，重启即可生效。")
                    .setNegativeButton("重启") { _, _ ->
                        AppUtils.relaunchApp()
                    }
                    .setCancelable(false)
                    .show()
            }
        }
    }
}