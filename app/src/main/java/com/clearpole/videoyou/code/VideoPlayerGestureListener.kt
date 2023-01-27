package com.clearpole.videoyou.code

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Vibrator
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.clearpole.videoyou.R
import com.clearpole.videoyou.databinding.ActivityVideoPlayerBinding
import com.clearpole.videoyou.objects.VideoPlayerObjects
import com.clearpole.videoyou.utils.BaseClickListener
import com.clearpole.videoyou.utils.TimeParse.Companion.timeParse
import com.google.android.material.slider.Slider

class VideoPlayerGestureListener {
    companion object {
        @Suppress("DEPRECATION")
        @SuppressLint("ClickableViewAccessibility", "SetTextI18n", "ResourceAsColor")
        fun gestureListener(
            context: Context,
            activityBinding: ActivityVideoPlayerBinding,
            resources:Resources
        ) {
            val player = activityBinding.videoView.player
            AnimationUtils.loadAnimation(
                context,
                com.google.android.material.R.anim.design_bottom_sheet_slide_in
            )
            AnimationUtils.loadAnimation(
                context,
                com.google.android.material.R.anim.design_bottom_sheet_slide_out
            )
            val slateAnimaTopSlideIn = AnimationUtils.loadAnimation(
                context,
                com.google.android.material.R.anim.abc_slide_in_top
            )
            slateAnimaTopSlideIn.duration = 150L
            val slateAnimaTopSlideOut = AnimationUtils.loadAnimation(
                context,
                com.google.android.material.R.anim.abc_slide_out_top
            )
            slateAnimaTopSlideOut.duration = 150L
            val slateAnimaBottomSlideIn = AnimationUtils.loadAnimation(
                context,
                com.google.android.material.R.anim.abc_slide_in_bottom
            )
            slateAnimaBottomSlideIn.duration = 150L
            val slateAnimaBottomSlideOut = AnimationUtils.loadAnimation(
                context,
                com.google.android.material.R.anim.abc_slide_out_bottom
            )
            slateAnimaBottomSlideOut.duration = 150L
            val alpha01 = AnimationUtils.loadAnimation(context,R.anim.alpha_0_1)
            val alpha10 = AnimationUtils.loadAnimation(context,R.anim.alpha_1_0)

            var isLongClickMode = false
            // 是否处于长按模式
            var isSpeedMode = false
            // 是否处于倍速模式
            var isOpenToolBar = false
            // 上下工具栏是否被打开
            var isPlayMode = true
            // 是否处于播放状态
            var isMoveMode = false
            // 是否处于手指移动状态
            var firstX = 0f
            // 手指刚接触屏幕的x轴位置
            var isImplement = false
            // 手指刚接触屏幕的事件执行情况
            var stateOfScroll = "暂未设置(LEFT|RIGHT)"
            // 滑动的状态：左或右
            var newProgressLong = 0L
            // 想要调整到的视频进度
            var isImplements = false
            // 是否执行
            var isOpenBottomToolBar = false
            // 底栏是否开启

            //设置进度条拖动事件
            activityBinding.videoPlayerBottomBarRoot.videoPlayerVideoSlider.addOnSliderTouchListener(object :
                Slider.OnSliderTouchListener {
                override fun onStartTrackingTouch(slider: Slider) {
                    VideoPlayerObjects.isMove = true
                    //开始拖动的时候设置变量，暂停进度条跟随线程变化进度
                }

                override fun onStopTrackingTouch(slider: Slider) {
                    player!!.seekTo(slider.value.toLong())
                    //结束拖动，将视频进度设置为进度条进度
                    VideoPlayerObjects.isMove = false
                    //还原变量，进度条继续跟随线程变化
                }

            })

            val vibrator = context.getSystemService(AppCompatActivity.VIBRATOR_SERVICE) as Vibrator
            // 申请振动器

            // 本方法涵盖：双击屏幕事件，单击屏幕事件
            activityBinding.videoPlayerControlRoot.setOnClickListener(object : BaseClickListener() {
                override fun onSingleClick(v: View?) {
                    // 单击屏幕，判断是否打开工具栏，执行操作
                    if (isOpenBottomToolBar) {
                        activityBinding.videoPlayerBottomBarRoot.videoPlayerBottomBar.visibility = View.GONE
                        isOpenBottomToolBar = false
                    } else {
                        if (!isOpenToolBar) {
                            // 如果没打开工具栏，执行打开
                            activityBinding.videoPlayerTopBarRoot.videoPlayerTopBar.visibility = View.VISIBLE
                            activityBinding.videoPlayerBottomBarRoot.videoPlayerBottomBar.visibility = View.VISIBLE
                            isOpenToolBar = true
                        } else {
                            // 如果打开了工具栏，执行关闭
                            activityBinding.videoPlayerTopBarRoot.videoPlayerTopBar.visibility = View.GONE
                            activityBinding.videoPlayerBottomBarRoot.videoPlayerBottomBar.visibility = View.GONE
                            isOpenToolBar = false
                        }
                    }
                }

                @SuppressLint("ResourceType")
                override fun onDoubleClick(v: View?) {
                    // 双击屏幕，判断是否处于播放状态
                    if (isPlayMode) {
                        // 如果正在播放，就暂停视频播放
                        Glide.with(context).load(R.drawable.baseline_play_arrow_24)
                            .into(activityBinding.videoPlayerAssemblyRoot.isPlayPause)
                        activityBinding.videoPlayerAssemblyRoot.isPlayPauseRoot.visibility = View.VISIBLE
                        activityBinding.videoPlayerAssemblyRoot.isPlayPauseRoot.startAnimation(alpha01)
                        player!!.pause()
                        activityBinding.videoModel?.pauseImg = Drawable.createFromXml(resources,resources.getXml(R.drawable.baseline_play_arrow_24))
                        isPlayMode = false
                    } else {
                        // 如果本来就是暂停播放，就开始播放
                        Glide.with(context).load(R.drawable.baseline_pause_24)
                            .into(activityBinding.videoPlayerAssemblyRoot.isPlayPause)
                        activityBinding.videoPlayerAssemblyRoot.isPlayPauseRoot.visibility = View.GONE
                        activityBinding.videoPlayerAssemblyRoot.isPlayPauseRoot.startAnimation(alpha10)
                        player!!.play()
                        activityBinding.videoModel?.pauseImg = Drawable.createFromXml(resources,resources.getXml(R.drawable.baseline_pause_24))
                        isPlayMode = true
                    }
                }

            })

            // 本方法涵盖：长按屏幕事件
            activityBinding.videoPlayerControlRoot.setOnLongClickListener {
                // 长按屏幕，判断是否处于倍速模式
                if (!isSpeedMode && ! VideoPlayerObjects.isMove) {
                    // 如果目前不是倍速模式，就启动倍速，震动手机一次并隐藏上下工具栏
                    isSpeedMode = true
                    vibrator.vibrate(30)
                    if (isOpenToolBar) {
                        activityBinding.videoPlayerTopBarRoot.videoPlayerTopBar.visibility = View.GONE
                        activityBinding.videoPlayerBottomBarRoot.videoPlayerBottomBar.visibility = View.GONE
                    }
                    activityBinding.videoPlayerAssemblyRoot.videoPlayer2x.visibility = View.VISIBLE
                    activityBinding.videoPlayerAssemblyRoot.videoPlayer2x.startAnimation(alpha01)
                    player!!.setPlaybackSpeed(2.0f)
                    isLongClickMode = true
                    // 声明：现在处于长按状态
                    isOpenToolBar = false
                    // 声明：工具栏已被隐藏
                }
                true
            }

            //此方法涵盖：左右滑动调整进度
            activityBinding.videoPlayerControlRoot.setOnTouchListener { _, event ->
                // 当手指刚接触的屏幕
                if (event.action == MotionEvent.ACTION_DOWN) {
                    if (!isImplement) {
                        isImplement = true
                        firstX = event.x
                        // 存储刚刚接触屏幕的X轴位置
                    }
                }
                // 当手指在屏幕上移动
                if (event.action == MotionEvent.ACTION_MOVE) {
                    if (!isImplements) {
                        player!!.pause()
                        VideoPlayerObjects.isMove = true
                        if (!isOpenBottomToolBar && !isOpenToolBar) {
                            activityBinding.videoPlayerBottomBarRoot.videoPlayerBottomBar.visibility = View.VISIBLE
                            isOpenBottomToolBar = true
                        }
                        isImplements = true
                    }
                    val nowPosition = player!!.currentPosition
                    // 目前视频播放进度（Long形式）
                    val nowTime = timeParse(nowPosition)
                    // 目前视频播放进度（时间形式）
                    val newPosition = nowPosition + (event.x.toLong() - firstX.toLong()) * 20
                    // 目前想要调整的视频播放进度：目前视频的播放进度+(现在手指在屏幕上的位置-第一次手指在屏幕上的位置)×20
                    val newTime = timeParse(newPosition)
                    // 目前想要调整的视频播放进度（时间形式）
                    if (stateOfScroll == "LEFT") {
                        // 如果是向左滑动
                        // 向左滑动，左边文本显示想要调整到的时间，右边文本显示现在播放的时间
                        activityBinding.videoPlayerAssemblyRoot.playProgressLeft.text = newTime
                        activityBinding.videoPlayerAssemblyRoot.playProgressLeft.paint.isFakeBoldText = true
                        activityBinding.videoPlayerAssemblyRoot.playProgressRight.paint.isFakeBoldText = false
                        activityBinding.videoPlayerAssemblyRoot.playProgressRight.text = nowTime
                        if (newTime!!.contains("-")) {
                            // 如果想要调整到的时间超过逻辑（负数），就返回00:00
                            activityBinding.videoPlayerAssemblyRoot.playProgressLeft.text = "00:00"
                            newProgressLong = 0L
                            // 调整到视频0进度
                        } else if (newPosition > player.duration) {
                            // 如果想要调整到的地方是大于视频总长度的，不符合逻辑，返回视频最后处
                            activityBinding.videoPlayerAssemblyRoot.playProgressLeft.text =
                                timeParse(player.duration)
                            newProgressLong = player.duration
                            // 调整到视频最大进度
                        } else {
                            // 不符合以上两种特殊情况，则为正常情况
                            activityBinding.videoPlayerAssemblyRoot.playProgressLeft.text = newTime
                            newProgressLong = newPosition
                            // 调整到想要调整的进度
                        }
                    } else {
                        // 如果是向右滑动，和上面原理一样，不做解释
                        activityBinding.videoPlayerAssemblyRoot.playProgressLeft.text = nowTime
                        activityBinding.videoPlayerAssemblyRoot.playProgressLeft.paint.isFakeBoldText = false
                        activityBinding.videoPlayerAssemblyRoot.playProgressRight.paint.isFakeBoldText = true
                        activityBinding.videoPlayerAssemblyRoot.playProgressRight.text = newTime
                        if (newTime!!.contains("-")) {
                            activityBinding.videoPlayerAssemblyRoot.playProgressRight.text = "00:00"
                            newProgressLong = 0
                        } else if (newPosition > player.duration) {
                            activityBinding.videoPlayerAssemblyRoot.playProgressRight.text =
                                timeParse(player.duration)
                            newProgressLong = player.duration
                        } else {
                            activityBinding.videoPlayerAssemblyRoot.playProgressRight.text = newTime
                            newProgressLong = newPosition
                        }
                    }

                    if (newPosition > nowPosition) {
                        // 如果想要调整的进度大于目前正在播放的进度，则手指正在向右滑动
                        activityBinding.videoPlayerAssemblyRoot.playProgressTo.text = "->"
                        stateOfScroll = "RIGHT"
                    } else {
                        // 如果想要调整的进度小于目前正在播放的进度，则手指正在向左滑动
                        activityBinding.videoPlayerAssemblyRoot.playProgressTo.text = "<-"
                        stateOfScroll = "LEFT"
                    }

                    if (!isMoveMode && !isSpeedMode) {
                        // 如果手指不处于移动状态且不在进行倍速播放，就显示调整进度的信息框架
                        isMoveMode = true
                        activityBinding.videoPlayerAssemblyRoot.playProgressRoot.visibility = View.VISIBLE
                        activityBinding.videoPlayerAssemblyRoot.playProgressTo.text = "->"
                    }
                    // player!!.seekTo(newProgressLong)
                    if (newProgressLong>1) {
                        activityBinding.videoPlayerBottomBarRoot.videoPlayerVideoSlider.value = newProgressLong.toFloat()
                    }else{
                        activityBinding.videoPlayerBottomBarRoot.videoPlayerVideoSlider.value = 0F
                    }
                } else if (event.action == MotionEvent.ACTION_UP) {
                    // 手指离开屏幕执行的事件
                    if (isMoveMode) {
                        activityBinding.videoPlayerAssemblyRoot.playProgressRoot.visibility = View.GONE
                        isMoveMode = false
                        isImplement = false
                        isImplements = false
                        VideoPlayerObjects.isMove = false
                        player!!.seekTo(newProgressLong)
                        player.play()
                        // 离开屏幕后将视频调整到欲调整的位置
                    }
                    if (isLongClickMode) {
                        // 如果正在倍速，离开屏幕就停止倍速
                        activityBinding.videoPlayerAssemblyRoot.videoPlayer2x.startAnimation(alpha10)
                        activityBinding.videoPlayerAssemblyRoot.videoPlayer2x.visibility = View.GONE
                        player!!.setPlaybackSpeed(1.0f)
                        isSpeedMode = false
                        isLongClickMode = false
                    }
                }
                false
            }
        }
    }
}