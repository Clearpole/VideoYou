package com.clearpole.videoyoux.ui.subgroup.home

import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.clearpole.videoyoux.R
import com.clearpole.videoyoux.logic.model.FolderModel
import com.clearpole.videoyoux.logic.utils.DatabaseStorage
import com.clearpole.videoyoux.ui.utils.SmoothRoundedCornerShape
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.kyant.monet.a1
import com.kyant.monet.n2
import com.kyant.monet.rangeTo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class FolderList {
    companion object {
        @Composable
        fun View(navController: NavController) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(25.dp),
                colors = CardDefaults.cardColors(100.a1..30.n2),
                shape = SmoothRoundedCornerShape(24.dp)
            ) {
                AndroidView(factory = {
                    RecyclerView(it).apply {
                        this.layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        CoroutineScope(Dispatchers.Main).launch {
                            this@apply.linear().setup {
                                addType<FolderModel> { R.layout.folder_item }
                            }.models = getData(
                                DatabaseStorage.readFolderByData(), navController
                            )
                        }
                    }
                })
            }
        }

        private fun getData(
            kv: JSONArray?, navController: NavController
        ): MutableList<Any> {
            return mutableListOf<Any>().apply {
                for (index in 0 until kv!!.length()) {
                    val json = JSONObject(kv.getString(index))
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