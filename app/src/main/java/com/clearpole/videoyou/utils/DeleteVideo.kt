package com.clearpole.videoyou.utils

import android.content.Context
import android.provider.MediaStore
import com.blankj.utilcode.util.ToastUtils
import java.io.File


public fun deleteVideo(file: File,context: Context) {
    if (file.isFile) {
        val filePath = file.path
        val res: Int = context.contentResolver.delete(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            MediaStore.Audio.Media.DATA + "= \"" + filePath + "\"",
            null
        )
        if (res > 0) {
            file.delete()
        } else {
            ToastUtils.showShort("删除失败")
        }
        return
    }
}