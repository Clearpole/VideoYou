package com.clearpole.videoyou.model

import android.content.Intent
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.clearpole.videoyou.PlayListActivity
import com.clearpole.videoyou.code.PlayList
import com.clearpole.videoyou.databinding.PlayListNameItemBinding
import com.clearpole.videoyou.utils.SubStringX.Companion.subStringX
import com.drake.brv.BindingAdapter
import com.drake.brv.item.ItemBind
import org.json.JSONObject

data class PlayListNameModel(val title: String, val time: String, val count: String) : ItemBind {
    override fun onBind(holder: BindingAdapter.BindingViewHolder) {
        val binding = PlayListNameItemBinding.bind(holder.itemView)
       try {
           val list = JSONObject(PlayList.readPlayListContent(title)!!)
           val key = list.toString().subStringX("\"", "\"")
           val uri = list.getString(key!!)

           binding.playListFilesTitle.text = title
           binding.webDavFilesPath.text = buildString {
               append(time)
               append(" · ")
               append(list.length())
           }
           Glide.with(holder.context).load(uri).diskCacheStrategy(DiskCacheStrategy.RESOURCE)
               .transition(
                   DrawableTransitionOptions.withCrossFade()
               ).into(binding.cover)
           binding.playListFilesRoot.setOnClickListener {
               val int = Intent(holder.context, PlayListActivity::class.java)
               int.putExtra("name", title)
               holder.context.startActivity(int)
           }
       }catch (e:Exception){
           binding.playListFilesTitle.text = title
           binding.webDavFilesPath.text = buildString {
               append(time)
               append(" · ")
               append(0)
           }
           binding.playListFilesRoot.setOnClickListener {
               val int = Intent(holder.context, PlayListActivity::class.java)
               int.putExtra("name", title)
               holder.context.startActivity(int)
           }
       }
    }
}