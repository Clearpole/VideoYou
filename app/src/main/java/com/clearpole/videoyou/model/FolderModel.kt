package com.clearpole.videoyou.model

import android.content.ContentResolver
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import com.clearpole.videoyou.utils.GetVideoThumbnail
import com.drake.brv.item.ItemExpand


open class FolderModel(
    override var itemGroupPosition: Int = 0,
    override var itemExpand: Boolean = false,
    override var itemSublist: List<Any?>? = null,
    val title : String,
    val uri : Uri,
    val contentResolver: ContentResolver,
    val path : String
) : ItemExpand{
    val videoTitle get() = title
    @Suppress("DEPRECATION")
    val videoTum = BitmapDrawable(GetVideoThumbnail.getVideoThumbnail(cr = contentResolver, uri = uri))

}