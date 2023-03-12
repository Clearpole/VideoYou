package com.clearpole.videoyoux.logic.utils

import com.blankj.utilcode.util.StringUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class IsVideoFile {
    companion object {
        suspend fun start(any: String): Boolean {
            var allow = false
            var index = -1
            withContext(Dispatchers.IO) {
                val suffix = any.substring(any.lastIndexOf(".") + 1, any.length)
                val allowSuffix = arrayListOf(
                    "AVI",
                    "FLV",
                    "MOV",
                    "MKV",
                    "MP4",
                    "MPEG",
                    "MPG",
                    "VOB",
                    "WMV",
                    "3GP",
                    "ASF",
                    "DIVX",
                    "F4V",
                    "M4V",
                    "WEBM",
                    "MXF",
                    "OGG",
                    "TS",
                    "M2TS",
                    "SWF"
                )
                while (any.contains(".")) {
                    index += 1
                    allow = StringUtils.equalsIgnoreCase(allowSuffix[index], suffix)
                    if (allow) {
                        break
                    }
                }
            }
            return allow
        }
    }
}