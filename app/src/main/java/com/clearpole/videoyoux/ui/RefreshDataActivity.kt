@file:OptIn(ExperimentalAnimationApi::class)

package com.clearpole.videoyoux.ui

import android.os.Bundle
import android.os.Environment
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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import com.clearpole.videoyoux.logic.utils.DatabaseStorage
import com.clearpole.videoyoux.logic.utils.ReadMediaStore
import com.clearpole.videoyoux.logic.utils.RefreshMediaStore
import com.clearpole.videoyoux.ui.theme.VideoYouTheme
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RefreshDataActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VideoYouTheme(hideBar = false) {
                val success = remember {
                    mutableStateOf(false)
                }
                val ing = remember {
                    mutableStateOf(false)
                }
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    RefreshMediaStore.updateMedia(
                        this, Environment.getExternalStorageDirectory().toString()
                    )
                    Column(Modifier.fillMaxSize()) {
                        Spacer(modifier = Modifier.height(80.dp))
                        Box(
                            Modifier
                                .weight(1f)
                                .fillMaxSize(),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            AnimatedContent(targetState = success.value, transitionSpec = {
                                slideInHorizontally { width -> width } + fadeIn() with slideOutHorizontally { width -> -width } + fadeOut()
                            }, label = "") {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = if (it.not()) "刷新数据库" else "刷新成功",
                                        fontSize = 23.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = if (it.not()) "请点击按钮以刷新数据库" else "重启VYX后数据才会重载哦",
                                        fontSize = 16.sp
                                    )
                                    Spacer(modifier = Modifier.height(120.dp))
                                    AnimatedContent(targetState = ing.value, label = "") {
                                        if (ing.value) {
                                            CircularProgressIndicator(modifier = Modifier.width(100.dp))
                                        }
                                    }
                                }
                            }
                        }
                        Box(
                            contentAlignment = Alignment.TopCenter,
                            modifier = Modifier
                                .weight(0.35f)
                                .fillMaxWidth()
                        ) {
                            AnimatedContent(targetState = success.value, label = "") {
                                Button(onClick = {
                                    ing.value = true
                                    if (it.not()) {
                                        CoroutineScope(Dispatchers.IO).launch {
                                            //DatabaseStorage.writeDataToData(ReadMediaStore.start(contentResolver))
                                            DatabaseStorage.writeFolderToData(ReadMediaStore.getFolder(contentResolver))
                                            withContext(Dispatchers.Main){
                                                MMKV.mmkvWithID("Settings").encode("init",true)
                                                success.value = true
                                                ing.value = false
                                            }
                                        }
                                    } else {
                                        AppUtils.relaunchApp(false)
                                    }
                                }) {
                                    Text(
                                        text = if (it.not()) "刷新媒体库/本地数据库" else "重启VideoYouX",
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


