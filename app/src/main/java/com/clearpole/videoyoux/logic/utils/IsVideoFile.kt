package com.clearpole.videoyoux.logic.utils

import com.blankj.utilcode.util.StringUtils
import com.drake.tooltip.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class IsVideoFile {
    companion object {
        suspend fun start(any: String): Boolean {
            var allow = false
            var index = -1
            withContext(Dispatchers.IO) {
                val suffix = any.substring(any.lastIndexOf(".") + 1, any.length)
                val allowSuffix = arrayListOf("mp4", "flv")
                    while (any.contains(".")){
                        index += 1
                        allow = StringUtils.equalsIgnoreCase(allowSuffix[index], suffix)
                        if (allow){
                            break
                        }
                    }
            }
            return allow
        }
    }
}