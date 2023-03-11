package com.clearpole.videoyoux.logic.model

import android.net.Uri
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.TimeUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.clearpole.videoyoux.databinding.VideoItemBinding
import com.clearpole.videoyoux.logic.utils.ByteToString
import com.clearpole.videoyoux.logic.utils.TimeParse
import com.clearpole.videoyoux.ui.PlayerActivity
import com.drake.brv.BindingAdapter
import com.drake.brv.item.ItemBind
import com.drake.serialize.intent.openActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VideoModel(
    val title: String,
    private val uri: Uri,
    private val path: String,
    private val duration: String,
    private val size: String,
    private val dateAdded: String
) : ItemBind {
    override fun onBind(holder: BindingAdapter.BindingViewHolder) {
        holder.getBinding<VideoItemBinding>().apply {
            itemName.text = StringUtils.toDBC(title.replace(" ", ""))
            itemRoot.setOnClickListener {
                holder.context.openActivity<PlayerActivity>(
                    "url" to path
                )
            }
            itemInfo.text = buildString {
                append(TimeUtils.millis2String(dateAdded.toLong()*1000L))
                append(" · ")
                append(ByteToString.byteToString(size.toLong()))
            }
            time.text = TimeParse.timeParse(duration = duration.toLong())
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.Main) {
                    Glide.with(holder.context).load(uri)
                        .transition(DrawableTransitionOptions.withCrossFade()).diskCacheStrategy(
                            DiskCacheStrategy.RESOURCE
                        ).into(itemCover)
                }
                cancel()
            }
        }
    }
}