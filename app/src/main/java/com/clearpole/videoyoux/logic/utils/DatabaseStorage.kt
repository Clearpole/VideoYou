package com.clearpole.videoyoux.logic.utils

import android.content.ContentResolver
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray

class DatabaseStorage {
    companion object {
        private val kv = MMKV.mmkvWithID("MediaStore")
        private val kv_folder = MMKV.mmkvWithID("MediaStoreFolders")
        fun writeDataToData(list: JSONArray): Boolean {
            var isSuccess = false
            CoroutineScope(Dispatchers.IO).launch {
                for (index in 0 until list.length()) {
                    isSuccess = kv.encode(index.toString(), list[index].toString())
                }
            }
            return isSuccess
        }

        fun writeFolderToData(list: JSONArray) {
            CoroutineScope(Dispatchers.IO).launch {
                for (index in 0 until list.length()) {
                    kv_folder.encode(index.toString(), list.getJSONObject(index).toString())
                }
            }
        }

        suspend fun readDataByData(): JSONArray? = withContext(Dispatchers.IO) {
            try {
                val kV = kv.allKeys()
                val array = JSONArray()
                for (index in kV!!.indices) {
                    val jsonObject = kv.decodeString(kV[index])
                    array.put(jsonObject)
                }
                array
            } catch (e: Exception) {
                null
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

        suspend fun clearData(contentResolver: ContentResolver) {
            kv.clearAll()
            writeDataToData(ReadMediaStore.start(contentResolver))
        }
    }
}