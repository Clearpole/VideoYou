package com.clearpole.videoyou.code

import android.annotation.SuppressLint
import com.blankj.utilcode.util.TimeUtils
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class PlayList {
    companion object {
        private val mainKv = MMKV.mmkvWithID("videoyou-playlist-name")
        private val nextKv = MMKV.mmkvWithID("videoyou-playlist-content")

        // 用于存放视频列表的名称，仅存放名称
        fun addList(name: String) {
            val json = JSONObject()
            json.put(
                "time", TimeUtils.getNowMills()
            )
            json.put("index", mainKv.count() + 1)
            json.put("count", 0)
            mainKv.putString(name, json.toString())
            // 将名称存入储存库，赋值的是它的索引
        }

        @SuppressLint("SimpleDateFormat")
        fun readList(): List<String> {
            return mainKv.allKeys()!!.sortedBy {
                TimeUtils.millis2Date(
                    JSONObject(
                        mainKv.decodeString(it).toString()
                    ).getLong("time")
                )
            }.reversed()
            // 返回名称存储库内所有的名称
        }

        fun readListContent(name: String): String? {
            return mainKv.decodeString(name)
        }

        fun removeList(name: String) {
            mainKv.remove(name)
            nextKv.remove(name)
        }

        fun addPlayListContent(name: String, list: ArrayList<String>) {
            if (!nextKv.decodeString(name).isNullOrEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    val late = JSONObject(nextKv.decodeString(name)!!)
                    val keys = JSONObject(nextKv.decodeString(name)!!).keys()
                    while (keys.hasNext()) {
                        val time = keys.next().toString()
                        val uri = late.getString(time)
                        if (list.contains(uri)) {
                            late.remove(time)
                        }
                    }
                    withContext(Dispatchers.IO) {
                        list.forEachIndexed { _, s ->
                            late.put(
                                TimeUtils.getNowMills().toString() + ":${(0..9999999).random()}", s
                            )
                            nextKv.encode(name, late.toString())
                        }
                    }
                }
            } else {
                val json = JSONObject()
                list.forEachIndexed { _, s ->
                    json.put(TimeUtils.getNowMills().toString() + ":${(0..9999999).random()}", s)
                    nextKv.encode(name, json.toString())
                }
            }
        }

        fun readPlayListContent(name: String): String? {
            return nextKv.decodeString(name)
        }
    }
}