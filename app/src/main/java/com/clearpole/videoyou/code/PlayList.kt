package com.clearpole.videoyou.code

import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.TimeUtils
import com.tencent.mmkv.MMKV
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PlayList {
    companion object{
        private val mainKv = MMKV.mmkvWithID("videoyou-playlist-name")
        // 用于存放视频列表的名称，仅存放名称
        fun addList(name:String){
            val json = JSONObject()
            json.put("time",
                SimpleDateFormat("yyyy-MM-dd-HH:mm", Locale.getDefault()).format(Date()))
            json.put("index",mainKv.count()+1)
            json.put("count",0)
            mainKv.putString(name,json.toString())
            // 将名称存入储存库，赋值的是它的索引
        }
        fun readList(): Array<out String>? {
            return mainKv.allKeys()
            // 返回名称存储库内所有的名称
        }
        fun readListContent(name: String): String? {
            return mainKv.decodeString(name)
        }
        fun removeList(name:String): Boolean {
            val mmkv = MMKV.mmkvWithID(name)
            for (index in 0 until mmkv.allKeys()!!.size){
                mmkv.remove(mmkv.allKeys()!![index])
            }
            return mainKv.remove(name).commit()
            // 返回是否删除成功
        }
    }
}