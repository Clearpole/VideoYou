package com.clearpole.videoyoux.logic.utils

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore
import android.text.TextUtils.substring
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject


class ReadMediaStore {
    companion object {
        @SuppressLint("Recycle")
        suspend fun start(contentResolver: ContentResolver): JSONArray {
            val array = JSONArray()
            withContext(Dispatchers.IO) {
                contentResolver.query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, null
                )!!.apply {
                    moveToPosition(-1)
                    while (moveToNext()) {
                        val itemJson = JSONObject()
                        itemJson.put(
                            "title", getString(getColumnIndexOrThrow(MediaStore.Video.Media.TITLE))
                        )
                        itemJson.put(
                            "size", getString(getColumnIndexOrThrow(MediaStore.Video.Media.SIZE))
                        )
                        itemJson.put(
                            "path", getString(getColumnIndexOrThrow(MediaStore.Video.Media.DATA))
                        )
                        itemJson.put(
                            "uri", Uri.withAppendedPath(
                                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                                getString(getColumnIndexOrThrow(MediaStore.Video.Media._ID))
                            )
                        )
                        itemJson.put(
                            "folder",
                            getString(getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME))
                        )
                        itemJson.put(
                            "duration",
                            getString(getColumnIndexOrThrow(MediaStore.Video.Media.DURATION))
                        )
                        itemJson.put(
                            "dateAdded",
                            getString(getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED))
                        )
                        array.put(itemJson)
                    }
                    close()
                }
            }
            return array
        }

        suspend fun getFolder(contentResolver: ContentResolver): JSONArray {
            val array = ArrayList<String>()
            val itemJson = JSONArray()
            withContext(Dispatchers.IO) {
                contentResolver.query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, null
                )!!.apply {
                    moveToPosition(-1)
                    while (moveToNext()) {
                        val folder =
                            getString(getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME))
                                ?: "根目录"
                        if (!array.contains(folder)) {
                            if (folder.isNotEmpty()) {
                                array.add(folder)
                                val json = JSONObject()
                                json.put(
                                    "name", folder
                                )
                                val data = getString(getColumnIndexOrThrow(MediaStore.Video.Media.DATA))
                                json.put(
                                    "path",
                                    data.substring(0,data.lastIndexOf("/"))
                                )
                                itemJson.put(json)
                            }
                        }
                    }
                    close()
                }

            }
            return itemJson
        }
    }
}