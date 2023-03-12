package com.clearpole.videoyoux.ui.subgroup.folder

import android.app.Activity
import android.media.MediaMetadataRetriever
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.clearpole.videoyoux.R
import com.clearpole.videoyoux.logic.model.VideoModel
import com.clearpole.videoyoux.logic.utils.IsVideoFile
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class FolderSubList {
    companion object {
        @Composable
        fun Start(activity: Activity, path: String) {
            AndroidView(modifier = Modifier.fillMaxWidth(), factory = {
                val rv = RecyclerView(it).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    CoroutineScope(Dispatchers.IO).launch {
                        val model = getList(activity, path.replace("[.].{.}", "/"))
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

        private suspend fun getList(activity: Activity, path: String): MutableList<Any> {
            return mutableListOf<Any>().apply {
                withContext(Dispatchers.IO) {
                    MediaMetadataRetriever().apply {
                        File(path).listFiles()!!.forEachIndexed { index, file ->
                            if (IsVideoFile.start(file.path)) {
                                setDataSource(file.path)
                                add(
                                    VideoModel(
                                        title = file.path.substring(file.path.lastIndexOf("/") + 1, file.path.lastIndexOf(".")),
                                        duration =
                                        extractMetadata(
                                            MediaMetadataRetriever.METADATA_KEY_DURATION
                                        )!!.toLong(),
                                        size = file.length(),
                                        uri = file.toUri(),
                                        path = file.path,
                                        dateAdded = file.lastModified()
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