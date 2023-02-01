package com.clearpole.videoyou.utils

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore


class GetVideoThumbnail {
    companion object {
        @Suppress("DEPRECATION", "UNUSED_EXPRESSION")
        @SuppressLint("Range")
        fun getVideoThumbnail(cr: ContentResolver, uri: Uri?): Bitmap? {
            return try {
                var bitmap: Bitmap?
                val options = BitmapFactory.Options()
                options.inDither = false
                options.inPreferredConfig = Bitmap.Config.RGB_565
                val cursor = cr.query(uri!!, arrayOf(MediaStore.Video.Media._ID), null, null, null)
                if (cursor == null || cursor.count == 0) {
                    null
                }
                cursor!!.moveToFirst()
                val videoId = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID))
                    ?: null //image id in image table.s
                cursor.close()
                val videoIdLong = videoId!!.toLong()
                bitmap = MediaStore.Video.Thumbnails.getThumbnail(
                    cr, videoIdLong, MediaStore.Images.Thumbnails.MINI_KIND, options
                )
                bitmap
            } catch (e: Exception) {
                null
            }
        }
    }
}