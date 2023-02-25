package com.clearpole.videoyou.utils

import com.tencent.mmkv.MMKV

class SettingsItemsUntil {
    companion object {
        private val kV = MMKV.mmkvWithID("Setting")!!
        private val settings = arrayListOf(
            arrayListOf("darkMode", "0"),
            arrayListOf("isAutoPicture", "false"),
            arrayListOf("isDialogPlayer", "false"),
            arrayListOf("isScreenOn", "true"),
            arrayListOf("isAutoExit", "false")
        )

        fun writeSettingData(key: String, value: String) {
            kV.encode(key, value)
        }

        fun readSettingData(key: String): String? {
            return kV.decodeString(key)
        }

        fun fixItems() {
            for (index in 0 until settings.size) {
                if (kV.decodeString(settings[index][0]).isNullOrEmpty()) {
                    kV.encode(settings[index][0], settings[index][1])
                }
            }
        }
    }
}