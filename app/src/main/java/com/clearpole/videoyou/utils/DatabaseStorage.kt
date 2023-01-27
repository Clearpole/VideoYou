package com.clearpole.videoyou.utils

import android.content.ContentResolver
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray

class DatabaseStorage {
    companion object{
        private val kv = MMKV.mmkvWithID("MediaStore")
        fun writeDataToData(list : JSONArray): Boolean {
            var isSuccess = false
            CoroutineScope(Dispatchers.IO).launch {
                for (index in 0 until list.length()){
                    isSuccess = kv.encode(index.toString(),list[index].toString())
                }
            }
            return isSuccess
        }
        fun readDataByData(): JSONArray {
            val kV = kv.allKeys()
            val array = JSONArray()
            for (index in kV!!.indices){
                val jsonObject = kv.decodeString(kV[index])
                array.put(jsonObject)
            }
            return array
        }
        fun clearData(contentResolver: ContentResolver){
            kv.clear()
            writeDataToData(ReadMediaStore.start(contentResolver))
        }
    }
}