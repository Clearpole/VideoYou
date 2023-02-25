package com.clearpole.videoyou.objects

import android.graphics.Bitmap
import android.net.Uri
import com.thegrizzlylabs.sardineandroid.DavResource
import org.json.JSONObject

class VideoPlayObjects {
    companion object {
        lateinit var type: String
        lateinit var paths: String
        lateinit var title: String
        lateinit var cover: Bitmap
        lateinit var uri: Uri
        var list = mutableListOf<Any>()
    }
}