package com.clearpole.videoyou.utils

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import org.json.JSONArray
import org.json.JSONObject


class ReadMediaStore {
    companion object {
        @SuppressLint("Recycle")
        fun start(contentResolver: ContentResolver): JSONArray {
            val cursor: Cursor = contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                null
            )!!
            val indexVideoId = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val indexVideoSize = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
            val indexVideoTitle = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)
            val indexVideoPath = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            val indexVideoFolder = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)
            cursor.moveToPosition(-1)
            val array = JSONArray()
            while (cursor.moveToNext()) {
                val title = cursor.getString(indexVideoTitle)
                val size =
                    ByteToString.byteToString(cursor.getString(indexVideoSize).toLong())
                val path = cursor.getString(indexVideoPath)
                val videoUri = Uri.withAppendedPath(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    cursor.getString(indexVideoId)
                )
                val folder = cursor.getString(indexVideoFolder)
                val itemJson = JSONObject()
                itemJson.put("title",title)
                itemJson.put("size",size)
                itemJson.put("path",path)
                itemJson.put("uri",videoUri.toString())
                itemJson.put("folder",folder)
                array.put(itemJson)
            }
            cursor.close()
            return array
        }

        fun getFolder(contentResolver: ContentResolver): ArrayList<String> {
            val cursor: Cursor = contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                null
            )!!
            val indexVideoFolder = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)
            cursor.moveToPosition(-1)
            val array = ArrayList<String>()
            while (cursor.moveToNext()) {
                val folder = cursor.getString(indexVideoFolder)
                if (!array.contains(folder)) {
                    array.add(folder)
                }
            }
            cursor.close()
            return array
        }
    }
}