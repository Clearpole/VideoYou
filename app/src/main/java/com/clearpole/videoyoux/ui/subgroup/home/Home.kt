package com.clearpole.videoyoux.ui.subgroup.home

import android.app.Activity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.clearpole.videoyoux.logic.utils.ColorEggDate

class Home {
    @OptIn(ExperimentalAnimationApi::class)
    companion object {
        @Composable
        fun Home(activity: Activity, navController: NavController) {
            val string = remember {
                mutableStateOf(ColorEggDate.string())
            }
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(80.dp))
                Box {
                    AnimatedContent(targetState = string.value, transitionSpec = {
                        slideInHorizontally { width -> width } + fadeIn() with slideOutHorizontally { width -> -width } + fadeOut()
                    }, label = "") {
                        Text(text = string.value,
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
                Banner.View(activity = activity)
                FolderList.View(navController)
            }
        }
    }
}