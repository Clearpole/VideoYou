package com.clearpole.videoyoux.logic.activity

import android.app.Activity
import android.content.Context
import com.clearpole.videoyoux.logic.utils.DatabaseStorage
import com.clearpole.videoyoux.ui.theme.utils.Toast
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class MainActivity {
    companion object {
        val PERMISSION = mutableListOf(Permission.READ_MEDIA_VIDEO)
        fun checkPermission(context: Context): Boolean {
            return XXPermissions.isGranted(context, PERMISSION)
        }
    }
}