package com.clearpole.videoyoux.logic.activity

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.navigation.NavHostController
import com.clearpole.videoyoux.logic.NavHost
import com.clearpole.videoyoux.ui.GuideActivity
import com.clearpole.videoyoux.ui.theme.utils.Toast
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
class GetPermission {
    companion object {
        fun getPermission(
            context: Context,
            activity: Activity,
            step:MutableState<String>,
            navController: NavHostController
        ) {
            XXPermissions.with(context).permission(MainActivity.PERMISSION)
                .request(object : OnPermissionCallback {
                    override fun onGranted(
                        permissions: MutableList<String>, allGranted: Boolean
                    ) {
                        if (!allGranted) {
                            Toast.showWarning(
                                activity, "仍有部分权限未授予", "获取部分权限成功"
                            )
                            return
                        }
                        navController.navigate(NavHost.NAV_GUIDE_WRITE_DATA)
                        step.value = GuideActivity.STEP_WRITE_DATA
                    }

                    override fun onDenied(
                        permissions: MutableList<String>, doNotAskAgain: Boolean
                    ) {
                        if (doNotAskAgain) {
                            Toast.showWarning(
                                activity, "请手动开启", "您拒绝了权限"
                            )
                            XXPermissions.startPermissionActivity(
                                activity, permissions
                            )
                        } else {
                            Toast.showError(
                                activity, "请重试", "获取权限失败"
                            )
                        }
                    }
                })
        }
    }
}