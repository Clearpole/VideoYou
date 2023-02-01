package com.clearpole.videoyou.model

import android.content.ContentResolver
import android.net.Uri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.clearpole.videoyou.databinding.FolderListItemBinding
import com.clearpole.videoyou.utils.GetVideoThumbnail
import com.drake.brv.BindingAdapter
import com.drake.brv.item.ItemAttached
import com.drake.brv.item.ItemBind
import com.drake.brv.item.ItemExpand
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


open class FolderModel(
    override var itemGroupPosition: Int = 0,
    override var itemExpand: Boolean = false,
    override var itemSublist: List<Any?>? = null,
    val title: String,
    val uri: Uri,
    val contentResolver: ContentResolver,
    val path: String
) : ItemExpand, ItemBind,ItemAttached {
    val videoTitle get() = title
    private var itemVisible: Boolean = false
    override fun onBind(holder: BindingAdapter.BindingViewHolder) {
        holder.itemView.postDelayed({
            if (itemVisible) {
                val binding = holder.getBinding<FolderListItemBinding>()
                CoroutineScope(Dispatchers.IO).launch{
                    val bitmap = GetVideoThumbnail.getVideoThumbnail(contentResolver, uri)
                    withContext(Dispatchers.Main){
                        Glide.with(holder.context)
                            .load(bitmap)
                            .transition(DrawableTransitionOptions.withCrossFade()).into(binding.page2RvItemImg)
                    }
                }
            }
        }, 300)
    }

    override fun onViewAttachedToWindow(holder: BindingAdapter.BindingViewHolder) {
        itemVisible = true
    }

    override fun onViewDetachedFromWindow(holder: BindingAdapter.BindingViewHolder) {
        itemVisible = false
    }

}