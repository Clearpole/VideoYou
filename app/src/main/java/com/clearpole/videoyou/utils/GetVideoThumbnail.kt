package com.clearpole.videoyou.utils

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore

class GetVideoThumbnail {
    companion object {
        @Suppress("DEPRECATION")
        @SuppressLint("Range")
        fun getVideoThumbnail(cr: ContentResolver, uri: Uri?): Bitmap? {
            val bitmap: Bitmap?
            val options = BitmapFactory.Options()
            options.inDither = false
            options.inPreferredConfig = Bitmap.Config.ARGB_8888
            val cursor = cr.query(uri!!, arrayOf(MediaStore.Video.Media._ID), null, null, null)
            if (cursor == null || cursor.count == 0) {
                return null
            }
            cursor.moveToFirst()
            val videoId = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID))
                ?: return null //image id in image table.s
            cursor.close()
            val videoIdLong = videoId.toLong()
            bitmap = MediaStore.Video.Thumbnails.getThumbnail(
                cr,
                videoIdLong,
                MediaStore.Images.Thumbnails.MINI_KIND,
                options
            )
            return bitmap
        }
    }
}