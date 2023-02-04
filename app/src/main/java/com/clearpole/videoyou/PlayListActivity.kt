package com.clearpole.videoyou

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.net.toUri
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.UriUtils
import com.clearpole.videoyou.code.PlayList
import com.clearpole.videoyou.databinding.ActivityPlayListBinding
import com.clearpole.videoyou.model.PlayListModel
import com.clearpole.videoyou.utils.GetVideoThumbnail
import com.clearpole.videoyou.utils.IsNightMode
import com.clearpole.videoyou.utils.SubStringX.Companion.subStringX
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.gyf.immersionbar.ImmersionBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File


class PlayListActivity : BaseActivity<ActivityPlayListBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImmersionBar.with(this).transparentBar()
            .statusBarDarkFont(!IsNightMode.isNightMode(resources)).init()
        val name = intent.getStringExtra("name")
        binding.topAppBar.title = name
        CoroutineScope(Dispatchers.IO).launch {
            val model = getData()
            withContext(Dispatchers.Main) {
                binding.listview.linear().setup {
                    addType<PlayListModel> { R.layout.media_store_list_item }
                }.models = model
            }
        }
    }

    private fun getData(): MutableList<Any> {
        return mutableListOf<Any>().apply {
            val list = JSONObject(PlayList.readPlayListContent(intent.getStringExtra("name")!!)!!)
            val keys = list.keys()
            val retriever = MediaMetadataRetriever()
            while (keys.hasNext()) {
                val key = keys.next().toString()
                val uri = list.getString(key)
                retriever.setDataSource(uri)
                val title = uri.substring(uri.lastIndexOf("/") + 1, uri.lastIndexOf("."))
                val duration =
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                val time = key.subStringX(null, ":")
                add(PlayListModel(uri, title, duration, time))
            }
        }
    }
}