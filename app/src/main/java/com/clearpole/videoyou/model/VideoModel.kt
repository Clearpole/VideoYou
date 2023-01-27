package com.clearpole.videoyou.model

import android.graphics.drawable.Drawable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable


class VideoModel : BaseObservable() {

    @Bindable
    var nowProgressString : String? = null
        set(str){
            field = str
            notifyChange()
        }

    @Bindable
    var nowProgressLong : Long? = null
        set(value){
            field = value
            notifyChange()
        }

    @Bindable
    var allProgressString : String? = null
        set(value) {
            field = value
            notifyChange()
        }

    @Bindable
    var allProgressFloat : Float? = null
        set(value){
            field = value
            notifyChange()
        }

    @Bindable
    var pauseImg : Drawable? = null
        set(value){
            field = value
            notifyChange()
        }

    @Bindable
    var screenImg : Drawable? = null
        set(value){
            field = value
            notifyChange()
        }

    companion object {
        lateinit var videoTitle: String
    }
}
