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
    private val duration: Long,
    private val size: Long,
    private val dateAdded: Long
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
                append(TimeUtils.millis2String(dateAdded*1000))
                append(" · ")
                append(ByteToString.byteToString(size))
            }
            time.text = TimeParse.timeParse(duration = duration)
            CoroutineScope(Dispatchers.IO).launch {
                val load =  Glide.with(holder.context).load(uri)
                    .transition(DrawableTransitionOptions.withCrossFade()).diskCacheStrategy(
                        DiskCacheStrategy.RESOURCE
                    )
                withContext(Dispatchers.Main) {
                   load.into(itemCover)
                }
                cancel()
            }
        }
    }
}