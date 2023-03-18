package com.clearpole.videoyoux.logic.activity

import android.content.Context
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions

class MainActivity {
    companion object {
        val PERMISSION = mutableListOf(Permission.READ_MEDIA_VIDEO)
        fun checkPermission(context: Context): Boolean {
            return XXPermissions.isGranted(context, PERMISSION)
        }
    }
}