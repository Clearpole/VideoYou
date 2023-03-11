package com.clearpole.videoyoux.ui.subgroup.folder

import android.app.Activity
import android.net.Uri
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.clearpole.videoyoux.R
import com.clearpole.videoyoux.logic.activity.MainActivity.Companion.getMediaStoreList
import com.clearpole.videoyoux.logic.model.VideoModel
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FolderSubList {
    companion object {
        @Composable
        fun Start(activity: Activity, title: String) {
            AndroidView(modifier = Modifier.fillMaxWidth(), factory = {
                val rv = RecyclerView(it).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    CoroutineScope(Dispatchers.IO).launch {
                        val model = getList(activity, title)
                        delay(450)
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

        private suspend fun getList(activity: Activity, title: String): MutableList<Any> {
            return mutableListOf<Any>().apply {
                withContext(Dispatchers.IO) {
                    val data = getMediaStoreList(activity = activity)
                    for (index in 0 until data.size) {
                        data[index]!!.apply {
                            if (this.getString("folder") == title) {
                                add(
                                    VideoModel(
                                        this.getString("title"),
                                        Uri.parse(this.getString("uri")),
                                        this.getString("path"),
                                        this.getString("duration"),
                                        this.getString("size"),
                                        this.getString("dateAdded")
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}