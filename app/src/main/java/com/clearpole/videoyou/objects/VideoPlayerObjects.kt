package com.clearpole.videoyou.objects

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.material.slider.Slider

class VideoPlayerObjects {
    companion object{
        var videoWidth : Int = 0
        var videoHeight : Int = 0
        var isAutoFinish : Boolean = false
        var newItem : Int = 0
        var isSpeedMode : Boolean = false
        var isPauseMode : Boolean = false
        var isToolBarOpen : Boolean = false
        var isInFullScreen : Boolean = false
        var isIntoHome : Boolean = false
        var isCrash : Boolean = false
        var isMove : Boolean = false
        var fingerX : Long = 0L
        var slider : Slider? = null
        var pausing : Boolean = false
        var first : Boolean = true
        var chose : ConstraintLayout? = null
        var rv: RecyclerView? = null
        lateinit var player: ExoPlayer
        fun fix(){
            videoWidth = 0
            videoHeight = 0
            isAutoFinish = false
            newItem = 0
            isSpeedMode = false
            isPauseMode  = false
            isToolBarOpen = false
            isInFullScreen = false
            isIntoHome = false
            fingerX = 0L
            isCrash = true
            isMove = false
            slider  = null
            first = true
            pausing = false
            chose = null
            rv = null
        }
    }
}