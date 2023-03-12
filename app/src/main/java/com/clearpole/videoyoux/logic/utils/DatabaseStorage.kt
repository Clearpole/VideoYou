package com.clearpole.videoyoux.logic.utils

import com.tencent.mmkv.MMKV
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray

class DatabaseStorage {
    companion object {
        private val kv_folder = MMKV.mmkvWithID("MediaStoreFolders")

        fun writeFolderToData(list: JSONArray) {
            CoroutineScope(Dispatchers.IO).launch {
                kv_folder.clearAll()
                for (index in 0 until list.length()) {
                    kv_folder.encode(index.toString(), list.getJSONObject(index).toString())
                }
            }
        }

        suspend fun readFolderByData(): JSONArray? = withContext(Dispatchers.IO) {
            try {
                val kV = kv_folder.allKeys()
                val array = JSONArray()
                for (index in kV!!.indices) {
                    val jsonObject = kv_folder.decodeString(kV[index])
                    array.put(jsonObject)
                }
                array
            } catch (e: Exception) {
                null
            }
        }
    }
}