package com.clearpole.videoyoux.logic.model

import androidx.navigation.NavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.clearpole.videoyoux.databinding.FolderItemBinding
import com.clearpole.videoyoux.logic.NavHost
import com.drake.brv.BindingAdapter
import com.drake.brv.item.ItemBind
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class FolderModel(val json: JSONObject, val navController: NavController) : ItemBind {
    override fun onBind(holder: BindingAdapter.BindingViewHolder) {
        val bind = holder.getBinding<FolderItemBinding>()
        json.apply {
            bind.folderName.text = getString("name")
            bind.root.setOnClickListener {
                navController.navigate("${NavHost.NAV_FOLDER}/${getString("name")}/${
                    buildString {
                        append(getString("time") + " created")
                        append(" · ")
                        append(getString("sonFileCount") + " videos")
                    }
                }/${getString("path").replace("/","[.].{.}")}")
            }
            bind.subCount.text = buildString {
                append(getString("sonFileCount") + " videos")
            }
            CoroutineScope(Dispatchers.IO).launch {
                val load = Glide.with(holder.context).load(getString("last"))
                    .transition(DrawableTransitionOptions.withCrossFade()).diskCacheStrategy(
                        DiskCacheStrategy.RESOURCE
                    )
                withContext(Dispatchers.Main) {
                    load.into(bind.cover)
                }
                cancel()
            }
        }
    }
}