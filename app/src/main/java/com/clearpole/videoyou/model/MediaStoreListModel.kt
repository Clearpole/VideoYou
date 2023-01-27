package com.clearpole.videoyou.model

import android.content.Intent
import android.graphics.Bitmap
import com.blankj.utilcode.util.ActivityUtils.startActivity
import com.clearpole.videoyou.VideoPlayer
import com.clearpole.videoyou.databinding.ActivityMainBinding
import com.clearpole.videoyou.databinding.MediaStoreListItemBinding
import com.clearpole.videoyou.objects.MainObjects
import com.clearpole.videoyou.objects.VideoPlayObjects
import com.drake.brv.BindingAdapter
import com.drake.brv.item.ItemBind
import com.google.android.material.card.MaterialCardView

data class MediaStoreListModel(val title: String, val size: String, val img: Bitmap?, val path: String,val mainBind: ActivityMainBinding?) :
    ItemBind {
    override fun onBind(holder: BindingAdapter.BindingViewHolder) {
            val binding = MediaStoreListItemBinding.bind(holder.itemView)
            binding.itemName.text = title
            binding.itemSize.text = size
            binding.itemCover.setImageBitmap(img)
            binding.historyItemRoot.setOnClickListener {
                if (MainObjects.isChoose) {
                    (it as MaterialCardView).isCheckable = true
                    it.isChecked = !it.isChecked
                    if (it.isChecked){
                        MainObjects.chooseList.put(path,"chosen")
                        mainBind!!.mainToolbar.title = "已选择 ${MainObjects.chooseList.length()} 项"
                    }else{
                        mainBind!!.mainToolbar.title = "已选择 ${MainObjects.chooseList.length()-1} 项"
                        MainObjects.chooseList.remove(path)
                    }
                } else {
                    (it as MaterialCardView).isCheckable = false
                    VideoPlayObjects.paths = path
                    VideoPlayObjects.title = title
                    VideoPlayObjects.type = "LOCAL"
                    val intent = Intent(holder.context, VideoPlayer::class.java)
                    startActivity(intent)
                }
            }
    }
}