package com.clearpole.videoyoux.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.clearpole.videoyoux.logic.NavHost
import com.clearpole.videoyoux.logic.activity.MainActivity
import com.clearpole.videoyoux.logic.utils.RefreshMediaStore
import com.clearpole.videoyoux.ui.subgroup.folder.Folder
import com.clearpole.videoyoux.ui.subgroup.home.Home
import com.clearpole.videoyoux.ui.theme.VideoYouTheme
import com.developer.crashx.config.CrashConfig
import com.drake.serialize.intent.openActivity
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.kyant.monet.*
import com.tencent.mmkv.MMKV

@OptIn(ExperimentalAnimationApi::class)
class MainActivity : ComponentActivity() {
    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            System.loadLibrary("monet")
            VideoYouTheme(hideBar = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    CrashConfig.Builder.create().errorActivity(CrashActivity::class.java)
                    if (MainActivity.checkPermission(this).not()) {
                        openActivity<GuideActivity>()
                    } else if (MMKV.mmkvWithID("Settings").decodeBool("init").not()) {
                        openActivity<GuideActivity>()
                    } else {
                        RefreshMediaStore.updateMedia(
                            this, Environment.getExternalStorageDirectory().toString()
                        )
                        NavHost()
                    }
                }
            }
        }
    }

    @Composable
    fun NavHost() {
        val navController = rememberAnimatedNavController()
        navController.currentBackStackEntryAsState()
        AnimatedNavHost(navController = navController, startDestination = NavHost.NAV_HOME) {
            composable(NavHost.NAV_HOME) {
                Home.Home(
                    activity = this@MainActivity, navController = navController, this@MainActivity
                )
            }
            composable(
                "${NavHost.NAV_FOLDER}/{title}/{info}/{path}",
                enterTransition = {
                    slideInHorizontally { width -> width } + fadeIn()
                },
                exitTransition = {
                    slideOutHorizontally { width -> -width } + fadeOut()
                },
                arguments = listOf(navArgument("title") { type = NavType.StringType },
                    navArgument("info") { type = NavType.StringType },
                    navArgument("path") { type = NavType.StringType })
            ) {
                val argument = requireNotNull(it.arguments)
                Folder.Folder(
                    title = argument.getString("title")!!,
                    info = argument.getString("info")!!,
                    path = argument.getString("path")!!,
                    activity = this@MainActivity,
                    navHostController = navController
                )
            }
            composable(
                NavHost.NAV_FOLDER_ALL, enterTransition = {
                slideInHorizontally { width -> width } + fadeIn()
            },
                exitTransition = {
                    slideOutHorizontally { width -> -width } + fadeOut()
                }) {
                Folder.FolderAll(navController)
            }
        }
    }
}