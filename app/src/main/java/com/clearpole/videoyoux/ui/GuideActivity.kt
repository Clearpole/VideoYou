@file:OptIn(ExperimentalAnimationApi::class)

package com.clearpole.videoyoux.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.blankj.utilcode.util.AppUtils
import com.clearpole.videoyoux.logic.NavHost
import com.clearpole.videoyoux.logic.activity.GetPermission
import com.clearpole.videoyoux.logic.utils.ReadMediaStore
import com.clearpole.videoyoux.ui.subgroup.guide.Permission
import com.clearpole.videoyoux.ui.subgroup.guide.Welcome
import com.clearpole.videoyoux.ui.subgroup.guide.WriteData
import com.clearpole.videoyoux.ui.theme.VideoYouTheme
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.kyant.monet.n2
import com.kyant.monet.rangeTo
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GuideActivity : ComponentActivity() {
    companion object {
        const val STEP_WELCOME = "你好，VideoYouX！"
        const val STEP_PERMISSION = "申请权限"
        const val STEP_WRITE_DATA = "刷新数据库"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val hasPermission = remember {
                mutableStateOf(false)
            }
            VideoYouTheme(hideBar = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = 95.n2..10.n2
                ) {
                    val navController = rememberAnimatedNavController()
                    val step = remember {
                        mutableStateOf(STEP_WELCOME)
                    }
                    Box(modifier = Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomStart)
                                .padding(bottom = 60.dp)
                        ) {
                            NavHost(navController)
                            Row(
                                modifier = Modifier.align(
                                    Alignment.BottomEnd
                                )
                            ) {
                                Button(modifier = Modifier.animateContentSize(finishedListener = { _, _ -> }),
                                    onClick = {
                                        when (step.value) {
                                            STEP_WELCOME -> {
                                                navController.navigate(NavHost.NAV_GUIDE_PERMISSION)
                                                step.value = STEP_PERMISSION
                                            }

                                            STEP_PERMISSION -> {
                                                GetPermission.getPermission(
                                                    this@GuideActivity,
                                                    this@GuideActivity,
                                                    step,
                                                    navController
                                                )
                                            }

                                            else -> {
                                                CoroutineScope(Dispatchers.IO).launch {
                                                    ReadMediaStore.writeData(contentResolver)
                                                }
                                                MMKV.mmkvWithID("Settings").putBoolean("init",true)
                                                AppUtils.relaunchApp(false)
                                            }
                                        }
                                    },
                                    contentPadding = PaddingValues(
                                        top = 10.dp, bottom = 10.dp, start = 35.dp, end = 35.dp
                                    ),
                                    content = {
                                        AnimatedContent(targetState = step.value,
                                            label = "",
                                            transitionSpec = {
                                                slideInHorizontally { width -> width } + fadeIn() with slideOutHorizontally { width -> -width } + fadeOut()
                                            }) {
                                            Text(text = step.value, fontSize = 17.sp)
                                        }
                                    })
                                Spacer(modifier = Modifier.width(35.dp))
                            }
                        }
                    }
                }
                BackHandler {
                    finish()
                }
            }
        }
    }

    @Composable
    fun NavHost(navController: NavHostController) {
        AnimatedNavHost(
            navController = navController,
            startDestination = NavHost.NAV_GUIDE_WELCOME
        ) {
            composable(NavHost.NAV_GUIDE_WELCOME, enterTransition = {
                slideInHorizontally { width -> width }
            }, exitTransition = {
                slideOutHorizontally { width -> -width }
            }) {
                Welcome.Start()
            }
            composable(
                NavHost.NAV_GUIDE_PERMISSION,
                enterTransition = {
                    slideInHorizontally { width -> width }
                },
                exitTransition = {
                    slideOutHorizontally { width -> -width }
                }) {
                Permission.Start()
            }
            composable(
                NavHost.NAV_GUIDE_WRITE_DATA,
                enterTransition = {
                    slideInHorizontally { width -> width }
                },
                exitTransition = {
                    slideOutHorizontally { width -> -width }
                }) {
                WriteData.Start()
            }
        }
    }
}
