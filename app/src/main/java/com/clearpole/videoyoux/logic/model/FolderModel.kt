package com.clearpole.videoyoux.logic.model

import androidx.navigation.NavController
import com.clearpole.videoyoux.databinding.FolderItemBinding
import com.clearpole.videoyoux.logic.NavHost
import com.drake.brv.BindingAdapter
import com.drake.brv.item.ItemBind

class FolderModel(val title:String,val navController: NavController):ItemBind {
    override fun onBind(holder: BindingAdapter.BindingViewHolder) {
        val bind = holder.getBinding<FolderItemBinding>()
        bind.folderName.text = title
        bind.root.setOnClickListener {
            navController.navigate("${NavHost.NAV_FOLDER}/$title")
        }
    }
}