package com.clearpole.videoyou

import android.media.MediaMetadataRetriever
import android.os.Bundle
import com.blankj.utilcode.util.ToastUtils
import com.clearpole.videoyou.code.PlayList
import com.clearpole.videoyou.databinding.ActivityPlayListBinding
import com.clearpole.videoyou.model.PlayListModel
import com.clearpole.videoyou.utils.IsNightMode
import com.clearpole.videoyou.utils.SubStringX.Companion.subStringX
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.google.android.material.R.style.MaterialAlertDialog_Material3
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.gyf.immersionbar.ImmersionBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject


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
        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.removePlayList -> {
                    MaterialAlertDialogBuilder(this, MaterialAlertDialog_Material3)
                        .setTitle("删除播放列表")
                        .setMessage("是否删除播放列表《$name》？")
                        .setNegativeButton("删除") { _, _ ->
                            PlayList.removeList(name!!)
                            finish()
                            ToastUtils.showShort("刷新后生效")
                        }
                        .setNeutralButton("取消") { _, _ -> }
                        .show()
                    true
                }

                else -> false
            }
        }
    }

    private fun getData(): MutableList<Any> {
        return mutableListOf<Any>().apply {
            try {
                val list =
                    JSONObject(PlayList.readPlayListContent(intent.getStringExtra("name")!!)!!)
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
                    add(PlayListModel(uri, title, duration, time,list))
                }
            } catch (_: Exception) {

            }
        }
    }
}