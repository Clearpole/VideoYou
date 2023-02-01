package com.clearpole.videoyou.utils

import com.tencent.mmkv.MMKV

class SettingsItemsUntil {
    companion object{
        private val kV = MMKV.mmkvWithID("Setting")!!
        fun writeSettingData(key: String,value: String){
            kV.encode(key,value)
        }
        fun readSettingData(key: String): String? {
            return kV.decodeString(key)
        }
        fun initializationItems(){
            if (kV.decodeInt("first")==1){
            }else{
                kV.encode("darkMode","0")
                kV.encode("isAutoPicture","false")
                kV.encode("first",1)
                kV.encode("isDialogPlayer",true)
            }
        }
    }
}