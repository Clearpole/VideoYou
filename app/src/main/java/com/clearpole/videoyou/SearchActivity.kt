package com.clearpole.videoyou

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.blankj.utilcode.util.ScreenUtils
import com.clearpole.videoyou.databinding.ActivitySearchBinding
import com.clearpole.videoyou.databinding.SearchItemBinding
import com.clearpole.videoyou.model.SearchModel
import com.clearpole.videoyou.utils.DatabaseStorage
import com.clearpole.videoyou.utils.IsNightMode
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.gyf.immersionbar.ImmersionBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject


class SearchActivity : BaseActivity<ActivitySearchBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImmersionBar.with(this).transparentBar()
            .statusBarDarkFont(!IsNightMode.isNightMode(resources)).init()
        binding.catSearchView.editText.setOnEditorActionListener { v, _, _ ->
            binding.catSearchBar.text = v.text
            setSearchList(v.text.toString())
            binding.catSearchView.hide()
            false
        }
        binding.searchRv.layoutParams.height = ScreenUtils.getAppScreenHeight()
    }

    @SuppressLint("CutPasteId", "SetTextI18n")
    private fun setSearchList(key: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val models = getDataForSearch(key, DatabaseStorage.readDataByData())
            launch(Dispatchers.Main) {
                binding.searchRv.linear().setup {
                    addType<SearchModel> { R.layout.search_item }
                    onCreate {
                        val binding = SearchItemBinding.bind(itemView)
                        binding.itemImg.layoutParams.height = (160..300).random()
                    }
                }.models = models
                binding.searchRv.layoutManager = StaggeredGridLayoutManager(
                    2,
                    StaggeredGridLayoutManager.VERTICAL
                )
            }
        }
    }

    private fun getDataForSearch(key: String, kv: JSONArray): MutableList<Any> {
        return mutableListOf<Any>().apply {
            for (index in 0 until kv.length()) {
                val jsonObject = JSONObject(kv.getString(index))
                if (jsonObject.getString("title").contains(key)) {
                    add(
                        SearchModel(
                            title = jsonObject.getString("title"),
                            size = jsonObject.getString("size"),
                            uri = Uri.parse(jsonObject.getString("uri")),
                            path = jsonObject.getString("path"),
                        )
                    )
                }
            }
        }
    }
}