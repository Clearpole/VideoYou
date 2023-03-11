package com.clearpole.videoyoux.logic.utils

import java.text.DecimalFormat

class ByteToString {
    companion object {
        fun byteToString(size: Long): String {
            val gB = (1024 * 1024 * 1024).toLong()
            val mB = (1024 * 1024).toLong()
            val kB: Long = 1024
            val df = DecimalFormat("0.00")
            val resultSize: String = if (size / gB >= 1) {
                df.format(size / gB.toFloat()) + " GB   "
            } else if (size / mB >= 1) {
                df.format(size / mB.toFloat()) + " MB   "
            } else if (size / kB >= 1) {
                df.format(size / kB.toFloat()) + " KB   "
            } else {
                "$size B   "
            }
            return resultSize
        }
    }
}