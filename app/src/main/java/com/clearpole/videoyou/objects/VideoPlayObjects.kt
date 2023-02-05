package com.clearpole.videoyou.objects

import com.thegrizzlylabs.sardineandroid.DavResource
import org.json.JSONObject

class VideoPlayObjects {
    companion object {
        lateinit var type: String
        lateinit var paths: String
        lateinit var title: String
        var list = listOf<Any>()
    }
}