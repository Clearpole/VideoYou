package com.clearpole.videoyou

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.viewbinding.ViewBinding
import com.clearpole.videoyou.objects.AppObjects
import com.clearpole.videoyou.utils.SettingsItemsUntil
import com.developer.crashx.config.CrashConfig
import com.dylanc.viewbinding.base.ViewBindingUtil
import com.google.android.material.color.DynamicColors
import com.tencent.mmkv.MMKV


abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {
    lateinit var binding: VB
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MMKV.initialize(this)
        val kv = MMKV.mmkvWithID("theme")
        val theme = kv.decodeInt("theme")
        if (theme.toString().isEmpty()) {
            // 第一次进入
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // 安卓版本大于12
                kv.encode("theme", 0)
                // 动态颜色
                DynamicColors.applyToActivityIfAvailable(this)
            } else {
                // 安卓版本小于12
                kv.encode("theme", R.style.hzt)
                setTheme(R.style.hzt)
                // 默认主题和紫棠
            }
        } else {
            // 不是第一次进入
            when (theme) {
                0 -> {
                    DynamicColors.applyToActivityIfAvailable(this)
                    AppObjects.theme = 0
                }

                R.style.hzt -> {
                    setTheme(R.style.hzt)
                    AppObjects.theme = 1
                }

                R.style.cxw -> {
                    setTheme(R.style.cxw)
                    AppObjects.theme = 2
                }

                R.style.szy -> {
                    setTheme(R.style.szy)
                    AppObjects.theme = 3
                }

                R.style.xfy -> {
                    setTheme(R.style.xfy)
                    AppObjects.theme = 4
                }
            }
        }
        try {
            when (SettingsItemsUntil.readSettingData("darkMode")?.toInt()!!) {
                0 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }

                1 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }

                2 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
        } catch (_: Exception) {
        }
        binding = ViewBindingUtil.inflateWithGeneric(this, layoutInflater)
        SettingsItemsUntil.fixItems()
        setContentView(binding.root)
        CrashConfig.Builder.create().errorActivity(ErrorCrashActivity::class.java)
    }
}