@file:OptIn(ExperimentalAnimationApi::class)

package com.clearpole.videoyoux.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blankj.utilcode.util.AppUtils
import com.clearpole.videoyoux.logic.activity.PermissionActivity
import com.clearpole.videoyoux.ui.theme.VideoYouTheme

class PermissionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VideoYouTheme(hideBar = false) {
                val hasPermission = remember {
                    mutableStateOf(false)
                }
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    Column(Modifier.fillMaxSize()) {
                        Spacer(modifier = Modifier.height(80.dp))
                        Box(
                            Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            AnimatedContent(targetState = hasPermission.value, transitionSpec = {
                                slideInHorizontally { width -> width } + fadeIn() with slideOutHorizontally { width -> -width } + fadeOut()
                            }, label = "") {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = if (it.not()) "您需要同意一些权限" else "恭喜您获得了全部权限",
                                        fontSize = 23.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = if (it.not()) "这些权限是VideoYou搭建所需要的地基" else "您现在可以踏上VY的大陆了！",
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                        Box(
                            contentAlignment = Alignment.TopCenter,
                            modifier = Modifier
                                .weight(0.35f)
                                .fillMaxWidth()
                        ) {
                            AnimatedContent(targetState = hasPermission.value) {
                                Button(onClick = {
                                    if (it.not()) {
                                        PermissionActivity.getPermission(
                                            this@PermissionActivity,
                                            this@PermissionActivity,
                                            hasPermission
                                        )
                                    } else {
                                        AppUtils.relaunchApp(false)
                                    }
                                }) {
                                    Text(
                                        text = if (it.not()) "我同意VideoYou使用它们" else "开启VideoYou之旅！",
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


