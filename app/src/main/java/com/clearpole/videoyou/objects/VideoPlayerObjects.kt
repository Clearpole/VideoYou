package com.clearpole.videoyou.objects

import com.google.android.exoplayer2.ExoPlayer
import com.google.android.material.slider.Slider

class VideoPlayerObjects {
    companion object{
        var isFirstLod : Boolean = true
        var videoWidth : Int = 0
        var videoHeight : Int = 0
        var isAutoFinish : Boolean = false
        var isIntoHome : Boolean = false
        var newItem : Int = 0
        var duration : Long = 0

        var isSpeedMode : Boolean = false
        var isPauseMode : Boolean = false
        var isToolBarOpen : Boolean = false
        var isInFullScreen : Boolean = false
        var isCrash : Boolean = false
        var isMove : Boolean = false
        var fingerX : Long = 0L
        var slider : Slider? = null
        lateinit var player: ExoPlayer
        fun fix(){
            isSpeedMode = false
            isPauseMode  = false
            isToolBarOpen = false
            isInFullScreen = false
            fingerX = 0L
            isCrash = true
            isMove = false
            slider  = null
        }
    }
}