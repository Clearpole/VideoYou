package com.clearpole.videoyoux.ui.subgroup.folder

import android.app.Activity
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.clearpole.videoyoux.R
import com.clearpole.videoyoux.logic.model.FolderModel
import com.clearpole.videoyoux.logic.model.VideoModel
import com.clearpole.videoyoux.logic.utils.ReadMediaStore
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLDecoder

class FolderSubList {
    companion object {
        @Composable
        fun Start(activity: Activity, folder: String) {
            AndroidView(modifier = Modifier.fillMaxWidth(), factory = {
                val rv = RecyclerView(it).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    CoroutineScope(Dispatchers.IO).launch {
                        val model = getList(activity, URLDecoder.decode(folder))
                        delay(500)
                        withContext(Dispatchers.Main) {
                            linear().setup {
                                addType<VideoModel> { R.layout.video_item }
                            }.models = model
                        }
                    }
                    layoutManager = StaggeredGridLayoutManager(
                        1, StaggeredGridLayoutManager.VERTICAL
                    )
                }
                LinearLayout(it).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    addView(rv)
                    clipChildren = true
                }
            })
        }

        private suspend fun getList(activity: Activity, folder: String): MutableList<Any> {
            return mutableListOf<Any>().apply {
                withContext(Dispatchers.IO) {
                    val array = ReadMediaStore.readVideos(folder)
                    for (index in 0..array.length()) {
                        try {
                            val obj = JSONObject(array.getString(index))
                            add(
                                VideoModel(
                                    title = obj.getString("title"),
                                    duration = obj.getLong("duration"),
                                    size = obj.getLong("size"),
                                    uri = Uri.parse(obj.getString("uri")),
                                    path = obj.getString("path"),
                                    dateAdded = obj.getLong("dateAdded")
                                )
                            )
                        }catch (_:Exception){}
                    }
                }
            }
        }

        @Composable
        fun FolderAll(navController: NavHostController){
            AndroidView(modifier = Modifier.fillMaxWidth(), factory = {
                val rv = RecyclerView(it).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    CoroutineScope(Dispatchers.IO).launch {
                        val model = getData(ReadMediaStore.readFolder(), navController = navController)
                        delay(500)
                        withContext(Dispatchers.Main) {
                            linear().setup {
                                addType<FolderModel> { R.layout.folder_item }
                            }.models = model
                        }
                    }
                    layoutManager = StaggeredGridLayoutManager(
                        1, StaggeredGridLayoutManager.VERTICAL
                    )
                }
                LinearLayout(it).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    addView(rv)
                    clipChildren = true
                }
            })
        }

        private fun getData(
            kv: ArrayList<String>, navController: NavController
        ): MutableList<Any> {
            return mutableListOf<Any>().apply {
                for (index in 0 until kv.size) {
                    val name = kv[index]
                    val data = JSONArray(ReadMediaStore.kv_video.decodeString(name))
                    val last = data.getJSONObject(0).getString("path")
                    val json = JSONObject().put("name", name).put("sonFileCount", data.length())
                        .put("last", last).put("path",last.substring(last.lastIndexOf("/"),last.length))
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
