package com.clearpole.videoyou

import android.os.Bundle
import com.clearpole.videoyou.databinding.ActivitySettingBinding
import com.clearpole.videoyou.utils.IsNightMode
import com.gyf.immersionbar.ImmersionBar

class SettingActivity : BaseActivity<ActivitySettingBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImmersionBar.with(this).transparentBar()
            .statusBarDarkFont(!IsNightMode.isNightMode(resources)).init()
        binding.topAppBar.title = intent.getStringExtra("name")
        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }
    }
}