package com.clearpole.videoyou.model

import android.content.ContentResolver
import android.net.Uri
import android.view.View
import androidx.databinding.BaseObservable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.clearpole.videoyou.databinding.FolderListItemBinding
import com.clearpole.videoyou.utils.GetVideoThumbnail
import com.drake.brv.BindingAdapter
import com.drake.brv.item.ItemBind
import com.drake.brv.item.ItemExpand
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


open class FolderModel(
    override var itemGroupPosition: Int = 0,
    override var itemExpand: Boolean = false,
    override var itemSublist: List<Any?>? = null,
    var checked: Boolean = false,
    var vis: Int = View.GONE,
    val title: String,
    val uri: Uri,
    val path: String
) : ItemExpand, ItemBind, BaseObservable() {
    val videoTitle get() = title
    override fun onBind(holder: BindingAdapter.BindingViewHolder) {

        val binding = holder.getBinding<FolderListItemBinding>()
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                Glide.with(holder.context)
                    .load(uri)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.page2RvItemImg)
            }
            this.cancel()
        }
    }

}
