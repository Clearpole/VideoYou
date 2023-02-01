package com.clearpole.videoyou

import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.clearpole.videoyou.databinding.ActivitySettingBinding
import com.clearpole.videoyou.model.SettingThemeModel
import com.clearpole.videoyou.utils.IsNightMode
import com.clearpole.videoyou.utils.SettingsItemsUntil
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
        }
    }

    private fun currency() {
        binding.settingCurrency.root.visibility = View.VISIBLE
        binding.settingCurrency.sDBL.isChecked = SettingsItemsUntil.readSettingData("isAutoPicture").toBoolean()
        binding.settingCurrency.isAutoPicRoot.setOnClickListener {
            val isTrue = SettingsItemsUntil.readSettingData("isAutoPicture")
            binding.settingCurrency.sDBL.isChecked = !isTrue.toBoolean()
            SettingsItemsUntil.writeSettingData("isAutoPicture",isTrue.toBoolean().not().toString())
        }
        binding.settingCurrency.sDBV.isChecked = SettingsItemsUntil.readSettingData("isDialogPlayer").toBoolean()
        binding.settingCurrency.isDialogPlayerRoot.setOnClickListener {
            val isTrue = SettingsItemsUntil.readSettingData("isDialogPlayer")
            binding.settingCurrency.sDBV.isChecked = !isTrue.toBoolean()
            SettingsItemsUntil.writeSettingData("isDialogPlayer",isTrue.toBoolean().not().toString())
        }
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
        binding.settingTheme.settingThemeDarkMode.text =
            when (mode) {
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
            MaterialAlertDialogBuilder(this)
                .setTitle("深色模式")
                .setSingleChoiceItems(
                    choices,
                    SettingsItemsUntil.readSettingData("darkMode")!!.toInt(),
                    null
                )
                .setPositiveButton("确定") { dialog: DialogInterface, _: Int ->
                    val checkedItemPosition: Int =
                        (dialog as AlertDialog).listView.checkedItemPosition
                    if (checkedItemPosition != AdapterView.INVALID_POSITION) {
                        binding.settingTheme.settingThemeDarkMode.text =
                            choices[checkedItemPosition]
                        SettingsItemsUntil.writeSettingData(
                            "darkMode",
                            checkedItemPosition.toString()
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
}