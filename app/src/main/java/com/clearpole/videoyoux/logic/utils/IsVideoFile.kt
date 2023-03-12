package com.clearpole.videoyoux.logic.utils

import com.blankj.utilcode.util.StringUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class IsVideoFile {
    companion object {
        suspend fun start(any: String): Boolean {
            return withContext(Dispatchers.IO) {
                val suffix = any.substring(any.lastIndexOf(".") + 1, any.length)
                if (StringUtils.equalsIgnoreCase("AVI", suffix)) {
                    true
                } else if (StringUtils.equalsIgnoreCase("FLV", suffix)) {
                    true
                } else if (StringUtils.equalsIgnoreCase("MOV", suffix)) {
                    true
                } else if (StringUtils.equalsIgnoreCase("MKV", suffix)) {
                    true
                } else if (StringUtils.equalsIgnoreCase("MP4", suffix)) {
                    true
                } else if (StringUtils.equalsIgnoreCase("MPEG", suffix)) {
                    true
                } else if (StringUtils.equalsIgnoreCase("MPG", suffix)) {
                    true
                } else if (StringUtils.equalsIgnoreCase("VOB", suffix)) {
                    true
                } else if (StringUtils.equalsIgnoreCase("WMV", suffix)) {
                    true
                } else if (StringUtils.equalsIgnoreCase("3GP", suffix)) {
                    true
                } else if (StringUtils.equalsIgnoreCase("ASF", suffix)) {
                    true
                } else if (StringUtils.equalsIgnoreCase("DIVX", suffix)) {
                    true
                } else if (StringUtils.equalsIgnoreCase("F4V", suffix)) {
                    true
                } else if (StringUtils.equalsIgnoreCase("M4V", suffix)) {
                    true
                } else if (StringUtils.equalsIgnoreCase("WEBM", suffix)) {
                    true
                } else if (StringUtils.equalsIgnoreCase("MXF", suffix)) {
                    true
                } else if (StringUtils.equalsIgnoreCase("OGG", suffix)) {
                    true
                } else if (StringUtils.equalsIgnoreCase("TS", suffix)) {
                    true
                } else if (StringUtils.equalsIgnoreCase("M2TS", suffix)) {
                    true
                } else StringUtils.equalsIgnoreCase("SWF", suffix)
            }

        }
    }
}