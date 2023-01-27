package com.clearpole.videoyou

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.developer.crashx.config.CrashConfig
import com.dylanc.viewbinding.base.ViewBindingUtil
import com.google.android.material.color.DynamicColors
import com.hjq.toast.ToastUtils
import com.tencent.mmkv.MMKV

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {
    lateinit var binding: VB
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ToastUtils.init(this.application)
        DynamicColors.applyToActivityIfAvailable(this)
        binding = ViewBindingUtil.inflateWithGeneric(this, layoutInflater)
        MMKV.initialize(this)
        setContentView(binding.root)
       /* CrashConfig.Builder.create()
            .errorActivity(ErrorCrashActivity::class.java)*/
    }
}