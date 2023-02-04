package com.clearpole.videoyou.utils

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject


class ReadMediaStore {
    companion object {
        @SuppressLint("Recycle")
        fun start(contentResolver: ContentResolver): JSONArray {
            val cursor: Cursor = contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, null
            )!!
            val indexVideoId = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val indexVideoSize = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
            val indexVideoTitle = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)
            val indexVideoPath = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            val indexVideoFolder =
                cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)
            cursor.moveToPosition(-1)
            val array = JSONArray()
            while (cursor.moveToNext()) {
                val title = cursor.getString(indexVideoTitle)
                val size = cursor.getString(indexVideoSize)
                val path = cursor.getString(indexVideoPath)
                val videoUri = Uri.withAppendedPath(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI, cursor.getString(indexVideoId)
                )
                val folder = cursor.getString(indexVideoFolder)
                val itemJson = JSONObject()
                itemJson.put("title", title)
                itemJson.put("size", size)
                itemJson.put("path", path)
                itemJson.put("uri", videoUri.toString())
                itemJson.put("folder", folder)
                array.put(itemJson)
            }
            cursor.close()
            return array
        }

        suspend fun getFolder(contentResolver: ContentResolver): ArrayList<String> {
            val array = ArrayList<String>()
            withContext(Dispatchers.IO) {
                try {
                    val cursor: Cursor = contentResolver.query(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, null
                    )!!
                    val indexVideoFolder =
                        cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)
                    cursor.moveToPosition(-1)
                    while (cursor.moveToNext()) {
                        val folder = cursor.getString(indexVideoFolder)?:"根目录"
                        if (!array.contains(folder)) {
                            if (folder.isNotEmpty()) {
                                array.add(folder)
                            }
                        }
                    }
                    cursor.close()
                } catch (e: Exception) {
                    array.add("")
                    withContext(Dispatchers.Main) {
                        //ToastUtils.show("文件所属加载错误")
                    }
                }
            }
            return array
        }
    }
}