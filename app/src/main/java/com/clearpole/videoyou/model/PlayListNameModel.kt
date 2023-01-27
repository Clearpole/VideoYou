package com.clearpole.videoyou.model

import com.clearpole.videoyou.databinding.PlayListNameItemBinding
import com.drake.brv.BindingAdapter
import com.drake.brv.item.ItemBind

data class PlayListNameModel(val title: String, val time: String, val count: String) : ItemBind {
    override fun onBind(holder: BindingAdapter.BindingViewHolder) {
        val binding = PlayListNameItemBinding.bind(holder.itemView)
        binding.playListFilesTitle.text = title
        binding.webDavFilesPath.text = buildString {
            append(time)
            append(" · ")
            append(count)
        }
        binding.playListFilesRoot.setOnClickListener {
          /*  val int = Intent(holder.context, PlayListActivity::class.java)
            int.putExtra("name", title)
            ContextCompat.startActivity(int)*/
        }
    }
}