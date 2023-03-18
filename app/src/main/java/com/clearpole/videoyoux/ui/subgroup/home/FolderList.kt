@file:OptIn(ExperimentalAnimationApi::class, ExperimentalAnimationApi::class)

package com.clearpole.videoyoux.ui.subgroup.home

import android.view.ViewGroup
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.clearpole.videoyoux.R
import com.clearpole.videoyoux.logic.model.FolderModel
import com.clearpole.videoyoux.logic.utils.ReadMediaStore
import com.clearpole.videoyoux.logic.utils.ReadMediaStore.Companion.kv_video
import com.clearpole.videoyoux.ui.utils.SmoothRoundedCornerShape
import com.drake.brv.utils.linear
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.kyant.monet.n2
import com.kyant.monet.rangeTo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class FolderList {
    companion object {
        @Composable
        fun View(navController: NavController) {
            val listLoaded = remember {
                mutableStateOf(false)
            }
            var rv: RecyclerView? = null
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(25.dp),
                colors = CardDefaults.cardColors(95.n2..10.n2),
                shape = SmoothRoundedCornerShape(24.dp)
            ) {
                AnimatedVisibility(
                    visible = listLoaded.value, enter = fadeIn(tween(2000)) + slideInVertically()
                ) {
                    AndroidView(factory = {
                        rv = RecyclerView(it).apply {
                            this.layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            )
                            CoroutineScope(Dispatchers.IO).launch {
                                val model = getData(
                                    ReadMediaStore.readFolder(), navController, false
                                )
                                withContext(Dispatchers.Main) {
                                    this@apply.linear().setup {
                                        addType<FolderModel> { R.layout.folder_item }
                                    }.models = model
                                }
                            }
                        }
                        rv!!
                    })
                }
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(15.dp)
                ) {
                    AnimatedContent(
                        targetState = listLoaded.value,
                        label = "",
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    val model = getData(
                                        ReadMediaStore.readFolder(), navController, true
                                    )
                                    withContext(Dispatchers.Main) {
                                        rv!!.models = model
                                    }
                                }
                            }, modifier = Modifier.align(Alignment.TopCenter)
                        ) {
                            Text(text = if (listLoaded.value.not()) "请坐和放宽.." else "查看全部文件夹")
                        }
                    }
                }
                listLoaded.value = true
            }
        }


        private fun getData(
            kv: ArrayList<String>, navController: NavController, isAll: Boolean
        ): MutableList<Any> {
            return mutableListOf<Any>().apply {
                for (index in 0 until if (isAll) kv.size else if (kv.size < 3) kv.size else 3) {
                    val name = kv[index]
                    val data = JSONArray(kv_video.decodeString(name))
                    val last = data.getJSONObject(0).getString("path")
                    val json = JSONObject().put("name", name).put("sonFileCount", data.length())
                        .put("last", last)
                        .put("path", last.substring(last.lastIndexOf("/"), last.length))
                    add(
                        FolderModel(
                            json = json,
                            navController = navController,
                        )
                    )
                }
            }
        }
    }
}