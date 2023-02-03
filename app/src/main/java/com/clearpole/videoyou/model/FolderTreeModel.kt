package com.clearpole.videoyou.model

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.databinding.BaseObservable
import com.clearpole.videoyou.objects.MainObjects
import com.drake.brv.item.ItemExpand
import com.drake.brv.item.ItemHover
import com.drake.brv.item.ItemPosition
import org.json.JSONArray
import org.json.JSONObject

open class FolderTreeModel(
    private val contentResolver: ContentResolver,
    val path: String,
    private val json: JSONArray,
    private val openImg: Drawable,
    private val closeImg: Drawable
) : ItemExpand, ItemHover, ItemPosition,
    BaseObservable() {

    override var itemGroupPosition: Int = 0
    override var itemExpand: Boolean = false
        set(value) {
            field = value
            notifyChange()
        }

    override var itemSublist: List<Any?>?
        get() = jsonSublist
        set(value) {
            @Suppress("UNCHECKED_CAST")
            jsonSublist = value as List<FolderModel>
        }
    var jCount = 0
    private var jsonSublist: List<FolderModel> = mutableListOf<FolderModel>().apply {
        for (index in 0 until json.length()) {
            val objects = JSONObject(json.getString(index))
            if (objects.getString("folder") == title) {
                add(
                    FolderModel(
                        title = objects.getString("title"),
                        uri = Uri.parse(objects.getString("uri")),
                        contentResolver = contentResolver,
                        path = objects.getString("path")
                    )
                )
                jCount += 1
                MainObjects.count = jCount
            }
        }
    }

    override var itemHover: Boolean = true
    override var itemPosition: Int = 0


    val title get() = path
    val expandIcon
        @SuppressLint("ResourceType")
        get() = if (itemExpand) openImg else closeImg
    val count get() = jCount
}