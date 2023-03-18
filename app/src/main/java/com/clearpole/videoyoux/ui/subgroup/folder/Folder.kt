package com.clearpole.videoyoux.ui.subgroup.folder

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.clearpole.videoyoux.logic.NavHost

class Folder {
    companion object {
        @Composable
        fun Folder(title: String, activity: Activity, info: String, path: String,navHostController: NavHostController) {
            val loaded = remember {
                mutableStateOf(false)
            }
            Box(modifier = Modifier
                .fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Spacer(modifier = Modifier.height(60.dp))
                    Header.Header(title, info)
                    AnimatedVisibility(visible = loaded.value, enter = fadeIn(tween(2000))) {
                        FolderSubList.Start(activity = activity, path)
                    }
                    loaded.value = true
                }
                BackHandler {
                    navHostController.popBackStack()
                }
            }
        }

        @Composable
        fun FolderAll(navHostController: NavHostController){
            val loaded = remember {
                mutableStateOf(false)
            }
            Box(modifier = Modifier
                .fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Spacer(modifier = Modifier.height(60.dp))
                    AnimatedVisibility(visible = loaded.value, enter = fadeIn(tween(2000))) {
                        FolderSubList.FolderAll(navController = navHostController)
                    }
                    loaded.value = true
                }
            }
        }
    }
}