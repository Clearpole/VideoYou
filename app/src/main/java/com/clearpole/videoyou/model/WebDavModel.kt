package com.clearpole.videoyou.model;

import com.drake.brv.BindingAdapter
import com.drake.brv.item.ItemBind
import com.thegrizzlylabs.sardineandroid.DavResource

data class WebDavModel(
    val title: DavResource
) :
    ItemBind {
    override fun onBind(holder: BindingAdapter.BindingViewHolder) {
    }
}