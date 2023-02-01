package com.clearpole.videoyou

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.clearpole.videoyou.databinding.ActivityErrorCrashBinding
import com.clearpole.videoyou.utils.IsNightMode
import com.developer.crashx.CrashActivity
import com.gyf.immersionbar.ImmersionBar

class ErrorCrashActivity : BaseActivity<ActivityErrorCrashBinding>() {
    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImmersionBar.with(this).transparentBar()
            .statusBarDarkFont(!IsNightMode.isNightMode(resources)).init()
        binding.errorLog.text = CrashActivity.getAllErrorDetailsFromIntent(this,intent)
    }
}