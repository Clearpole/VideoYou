package com.clearpole.videoyou

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.clearpole.videoyou.databinding.ActivitySettingBinding
import com.clearpole.videoyou.model.SettingThemeModel
import com.clearpole.videoyou.utils.IsNightMode
import com.clearpole.videoyou.utils.SettingsItemsUntil
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.google.android.material.R.style.MaterialAlertDialog_Material3
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.materialswitch.MaterialSwitch
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
        when (intent.getStringExtra("name")) {
            "通用" -> {
                currency()
            }

            "主题" -> {
                theme()
            }

            "手势" -> {

            }

            "关于" -> {
                about()
            }
        }
    }

    @SuppressLint("MissingInflatedId")
    private fun about() {
        binding.settingAbout.root.visibility = View.VISIBLE
        binding.settingAbout.intoQq.setOnClickListener {
            MaterialAlertDialogBuilder(
                this, MaterialAlertDialog_Material3
            ).setTitle("加入Telegram群组").setCancelable(false)
                .setMessage("加入频道可获取最新版本更新，是否访问外部链接以加入TG频道？")
                .setNegativeButton("取消") { _, _ -> }.setPositiveButton("让我访问！") { _, _ ->
                    val uri = Uri.parse("https://t.me/VideoYouNotice")
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(intent)
                }.show()
        }
        binding.settingAbout.intoWeb.setOnClickListener {
            val uri = Uri.parse("https://clearpole.gitee.io/videoyou-website/")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
        binding.settingAbout.intoDon.setOnClickListener {
            val view = this.layoutInflater.inflate(R.layout.setting_about_do, null)
            Glide.with(this)
                .load("https://img-blog.csdnimg.cn/fe506848b4514d9b84ad4f67358866e6.png")
                .into(view.findViewById(R.id.don))
            MaterialAlertDialogBuilder(this, MaterialAlertDialog_Material3).setTitle("捐赠")
                .setMessage("捐赠并不会对你带来任何好处，但可以让开发者坚定维护软件的信心。您的捐赠信息会被投放在VideoYou的官方频道。感谢每一位捐赠的人！")
                .setView(view).setNegativeButton("确定") { _, _ -> }
                .setPositiveButton("打开微信") { _, _ ->
                    val intent: Intent =
                        this.packageManager.getLaunchIntentForPackage("com.tencent.mm")!!
                    if (this.packageManager
                            .resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null
                    ) {
                        intent.putExtra("LauncherUI.From.Scaner.Shortcut", true)
                        intent.action = "android.intent.action.VIEW"
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    } else {
                        ToastUtils.showShort("打开失败啦")
                    }
                }.show()
        }
    }

    private fun currency() {
        binding.settingCurrency.root.visibility = View.VISIBLE
        switchSet(binding.settingCurrency.screenOn, "isScreenOn")
        switchSet(binding.settingCurrency.autoEdit, "isAutoExit")
        switchSet(binding.settingCurrency.sDBL, "isAutoPicture")
        switchSet(binding.settingCurrency.sDBV, "isDialogPlayer")
    }

    private fun theme() {
        binding.settingTheme.root.visibility = View.VISIBLE
        binding.settingTheme.themeListview.linear().setup {
            addType<SettingThemeModel> { R.layout.setting_theme_item }
        }.models = themeModels()
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.settingTheme.themeListview.layoutManager = layoutManager
        val mode = SettingsItemsUntil.readSettingData("darkMode")?.toInt()!!
        binding.settingTheme.settingThemeDarkMode.text = when (mode) {
            0 -> {
                "跟随系统"
            }

            1 -> {
                "始终开启"
            }

            2 -> {
                "始终关闭"
            }

            else -> {
                "错误"
            }
        }

        binding.settingTheme.settingThemeDayNightMode.setOnClickListener {
            val choices = arrayOf<CharSequence>("跟随系统", "始终开启", "始终关闭")
            MaterialAlertDialogBuilder(this).setTitle("深色模式").setSingleChoiceItems(
                choices, SettingsItemsUntil.readSettingData("darkMode")!!.toInt(), null
            ).setPositiveButton("确定") { dialog: DialogInterface, _: Int ->
                val checkedItemPosition: Int = (dialog as AlertDialog).listView.checkedItemPosition
                if (checkedItemPosition != AdapterView.INVALID_POSITION) {
                    binding.settingTheme.settingThemeDarkMode.text = choices[checkedItemPosition]
                    SettingsItemsUntil.writeSettingData(
                        "darkMode", checkedItemPosition.toString()
                    )
                }
                when (checkedItemPosition) {
                    0 -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    }

                    1 -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    }

                    else -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    }
                }
            }.show()
        }
    }

    private fun themeModels(): MutableList<Any> {
        return mutableListOf<Any>().apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                add(SettingThemeModel("动态颜色", 0))
            }
            add(SettingThemeModel("和紫棠", R.style.hzt))
            add(SettingThemeModel("晨夕雾", R.style.cxw))
            add(SettingThemeModel("深竹月", R.style.szy))
            add(SettingThemeModel("绣绯樱", R.style.xfy))
        }
    }

    private fun switchSet(view: MaterialSwitch, setItemName: String) {
        view.isChecked = SettingsItemsUntil.readSettingData(setItemName).toBoolean()
        view.setOnClickListener {
            val isTrue = SettingsItemsUntil.readSettingData(setItemName)
            view.isChecked = !isTrue.toBoolean()
            SettingsItemsUntil.writeSettingData(
                setItemName, isTrue.toBoolean().not().toString()
            )
        }
    }
}