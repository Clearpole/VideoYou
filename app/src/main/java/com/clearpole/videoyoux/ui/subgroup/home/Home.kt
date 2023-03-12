package com.clearpole.videoyoux.ui.subgroup.home

import android.app.Activity
import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.blankj.utilcode.util.TimeUtils
import com.clearpole.videoyoux.logic.utils.ColorEggDate
import com.clearpole.videoyoux.ui.RefreshDataActivity
import com.drake.serialize.intent.openActivity

class Home {
    @OptIn(ExperimentalAnimationApi::class)
    companion object {
        @Composable
        fun Home(activity: Activity, navController: NavController, context: Context) {
            val string = remember {
                mutableStateOf(ColorEggDate.string())
            }
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(80.dp))
                Box(Modifier.fillMaxWidth()) {
                    Column {
                        Box {
                            AnimatedContent(targetState = string.value, transitionSpec = {
                                slideInHorizontally { width -> width } + fadeIn() with slideOutHorizontally { width -> -width } + fadeOut()
                            }, label = "") {
                                Text(
                                    text = string.value,
                                    fontSize = 25.sp,
                                    modifier = Modifier
                                        .padding(start = 25.dp, end = 25.dp)
                                        .clickable(onClick = {
                                            while (true) {
                                                val now = string.value
                                                val str = ColorEggDate.string()
                                                if (now != str) {
                                                    string.value = str
                                                    break
                                                }
                                            }
                                        }, indication = null, interactionSource = remember {
                                            MutableInteractionSource()
                                        })
                                )
                            }
                        }
                        val timeHH =
                            TimeUtils.getSafeDateFormat("HH").format(TimeUtils.getNowDate()).toInt()
                        Text(
                            text = TimeUtils.getSafeDateFormat("MM月dd日")
                                .format(TimeUtils.getNowDate()) + " " + TimeUtils.getChineseWeek(
                                TimeUtils.getNowDate()
                            ) + " " + when (timeHH) {
                                in 0..5 -> {
                                    "凌晨"
                                }

                                in 6..13 -> {
                                    "上午"
                                }

                                in 14..18 -> {
                                    "下午"
                                }

                                in 19..23 -> {
                                    "晚上"
                                }

                                else -> {
                                    "深夜"
                                }
                            },
                            fontSize = 13.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 5.dp, start = 25.dp),
                        )
                    }
                    Row(
                        modifier = Modifier
                            .padding(end = 15.dp)
                            .align(Alignment.TopEnd)
                    ) {
                        Column(
                            Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .width(40.dp)
                                .height(40.dp)
                        ) {
                            Column(
                                Modifier
                                    .fillMaxSize()
                                    .clickable {
                                        context.openActivity<RefreshDataActivity>()
                                    }, verticalArrangement = Arrangement.Center) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "刷新",
                                    modifier = Modifier.align(
                                        Alignment.CenterHorizontally
                                    )
                                )
                            }
                        }
                    }
                }
                Banner.View(activity = activity)
                FolderList.View(navController)
            }
        }
    }
}